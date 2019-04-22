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

	public List<Order> findAll() throws Exception;

	public List<Order> findOrdersOfDelivery(long deliveryId) throws Exception;

	public Set<ProductsToOrders> findProductsOfOrder(long orderId) throws Exception;

	public String deleteOrder(long id);

	public List<ProductAndQuantityResponse> getProductsOfOrderList(long orderId) throws Exception;

	public void editOrder(Map<Object, Object> map);

	public List<Order> findOrdersForDelivery();
	
	/** 
	 * @param cargoSize
	 * @param cargoLimit
	 * @param delivery : the {@link Delivery} which the orders will belong to
	 * @return
	 * Not sorted list of Orders for the delivery in param
	 */
	public List<Order> findOrdersForDelivery2(Double cargoSize, double cargoLimit, Delivery delivery);

	public Order getOrderById(long orderId);

	public int setOrderDone(long orderId, boolean b);

	public String makeAnOrder(String email, HashMap<Product, Integer> items);

	public List<Pair<Product, Integer>> getProductsOfOrder(long orderId);

	public List<Pair<Product, Integer>> getMissingProductList(HashMap<Product, Integer> subCart);

	/**
	 * @return if there is enough order to make a delivery or not 
	 */
	public boolean hasEnoughForOneDelivery(double cargoSize);

	/**
	 * @return true if there is any order that's deadLine is in 48 hours
	 */
	public boolean hasUrgentOrder();

	public String deleteProductFromOrder(long productId, long orderId);

	public boolean deleteOrderFromDelivery(long parseLong, Delivery deliveryToEdit);
}
