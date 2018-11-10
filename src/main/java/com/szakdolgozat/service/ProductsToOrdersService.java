package com.szakdolgozat.service;

import com.szakdolgozat.domain.ProductsToOrders;

public interface ProductsToOrdersService {
	
	void deleteByOrderIdAndProductId(long orderId, long productId) throws Exception;
	
	ProductsToOrders findByOrderAndProduct(long orderId, long productId) throws Exception;
}
