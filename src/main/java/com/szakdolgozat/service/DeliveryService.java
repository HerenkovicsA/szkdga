package com.szakdolgozat.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.szakdolgozat.domain.Delivery;
import com.szakdolgozat.domain.Order;

public interface DeliveryService {
	
	public String getShortestRoute(boolean askGoogle, int popSize, int iterationMax, String[] addresses);

	public Delivery findDeliveryById(long deliveryId) throws Exception;

	public List<Delivery> findAll() throws Exception;

	public String deleteDelivery(long id);

	public Map<String, Order> getOrderOfDelivery(long deliveryId) throws Exception;

	public void editDelivery(Map<Object, Object> map);
}
