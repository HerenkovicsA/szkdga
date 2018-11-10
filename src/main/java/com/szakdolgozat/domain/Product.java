package com.szakdolgozat.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
@Table(name = "products")
public class Product implements Serializable{
	
	private static final long serialVersionUID = 6652648022347822854L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private long Id;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private double price;
	@Column(name = "onstock", nullable = false)
	private int onStock;
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.EAGER)
	@JsonManagedReference
	private Set<ProductsToOrders> productsToOrder = new HashSet<ProductsToOrders>();
	@Column(length = 2000, nullable = false)
	private String pathToPicture;

	public Product() {
	}

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getOnStock() {
		return onStock;
	}

	public void setOnStock(int onStock) {
		this.onStock = onStock;
	}	
	
	public String getPathToPicture() {
		return pathToPicture;
	}

	public void setPathToPicture(String pathToPicture) {
		this.pathToPicture = pathToPicture;
	}

	public Set<ProductsToOrders> getProductstoOrder() {
		return productsToOrder;
	}

	public void setProductstoOrder(Set<ProductsToOrders> productsToOrder) {
		this.productsToOrder = productsToOrder;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (Id ^ (Id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + onStock;
		result = prime * result + ((pathToPicture == null) ? 0 : pathToPicture.hashCode());
		long temp;
		temp = Double.doubleToLongBits(price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Product other = (Product) obj;
		if (Id != other.Id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (onStock != other.onStock)
			return false;
		if (pathToPicture == null) {
			if (other.pathToPicture != null)
				return false;
		} else if (!pathToPicture.equals(other.pathToPicture))
			return false;
		if (Double.doubleToLongBits(price) != Double.doubleToLongBits(other.price))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Product [Id=" + Id + ", name=" + name + ", price=" + price + ", onStock=" + onStock
				+ ", producttoorders=" + productsToOrder + "]";
	}

		
	
}
