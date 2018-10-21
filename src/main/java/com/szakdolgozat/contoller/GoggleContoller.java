package com.szakdolgozat.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.szakdolgozat.domain.User;
import com.szakdolgozat.repository.UserRepository;
import com.szakdolgozat.service.UserService;
import com.szakdolgozat.service.UserServiceImpl;

@RestController
public class GoggleContoller {
	
	private UserService us;
	private UserRepository ur;
	
	@Autowired
	public void UserService(UserService us, UserRepository ur) {
		this.us = us;
		this.ur = ur;
	}
	
	@RequestMapping("/google")
	public String home(){
		StringBuilder sb = new StringBuilder();
		for (User user : ur.findAll()) {
			sb.append(user.toString());
			sb.append('\n');
		}
		System.out.println(sb.toString());
		return sb.toString();
	}
	

}
