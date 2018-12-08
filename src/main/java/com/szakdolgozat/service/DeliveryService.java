package com.szakdolgozat.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.szakdolgozat.domain.Delivery;
import com.szakdolgozat.domain.Order;
import com.szakdolgozat.domain.User;

import javafx.util.Pair;

public interface DeliveryService {
	
	public Delivery findDeliveryById(long deliveryId) throws Exception;

	public List<Delivery> findAll() throws Exception;

	public String deleteDelivery(long id);

	public Map<String, Order> getOrderOfDelivery(long deliveryId) throws Exception;

	public void editDelivery(Map<Object, Object> map);

	public Pair<Double, List<Order>> getNewDeliveryForEmployee(String email) throws Exception;

	public Set<Delivery> getAllDeliveryForEmployee(String email);

	/**
	 * @return
	 * The ordered list of orders for delivery in Pair with distance/deadline
	 */
	public Pair<Pair<Double, Date>, List<Order>> getDeliveryForEmployee(long deliveryId) throws Exception;

	public void setDeliveryToDone(long deliveryId);

	/**
	 * Adds a delivery to the employee.
	 * Returns a delivery order with distance.<br/>
	 */
	public Pair<Double, List<Order>> newDeliveryForEmployee(String email) throws Exception;

	/**
	 * Makes a new delivery which doesn't belong to any employee yet 
	 * @throws Exception if there is no order to deliver
	 */
	public void makeNewDelivery();
}
