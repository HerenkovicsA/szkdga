package com.szakdolgozat.service;

import java.util.List;
import java.util.Set;

import com.szakdolgozat.domain.Order;
import com.szakdolgozat.domain.ProductsToOrders;

public interface OrderService {

	public List<Order> findAll() throws Exception;

	public List<Order> findOrdersOfDelivery(long deliveryId) throws Exception;

	public Set<ProductsToOrders> findProductsOfOrder(long orderId) throws Exception;
}
