package com.szakdolgozat.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szakdolgozat.components.ProductAndQuantityResponse;
import com.szakdolgozat.domain.Delivery;
import com.szakdolgozat.domain.Order;
import com.szakdolgozat.domain.Product;
import com.szakdolgozat.domain.ProductsToOrders;
import com.szakdolgozat.domain.Role;
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
	public OrderServiceImpl(OrderRepository or, DeliveryRepository dr, UserRepository ur, ProductsToOrdersService ptos, ProductsToOrdersRepository ptor) {
		this.or = or;
		this.dr = dr;
		this.ur	= ur;
		this.ptos = ptos;
		this.ptor = ptor;
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
	@Transactional
	public String deleteOrder(long id) {
		Optional<Order> orderToRemove = or.findById(id);
		if(orderToRemove.isPresent()) {
			removeOrderFromUser(orderToRemove.get());
			removeOrderFromDelivery(orderToRemove.get());
			removeOrderFromProductsToOrders(orderToRemove.get());
			or.deleteById(id);
			return "deleted";
		}else {
			return "not exists";
		}
	}
	
	private void removeOrderFromProductsToOrders(Order order) {
		Set<ProductsToOrders> ptoIterbale =  order.getProductsToOrder();
		System.out.println(order.getProductsToOrder());
		for (ProductsToOrders productsToOrders : ptoIterbale) {
			ptos.removeProductsToOrdersFromProducts(productsToOrders, productsToOrders.getProduct());
			ptor.delete(productsToOrders);
		}
		System.out.println(order.getProductsToOrder());
	}

	private void removeOrderFromUser(Order orderToRemove) {
		User user = orderToRemove.getUser();
		Set<Order> orderSet = user.getOrdersOfUser();
		Set<Order> newOrderSet = new HashSet<Order>();
		boolean hasChanged = false;
		System.out.println(orderSet.size());
		for (Order order : orderSet) {
			if(!order.equals(orderToRemove)) {
				newOrderSet.add(order);
			}else {
				hasChanged = true;
			}
		}
		if(!hasChanged) {
			log.error("Order with " + orderToRemove.getId() + " does not belong to user: " + user.getName());
		}else {
			user.clearOrdersOfUser();
			for (Order order : newOrderSet) {
				user.addToOrdersOfUser(order);
			}
			System.out.println(user.getOrdersOfUser().size());
		}
	}
	
	private void removeOrderFromDelivery(Order orderToRemove) {
		Delivery delivery = orderToRemove.getDelivery();
		if(delivery != null) {
			Set<Order> orderSet = delivery.getOrdersOfDelivery();
			System.out.println(delivery.getOrdersOfDelivery().size());
			if(orderSet.contains(orderToRemove)) {
				orderSet.remove(orderToRemove);
				delivery.setOrdersOfDelivery(orderSet);
			} else log.error("Order with " + orderToRemove.getId() + " does not belong to delivery with: " + delivery.getId() + " id");
			System.out.println(delivery.getOrdersOfDelivery().size());
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
				
			}else {
				Set<ProductsToOrders> productsToOrders = orderToEdit.getProductsToOrder();
				for (ProductsToOrders productsToOrder : productsToOrders) {
					if(productsToOrder.getProduct().getId() == Long.parseLong(productArrayInfo[0])) {
						productsToOrder.setQuantity(Integer.parseInt(productArrayInfo[1]));
					}
				}
			}
		}
		or.save(orderToEdit);
	}
	


}
