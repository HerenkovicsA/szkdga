package com.szakdolgozat.service;

import java.util.List;
import java.util.Set;

import com.szakdolgozat.domain.Delivery;
import com.szakdolgozat.domain.Order;
import com.szakdolgozat.domain.User;

public interface UserService {
	
	int registerUser(User userToRegister);

	User findByEmail(String email);

	List<User> findAllEmployees() throws Exception;

	List<User> findAllUsers() throws Exception;

	Set<Order> findOrdersOfUser(long userId) throws Exception;

	Set<Delivery> findDeliveriesOfEmployee(long employeeId) throws Exception;

	void editUser(User user) throws Exception;

	String deleteUser(long id);

	Object getAllUserNameAndId(boolean user) throws Exception;

	User findUserById(long userId) throws Exception;
	
	boolean hasActiveDelivery(User user);

	List<Order> findOrdersOfUser(String email);

}
