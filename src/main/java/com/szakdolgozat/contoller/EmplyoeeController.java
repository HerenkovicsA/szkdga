package com.szakdolgozat.contoller;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.szakdolgozat.domain.Delivery;
import com.szakdolgozat.domain.Order;
import com.szakdolgozat.domain.User;
import com.szakdolgozat.service.DeliveryService;
import com.szakdolgozat.service.OrderService;
import com.szakdolgozat.service.UserService;

import javafx.util.Pair;

@Controller
public class EmplyoeeController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private DeliveryService ds;
	private OrderService os;
	private UserService us;
	
	@Autowired
	public EmplyoeeController(DeliveryService ds, OrderService os, UserService us) {
		this.ds = ds;
		this.os = os;
		this.us = us;
	}
	
	@GetMapping("/employee")
	public String employee(Model model, Authentication authentication){
		model.addAttribute("employeeName",us.findByEmail(authentication.getName()).getName());
		model.addAttribute("employeeEmail",authentication.getName());
		return "employee";
	}
	
	@GetMapping(value = "/employee/newDelivery", params="email")
	public String getNewDelivery(Model model, @RequestParam String email, Authentication authentication){
		model.addAttribute("employeeName",us.findByEmail(authentication.getName()).getName());
		try {
			Pair<Double ,List<Order>> resultPair = ds.getNewDeliveryForEmployee(email);
			model.addAttribute("distance", resultPair.getKey());
			model.addAttribute("orderList", resultPair.getValue());
		} catch (Exception e) {
			if (e.getMessage().equals("Employee has active delivery")) {
				model.addAttribute("error", e.getMessage());
			} else if (e.getMessage().equals("Employee has active delivery")) {
				model.addAttribute("error", e.getMessage());
			} else {
				e.printStackTrace();
			}
			
		}
		return "employee";
	}
	
	@GetMapping(value = "/employee/allDeliveries", params="email")
	public String getAllDeliveries(Model model, @RequestParam String email, Authentication authentication){
		model.addAttribute("employeeName",us.findByEmail(authentication.getName()).getName());
		Set<Delivery> deliveries = ds.getAllDeliveryForEmployee(email);
		if(deliveries.isEmpty()) {
			model.addAttribute("error", "Nincs megjeleníthető kiszállítás");
		}else {
			model.addAttribute("deliveries", deliveries);
		}
		return "employee";
	}
	
	@GetMapping(value = "employee/delivery", params="deliveryId")
	public String getDelivery(Model model, @RequestParam long deliveryId, Authentication authentication){
		model.addAttribute("employeeName",us.findByEmail(authentication.getName()).getName());
		Pair<Pair<Double, Date>, List<Order>> resultPair;
		try {
			resultPair = ds.getDeliveryForEmployee(deliveryId);
			model.addAttribute("deadLine", resultPair.getKey().getValue());
			model.addAttribute("distance", resultPair.getKey().getKey());
			model.addAttribute("orderList", resultPair.getValue());
		} catch (NoSuchElementException nsee) {
			model.addAttribute("error", "A kiszállítás (" + deliveryId + ") nem létezik");
		}catch (Exception e) {
			if(e.getMessage().equalsIgnoreCase("Delviery does not exists")) {
				model.addAttribute("error", "Kiszállítás nem található (id=" + deliveryId + ")");
			}else {
				e.printStackTrace();	
			}
		}
		
		return "employee";
	}
	
	@GetMapping(value = "employee/orders", params = "orderId")
	public String getOrder(Model model, @RequestParam long orderId, Authentication authentication){
		model.addAttribute("employeeName",us.findByEmail(authentication.getName()).getName());
		Order order = os.getOrderById(orderId);
		if(order == null) {
			model.addAttribute("error", "Rendelés nem található (id=" + orderId + ")");
		} else {
			model.addAttribute("order", order);	
		}
		return "employee";
	}
	
	@PostMapping(value = "employee/orderIsDone", params= {"orderId","b"})
	public String setOrderDone(@RequestParam long orderId, @RequestParam boolean b) {
		int result = os.setOrderDone(orderId,b);
		if(result == 0) {
			log.info("Didn't change order's (" + orderId + ") done property to " + b);
		} else {
			log.debug("Changed order's (" + orderId + ") done property to " + b);
		}
		return "employee";
	}
	
	@PostMapping(value = "employee/deliveryIsDone", params= "deliveryId")
	public @ResponseBody String setDeliveryToDone(@RequestParam long deliveryId, Authentication authentication) {
		ds.setDeliveryToDone(deliveryId);
		return "/employee/allDeliveries?ad&email=" + authentication.getName();
	}
	
}
