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
@Table(name = "users")
public class User {
	
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 6652648022347822854L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long Id;
	private String name;
	private Date birthday;
	private String email;
	private String phoneNumber;
	private boolean male;
	private String city;
	private String address;
	@Column(name = "housenumber")
	private int houseNumber;
	private String longitude;
	private String latitude;
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Order> ordersOfUser = new HashSet<Order>();
	
	
	public User() {
		
	}


	public User( String name, Date birthday, String email, String phoneNumber, boolean male, String city,
			String address, int houseNumber, String longitude, String latitude, Set<Order> ordersOfUser) {
		this.name = name;
		this.birthday = birthday;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.male = male;
		this.city = city;
		this.address = address;
		this.houseNumber = houseNumber;
		this.longitude = longitude;
		this.latitude = latitude;
		this.ordersOfUser = ordersOfUser;
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


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
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


	public String getLongitude() {
		return longitude;
	}


	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}


	public String getLatitude() {
		return latitude;
	}


	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}


	public Set<Order> getOrdersOfUser() {
		return ordersOfUser;
	}


	public void setOrdersOfUser(Set<Order> ordersOfUser) {
		this.ordersOfUser = ordersOfUser;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (Id ^ (Id >>> 32));
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((birthday == null) ? 0 : birthday.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + houseNumber;
		result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result + ((longitude == null) ? 0 : longitude.hashCode());
		result = prime * result + (male ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((ordersOfUser == null) ? 0 : ordersOfUser.hashCode());
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
		User other = (User) obj;
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
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (houseNumber != other.houseNumber)
			return false;
		if (latitude == null) {
			if (other.latitude != null)
				return false;
		} else if (!latitude.equals(other.latitude))
			return false;
		if (longitude == null) {
			if (other.longitude != null)
				return false;
		} else if (!longitude.equals(other.longitude))
			return false;
		if (male != other.male)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (ordersOfUser == null) {
			if (other.ordersOfUser != null)
				return false;
		} else if (!ordersOfUser.equals(other.ordersOfUser))
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
		return "User [Id=" + Id + ", name=" + name + ", birthday=" + birthday + ", emailAddress=" + email
				+ ", phoneNumber=" + phoneNumber + ", male=" + male + ", city=" + city + ", address=" + address
				+ ", houseNumber=" + houseNumber + ", longitude=" + longitude + ", latitude=" + latitude
				+ ", ordersOfUser=" + ordersOfUser + "]";
	}

	
		
	
}
