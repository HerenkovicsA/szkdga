package com.szakdolgozat.repository;

import org.springframework.data.repository.CrudRepository;

import com.szakdolgozat.domain.User;

public interface UserRepository extends CrudRepository<User, Long> {

	User findByName(String name);

	User findByEmail(String email);
}
