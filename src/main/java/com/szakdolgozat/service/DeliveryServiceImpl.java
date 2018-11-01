package com.szakdolgozat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.szakdolgozat.domain.Delivery;
import com.szakdolgozat.domain.Order;
import com.szakdolgozat.geneticAlg.CandD;
import com.szakdolgozat.geneticAlg.GenAlgBusiness;
import com.szakdolgozat.repository.DeliveryRepository;

@Service
public class DeliveryServiceImpl implements DeliveryService{

	private GoogleService gs;
	private DeliveryRepository dr;
	
	public GoogleService getGs() {
		return gs;
	}
	
	@Autowired
	public void setGoogleService(GoogleService gs, DeliveryRepository dr) {
		this.gs = gs;
		this.dr = dr;
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
	
}
