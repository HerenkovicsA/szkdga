package com.szakdolgozat.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.szakdolgozat.components.ProductAndQuantityResponse;
import com.szakdolgozat.domain.Delivery;
import com.szakdolgozat.domain.Order;
import com.szakdolgozat.domain.Product;
import com.szakdolgozat.domain.ProductsToOrders;

import org.springframework.data.util.Pair;

public interface OrderService {

	List<Order> findAll() throws Exception;

	List<Order> findOrdersOfDelivery(long deliveryId) throws Exception;

	Set<ProductsToOrders> findProductsOfOrder(long orderId) throws Exception;

	String deleteOrder(long id);

	List<ProductAndQuantityResponse> getProductsOfOrderList(long orderId) throws Exception;

	void editOrder(Map<Object, Object> map);
	
	/** 
	 * @param cargoSize
	 * @param cargoLimit
	 * @param delivery : the {@link Delivery} which the orders will belong to
	 * @return
	 * Not sorted list of Orders for the delivery in param
	 */
	List<Order> findOrdersForDelivery2(Double cargoSize, double cargoLimit, Delivery delivery);

	Order getOrderById(long orderId);

	int setOrderDone(long orderId, boolean b);

	String makeAnOrder(String email, HashMap<Product, Integer> items);

	List<Pair<Product, Integer>> getProductsOfOrder(long orderId);

	List<Pair<Product, Integer>> getMissingProductList(HashMap<Product, Integer> subCart);

	/**
	 * @return if there is enough order to make a delivery or not 
	 */
	boolean hasEnoughForOneDelivery(double cargoSize);

	/**
	 * @return true if there is any order that's deadLine is in 48 hours
	 */
	boolean hasUrgentOrder();

	String deleteProductFromOrder(long productId, long orderId);

	boolean deleteOrderFromDelivery(long parseLong, Delivery deliveryToEdit);
	
	Order getFakeOrder();
	
	Order getOrderFromSetById(Set<Order> orderSet, long id) throws Exception;
}
