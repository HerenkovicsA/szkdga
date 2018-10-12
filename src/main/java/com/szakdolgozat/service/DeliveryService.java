package com.szakdolgozat.service;

import java.lang.reflect.Array;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.szakdolgozat.geneticAlg.CandD;
import com.szakdolgozat.geneticAlg.GenAlgBusiness;

@Service
public class DeliveryService {

	private GoogleService gs;
	
	public GoogleService getGs() {
		return gs;
	}
	
	@Autowired
	public void setGoogleService(GoogleService gs) {
		this.gs = gs;
	}
	
	private CandD createCandD(boolean askGoogle, String[] addresses) {
		int[][] distanceMatrix = new int[addresses.length][addresses.length];
		
		for (int i = 0; i < addresses.length; i++) {
			for (int j = 0; j < addresses.length; j++) {
				if(i==j) {
					distanceMatrix[i][j] = 0 ;
				}else {
					System.out.println(gs);
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
	
}
