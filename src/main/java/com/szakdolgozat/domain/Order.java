package com.szakdolgozat.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name ="orders")
public class Order implements Serializable{

	private static final long serialVersionUID = 6652648022347822854L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private long Id;
	@Column(nullable = false)
	private LocalDate deadLine;
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	@JsonBackReference
	private User user;
	@Column(nullable = false)
	private boolean done;
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JsonManagedReference
	private Set<ProductsToOrders> productsToOrder = new HashSet<ProductsToOrders>();
	@ManyToOne
	@JoinColumn(name = "deliver_id")
	@JsonBackReference
	private Delivery delivery;
	@Column(columnDefinition="Decimal(15,2)")
	private double value;
	@Transient
	private double volume;
	
	public Order() {
		
	}

	public long getId() {
		return Id;
	}
	
	public Order(LocalDate deadLine, User user, boolean done, Set<ProductsToOrders> producttoorders, Delivery delivery, double value) {
		this.deadLine = deadLine;
		this.user = user;
		this.done = done;
		this.productsToOrder = producttoorders;
		this.delivery = delivery;
		this.value = value;
	}
	
	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public Delivery getDelivery() {
		return delivery;
	}

	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}

	public LocalDate getDeadLine() {
		return deadLine;
	}

	public void setDeadLine(LocalDate deadLine) {
		this.deadLine = deadLine;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public Set<ProductsToOrders> getProductsToOrder() {
		return productsToOrder;
	}

	public void setProductsToOrder(Set<ProductsToOrders> productsToOrder) {
		this.productsToOrder = productsToOrder;
	}

	public double getVolume() {
		return volume;
	}
	
	public void countVolume() {
		this.volume = 0;
		if(!productsToOrder.isEmpty()) {
			for (ProductsToOrders pto : this.productsToOrder) {
				this.volume += pto.getProduct().getVolume() * pto.getQuantity(); 
			}
		}
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public void setId(long id) {
		Id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (Id ^ (Id >>> 32));
		result = prime * result + ((deadLine == null) ? 0 : deadLine.hashCode());
		result = prime * result + ((delivery == null) ? 0 : delivery.hashCode());
		result = prime * result + (done ? 1231 : 1237);
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		long temp;
		temp = Double.doubleToLongBits(value);
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
		Order other = (Order) obj;
		if (Id != other.Id)
			return false;
		if (deadLine == null) {
			if (other.deadLine != null)
				return false;
		} else if (!deadLine.equals(other.deadLine))
			return false;
		if (delivery == null) {
			if (other.delivery != null)
				return false;
		} else if (!delivery.equals(other.delivery))
			return false;
		if (done != other.done)
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Order [Id=" + Id + ", deadLine=" + deadLine + ", userId=" + user.getId() + ", done=" + done + ", producttoorders="
				+ productsToOrder + ", deliveryId=" + delivery.getId() + "value=" + value + "]";
	}

	
	
}