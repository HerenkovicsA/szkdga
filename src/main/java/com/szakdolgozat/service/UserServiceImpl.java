package com.szakdolgozat.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.comparator.Comparators;

import com.szakdolgozat.repository.RoleRepository;
import com.szakdolgozat.service.UserDetailsImpl;
import com.szakdolgozat.domain.Delivery;
import com.szakdolgozat.domain.Order;
import com.szakdolgozat.domain.Product;
import com.szakdolgozat.domain.ProductsToOrders;
import com.szakdolgozat.domain.Role;
import com.szakdolgozat.domain.User;
import com.szakdolgozat.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService, UserDetailsService{

	private UserRepository userRepo;
	private RoleService roleService;
	private BCryptPasswordEncoder passwordEncoder;

	private final String USER_ROLE = "USER";
	private final String ADMIN_ROLE = "ADMIN";
	private final String EMPLOYEE_ROLE = "EMPLOYEE";
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	public UserServiceImpl() {
		
	}
	
	@Autowired
	public void setUserRepo(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	@Autowired
	public void setRoleRepo(RoleService roleService) {
		this.roleService = roleService;
	}

	@Autowired
	public void setPasswordEncoder(BCryptPasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public void createUser(User user){
		userRepo.save(user);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = findByEmail(username);
		if (user == null) {
			throw new UsernameNotFoundException(username);
		}
		System.out.println(user.getPassword());

		return new UserDetailsImpl(user);
	}

	@Override
	public User findByName(String name) {
		return userRepo.findByName(name);
	}
	
	@Override
	public User findByEmail(String email) {
		return userRepo.findByEmail(email);
	}
	
	@Override
	public int registerUser(User userToRegister) {
		User userCheck = findByEmail(userToRegister.getEmail());
		if (userCheck != null)	return 0;
		userToRegister.setRole(roleService.findRoleByName(USER_ROLE));
		userToRegister.setPhoneNumber(userToRegister.getPhoneNumber());
		userToRegister.setPassword(passwordEncoder.encode(userToRegister.getPassword()));
		userRepo.save(userToRegister);

		return 1;
	}

	@Override
	public void encodeExistingUserPassword() {
		Iterable<User> users = userRepo.findAll();
		for (User user : users) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			userRepo.save(user);
		}
	}

	@Override
	public List<User> findAllEmployees() throws Exception {
		List<User> employees = userRepo.findAllByRole(roleService.findRoleByName(EMPLOYEE_ROLE));
		if(employees.isEmpty()) throw new Exception("No employee found");
		return employees;
	}

	@Override
	public List<User> findAllUsers() throws Exception {
		List<User> users = userRepo.findAllByRole(roleService.findRoleByName(USER_ROLE));
		if(users.isEmpty()) throw new Exception("No user found");
		return users;
	}

	@Override
	public Set<Order> findOrdersOfUser(long userId) throws Exception {
		User user = userRepo.findById(userId).get();
		Set<Order> ordersOfUserSet = user.getOrdersOfUser();
		if(ordersOfUserSet.isEmpty()) throw new Exception("No orders for user");
		return ordersOfUserSet;
	}

	@Override
	public Set<Delivery> findDeliveriesOfEmployee(long employeeId) throws Exception {
		User employee = userRepo.findById(employeeId).get();
		Set<Delivery> deliveriesOfEmployeeSet = employee.getDeliveriesOfEmployee();
		if(deliveriesOfEmployeeSet.isEmpty()) throw new Exception("No delivery to employee");
		return deliveriesOfEmployeeSet;
	}

	@Override
	public void editUser(User userToEdit) throws Exception{
		User user = userRepo.findById(userToEdit.getId()).get();
		if( !user.getEmail().equals(userToEdit.getEmail()) && userRepo.findByEmail(userToEdit.getEmail())!=null)
			throw new Exception("Az email cím (" + userToEdit.getEmail() + ") már foglalt.");
		if(!userToEdit.getPassword().isEmpty()) 
			user.setPassword(passwordEncoder.encode(userToEdit.getPassword()));
		user.setAddress(userToEdit.getAddress());
		user.setBirthday(userToEdit.getBirthday());
		user.setCity(userToEdit.getCity());
		user.setEmail(userToEdit.getEmail());
		user.setHouseNumber(userToEdit.getHouseNumber());
		user.setName(userToEdit.getName());
		user.setPhoneNumber(userToEdit.getPhoneNumber());
		user.setPostCode(userToEdit.getPostCode());
		if(userToEdit.getRole() != null)user.setRole(userToEdit.getRole());
		user.setSex(userToEdit.getSex());
		userRepo.save(user);
	}

	@Override
	@Transactional
	public String deleteUser(long id) {
		Optional<User> userToRemove = userRepo.findById(id);
		if(userToRemove.isPresent()) {
			removeUserFromRoles(userToRemove.get());
			userRepo.deleteById(id);
			return "deleted";
		}else {
			return "not exists";
		}
	}

	@Override
	public Object getAllUserNameAndId(boolean isUser) throws Exception {
		Map<Long, String> users = new HashMap<Long, String>();
		List<User> userList;
		if (isUser) {
			userList = findAllUsers();
		}else {
			userList = findAllEmployees();
		}		
		for (User user : userList) {
			users.put(user.getId(), user.getName());
		}
		return users;
	}
	
	private void removeUserFromRoles(User userToRemove) {
		Role role = userToRemove.getRole();
		Set<User> userSet = role.getUsersWithRole();
		if(userSet.contains(userToRemove)) {
			userSet.remove(userToRemove);
			role.setUsersWithRole(userSet);
		} else log.error("User " + userToRemove.getName() + " does not have role: " + role.getName());
	}

	@Override
	public User findUserById(long userId) throws Exception {
		Optional<User> user = userRepo.findById(userId);
		if(user.isPresent()) {
			return user.get();
		} else {
			throw new Exception("Nincs felhasználó " + userId + " ezzel az id-vel");
		}
	}
	
	public boolean hasActiveDelivery(User user) {
		Set<Delivery> deliveryList = user.getDeliveriesOfEmployee();
		if (deliveryList.isEmpty()) return false;
		for (Delivery delivery : deliveryList) {
			if(!delivery.isDone()) return true;
		}
		return false;
	}

	@Override
	public List<Order> findOrdersOfUser(String email) {
		User user = findByEmail(email);
		List<Order> orderList = new ArrayList<Order>();
		try {
			orderList.addAll(findOrdersOfUser(user.getId()));
			orderList.sort((Order o1, Order o2) -> o2.getDeadLine().compareTo(o1.getDeadLine()));
			return orderList;
		} catch (Exception e) {
			log.warn(e.getMessage());
			return null;
		}
	}
}
