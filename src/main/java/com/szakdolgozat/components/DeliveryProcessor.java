package com.szakdolgozat.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.szakdolgozat.service.DeliveryService;
import com.szakdolgozat.service.OrderService;

public class DeliveryProcessor implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(DeliveryProcessor.class);
	private static final Double CARGO_SIZE = 14900000D;
	
	private DeliveryService ds;
	private OrderService os;
	
	public DeliveryProcessor() {
	
	}
	
	public DeliveryProcessor(DeliveryService ds, OrderService os) {
		this.ds = ds;
		this.os = os;
	}
	
	@Override
	public void run() {
		LOG.info("Starting DeliveryProcessor thread...");
		while(os.hasEnoughForOneDelivery(CARGO_SIZE) || os.hasUrgentOrder()) {
			LOG.info("Start making new delivery...");
			try {
				ds.makeNewDelivery();
			} catch (Exception e) {
				LOG.error("Error while creating delivery. Stoping thread to retry later.\n" + e.getMessage());
				break;
			} 
		}
		LOG.info("Stopping DeliveryProcessor thread...");
	}

}
