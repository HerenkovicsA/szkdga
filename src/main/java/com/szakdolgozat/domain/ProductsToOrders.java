package com.szakdolgozat.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "productstoorders")
public class ProductsToOrders implements Serializable{
	
	private static final long serialVersionUID = 6652648022347822854L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private long Id;
	@ManyToOne
	@JoinColumn(name = "productId", nullable = false)
	@JsonBackReference
	private Product product;
	@ManyToOne
	@JoinColumn(name = "orderId", nullable = false)
	@JsonBackReference
	private Order order;
	@Column(nullable = false)
	private int quantity;
	@Column(name = "prod_act_value")
	private double prodActValue;

	public ProductsToOrders() {

	}

	public ProductsToOrders(Product product, Order order, int quantity) {
		this.product = product;
		this.order = order;
		this.quantity = quantity;
		this.prodActValue = product.getPrice() * quantity;
	}

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getProdActValue() {
		return prodActValue;
	}

	public void setProdActValue(double prodActValue) {
		this.prodActValue = prodActValue;
	}
	
	public void updateProdActValue() {
		this.prodActValue = this.quantity * this.product.getPrice();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (Id ^ (Id >>> 32));
		long temp;
		temp = Double.doubleToLongBits(prodActValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + quantity;
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
		ProductsToOrders other = (ProductsToOrders) obj;
		if (Id != other.Id)
			return false;
		if (Double.doubleToLongBits(prodActValue) != Double.doubleToLongBits(other.prodActValue))
			return false;
		if (quantity != other.quantity)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProductsToOrders [Id=" + Id + ", productId=" + product.getId() + ", orderId=" + order.getId() + ", quantity=" + quantity
				+ " prodActValue=" + prodActValue + "]";
	}
	
	
	
}