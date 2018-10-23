package com.szakdolgozat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.szakdolgozat.repository.RoleRepository;
import com.szakdolgozat.service.UserDetailsImpl;
import com.szakdolgozat.domain.Role;
import com.szakdolgozat.domain.User;
import com.szakdolgozat.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService, UserDetailsService{

	private UserRepository userRepo;
	private RoleRepository roleRepo;
	private BCryptPasswordEncoder passwordEncoder;

	private final String USER_ROLE = "USER";
	private final String ADMIN_ROLE = "ADMIN";
	private final String EMPLOYEE_ROLE = "EMPLOYEE";
	
	@Autowired
	public UserServiceImpl(UserRepository userRepo, RoleRepository roleRepo, BCryptPasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.roleRepo = roleRepo;
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
		userToRegister.setRole(roleRepo.findByName(USER_ROLE));
		userToRegister.setPhoneNumber(userToRegister.getPhoneNumber());
		userToRegister.setPassword(passwordEncoder.encode(userToRegister.getPassword()));
		userRepo.save(userToRegister);

		return 1;
	}

	@Override
	public void encodeExistingUserPassword() {
		System.out.println("----------------------------\nPASSWORD EDIT");
		Iterable<User> users = userRepo.findAll();
		for (User user : users) {
			System.out.print(user.getName() + " :  " + user.getPassword());
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			System.out.println("     encoded:  " + user.getPassword()) ;
			userRepo.save(user);
		}
	}
}
