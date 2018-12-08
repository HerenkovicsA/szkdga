package com.szakdolgozat.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Table(name = "deliveries")
public class Delivery implements Serializable{
	
	private static final long serialVersionUID = 6652648022347822854L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private long Id;
	@ManyToOne
	@JoinColumn(name = "employee_id")
	@JsonBackReference
	private User user;
	@Column
	private Date deliveryDate;
	@Column
	private boolean done;
	@OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private Set<Order> ordersOfDelivery = new HashSet<Order>();
	@Column(name= "delivery_order")
	private String deliveryOrder;
	@Column
	private Double distance;
	@Column
	private Integer priority;

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
	
	public String getDeliveryOrder() {
		return deliveryOrder;
	}

	public void setDeliveryOrder(String deliveryOrder) {
		this.deliveryOrder = deliveryOrder;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (Id ^ (Id >>> 32));
		result = prime * result + ((deliveryDate == null) ? 0 : deliveryDate.hashCode());
		result = prime * result + ((deliveryOrder == null) ? 0 : deliveryOrder.hashCode());
		result = prime * result + ((distance == null) ? 0 : distance.hashCode());
		result = prime * result + (done ? 1231 : 1237);
		result = prime * result + ((priority == null) ? 0 : priority.hashCode());
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
		if (deliveryOrder == null) {
			if (other.deliveryOrder != null)
				return false;
		} else if (!deliveryOrder.equals(other.deliveryOrder))
			return false;
		if (distance == null) {
			if (other.distance != null)
				return false;
		} else if (!distance.equals(other.distance))
			return false;
		if (done != other.done)
			return false;
		if (priority == null) {
			if (other.priority != null)
				return false;
		} else if (!priority.equals(other.priority))
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
				+ ", ordersOfDelivery=" + ordersOfDelivery + ", deliveryOrder=" + deliveryOrder + ",distance="
				+ distance + "]";
	}
	
	
	
	
}