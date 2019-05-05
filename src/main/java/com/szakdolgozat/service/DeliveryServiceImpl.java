package com.szakdolgozat.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.szakdolgozat.domain.Delivery;
import com.szakdolgozat.domain.Order;
import com.szakdolgozat.domain.User;
import com.szakdolgozat.geneticAlg.CandD;
import com.szakdolgozat.geneticAlg.GenAlgBusiness;
import com.szakdolgozat.repository.DeliveryRepository;

@Service
public class DeliveryServiceImpl implements DeliveryService{

	private static final Double CARGO_SIZE = 14900000D;
	private static final double CARGO_LIMIT = 0.7; 
	
	private GoogleService gs;
	private DeliveryRepository dr;
	private UserService us;
	private OrderService os;
	
	private static final Logger LOG = LoggerFactory.getLogger(DeliveryServiceImpl.class);
	
	public DeliveryServiceImpl() {
	}
	
	public DeliveryRepository getDr() {
		return dr;
	}

	@Autowired
	public void setDr(DeliveryRepository dr) {
		this.dr = dr;
	}

	public GoogleService getGs() {
		return gs;
	}

	@Autowired
	public void setGs(GoogleService gs) {
		this.gs = gs;
	}

	public UserService getUs() {
		return us;
	}

	@Autowired
	public void setUs(UserService us) {
		this.us = us;
	}

	public OrderService getOs() {
		return os;
	}

	@Autowired
	public void setOs(OrderService os) {
		this.os = os;
	}

	private CandD createCandD(String[] addresses) {
		int[][] distanceMatrix = new int[addresses.length][addresses.length];
		
		for (int i = 0; i < addresses.length; i++) {
			for (int j = 0; j < addresses.length; j++) {
				if(i==j || addresses[i].equalsIgnoreCase(addresses[j])) {
					distanceMatrix[i][j] = 0 ;
				}else {
					try {
						distanceMatrix[i][j] = gs.getDistance(addresses[i], addresses[j]);
					} catch (Exception e) {
						LOG.warn("Error while getting distance.");
						try {
							distanceMatrix[i][j] = gs.getDistance(addresses[i], addresses[j]);
						} catch (Exception e1) {
							LOG.error("Error happened again. Stop creatin Delivery.");
							return null;
						}
					}
				}
				
			}
		}
		
		return new CandD(distanceMatrix, addresses);
	}
	
	private Pair<Double, List<Order>> getShortestRoute(String[] addresses, List<Order> orders) {
		long startTime = System.currentTimeMillis();
		CandD cd = createCandD(addresses);
		if(cd == null) {
			return null;
		}
		long stopTime = System.currentTimeMillis();
		LOG.info("Getting data from Google API took: " + (stopTime - startTime) + " ms");
		int popSize = addresses.length * 5;
		int iterationMax = addresses.length * addresses.length;
		GenAlgBusiness gab = new GenAlgBusiness(popSize, iterationMax, popSize/10, orders, cd.getDistances());

		return gab.go();
	}

	@Override
	public Delivery findDeliveryById(long deliveryId) throws NoSuchElementException{
		Delivery delivery = dr.findById(deliveryId).get();
		return delivery;
	}

	@Override
	public List<Delivery> findAll() throws Exception {
		List<Delivery> deliveryList = new ArrayList<Delivery>();
		for (Delivery delivery : dr.findAll()) {
			deliveryList.add(delivery);
		}
		if(deliveryList.isEmpty()) throw new Exception("No delivery found!");
		return deliveryList;
	}

	@Override
	@Transactional
	public String deleteDelivery(long id) {
		Optional<Delivery> deliveryToRemove = dr.findById(id);
		if(deliveryToRemove.isPresent()) {
			removeDeliveryFromEmployee(deliveryToRemove.get());
			nullDeliveryOfOrders(deliveryToRemove.get());
			dr.deleteById(id);
			return "deleted";
		}else {
			return "not exists";
		}
	}
	
	private void nullDeliveryOfOrders(Delivery deliveryToRemove) {
		Set<Order> orderSet = deliveryToRemove.getOrdersOfDelivery();
		for (Order order : orderSet) {
			order.setDelivery(null);
		}
	}
	
	private void removeDeliveryFromEmployee(Delivery deliveryToRemove) {
		User employee = deliveryToRemove.getEmployee();
		if(employee != null) {
			Set<Delivery> deliverySet = employee.getDeliveriesOfEmployee();
			if(deliverySet.contains(deliveryToRemove)) {
				deliverySet.remove(deliveryToRemove);
			} else {
				LOG.error("Delivery with " + deliveryToRemove.getId() + " id does not belongs to employee " + employee.getName());
			}
		}
	}

