package com.szakdolgozat.contoller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.szakdolgozat.domain.Product;
import com.szakdolgozat.domain.User;
import com.szakdolgozat.service.DeliveryService;
import com.szakdolgozat.service.OrderService;
import com.szakdolgozat.service.PostCodeService;
import com.szakdolgozat.service.ProductService;
import com.szakdolgozat.service.StoreFileService;
import com.szakdolgozat.service.UserService;
import com.szakdolgozat.validation.ErrorMessage;
import com.szakdolgozat.validation.ValidationResponse;

@Controller
public class AdminController {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private UserService us;
	private ProductService ps;
	private OrderService os;
	private DeliveryService ds;
	private PostCodeService pcs;
	private StoreFileService sfs;
	
	@Autowired
	public AdminController(UserService us, ProductService ps, OrderService os, DeliveryService ds, PostCodeService pcs,
			StoreFileService sfs) {
		this.us = us;
		this.ps = ps;
		this.os = os;
		this.ds = ds;
		this.pcs = pcs;
		this.sfs = sfs;
	}

	@GetMapping("/admin")
	public String admin(){
		System.out.println("admin");
		return "admin";
	}
	
	@GetMapping("/admin/employees")
	public String employees(Model model){
		try {
			model.addAttribute("employeeList", us.findAllEmployees());
			model.addAttribute("user", new User());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return "admin";
	}
	
	@GetMapping("/admin/users")
	public String users(Model model){
		try {
			model.addAttribute("userList", us.findAllUsers());
			model.addAttribute("user", new User());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return "admin";
	}
	
	@GetMapping("/admin/products")
	public String products(Model model){
		try {
			model.addAttribute("productList", ps.findAll());
			model.addAttribute("newProduct", new Product());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return "admin";
	}
	
	//on orders site show list of orders of userId
	@GetMapping(value = "/admin/orders", params = "userId")
	public String ordersOfUser(@RequestParam long userId, Model model) {
		System.out.println("ordersOfUser " + userId);
		try {
			model.addAttribute("orderList", us.findOrdersOfUser(userId));
		} catch (Exception e) {
			model.addAttribute("orderList", "");
			log.error(e.getMessage());
		}
		return "admin";
	}
	
	//on orders site list orders with deliveryId
	@GetMapping(value = "/admin/orders", params = "deliveryId")
	public String deliveryInformations(@RequestParam long deliveryId, Model model) {
		System.out.println("deliveryInformations " + deliveryId);
		try {
			model.addAttribute("orderList", os.findOrdersOfDelivery(deliveryId));
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return "admin";
	}

	//on products site list products with orderId
	@GetMapping(value = "/admin/products", params = "orderId")
	public String listProductsToOrder(@RequestParam long orderId, Model model) {
		System.out.println("listProductsToOrder " + orderId);
		try {
			model.addAttribute("productsOfOrderList", os.findProductsOfOrder(orderId));
			model.addAttribute("newProduct", new Product());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return "admin";
	}
	
	//on deliveries site show delivery of order
	@GetMapping(value = "/admin/deliveries", params = "deliveryId")
	public String listOrdersToDelivery(@RequestParam long deliveryId, Model model) {
		System.out.println("listOrdersToDelivery " + deliveryId);
		try {
			model.addAttribute("deliveryList",ds.findDeliveryById(deliveryId));
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return "admin";
	}
	
	//on delivery site show all delivery of emlpyoeeId
	@GetMapping(value = "/admin/deliveries", params = "employeeId")
	public String listDeliveriesOfEmployee(@RequestParam long employeeId, Model model) {
		System.out.println("listDeliveriesOfEmployee " + employeeId);
		try {
			model.addAttribute("deliveryList", us.findDeliveriesOfEmployee(employeeId));
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return "admin";
	}
	
	@GetMapping("/admin/orders")
	public String orders(Model model){
		try {
			model.addAttribute("orderList", os.findAll());
			System.out.println(os.findAll());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return "admin";
	}
	
	@GetMapping("/admin/deliveries")
	public String deliveries(Model model){
		try {
			model.addAttribute("deliveryList", ds.findAll());
			System.out.println(ds.findAll());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return "admin";
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
	}
	
	@PostMapping("/admin/editUser")
	public @ResponseBody ValidationResponse foobarPost(@ModelAttribute @Valid User user, BindingResult bindingResult) {

		ValidationResponse res = new ValidationResponse();
		final List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
		if(bindingResult.hasErrors()){
			res.setStatus("FAIL");
			List<FieldError> allErrors = bindingResult.getFieldErrors();
			for (FieldError objectError : allErrors) {
				errorMessages.add(new ErrorMessage(objectError.getField(), objectError.getDefaultMessage()));	
			}	
			res.setErrorMessageList(errorMessages);
		} else {
			if(!pcs.checkPostCodeAndCity(user.getPostCode(), user.getCity())) {
				res.setStatus("FAIL");
				errorMessages.add(new ErrorMessage("postCodeError", user.getCity() + " irányítószáma nem: " + user.getPostCode() + " !"));
				res.setErrorMessageList(errorMessages);
			}else {
				try {
					us.editUser(user);
				} catch (Exception e) {
					res.setStatus("FAIL");
					errorMessages.add(new ErrorMessage("emailError",e.getMessage()));
					res.setErrorMessageList(errorMessages);
				}
			}
			
		} 
		
		return res;		
	}
	
	@PostMapping("/admin/addProduct")
    public String addNewProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile file) {
		ps.addNewProduct(product, sfs.store(file));
        return "redirect:/admin/products?p";
    }

	@PostMapping(value = "/admin/deleteProduct", params = "id")
    public @ResponseBody String deleteProduct(@RequestParam long id) {
		
		return ps.deleteProduct(id);
    }

}
