package com.szakdolgozat.service;

import com.szakdolgozat.domain.Product;
import com.szakdolgozat.domain.ProductsToOrders;

public interface ProductsToOrdersService {
	
	void deleteByOrderIdAndProductId(long orderId, long productId) throws Exception;

	void removeProductsToOrdersFromProducts(ProductsToOrders ptoToRemove, Product product);

	void deletePtO(ProductsToOrders productsToOrders);

	void updatePtO(ProductsToOrders productsToOrders);
}