	@Override
	public Map<String, Order> getOrdersOfDelivery(long deliveryId) throws Exception {
		Optional<Delivery> delivery = dr.findById(deliveryId);
		if(delivery.isPresent()) {
			Set<Order> orderSet = delivery.get().getOrdersOfDelivery();
			if(orderSet.isEmpty()) throw new Exception("Nincs rendelés a kiszállításhoz");
			Map<String, Order> resultMap = new HashMap<String, Order>();
			for (Order order : orderSet) {
				order.setProductsToOrder(null);
				resultMap.put(order.hashCode() + "|" + order.getUser().getId() + "|" + order.getUser().getEmail(), order);
			}
			return resultMap;
		}else {
			throw new Exception("Nem létezik kiszállítás " + deliveryId + " id-vel");
		}
	}

	@Override
	public void editDelivery(Map<Object, Object> map) throws Exception {
		boolean deleted = false;
		if(map.isEmpty()) LOG.error("Map is empty");
		Delivery deliveryToEdit = dr.findById(Long.valueOf(map.get("deliveryId").toString())).get();
		deliveryToEdit.setDone(Boolean.parseBoolean(map.get("done").toString()));
		if(map.get("employeeId") != null) {
			if(deliveryToEdit.getEmployee() == null && !map.get("employeeId").toString().isEmpty()
					|| deliveryToEdit.getEmployee() != null	&& deliveryToEdit.getEmployee().getId() != Long.parseLong(map.get("employeeId").toString())) {
				User newEmployee;
				try {
					newEmployee = us.findUserById(Long.parseLong(map.get("employeeId").toString()));
					deliveryToEdit.setEmployee(newEmployee);
				} catch (Exception e) {
					LOG.error(e.getMessage());
				}			
			}
		} 
		String[] fullDate = map.get("deliveryDate").toString().split("-");
		int year = Integer.parseInt(fullDate[0]);
		int month = Integer.parseInt(fullDate[1]);
		int day = Integer.parseInt(fullDate[2]);
		deliveryToEdit.setDeliveryDate(LocalDate.of(year, month, day));
		ArrayList<String> orderInfos = (ArrayList)map.get("orders");
		String[] orderArrayInfo; // values are: id;deadLine;done;delete "2;2018-11-29;false;false"]
		for (String orderInfo : orderInfos) {
			orderArrayInfo = orderInfo.split(";");
			
			if(Boolean.parseBoolean(orderArrayInfo[3])) {
				if(os.deleteOrderFromDelivery(Long.parseLong(orderArrayInfo[0]), deliveryToEdit)) {
					deleted = true;
					LOG.info("Order with " + orderArrayInfo[0] + " id is removed from delivery");
				} else {
					LOG.error("Order with " + orderArrayInfo[0] + " id does not exists");
				}				
			}else {
				Set<Order> orders = deliveryToEdit.getOrdersOfDelivery();
				for (Order order : orders) {
					if(order.getId() == Long.parseLong(orderArrayInfo[0])) {
						order.setDone(Boolean.parseBoolean(orderArrayInfo[2]));
						fullDate = orderArrayInfo[1].split("-");
						year = Integer.parseInt(fullDate[0]);
						month = Integer.parseInt(fullDate[1]);
						day = Integer.parseInt(fullDate[2]);
						order.setDeadLine(LocalDate.of(year, month, day));
					}
				}
			}
		}
		if(deleted && !deliveryToEdit.getOrdersOfDelivery().isEmpty()) {
			List<Order> orderList = new ArrayList<Order>(deliveryToEdit.getOrdersOfDelivery());
			User employee = deliveryToEdit.getEmployee();
			
			deleteDelivery(deliveryToEdit.getId());
			Thread delMakerThread = new Thread(() -> {
				Delivery newDelivery = new Delivery();
				newDelivery = dr.save(newDelivery);
				try {
					makeDelivery(orderList, newDelivery, employee);
				} catch (Exception e) {
					LOG.error(e.getMessage());
				}
			});
			delMakerThread.start();
			
		} else {
			dr.save(deliveryToEdit);
		}
	}
	
	@Override
	public void makeNewDelivery() throws Exception{
		Delivery newDelivery = new Delivery();
		dr.save(newDelivery);
		List<Order> orders = os.findOrdersForDelivery2(CARGO_SIZE, CARGO_LIMIT, newDelivery);
		if(orders == null || orders.isEmpty()) {
			LOG.error("No free order for delivery");
			dr.delete(newDelivery);
			return;
		}

		makeDelivery(orders, newDelivery, null);
	}
	
