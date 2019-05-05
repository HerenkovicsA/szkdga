package com.szakdolgozat.contoller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.szakdolgozat.components.DeliveryProcessor;
import com.szakdolgozat.domain.Order;
import com.szakdolgozat.domain.User;
import com.szakdolgozat.service.DeliveryService;
import com.szakdolgozat.service.GoogleService;
import com.szakdolgozat.service.OrderService;
import com.szakdolgozat.service.PostCodeService;
import com.szakdolgozat.service.ProductService;
import com.szakdolgozat.service.ShoppingCartService;
import com.szakdolgozat.service.UserService;

@Controller
public class HomeController {
	
	private static final Logger LOG = LoggerFactory.getLogger(HomeController.class);
	
	private UserService us;
	private PostCodeService pcs;
	private ShoppingCartService scs;
	private ProductService ps;
	private OrderService os;
	private DeliveryService ds;
	private GoogleService gs;
	

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
	}
	
	@Autowired
	public HomeController(UserService us, PostCodeService pcs, ShoppingCartService scs, ProductService ps, OrderService os, DeliveryService ds, GoogleService gs) {
		this.us = us;
		this.pcs = pcs;
		this.scs = scs;
		this.ps = ps;
		this.os = os;
		this.ds = ds;
		this.gs = gs;
		startMakingDeliveries();
	}
	
	private void startMakingDeliveries() {
		ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
		ses.scheduleAtFixedRate(new DeliveryProcessor(ds,os), 0, 1, TimeUnit.HOURS);	
	}

	@RequestMapping("/")
	public String home(Model model, Authentication authentication){
		User user = us.findByEmail(authentication.getName());
		model.addAttribute("userName",user.getName());
		model.addAttribute("userId",user.getId());
		try {
			model.addAttribute("products", ps.findAll());
		} catch (Exception e) {
			model.addAttribute("productError", "Nincs termék");
			LOG.error(e.getMessage());
		}
		return "index";
	}
	
	@GetMapping("/user")
	public String getUser(Model model) {
		model.addAttribute("user", new User());
		return "index";
	}
	
	@PostMapping("/getLogedUser")
	public @ResponseBody User getLogedUser(Authentication authentication){
		return us.findByEmail(authentication.getName()); 
	}
	
	@PostMapping("/user")
    public String editUser(@ModelAttribute @Valid User user, BindingResult bindingResult, Model model) {
		if(bindingResult.hasErrors())
        {
			LOG.error("Error with user edit: " + bindingResult.getAllErrors());
        }else if(!pcs.checkPostCodeAndCity(user.getPostCode(), user.getCity())) {
			LOG.debug("City: " + user.getCity() + " and postcode: " + user.getPostCode() + " is not equal");
			model.addAttribute("postCodeError", user.getCity() + " irányítószáma nem: " + user.getPostCode() + " !");
		} else if(!gs.validateAddress(user.getFullAddress())) {
			LOG.warn("Address is probably not valid");
			model.addAttribute("invalidAddress", "A cím: " + user.getFullAddress() + " nem biztos, hogy létezik. Kérem ellenőrizze, hogy nem írt el"
					+ "valamit. (pl. 'ut'-at írt 'út' vagy 'utca' helyett)");
		} else {
			try { 
				us.editUser(user);
			}catch(Exception e) {
				if(e.getMessage().contains("email cím")) {
					LOG.debug("reg problem: email already exists");
					model.addAttribute("emailError",e.getMessage());
				}
				e.printStackTrace();
			}
		}
		return "index";
    }
	
	@GetMapping("/user/cart")
	public String goToCart(Model model, Authentication auth) {
		User user = us.findByEmail(auth.getName());
		model.addAttribute("cart", scs.getCart(auth.getName()));
		model.addAttribute("userName",user.getName());
		return "index";
	}
	
	@PostMapping(value = "/user/addToCart", params = "productId")
	public @ResponseBody Object addToCart(@RequestParam long productId, Authentication auth) {
		if(scs.addToCart(productId, auth.getName()) == 1) {
			return scs.getCart(auth.getName());
		}
		return "error";
	}
	
	@GetMapping("/emptyCart")
	public @ResponseBody String emptyCart(Authentication auth) {
		scs.emptyCart(auth.getName());
		return "ok";
	}
	
	@PostMapping(value = "/removeFromCart", params = "productId")
	public @ResponseBody String removeFromCart(@RequestParam long productId, Authentication auth) {
		return scs.removeFromCart(productId, auth.getName());
	}
	
	@PostMapping(value = "/changeAmount", params = {"productId","quantity"})
	public @ResponseBody String changeAmount(@RequestParam long productId, @RequestParam int quantity, Authentication auth) {
		return scs.changeAmount(productId, quantity, auth.getName());
	}
	
	@PostMapping(value = "/makeAnOrder")
	public @ResponseBody String makeAnOrder(Authentication auth) {
		return scs.makeOrders(auth.getName());
	}
	
	@GetMapping("/user/orders")
	public String getOrders(Authentication auth, Model model) {
		User user = us.findByEmail(auth.getName());
		List<Order> orderList = us.findOrdersOfUser(auth.getName());
		if(orderList != null && !orderList.isEmpty()) {
			model.addAttribute("orderList", orderList);
		} else {
			model.addAttribute("error", "Nincsenek még rendelései");
		}
		model.addAttribute("userName", user.getName());
		return "index";
	}
	
	@GetMapping(value = "/cancelOrder", params = "orderId")
	public String cancelOrder(Authentication auth, @RequestParam long orderId, Model model) {
		if(!os.deleteOrder(orderId).equals("deleted")) {
			model.addAttribute("error", "A törlés nem sikerült");
			return getOrders(auth, model);
		}
		return "redirect:/user/orders?o";
	}
	
	@PostMapping(value = "/user/products", params = "orderId")
	public @ResponseBody Object getProductsForOrder(@RequestParam long orderId) {
		try {
			return os.getProductsOfOrder(orderId);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return "error";
		}
	}
}
