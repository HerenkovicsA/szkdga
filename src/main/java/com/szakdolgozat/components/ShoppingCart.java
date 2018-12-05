package com.szakdolgozat.components;

import java.util.HashMap;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.szakdolgozat.domain.Product;

@Component
public class ShoppingCart {

	private HashMap<Product, Integer> items;
	private double summOfVolume;

	public ShoppingCart() {
		this.items = new HashMap<Product, Integer>();
	}

	public HashMap<Product, Integer> getItems() {
		return items;
	}

	public void setItems(HashMap<Product, Integer> cart) {
		this.items = cart;
	}
	
	public double getSummOfVolume() {
		return summOfVolume;
	}

	public void setSummOfVolume(double summOfVolume) {
		this.summOfVolume = summOfVolume;
	}
	
	public double sumVolume() {
		summOfVolume = 0;
		for(Product key : items.keySet()) {
			summOfVolume += key.getVolume() * items.get(key);
		}
		return summOfVolume;
	}

	public double getVolumeOfProduct(Product product) {
		double result = 0;
		for(Product prod : items.keySet()) {
			if(prod.equals(product)) {
				result = prod.getVolume() * items.get(prod);
				break;
			}
		}
		return result;
	}
	
	public void addToCart(Product product) {
		if(items.containsKey(product)) {
			addToCart(product, items.get(product) + 1);
		} else {
			addToCart(product, 1);
		}
	}
	
	public void addToCart(Product product, int quantity) {
		this.items.put(product, quantity);
	}
	
	public void emptyCart() {
		this.items.clear();
	}
	
	public Object removeProduct(Product product) {
		return items.remove(product);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShoppingCart other = (ShoppingCart) obj;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ShoppingCart [cart=" + items + "]";
	}
}
