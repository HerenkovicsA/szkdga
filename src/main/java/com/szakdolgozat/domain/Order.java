package com.szakdolgozat.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name ="orders")
public class Order {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 6652648022347822854L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long Id;
	private Date deadLine;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	private boolean done;
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ProductsToOrders> productstoorder = new HashSet<ProductsToOrders>();
	@ManyToOne
	@JoinColumn(name = "deliver_id")
	private Delivery delivery;
	
	public Order() {
		
	}

	public long getId() {
		return Id;
	}
	
	public Order(Date deadLine, User user, boolean done, Set<ProductsToOrders> productstoorder, Delivery delivery) {
		super();
		this.deadLine = deadLine;
		this.user = user;
		this.done = done;
		this.productstoorder = productstoorder;
		this.delivery = delivery;
	}

	public Delivery getDelivery() {
		return delivery;
	}

	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}

	public Date getDeadLine() {
		return deadLine;
	}

	public void setDeadLine(Date deadLine) {
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

	public Set<ProductsToOrders> getProductstoorder() {
		return productstoorder;
	}

	public void setProductstoorder(Set<ProductsToOrders> productstoorder) {
		this.productstoorder = productstoorder;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (Id ^ (Id >>> 32));
		result = prime * result + ((deadLine == null) ? 0 : deadLine.hashCode());
		result = prime * result + ((delivery == null) ? 0 : delivery.hashCode());
		result = prime * result + (done ? 1231 : 1237);
		result = prime * result + ((productstoorder == null) ? 0 : productstoorder.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		if (productstoorder == null) {
			if (other.productstoorder != null)
				return false;
		} else if (!productstoorder.equals(other.productstoorder))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Order [Id=" + Id + ", deadLine=" + deadLine + ", user=" + user + ", done=" + done + ", productstoorder="
				+ productstoorder + ", delivery=" + delivery + "]";
	}

	
	
}