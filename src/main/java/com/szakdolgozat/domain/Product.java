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
import javax.persistence.Transient;

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
	@Column//(nullable = false)
	private Double width;
	@Column//(nullable = false)
	private Double height;
	@Column//(nullable = false)
	private Double length;
//TODO x,y,z everywhere!!!!!!!!!!
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

	public Double getWidth() {
		return width;
	}

	public void setWidth(Double width) {
		this.width = width;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public Double getLength() {
		return length;
	}

	public void setLength(Double length) {
		this.length = length;
	}
	
	@Transient
	public double getVolume() {
		return height * length * width;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (Id ^ (Id >>> 32));
		result = prime * result + ((height == null) ? 0 : height.hashCode());
		result = prime * result + ((length == null) ? 0 : length.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((pathToPicture == null) ? 0 : pathToPicture.hashCode());
		result = prime * result + ((width == null) ? 0 : width.hashCode());
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
		if (height == null) {
			if (other.height != null)
				return false;
		} else if (!height.equals(other.height))
			return false;
		if (length == null) {
			if (other.length != null)
				return false;
		} else if (!length.equals(other.length))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (pathToPicture == null) {
			if (other.pathToPicture != null)
				return false;
		} else if (!pathToPicture.equals(other.pathToPicture))
			return false;
		if (width == null) {
			if (other.width != null)
				return false;
		} else if (!width.equals(other.width))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Product [Id=" + Id + ", name=" + name + ", price=" + price + ", onStock=" + onStock
				+ ", producttoorders=" + productsToOrder + "]";
	}

		
	
}