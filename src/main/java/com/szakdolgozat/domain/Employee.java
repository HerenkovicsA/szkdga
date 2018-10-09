package com.szakdolgozat.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "employees")
public class Employee {
	
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 6652648022347822854L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long Id;
	private String name;
	private Date birthday;
	private String emailAddress;
	private String phoneNumber;
	private boolean male;
	private String city;
	private String address;
	@Column(name = "housenumber")
	private int houseNumber;
	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Delivery> deliveries = new HashSet<Delivery>();

	public Employee() {
		
	}

	public Employee(long id, String name, Date birthday, String emailAddress, String phoneNumber, boolean male,
			String city, String address, int houseNumber, Set<Delivery> deliveries) {
		Id = id;
		this.name = name;
		this.birthday = birthday;
		this.emailAddress = emailAddress;
		this.phoneNumber = phoneNumber;
		this.male = male;
		this.city = city;
		this.address = address;
		this.houseNumber = houseNumber;
		this.deliveries = deliveries;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(int houseNumber) {
		this.houseNumber = houseNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public boolean isMale() {
		return male;
	}

	public void setMale(boolean male) {
		this.male = male;
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

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Set<Delivery> getDeliveries() {
		return deliveries;
	}

	public void setDeliveries(Set<Delivery> deliveries) {
		this.deliveries = deliveries;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (Id ^ (Id >>> 32));
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((birthday == null) ? 0 : birthday.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((deliveries == null) ? 0 : deliveries.hashCode());
		result = prime * result + ((emailAddress == null) ? 0 : emailAddress.hashCode());
		result = prime * result + houseNumber;
		result = prime * result + (male ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
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
		Employee other = (Employee) obj;
		if (Id != other.Id)
			return false;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (birthday == null) {
			if (other.birthday != null)
				return false;
		} else if (!birthday.equals(other.birthday))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (deliveries == null) {
			if (other.deliveries != null)
				return false;
		} else if (!deliveries.equals(other.deliveries))
			return false;
		if (emailAddress == null) {
			if (other.emailAddress != null)
				return false;
		} else if (!emailAddress.equals(other.emailAddress))
			return false;
		if (houseNumber != other.houseNumber)
			return false;
		if (male != other.male)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (phoneNumber == null) {
			if (other.phoneNumber != null)
				return false;
		} else if (!phoneNumber.equals(other.phoneNumber))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Employee [Id=" + Id + ", name=" + name + ", birthday=" + birthday + ", emailAddress=" + emailAddress
				+ ", phoneNumber=" + phoneNumber + ", male=" + male + ", city=" + city + ", address=" + address
				+ ", houseNumber=" + houseNumber + ", deliveries=" + deliveries + "]";
	}
	
	
	
}
