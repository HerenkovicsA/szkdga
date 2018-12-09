package com.szakdolgozat.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.szakdolgozat.components.ProductAndQuantityResponse;
import com.szakdolgozat.domain.Delivery;
import com.szakdolgozat.domain.Order;
import com.szakdolgozat.domain.Product;
import com.szakdolgozat.domain.ProductsToOrders;
import com.szakdolgozat.domain.User;
import com.szakdolgozat.geneticAlg.CandD;
import com.szakdolgozat.geneticAlg.GenAlgBusiness;
import com.szakdolgozat.repository.DeliveryRepository;
import com.szakdolgozat.repository.UserRepository;

import javafx.util.Pair;

@Service
public class DeliveryServiceImpl implements DeliveryService{

	private final Double CARGO_SIZE = 14900000D;
	private final double CARGO_LIMIT = 0.7; 
	
	private GoogleService gs;
	private DeliveryRepository dr;
	private UserService us;
	private OrderService os;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private final String COMPANYS_ADDRESS = "9026, Győr, Egyetem tér 1.";
	private final String TEST_STRING = "330.194 km : 9026, Győr, Egyetem tér 1. -> 1064, Budapest Andrássy út 5 -> 1064, Budapest Andrássy út 5 ->"
			+ " 9099, Pér Szent Imer utca 46 -> 9084, Győrság Ország út 29 -> 9082, Nyúl Táncsics Mihály utca 139 -> 9081, Győrújbarát Veres Péter utca 33 -> "
			+ "9012, Győr Hegyalja utca 12 -> 9024, Győr Cuha utca 8 -> 9028, Győr József Attila utca 175 -> 9027, Győr Gömb utca 16 -> 9022, Győr Bisinger setany 13 -> "
			+ "9022, Győr Bisinger setany 13 -> 9022, Győr Bisinger setany 13 -> 9184, Kunsziget Ifjúság utca 13 -> 9153, Öttevény Kossuth utca 23 -> "
			+ "9152, Börcs Rákóczi Ferenc utca 1 -> 9151, Abda Árpád utca 35 -> 9151, Abda Szent Imre utca 40 -> 9025, Győr Haladás utca 26 -> 9025, Győr Botond utca 5 -> "
			+ "9026, Győr, Egyetem tér 1.";
	
	@Autowired
	public DeliveryServiceImpl(GoogleService gs, DeliveryRepository dr, UserService us, OrderService os) {
		this.gs = gs;
		this.dr = dr;
		this.us = us;
		this.os = os;
	}
	
	private CandD createCandD(boolean askGoogle, String[] addresses) {
		int[][] distanceMatrix = new int[addresses.length][addresses.length];
		
		for (int i = 0; i < addresses.length; i++) {
			for (int j = 0; j < addresses.length; j++) {
				if(i==j || addresses[i].equalsIgnoreCase(addresses[j])) {
					distanceMatrix[i][j] = 0 ;
				}else {
					distanceMatrix[i][j] = gs.getDistance(askGoogle, addresses[i], addresses[j]);
				}
				
			}
		}
		
		return new CandD(distanceMatrix, addresses);
	}
	
