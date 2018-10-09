package com.szakdolgozat;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.szakdolgozat.domain.User;
import com.szakdolgozat.repository.UserRepository;

@SpringBootApplication
public class SzakdolgozatApplication {

	private static	UserRepository userRepo;
	
	@Autowired
	private void setUserRepo(UserRepository userRepo) {
		this.userRepo = userRepo;
	}
	
	private static void init() {
		User entity = new User();
		entity.setBirthday(new Date(1995,2,12));
		entity.setEmail("herenkovics95@gmail.com");
		entity.setLatitude("17.2346846");
		entity.setLongitude("46.1235488");
		entity.setMale(true);
		entity.setName("Herenkovics András");
		entity.setCity("Dunaszeg");
		entity.setAddress("Rakoczi Ferenc utca");
		entity.setHouseNumber(8);
		
		userRepo.save(entity);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(SzakdolgozatApplication.class, args);
		init();
	}
}
