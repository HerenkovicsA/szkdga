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

	public Pair<Pair<Double, Date>, List<Order>> getDeliveryForEmployee(long deliveryId) throws Exception;

	public void setDeliveryToDone(long deliveryId);

	public Pair<Double, List<Order>> newDeliveryForEmployee(String email) throws Exception;

	public void makeNewDelivery() throws Exception;
}