	private Pair<Double, List<Order>> getShortestRoute(boolean askGoogle,String[] addresses, List<Order> orders) {
		long startTime = System.currentTimeMillis();
		CandD cd = createCandD(askGoogle, addresses);
		long stopTime = System.currentTimeMillis();
		log.info("Getting data from Google API took: " + (stopTime - startTime) + " ms");
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
	public String deleteDelivery(long id) {
		Optional<Delivery> deliveryToRemove = dr.findById(id);
		if(deliveryToRemove.isPresent()) {
			removedDeliveryFromEmployee(deliveryToRemove.get());
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
	
	private void removedDeliveryFromEmployee(Delivery deliveryToRemove) {
		User employee = deliveryToRemove.getEmployee();
		if(employee != null) {
			Set<Delivery> deliverySet = employee.getDeliveriesOfEmployee();
			if(deliverySet.contains(deliveryToRemove)) {
				deliverySet.remove(deliveryToRemove);
				employee.setDeliveriesOfEmployee(deliverySet);
			} else log.error("Delivery with " + deliveryToRemove.getId() + " id does not belongs to employee " + employee.getName());
		}
	}

	@Override
	public Map<String, Order> getOrderOfDelivery(long deliveryId) throws Exception {
		Optional<Delivery> delivery = dr.findById(deliveryId);
		if(delivery.isPresent()) {
			Set<Order> orderSet = delivery.get().getOrdersOfDelivery();
			if(orderSet.isEmpty()) throw new Exception("Nincs rendelés a kiszállításhoz");
			Map<String, Order> resultMap = new HashMap<String, Order>();
			for (Order order : orderSet) {
				order.setProductsToOrder(null);
				resultMap.put(order.hashCode() + "|" + order.getUser().getId() + "|" + order.getUser().getEmail(), order);
				System.out.println(order.getDeadLine().getTimezoneOffset());
			}
			log.warn(orderSet.toString());
			return resultMap;
		}else {
			throw new Exception("Nem létezik kiszállítás " + deliveryId + " id-vel");
		}
	}

	@Override
	public void editDelivery(Map<Object, Object> map) {
		if(map.isEmpty()) log.error("Map is empty");
		Delivery deliveryToEdit = dr.findById(Long.valueOf(map.get("deliveryId").toString())).get();
		deliveryToEdit.setDone(Boolean.parseBoolean(map.get("done").toString()));
		if(deliveryToEdit.getEmployee().getId() != Long.parseLong(map.get("employeeId").toString())) {
			User newEmployee;
			try {
				newEmployee = us.findUserById(Long.parseLong(map.get("employeeId").toString()));
				deliveryToEdit.setEmployee(newEmployee);
			} catch (Exception e) {
				log.error(e.getMessage());
			}			
		}
		String[] fullDate = map.get("deliveryDate").toString().split("-");
		int year = Integer.parseInt(fullDate[0]);
		int month = Integer.parseInt(fullDate[1]);
		int day = Integer.parseInt(fullDate[2]);
		deliveryToEdit.setDeliveryDate(new Date(year-1900, month-1, day));
		ArrayList<String> orderInfos = (ArrayList)map.get("orders");
		String[] orderArrayInfo; // values are: id;deadLine;done;delete "2;2018-11-29;false;false"]
		for (String orderInfo : orderInfos) {
			orderArrayInfo = orderInfo.split(";");
			
			if(Boolean.parseBoolean(orderArrayInfo[3])) {
				if(os.deleteOrder(Long.parseLong(orderArrayInfo[0])).equals("not exists")) {
					log.error("Order with " + orderArrayInfo[0] + " id does not exists");
				} else {
					log.info("Order with " + orderArrayInfo[0] + " id is deleted from database");
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
						order.setDeadLine(new Date(year-1900,month-1,day));
						System.out.println("order");
						System.out.println(order);
						System.out.println("++++++++++");
					}
				}
			}
		}
		dr.save(deliveryToEdit);	
	}

	/**
	 * @deprecated Use {@link #newDeliveryForEmployee}
	 */
	public Pair<Double, List<Order>> getNewDeliveryForEmployee(String email) throws Exception {
		User employee = us.findByEmail(email);
		if(employee == null) {
			log.error("Employee is not found with email address: " + email);
			throw new Exception("Employee is not found with email address: " + email);
		}
		if(us.hasActiveDelivery(employee)) {
			log.error("Employee has active delivery");
			throw new Exception("Employee has active delivery");
		}
		List<Order> orders = os.findOrdersForDelivery();
		if(orders.isEmpty()) {
			log.error("No free order for delivery");
			throw new Exception("No free order");
		}
		
		orders.add(0, getFakeOrder());
		
		String[] addresses = new String[orders.size()];
		for (int i = 0; i < orders.size(); i++) {
			addresses[i] = orders.get(i).getUser().getFullAddress();
		}
		Pair<Double, List<Order>> resultPair = getShortestRoute(true, addresses, orders);	
		createNewDelivery(orders,employee,resultPair.getKey(),createOrderStringForDelivery(resultPair.getValue()));
		return resultPair;
	}
	
	@Override
	public void makeNewDelivery() {
		Delivery newDelivery = new Delivery();
		dr.save(newDelivery);
		List<Order> orders = os.findOrdersForDelivery2(CARGO_SIZE, CARGO_LIMIT, newDelivery);
		if(orders == null || orders.isEmpty()) {
			log.error("No free order for delivery");
			return;
		}

		orders.add(0, getFakeOrder());
		
		String[] addresses = new String[orders.size()];
		for (int i = 0; i < orders.size(); i++) {
			addresses[i] = orders.get(i).getUser().getFullAddress();
		}
		Pair<Double, List<Order>> resultPair = getShortestRoute(true, addresses, orders);	
		createNewDelivery(orders,resultPair.getKey(),createOrderStringForDelivery(resultPair.getValue()), newDelivery);
	}
	
	@Override
	public Pair<Double, List<Order>> newDeliveryForEmployee(String email) throws Exception {
		User employee = us.findByEmail(email);
		if(employee == null) {
			log.error("Employee is not found with email address: " + email);
			throw new Exception("Employee is not found with email address: " + email);
		}
		if(us.hasActiveDelivery(employee)) {
			log.error("Employee has active delivery");
			throw new Exception("Employee has active delivery");
		}
		Delivery delivery = findDeliveryForEmployee();
		if(delivery != null) {
			employee.addToDelvierisOfEmployee(delivery);
			delivery.setEmployee(employee);
			dr.save(delivery);
			Pair<Pair<Double, Date>, List<Order>> tmp = getDeliveryForEmployee(delivery.getId());
			return new Pair<Double, List<Order>>(tmp.getKey().getKey(), tmp.getValue());
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
	 * Edits and saves the new delivery from the params with no employee
	 * @param orders : list of orders to give to the delivery
	 * @param distance
	 * @param deliverOrder :  order to follow when delivering the orders
	 * @param newDelivery : the delivery to be saved
	 */
	private void createNewDelivery(List<Order> orders, Double distance, String deliverOrder, Delivery newDelivery) {
		createNewDelivery(orders, null, distance, deliverOrder, newDelivery);
	}
	
	/**
	 * Creates the new delivery from the params
	 * @param orders : list of orders to give to the delivery
	 * @param employee
	 * @param distance
	 * @param deliverOrder :  order to follow when delivering the orders
	 */
	private void createNewDelivery(List<Order> orders, User employee, Double distance, String deliverOrder) {
		createNewDelivery(orders, employee, distance, deliverOrder, new Delivery());
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
		long startTime = System.currentTimeMillis();
		newDelivery.setEmployee(employee);
		if(employee != null) {
			employee.addToDelvierisOfEmployee(newDelivery);
		}		
		Date earliestDate = new Date(5000,1,1);
		Set<Order> orderSet = new HashSet<Order>();
		for (Order order : orders) {
			order.setDelivery(newDelivery);
			orderSet.add(order);
			if(order.getDeadLine().before(earliestDate)) earliestDate = order.getDeadLine();
		}
		newDelivery.setDeliveryDate(earliestDate);
		newDelivery.setOrdersOfDelivery(orderSet);
		newDelivery.setDone(false);
		newDelivery.setDistance(distance);
		newDelivery.setDeliveryOrder(deliverOrder);
		dr.save(newDelivery);
		long stopTime = System.currentTimeMillis();
		log.info("createNewDelivery took: " + (stopTime - startTime) + " ms");
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
	public Pair<Pair<Double, Date>, List<Order>> getDeliveryForEmployee(long deliveryId) throws Exception {
		Delivery delivery = findDeliveryById(deliveryId);
		List<Order> newOrderList = new ArrayList<Order>();
		Set<Order> orderSet = delivery.getOrdersOfDelivery();
		String[] orderedOrderIds = delivery.getDeliveryOrder().split(";");
		for (String orderId : orderedOrderIds) {
			newOrderList.add(getOrderFromSetById(orderSet, Long.parseLong(orderId)));
		}
		return new Pair<Pair<Double, Date>, List<Order>>(new Pair<Double, Date>(delivery.getDistance(),
				delivery.getDeliveryDate()),newOrderList);
	}
	
	private Order getFakeOrder() {
		User fakeUserForStart = new User();
		fakeUserForStart.setFullAddress(COMPANYS_ADDRESS);
		Order fakeOrderForStart = new Order();
		fakeOrderForStart.setUser(fakeUserForStart);
		return fakeOrderForStart;
	}
	
	private Order getOrderFromSetById(Set<Order> orderSet, long id) throws Exception {
		if(id == 0 ) {
			return getFakeOrder();
		} else {
			for (Order order : orderSet) {
				if(order.getId() == id) return order;
			}
		}
		log.error("Order is not found (id=" + id +")");
		throw new Exception("Order is not found");
	}

	@Override
	public void setDeliveryToDone(long deliveryId) {
		Delivery delivery = findDeliveryById(deliveryId);
		delivery.setDone(true);
		dr.save(delivery);
	}

}
