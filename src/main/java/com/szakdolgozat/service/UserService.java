package com.szakdolgozat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.szakdolgozat.domain.User;
import com.szakdolgozat.repository.UserRepository;

@Service
public class UserService {

	private UserRepository userRepo;
	
	public void createUer(User user){
		userRepo.save(user);
	}

	public UserService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}
	
}
