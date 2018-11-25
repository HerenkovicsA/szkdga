package com.szakdolgozat.service;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import com.szakdolgozat.domain.Delivery;
import com.szakdolgozat.domain.Order;
import com.szakdolgozat.domain.User;
import com.szakdolgozat.repository.UserRepository;

public interface UserService {
	
	public void createUser(User user);
	
	public User findByName(String name);
	
	public int registerUser(User userToRegister);

	public User findByEmail(String email);
	
	public void encodeExistingUserPassword();

	public List<User> findAllEmployees() throws Exception;

	public List<User> findAllUsers() throws Exception;

	public Set<Order> findOrdersOfUser(long userId) throws Exception;

	public Set<Delivery> findDeliveriesOfEmployee(long employeeId) throws Exception;

	public void editUser(User user) throws Exception;

	public String deleteUser(long id);

	public Object getAllUserNameAndId(boolean user) throws Exception;

	public User findUserById(long userId) throws Exception;
	
	public boolean hasActiveDelivery(User user);

	public List<Order> findOrdersOfUser(String email);

}
