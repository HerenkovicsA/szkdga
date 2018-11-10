package com.szakdolgozat.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.szakdolgozat.components.ProductAndQuantityResponse;
import com.szakdolgozat.domain.Order;
import com.szakdolgozat.domain.Product;
import com.szakdolgozat.domain.ProductsToOrders;

public interface OrderService {

	public List<Order> findAll() throws Exception;

	public List<Order> findOrdersOfDelivery(long deliveryId) throws Exception;

	public Set<ProductsToOrders> findProductsOfOrder(long orderId) throws Exception;

	public String deleteOrder(long id);

	public List<ProductAndQuantityResponse> getProductsOfOrderList(long orderId) throws Exception;

	public void editOrder(Map<Object, Object> map);
}
