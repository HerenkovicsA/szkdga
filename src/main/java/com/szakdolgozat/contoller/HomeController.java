package com.szakdolgozat.contoller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.szakdolgozat.service.DeliveryService;

@Controller
public class HomeController {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private DeliveryService ds;
	private String[] addresses = {"Dunaszeg Rákóczi Ferenc utca 8.", "Abda Szent Imre utca 8.", "Győr Március 15. utca 3."}; 

	@Autowired
	public HomeController(DeliveryService ds) {
		this.ds = ds;
	}
	
	@RequestMapping("/")
	public String home(){
		return "index";
	}
	
	@RequestMapping("/solution")
	public String solution(Model model) {
		model.addAttribute("solution", ds.getShortestRoute(false, 30, 500, addresses));
		return "index";
	}
	
	@RequestMapping("/admin")
	public String admin(){
		return "admin";
	}
	
	@RequestMapping("/employee")
	public String employee(){
		return "employee";
	}
	
}
