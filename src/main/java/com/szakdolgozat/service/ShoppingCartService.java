package com.szakdolgozat.service;

import com.szakdolgozat.components.ShoppingCart;

public interface ShoppingCartService {

	int addToCart(long productId, String email);
	
	void emptyCart(String email);
	
	ShoppingCart getCart(String email);

	String removeFromCart(long productId, String email);

	String changeAmount(long productId, int quantity, String email);

	String makeAnOrder(String name);
}
