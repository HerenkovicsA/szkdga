package com.szakdolgozat.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.szakdolgozat.domain.Order;
import com.szakdolgozat.domain.Product;
import com.szakdolgozat.domain.ProductsToOrders;
import com.szakdolgozat.repository.DeliveryRepository;
import com.szakdolgozat.repository.OrderRepository;

@Service
public class OrderServiceImpl implements OrderService {

	private OrderRepository or;
	private DeliveryRepository dr;
	
	@Autowired
	public OrderServiceImpl(OrderRepository or, DeliveryRepository dr) {
		this.or = or;
		this.dr = dr;
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

}
