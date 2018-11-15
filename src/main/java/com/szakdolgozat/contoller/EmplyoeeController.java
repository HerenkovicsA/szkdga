package com.szakdolgozat.contoller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class EmplyoeeController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	public EmplyoeeController() {
		
	}
	
	@RequestMapping("/employee/a")
	public String employee(Authentication authentication){
		log.info(authentication.getName());
		return "employee";
	}
	
}
