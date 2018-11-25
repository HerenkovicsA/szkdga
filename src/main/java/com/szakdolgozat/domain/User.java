package com.szakdolgozat.domain;

import java.io.Serializable;
import java.util.Date;
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
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.szakdolgozat.components.ShoppingCart;

@Entity
@Table(name = "users")
public class User implements Serializable{
	
	private static final long serialVersionUID = 6652648022347822854L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private long Id;
	@ManyToOne
	@JoinColumn(name = "role_id", nullable = false)
	@JsonBackReference
	private Role role;
	@Column(nullable = false)
	@Pattern(regexp = "(?U)[-.\\p{L}\\s]+", message = "A név csak unicode karaktereket tartalmazhat (számokat nem)")
	private String name;
	@Column(nullable = false)
	private String password;
	@Column(nullable = false)
	@Past(message = "Születésnap a mai napnál korábbi kell, hogy legyen")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date birthday;
	@Column(nullable = false)
	@Email(message = "Email cím csak valid cím lehet (valami@valami.valami).")
	private String email;
	@Column(name = "phone_number", nullable = false)
	@Pattern(regexp = "([237]0\\d{7}+)|(\\d{8}+)", message = "Mobil: 20 vagy 30 vagy 70 -el kell kezdődnie, "
			+ "majd további 7 szám (201234567) vagy vezetékes: 8 szám (96123456)")
	private String phoneNumber;
	@Column(nullable = false)
	private int sex;
	@Column(nullable = false)
	private String city;
	@Column(nullable = false)
	private String address;
	@Column(name= "post_Code", nullable = false)
	@Min(1000)
	@Max(9999)
	private int postCode;
	@Column(name = "house_number", nullable = false)
	private int houseNumber;
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.EAGER)
	@JsonBackReference
	private Set<Order> ordersOfUser = new HashSet<Order>();
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.EAGER)
	@JsonBackReference
	private Set<Delivery> deliveriesOfEmployee = new HashSet<Delivery>();
	private String fullAddress;
	
	public User() {
		
	}
	
	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public int getPostCode() {
		return postCode;
	}

	public void setPostCode(int postCode) {
		this.postCode = postCode;
	}

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
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

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
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

	public Set<Order> getOrdersOfUser() {
		return ordersOfUser;
	}

	public void setOrdersOfUser(Set<Order> ordersOfUser) {
		this.ordersOfUser = ordersOfUser;
	}

	public Set<Delivery> getDeliveriesOfEmployee() {
		return deliveriesOfEmployee;
	}

	public void setDeliveriesOfEmployee(Set<Delivery> deliveriesOfEmployee) {
		this.deliveriesOfEmployee = deliveriesOfEmployee;
	}

	public void clearOrdersOfUser() {
		this.ordersOfUser.clear();
	}
	
	public void addToOrdersOfUser(Order order) {
		this.ordersOfUser.add(order);
	}
	
	public void addToDelvierisOfEmployee(Delivery delivery) {
		this.deliveriesOfEmployee.add(delivery);
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
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
		result = prime * result + postCode;
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result + sex;
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (phoneNumber == null) {
			if (other.phoneNumber != null)
				return false;
		} else if (!phoneNumber.equals(other.phoneNumber))
			return false;
		if (postCode != other.postCode)
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		if (sex != other.sex)
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "User [Id=" + Id + ", role name=" + role.getName() + ", name=" + name + ", password=" + password + ", birthday="
				+ birthday + ", email=" + email + ", phoneNumber=" + phoneNumber + ", sex=" + sex + ", city=" + city
				+ ", address=" + address + ", postCode=" + postCode + ", houseNumber=" + houseNumber + ", ordersOfUser="
				+ ordersOfUser + ", deliveriesOfEmployee=" + deliveriesOfEmployee + "]";
	}

	private void initFullAddress() {
		this.fullAddress = getPostCode() + ", " + getCity() + " " + getAddress() + " " + getHouseNumber();
	}
		
	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}
	
	public String getFullAddress() {
		if(this.fullAddress == null )initFullAddress();
		return this.fullAddress;
	}
}