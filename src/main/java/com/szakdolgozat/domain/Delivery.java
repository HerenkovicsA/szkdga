package com.szakdolgozat.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Table(name = "deliveries")
public class Delivery implements Serializable{
	
	private static final long serialVersionUID = 6652648022347822854L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long Id;
	@ManyToOne
	@JoinColumn(name = "employee_id")
	private User user;
	private Date deliveryDate;
	private boolean done;
	@OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Order> ordersOfDelivery = new HashSet<Order>();
	
	public Delivery() {
	
	}

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public User getEmployee() {
		return user;
	}

	public void setEmployee(User employee) {
		this.user = employee;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public Set<Order> getOrdersOfDelivery() {
		return ordersOfDelivery;
	}

	public void setOrdersOfDelivery(Set<Order> ordersOfDelivery) {
		this.ordersOfDelivery = ordersOfDelivery;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (Id ^ (Id >>> 32));
		result = prime * result + ((deliveryDate == null) ? 0 : deliveryDate.hashCode());
		result = prime * result + (done ? 1231 : 1237);
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
		Delivery other = (Delivery) obj;
		if (Id != other.Id)
			return false;
		if (deliveryDate == null) {
			if (other.deliveryDate != null)
				return false;
		} else if (!deliveryDate.equals(other.deliveryDate))
			return false;
		if (done != other.done)
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
		return "Delivery [Id=" + Id + ", employeeId=" + user.getId() + ", deliveryDate=" + deliveryDate + ", done=" + done
				+ ", ordersOfDelivery=" + ordersOfDelivery + "]";
	}
	
	
	
	
}
