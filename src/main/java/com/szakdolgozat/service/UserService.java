package com.szakdolgozat.service;

import com.szakdolgozat.domain.User;
import com.szakdolgozat.repository.UserRepository;

public interface UserService {
	
	public void createUser(User user);
	
	public User findByName(String name);
	
	public int registerUser(User userToRegister);

	public User findByEmail(String email);
	
	public void encodeExistingUserPassword();
}
