package com.szakdolgozat.service;

import com.szakdolgozat.components.ShoppingCart;

public interface ShoppingCartService {

	int addToCart(long productId, String email);
	
	void emptyCart(String email);
	
	ShoppingCart getCart(String email);

	String removeFromCart(long productId, String email);

	String changeAmount(long productId, int quantity, String email);

	String makeAnOrder(String name);

	/**
	 * Creates orders for the user from the products in the cart.
	 * @return <b>string</b>
	 *  <ul>
	 *  	<li>If there isn't enough product on stock, than a <b>;</b> sparated list of product name : onstock string, 
	 *  starting with MISSING</li>
	 *  	<li>If there is nothing in the cart than a 'Nincs semmi a kocsiban' string</li>  
	 *  	<li>On <b>succes</b>, creates orders and returns 'ok'</li>
	 *  </ul>
	 */
	String makeOrders(String email);
}
