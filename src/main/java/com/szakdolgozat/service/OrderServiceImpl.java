package com.szakdolgozat.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szakdolgozat.components.ProductAndQuantityResponse;
import com.szakdolgozat.domain.Order;
import com.szakdolgozat.domain.Product;
import com.szakdolgozat.domain.ProductsToOrders;
import com.szakdolgozat.domain.User;
import com.szakdolgozat.repository.DeliveryRepository;
import com.szakdolgozat.repository.OrderRepository;
import com.szakdolgozat.repository.ProductsToOrdersRepository;
import com.szakdolgozat.repository.UserRepository;

@Service
public class OrderServiceImpl implements OrderService {

	private OrderRepository or;
	private DeliveryRepository dr;
	private UserRepository ur;
	private ProductsToOrdersService ptos;
	private ProductsToOrdersRepository ptor;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	public OrderServiceImpl(OrderRepository or, DeliveryRepository dr, UserRepository ur, ProductsToOrdersService ptos) {
		this.or = or;
		this.dr = dr;
		this.ur	= ur;
		this.ptos = ptos;
	}
	
	@Override
	public List<Order> findAll() throws Exception {
		List<Order> orderList = new ArrayList<Order>();
		for (Order order : or.findAll()) {
			orderList.add(order);
		}
		if(orderList.isEmpty()) throw new Exception("No order found!");
		return orderList;
	}

	@Override
	public List<Order> findOrdersOfDelivery(long deliveryId) throws Exception {
		List<Order> orderList = new ArrayList<Order>();
		for (Order order : or.findAllByDelivery(dr.findById(deliveryId).get())) {
			orderList.add(order);
		}
		if(orderList.isEmpty()) throw new Exception("No order found for delivery id!");
		return orderList;
	}

	@Override
	public Set<ProductsToOrders> findProductsOfOrder(long orderId) throws Exception{
		Order order = or.findById(orderId).get();
		Set<ProductsToOrders> productsToOrderSet = order.getProductsToOrder();
		if(productsToOrderSet.isEmpty()) throw new Exception("No products to order");
		return productsToOrderSet;
	}

	@Override
	public String deleteOrder(long id) {
		boolean exists = or.findById(id).isPresent();
		if(exists) {
			or.deleteById(id);
			return "deleted";
		}else {
			return "not exists";
		}
	}

	@Override
	public List<ProductAndQuantityResponse> getProductsOfOrderList(long orderId) throws Exception {
		List<ProductAndQuantityResponse> resultList = new ArrayList<ProductAndQuantityResponse>();
		Order order = or.findById(orderId).get();
		Set<ProductsToOrders> productsToOrderSet = order.getProductsToOrder();
		if(productsToOrderSet.isEmpty()) throw new Exception("No products to order");
		for (ProductsToOrders productToOrder : productsToOrderSet) {
			resultList.add(new ProductAndQuantityResponse(productToOrder.getProduct().getId(), 
					productToOrder.getProduct().getName(), productToOrder.getQuantity(), productToOrder.getProduct().getPrice()));
		}
		return resultList;
	}

	@Override
	public void editOrder(Map<Object, Object> map) {
		if(map.isEmpty()) log.error("Map is empty");
		Order orderToEdit = or.findById(Long.valueOf(map.get("orderId").toString())).get();
		if(orderToEdit.getUser().getId() != Long.parseLong(map.get("userId").toString())) {
			User newUser = ur.findById(Long.parseLong(map.get("userId").toString())).get();
			orderToEdit.setUser(newUser);
		}
		orderToEdit.setValue(Double.parseDouble(map.get("value").toString()));
		String[] fullDate = map.get("deadLine").toString().split("-");
		int year = Integer.parseInt(fullDate[0]);
		int month = Integer.parseInt(fullDate[1]);
		int day = Integer.parseInt(fullDate[2]);
		orderToEdit.setDeadLine(new Date(year-1900, month-1, day));
		orderToEdit.setDone(Boolean.parseBoolean(map.get("done").toString()));
		System.out.println(orderToEdit);
		ArrayList<String> productInfos = (ArrayList)map.get("products");
		String[] productArrayInfo; // values are: productId, piece, delete?
		for (String productInfo : productInfos) {
			productArrayInfo = productInfo.split(";");
			
			if(Boolean.parseBoolean(productArrayInfo[2])) {
				try {
					ptos.deleteByOrderIdAndProductId(Long.valueOf(map.get("orderId").toString()) ,Long.parseLong(productArrayInfo[0]));
				} catch (Exception e) {
					log.error(e.getMessage());
				}
				
			}
		}
		or.save(orderToEdit);
	}
	


}