	private void makeDelivery(List<Order> orders, Delivery newDelivery, User employee) throws Exception {
		orders.add(0, os.getFakeOrder());
		
		String[] addresses = new String[orders.size()];
		for (int i = 0; i < orders.size(); i++) {
			addresses[i] = orders.get(i).getUser().getFullAddress();
		}
		Pair<Double, List<Order>> resultPair = getShortestRoute(addresses, orders);
		if(resultPair == null) {
			deleteDelivery(newDelivery.getId());
			throw new Exception("Creating was unsuccessful");
		}
		createNewDelivery(orders,resultPair.getFirst(),createOrderStringForDelivery(resultPair.getSecond()), newDelivery, employee);
	}
	
	@Override
	public Pair<Double, List<Order>> newDeliveryForEmployee(String email) throws Exception {
		User employee = us.findByEmail(email);
		if(employee == null) {
			LOG.error("Employee is not found with email address: " + email);
			throw new Exception("Employee is not found with email address: " + email);
		}
		if(us.hasActiveDelivery(employee)) {
			LOG.error("Employee has active delivery");
			throw new Exception("Employee has active delivery");
		}
		Delivery delivery = findDeliveryForEmployee();
		if(delivery != null) {
			employee.addToDelvierisOfEmployee(delivery);
			delivery.setEmployee(employee);
			dr.save(delivery);
			Pair<Pair<Double, LocalDate>, List<Order>> tmp = getDeliveryForEmployee(delivery.getId());
			return Pair.of(tmp.getFirst().getFirst(), tmp.getSecond());
		}
		return null;
	}

	/**
	 * 
	 * @return 
	 * A delivery entity or <b>null</b> if there is no delivery in db
	 */
	private Delivery findDeliveryForEmployee() {
		Optional<Delivery> optDelivery = dr.findTop1ByUserIsNullOrderByDeliveryDateAsc();
		if(optDelivery.isPresent()) return optDelivery.get();
		return null;
	}

	/**
	 * Edits and saves the new delivery from the params
	 * @param orders : list of orders to give to the delivery
	 * @param distance
	 * @param deliverOrder :  order to follow when delivering the orders
	 * @param newDelivery : the delivery to be saved
	 * @param employee
	 */
	private void createNewDelivery(List<Order> orders, Double distance, String deliverOrder, Delivery newDelivery, User employee) {
		createNewDelivery(orders, employee, distance, deliverOrder, newDelivery);
	}
	
	/**
	 * Edits and saves the new delivery from the params
	 * @param orders : list of orders to give to the delivery
	 * @param employee
	 * @param distance 
	 * @param deliverOrder : order to follow when delivering the orders
	 * @param newDelivery : the delivery to be saved
	 */
	private void createNewDelivery(List<Order> orders, User employee, Double distance, String deliverOrder,Delivery newDelivery) {
		orders.remove(0);
		newDelivery.setEmployee(employee);
		if(employee != null) {
			employee.addToDelvierisOfEmployee(newDelivery);
		}		
		LocalDate earliestDate = LocalDate.MAX;
		Set<Order> orderSet = new HashSet<Order>();
		for (Order order : orders) {
			order.setDelivery(newDelivery);
			orderSet.add(order);
			if(order.getDeadLine().isBefore(earliestDate)) earliestDate = order.getDeadLine();
		}
		newDelivery.setDeliveryDate(earliestDate);
		newDelivery.setOrdersOfDelivery(orderSet);
		newDelivery.setDone(false);
		newDelivery.setDistance(distance);
		newDelivery.setDeliveryOrder(deliverOrder);
		dr.save(newDelivery);
	}
	
	private String createOrderStringForDelivery(List<Order> finalList) {
		String result = "";
		for (Order order : finalList) {
			result += order.getId() + ";";
		}
		return result.substring(0, result.length() - 1);
	}

	@Override
	public Set<Delivery> getAllDeliveryForEmployee(String email) {
		User employee = us.findByEmail(email);
		return employee.getDeliveriesOfEmployee();
	}

	@Override
	public Pair<Pair<Double, LocalDate>, List<Order>> getDeliveryForEmployee(long deliveryId) throws Exception {
		Delivery delivery = findDeliveryById(deliveryId);
		List<Order> newOrderList = new ArrayList<Order>();
		Set<Order> orderSet = delivery.getOrdersOfDelivery();
		String[] orderedOrderIds = delivery.getDeliveryOrder().split(";");
		for (String orderId : orderedOrderIds) {
			newOrderList.add(os.getOrderFromSetById(orderSet, Long.parseLong(orderId)));
		}
		return Pair.of(Pair.of(delivery.getDistance(),
				delivery.getDeliveryDate()),newOrderList);
	}

	@Override
	public void setDeliveryToDone(long deliveryId) {
		Delivery delivery = findDeliveryById(deliveryId);
		delivery.setDone(true);
		dr.save(delivery);
	}

}
