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
	private ProductService ps;
	private OrderService os;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	public ProductsToOrdersServiceImpl(ProductsToOrdersRepository ptor, ProductService ps, OrderService os) {
		this.ptor = ptor;
		this.ps = ps;
		this.os = os;
	}

	@Override
	@Transactional
	public void deleteByOrderIdAndProductId(long orderId, long productId) throws Exception{
		Order order = os.getOrderById(orderId);
		if(order == null) throw new Exception("Order not found");
		Product product = ps.getProductById(productId);
		if(product == null) throw new Exception("Product not found");
		ProductsToOrders ptoToRemove = ptor.findByOrderAndProduct(order, product);
		removeProductsToOrdersFromOrder(ptoToRemove, order);
		removeProductsToOrdersFromProducts(ptoToRemove, product);
		ptor.delete(ptoToRemove);
	}

	@Override
	public ProductsToOrders findByOrderAndProduct(long orderId, long productId) throws Exception {
		Order order = os.getOrderById(orderId);
		if(order == null) throw new Exception("Order not found");
		Product product = ps.getProductById(productId);
		if(product == null) throw new Exception("Product not found");
		return ptor.findByOrderAndProduct(order, product);
	}

	private void removeProductsToOrdersFromOrder(ProductsToOrders ptoToRemove, Order order) {
		Set<ProductsToOrders> ptoSet = order.getProductsToOrder();
		if(ptoSet.contains(ptoToRemove)) {
			ptoSet.remove(ptoToRemove);
			order.setProductsToOrder(ptoSet);
		} else log.error("ProductsToOrders with " + ptoToRemove.getId() + " id is not belongs to order with " + order.getId() + " id");
	}
	
	@Override
	public void removeProductsToOrdersFromProducts(ProductsToOrders ptoToRemove, Product product) {
		Set<ProductsToOrders> ptoSet = product.getProductstoOrder();
		if(ptoSet.contains(ptoToRemove)) {
			ptoSet.remove(ptoToRemove);
			product.setProductstoOrder(ptoSet);
			product.setOnStock(product.getOnStock() + ptoToRemove.getQuantity());
			ps.saveProduct(product);
		} else log.error("ProductsToOrders with " + ptoToRemove.getId() + " id is not belongs to order with " + product.getId() + " id");
	}

	@Override
	public void deletePtO(ProductsToOrders productsToOrders) {
		ptor.delete(productsToOrders);		
	}
	
	@Override
	public void updatePtO(ProductsToOrders productsToOrders) {
		ptor.save(productsToOrders);
	}
}
