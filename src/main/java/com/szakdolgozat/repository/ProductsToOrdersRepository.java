package com.szakdolgozat.repository;

import org.springframework.data.repository.CrudRepository;

import com.szakdolgozat.domain.Order;
import com.szakdolgozat.domain.Product;
import com.szakdolgozat.domain.ProductsToOrders;

public interface ProductsToOrdersRepository extends CrudRepository<ProductsToOrders, Long> {

	void deleteByOrderAndProduct(Order order, Product product);
	
	ProductsToOrders findByOrderAndProduct(Order order, Product product);

	void deleteAllByOrder(Order order);
}
