package com.szakdolgozat.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.szakdolgozat.domain.Role;
import com.szakdolgozat.domain.User;

public interface UserRepository extends CrudRepository<User, Long> {

	User findByName(String name);

	User findByEmail(String email);

	List<User> findAllByRole(Role role);
}
