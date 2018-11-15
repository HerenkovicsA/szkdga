package com.szakdolgozat.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

@Service
public class DeliveryServiceImpl implements DeliveryService{

	private GoogleService gs;
	private DeliveryRepository dr;
	private UserRepository ur;
	private OrderService os;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	public GoogleService getGs() {
		return gs;
	}
	
	@Autowired
	public void setGoogleService(GoogleService gs, DeliveryRepository dr, UserRepository ur, OrderService os) {
		this.gs = gs;
		this.dr = dr;
		this.ur = ur;
		this.os = os;
	}
	
	private CandD createCandD(boolean askGoogle, String[] addresses) {
		int[][] distanceMatrix = new int[addresses.length][addresses.length];
		
		for (int i = 0; i < addresses.length; i++) {
			for (int j = 0; j < addresses.length; j++) {
				if(i==j) {
					distanceMatrix[i][j] = 0 ;
				}else {
					distanceMatrix[i][j] = gs.getDistance(askGoogle, addresses[i], addresses[j]);
				}
				
			}
		}
		
		return new CandD(distanceMatrix, addresses);
	}
	
	public String getShortestRoute(boolean askGoogle, int popSize, int iterationMax, String[] addresses) {
		CandD cd = createCandD(askGoogle, addresses);
		GenAlgBusiness gab = new GenAlgBusiness(popSize, iterationMax, popSize/10, cd.getCities(), cd.getDistances());

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
		Set<Delivery> deliverySet = employee.getDeliveriesOfEmployee();
		if(deliverySet.contains(deliveryToRemove)) {
			deliverySet.remove(deliveryToRemove);
			employee.setDeliveriesOfEmployee(deliverySet);
		} else log.error("Delivery with " + deliveryToRemove.getId() + " id does not belongs to employee " + employee.getName());
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
		System.out.println("map");
		System.out.println(map);
		System.out.println("----------------");
		if(map.isEmpty()) log.error("Map is empty");
		Delivery deliveryToEdit = dr.findById(Long.valueOf(map.get("deliveryId").toString())).get();
		deliveryToEdit.setDone(Boolean.parseBoolean(map.get("done").toString()));
		if(deliveryToEdit.getEmployee().getId() != Long.parseLong(map.get("employeeId").toString())) {
			User newEmployee = ur.findById(Long.parseLong(map.get("employeeId").toString())).get();
			deliveryToEdit.setEmployee(newEmployee);
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
	
}
