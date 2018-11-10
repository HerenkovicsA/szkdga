package com.szakdolgozat.service;

import javax.transaction.Transactional;

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
	
	@Autowired
	public ProductsToOrdersServiceImpl(ProductsToOrdersRepository ptor, ProductRepository pr, OrderRepository or) {
		this.ptor = ptor;
		this.pr = pr;
		this.or = or;
	}

	@Override
	//@Transactional
	public void deleteByOrderIdAndProductId(long orderId, long productId) throws Exception{
		Order order = or.findById(orderId).get();
		Product product = pr.findById(productId).get();
		ptor.deleteByOrderAndProduct(order, product);
		System.out.println("done");
	}

	@Override
	public ProductsToOrders findByOrderAndProduct(long orderId, long productId) throws Exception {
		Order order = or.findById(orderId).get();
		Product product = pr.findById(productId).get();
		return ptor.findByOrderAndProduct(order, product);
	}

}
