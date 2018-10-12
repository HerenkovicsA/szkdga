package com.szakdolgozat.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.szakdolgozat.service.DeliveryService;

@RestController
public class GoggleContoller {
	
	private DeliveryService ds;
	
	@Autowired
	public GoggleContoller(DeliveryService ds) {
		this.ds = ds;
	}
	

}
