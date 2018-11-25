package com.szakdolgozat.service;

import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.szakdolgozat.domain.Order;
import com.szakdolgozat.domain.Product;
import com.szakdolgozat.domain.ProductsToOrders;
import com.szakdolgozat.repository.OrderRepository;
import com.szakdolgozat.repository.ProductRepository;
import com.szakdolgozat.repository.ProductsToOrdersRepository;

@Service
public class ProductsToOrdersServiceImpl implements ProductsToOrdersService {
	
	private ProductsToOrdersRepository ptor;
	private ProductRepository pr;
	private OrderRepository or;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	public ProductsToOrdersServiceImpl(ProductsToOrdersRepository ptor, ProductRepository pr, OrderRepository or) {
		this.ptor = ptor;
		this.pr = pr;
		this.or = or;
	}

	@Override
	@Transactional
	public void deleteByOrderIdAndProductId(long orderId, long productId) throws Exception{
		Order order = or.findById(orderId).get();
		Product product = pr.findById(productId).get();
		ProductsToOrders ptoToRemove = ptor.findByOrderAndProduct(order, product);
		removeProductsToOrdersFromOrder(ptoToRemove, order);
		removeProductsToOrdersFromProducts(ptoToRemove, product);
		ptor.delete(ptoToRemove);
		System.out.println("done");
	}

	@Override
	public ProductsToOrders findByOrderAndProduct(long orderId, long productId) throws Exception {
		Order order = or.findById(orderId).get();
		Product product = pr.findById(productId).get();
		return ptor.findByOrderAndProduct(order, product);
	}

	private void removeProductsToOrdersFromOrder(ProductsToOrders ptoToRemove, Order order) {
		System.out.println(order.getProductsToOrder());
		Set<ProductsToOrders> ptoSet = order.getProductsToOrder();
		if(ptoSet.contains(ptoToRemove)) {
			ptoSet.remove(ptoToRemove);
			order.setProductsToOrder(ptoSet);
		} else log.error("ProductsToOrders with " + ptoToRemove.getId() + " id is not belongs to order with " + order.getId() + " id");
		System.out.println(order.getProductsToOrder());
	}
	
	@Override
	public void removeProductsToOrdersFromProducts(ProductsToOrders ptoToRemove, Product product) {
		System.out.println(product.getProductstoOrder());
		Set<ProductsToOrders> ptoSet = product.getProductstoOrder();
		if(ptoSet.contains(ptoToRemove)) {
			ptoSet.remove(ptoToRemove);
			product.setProductstoOrder(ptoSet);
		} else log.error("ProductsToOrders with " + ptoToRemove.getId() + " id is not belongs to order with " + product.getId() + " id");
		System.out.println(product.getProductstoOrder());
	}

	@Override
	public void deletePtO(ProductsToOrders productsToOrders) {
		ptor.delete(productsToOrders);		
	}
}
