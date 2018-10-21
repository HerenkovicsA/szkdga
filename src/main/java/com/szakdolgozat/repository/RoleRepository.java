package com.szakdolgozat.repository;

import org.springframework.data.repository.CrudRepository;

import com.szakdolgozat.domain.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {

	Role findByName(String name);
}
