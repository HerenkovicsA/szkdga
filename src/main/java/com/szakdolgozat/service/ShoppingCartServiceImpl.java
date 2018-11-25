package com.szakdolgozat.service;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.szakdolgozat.components.ShoppingCart;
import com.szakdolgozat.domain.Product;
import com.szakdolgozat.domain.User;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

	private ProductService ps;
	private OrderService os;
	private HashMap<String, ShoppingCart> userCartMap;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	public ShoppingCartServiceImpl(ProductService ps, OrderService os) {
		this.ps = ps;
		this.os = os;
		this.userCartMap = new HashMap<String, ShoppingCart>();
	}
	
	@Override
	public int addToCart(long productId, String email) {
		Product product = ps.getProductForCart(productId);
		if(product != null) {
			ShoppingCart cart = getCart(email);
			cart.addToCart(product);
			return 1;
		}
		return 0;
	}

	@Override
	public void emptyCart(String email) {
		ShoppingCart cart = getCart(email);
		cart.emptyCart();
	}

	@Override
	public ShoppingCart getCart(String email) {
		return getOrCreateCart(email);
	}

	private boolean checkIfUserHasCar(String email) {
		if(userCartMap.containsKey(email)) return true;
		return false;
	}
	
	private void addCartForUser(String email) {
		userCartMap.put(email, new ShoppingCart());
	}
	
	private ShoppingCart getOrCreateCart(String email) {
		if(!checkIfUserHasCar(email)) addCartForUser(email);
		return userCartMap.get(email);
	}

	@Override
	public String removeFromCart(long productId, String email) {
		ShoppingCart cart = userCartMap.get(email);
		Product product = ps.getProductById(productId);
		if(product != null) {
			Object result = cart.removeProduct(product);
			if(result != null) {
				return "ok";
			} else {
				log.error("Couldn't remove product (" + productId + ")");
			}
		}
		log.error("Couldn't find product (" + productId + ")");
		return "error";
	}

	@Override
	public String changeAmount(long productId, int quantity, String email) {
		ShoppingCart cart = getCart(email);
		Product product = ps.getProductById(productId);
		if(product != null) {
			cart.addToCart(product, quantity);
			return "ok";
		}
		log.error("Couldn't change product's amount");
		return "error";
	}

	@Override
	public String makeAnOrder(String email) {
		ShoppingCart cart = getCart(email);
		if(!cart.getItems().isEmpty()) {
			String result = os.makeAnOrder(email, cart.getItems());
			if(result.equals("ok")) {
				cart.emptyCart();
			}
			return result;
		}
		log.error("The cart is empty");
		return "Nincs semmi a kocsiban";
		
	}
}
