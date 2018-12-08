package com.szakdolgozat.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.szakdolgozat.components.DeliveryProcessor;
import com.szakdolgozat.components.ShoppingCart;
import com.szakdolgozat.domain.Order;
import com.szakdolgozat.domain.Product;

import javafx.util.Pair;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

	private final Double CARGO_SIZE = 14900000D;
	
	private ProductService ps;
	private OrderService os;
	private DeliveryService ds;
	private HashMap<String, ShoppingCart> userCartMap;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	public ShoppingCartServiceImpl(ProductService ps, OrderService os, DeliveryService ds) {
		this.ps = ps;
		this.os = os;
		this.ds = ds;
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
	
	@Override
	public String asyncMakeOrders(String email) {
		ShoppingCart cart = getCart(email);
		if(!cart.getItems().isEmpty()) {
			String result = "";
			List<Pair<Product, Integer>> missingList = os.getMissingProductList(cart.getItems());
			if(missingList.isEmpty()) {
				double sumVolume = cart.sumVolume();
				if(sumVolume > CARGO_SIZE) {
					List<HashMap<Product, Integer>> carts = takeCartIntoPieces(cart);
					for (HashMap<Product, Integer> subCart : carts) {
						os.makeAnOrder(email,subCart);
					}
					cart.emptyCart();
					return "A rendelés nem fért egy kocsiban, ezért " + carts.size() + " különböző fuvarral lesz kiszállítva.";
				} else {
					result = os.makeAnOrder(email, cart.getItems());
					if(result.equals("ok")) {
						cart.emptyCart();
					}
				}
				return "ok";
			} else {
				result += "MISSING";
				for (Pair<Product, Integer> pair : missingList) {
					result += ";" + pair.getKey().getName() + ":" + pair.getValue() + ":" + pair.getKey().getId() ;
				}
				return result;
			}
			
		}
		log.error("The cart is empty");
		return "Nincs semmi a kocsiban";
		
	}
	
	/**
	 * Takes the {@link ShoppingCart}'s items into more orders.
	 * @param cart : the ShoppingCart to be sorted into more orders
	 * @return List of orders to be created
	 */
	private List<HashMap<Product, Integer>> takeCartIntoPieces(ShoppingCart cart) {
		List<HashMap<Product, Integer>> orderList = new ArrayList<HashMap<Product, Integer>>();
		Map<Product, Integer> itemsInActualCart = cart.getItems();
		Set<Product> productsInCart = itemsInActualCart.keySet();
		List<Double> spaceLeft = new ArrayList<Double>();
		//at least 2 orders
		orderList.add(new HashMap<Product, Integer>());
		orderList.add(new HashMap<Product, Integer>());
		spaceLeft.add(CARGO_SIZE);
		spaceLeft.add(CARGO_SIZE);
		double volumeOfProductInCart = 0;
		for(Product product : productsInCart) {
			volumeOfProductInCart = cart.getVolumeOfProduct(product);
			if(volumeOfProductInCart > CARGO_SIZE) {
				//take product into more "orders"
				takeProductIntoPieces(orderList,spaceLeft,volumeOfProductInCart,product,itemsInActualCart.get(product));
			} else {
				addToOrder(orderList,spaceLeft,volumeOfProductInCart,product,itemsInActualCart.get(product));
			}
		}
		return orderList;
	}
	
	/**
	 * Takes the <i>amountOfProd</i> {@link Product} form the cart into more orders<br/>
	 * as it's size would be to large for one order.
	 * Adds the <<i>product</i> to the <i>orderList</i> and calls {@link #addToOrder} 
	 * @param orderList : the list of orders where the product will be added
	 * @param spaceLeft : space left for new products in orders of orderList
	 * @param volumeOfProductInCart : the volume taken by the products that wanted to be placed
	 * @param product : {@link Product} to be placed in an order
	 * @param amountOfProd
	 */
	private void takeProductIntoPieces(List<HashMap<Product, Integer>> orderList, List<Double> spaceLeft, double volumeOfProductInCart, 
			Product product, Integer amountOfProd) {
		double volumeOfOneProduct = product.getVolume();
		int maxNumberOfProductsInOneOrder = new Double(CARGO_SIZE / volumeOfOneProduct).intValue();
		int productLeftForSorting = amountOfProd;
		int amountOfProductToOrder = 0;
		int numberOfOrdersToMake = Math.floorDiv(amountOfProd, maxNumberOfProductsInOneOrder);
		if(amountOfProd % maxNumberOfProductsInOneOrder != 0) numberOfOrdersToMake++;
		for(int i = 0; i < numberOfOrdersToMake; i++) {
			if(productLeftForSorting - maxNumberOfProductsInOneOrder >= 0) {
				productLeftForSorting -= maxNumberOfProductsInOneOrder;
				amountOfProductToOrder = maxNumberOfProductsInOneOrder;
			} else {
				amountOfProductToOrder = productLeftForSorting;
			}
			addToOrder(orderList, spaceLeft, volumeOfOneProduct * amountOfProductToOrder, product, amountOfProductToOrder);
		}
	}

	/**
	 * Adds the product to an "Order" in orderList based on the volume(spaceLeft) left in that order<br/>
	 * If there is not enough space in orders for the whole amount of product than make a new order
	 * @param orderList : the list of orders where the product will be added
	 * @param spaceLeft : space left for new products in orders of orderList
	 * @param volumeOfProductInCart : the volume taken by the products that wanted to be placed
	 * @param product : {@link Product} to be placed in an order
	 * @param amountOfProd
	 */
	private void addToOrder(List<HashMap<Product, Integer>> orderList, List<Double> spaceLeft, double volumeOfProductInCart, Product product, int amountOfProd) {
		HashMap<Product, Integer> order;
		boolean placed = false;
		for (int i = 0; i < spaceLeft.size(); i++) {
			if(spaceLeft.get(i) - volumeOfProductInCart >= 0) {//if the order fit in an order, put in it
				spaceLeft.set(i, spaceLeft.get(i) - volumeOfProductInCart);
				orderList.get(i).put(product, amountOfProd);
				placed = true;
				break;
			}
		}
		if(!placed) {//if the product does'n fit in any order than make a new one
			order = new HashMap<Product, Integer>();
			order.put(product, amountOfProd);
			orderList.add(order);
			spaceLeft.add(CARGO_SIZE - volumeOfProductInCart);
		}
	}
}
