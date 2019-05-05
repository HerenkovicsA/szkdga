package com.szakdolgozat.contoller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szakdolgozat.components.ErrorMessage;
import com.szakdolgozat.components.ValidationResponse;
import com.szakdolgozat.domain.Order;
import com.szakdolgozat.domain.Product;
import com.szakdolgozat.domain.User;
import com.szakdolgozat.service.DeliveryService;
import com.szakdolgozat.service.GoogleService;
import com.szakdolgozat.service.OrderService;
import com.szakdolgozat.service.PostCodeService;
import com.szakdolgozat.service.ProductService;
import com.szakdolgozat.service.StoreFileService;
import com.szakdolgozat.service.UserService;

@Controller
public class AdminController {
	
	private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);
	
	private UserService us;
	private ProductService ps;
	private OrderService os;
	private DeliveryService ds;
	private PostCodeService pcs;
	private GoogleService gs;
	private ServletContext context;
	
	@Autowired
	public AdminController(UserService us, ProductService ps, OrderService os, DeliveryService ds, PostCodeService pcs,
			GoogleService gs, ServletContext context) {
		this.us = us;
		this.ps = ps;
		this.os = os;
		this.ds = ds;
		this.pcs = pcs;
		this.gs = gs;
		this.context = context;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
	}

	@GetMapping("/admin")
	public String admin(){
		return "admin";
	}
	
	@GetMapping("/admin/employees")
	public String employees(Model model){
		try {
			model.addAttribute("employeeList", us.findAllEmployees());
			model.addAttribute("user", new User());
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return "admin";
	}
	
	@GetMapping("/admin/users")
	public String users(Model model){
		try {
			model.addAttribute("userList", us.findAllUsers());
			model.addAttribute("user", new User());
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return "admin";
	}
	
	@GetMapping("/admin/products")
	public String products(Model model){
		try {
			model.addAttribute("productList", ps.findAll());
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		model.addAttribute("newProduct", new Product());
		return "admin";
	}
	
	//on orders site show list of orders of userId
	@GetMapping(value = "/admin/orders", params = "userId")
	public String ordersOfUser(@RequestParam long userId, Model model) {
		try {
			model.addAttribute("orderList", us.findOrdersOfUser(userId));
			model.addAttribute("editedOrder", new Order());
		} catch (Exception e) {
			model.addAttribute("orderList", new HashSet());
			LOG.error(e.getMessage());
		}
		return "admin";
	}
	
	//on orders site list orders with deliveryId
	@GetMapping(value = "/admin/orders", params = "deliveryId")
	public String deliveryInformations(@RequestParam long deliveryId, Model model) {
		try {
			model.addAttribute("orderList", os.findOrdersOfDelivery(deliveryId));
			model.addAttribute("editedOrder", new Order());
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return "admin";
	}

	//on products site list products with orderId
	@GetMapping(value = "/admin/products", params = "orderId")
	public String listProductsToOrder(@RequestParam long orderId, Model model) {
		try {
			model.addAttribute("productsOfOrderList", os.findProductsOfOrder(orderId));
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		model.addAttribute("newProduct", new Product());
		return "admin";
	}
	
	//on deliveries site show delivery of order
	@GetMapping(value = "/admin/deliveries", params = "deliveryId")
	public String listOrdersToDelivery(@RequestParam long deliveryId, Model model) {
		try {
			model.addAttribute("deliveryList",ds.findDeliveryById(deliveryId));
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return "admin";
	}
	
	//on delivery site show all delivery of emlpyoeeId
	@GetMapping(value = "/admin/deliveries", params = "employeeId")
	public String listDeliveriesOfEmployee(@RequestParam long employeeId, Model model) {
		try {
			model.addAttribute("deliveryList", us.findDeliveriesOfEmployee(employeeId));
		} catch (Exception e) {
			model.addAttribute("deliveryList", null);
			LOG.error(e.getMessage());
		}
		return "admin";
	}
	
	@GetMapping("/admin/orders")
	public String orders(Model model){
		try {
			model.addAttribute("orderList", os.findAll());
			model.addAttribute("editedOrder", new Order());
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return "admin";
	}
	
	@GetMapping("/admin/deliveries")
	public String deliveries(Model model){
		try {
			model.addAttribute("deliveryList", ds.findAll());
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return "admin";
	}
	
	@PostMapping("/admin/editUser")
	public @ResponseBody ValidationResponse editUser(@ModelAttribute @Valid User user, BindingResult bindingResult) {

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
			} else if(!gs.validateAddress(user.getFullAddress())) {
				res.setStatus("FAIL");
				errorMessages.add(new ErrorMessage("invalidAddress", "A cím: " + user.getFullAddress() + " nem biztos, hogy létezik. "
						+ "Kérem ellenőrizze, hogy nem írt el valamit. (pl. 'ut'-at írt 'út' vagy 'utca' helyett)"));
				res.setErrorMessageList(errorMessages);
				
			} else {
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
	
	@PostMapping("/admin/addOrEditProduct")
    public String addOrEditProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile file) {
		ps.addOrEditProduct(product, file, context.getRealPath(""));
        return "redirect:/admin/products?p";
    }

	@PostMapping(value = "/admin/deleteProduct", params = "id")
    public @ResponseBody String deleteProduct(@RequestParam long id) {
		
		return ps.deleteProduct(id);
    }
	
	@PostMapping(value = "/admin/deleteProductFromOrder", params = {"productId","orderId"})
    public @ResponseBody String productFromOrder(@RequestParam long productId, @RequestParam long orderId) {
		
		return os.deleteProductFromOrder(productId, orderId);
    }
	
	@PostMapping(value = "/admin/recycleProduct", params = "id")
    public @ResponseBody String recycleProduct(@RequestParam long id) {
		
		return ps.recycleProduct(id);
    }
	
	@PostMapping(value = "/admin/deleteUser", params = "id")
    public @ResponseBody String deleteUser(@RequestParam long id) {
		
		return us.deleteUser(id);
    }

	@PostMapping(value = "/admin/deleteOrder", params = "id")
    public @ResponseBody String deleteOrder(@RequestParam long id) {
		
		return os.deleteOrder(id);
    }
	
	@PostMapping(value = "/admin/deleteDelivery", params = "id")
    public @ResponseBody String deleteDelivery(@RequestParam long id) {
		
		return ds.deleteDelivery(id);
    }
	
	@PostMapping(value = "/getAllUser", params = "user")
    public @ResponseBody Object getAllUser(@RequestParam String user) {
		try {
			return us.getAllUserNameAndId(Boolean.parseBoolean(user));
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return "error";
		}
    }
	
	@PostMapping(value = "/getProductsForModalTable", params="orderId")
    public @ResponseBody Object getProductsForModalTable(@RequestParam long orderId) {
		try {
			return os.getProductsOfOrderList(orderId);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return "error";
		}
    }
	
	@PostMapping("/admin/editOrder")
	public String editOrder(HttpServletRequest request) {
		ObjectMapper mapper = new ObjectMapper();
		Map<Object, Object> map = new HashMap<Object, Object>();
		try {
			map = mapper.readValue(request.getParameter("json"), new TypeReference<HashMap>(){});
			os.editOrder(map);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return "FAIL";
		}
		return "admin";
	}
	
	@PostMapping(value = "/getOrdersForModalTable", params="deliveryId")
    public @ResponseBody Object getOrdersForModalTable(@RequestParam long deliveryId) {
		try {
			return ds.getOrdersOfDelivery(deliveryId);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return e.getMessage();
		}
    }
	
	@RequestMapping(value = "/admin/user", params="userId")
    public String getUserById(@RequestParam long userId, Model model) {
		try {
			model.addAttribute("userList",us.findUserById(userId));
			model.addAttribute("user", new User());
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return "admin";
    }
	
	@PostMapping("/admin/editDelivery")
	public String editDelivery(HttpServletRequest request) {
		ObjectMapper mapper = new ObjectMapper();
		Map<Object, Object> map = new HashMap<Object, Object>();
		try {
			map = mapper.readValue(request.getParameter("json"), new TypeReference<HashMap>(){});
			ds.editDelivery(map);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			return "redirect:/error";
		}
		return "admin";
	}
}
