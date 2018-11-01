package com.szakdolgozat.service;

import java.util.List;

import com.szakdolgozat.domain.Delivery;

public interface DeliveryService {
	
	public String getShortestRoute(boolean askGoogle, int popSize, int iterationMax, String[] addresses);

	public Delivery findDeliveryById(long deliveryId) throws Exception;

	public List<Delivery> findAll() throws Exception;
}
