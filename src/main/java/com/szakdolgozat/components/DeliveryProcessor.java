package com.szakdolgozat.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.szakdolgozat.service.DeliveryService;
import com.szakdolgozat.service.OrderService;

public class DeliveryProcessor implements Runnable {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private final Double CARGO_SIZE = 14900000D;
	
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
		log.info("Starting DeliveryProcessor thread...");
		if(os.hasEnoughForOneDelivery(CARGO_SIZE)) {
			log.info("Start making new delivery...");
			ds.makeNewDelivery();
		} else {
			log.info("Stopping DeliveryProcessor thread...");
			return;
		}
	}

}
