package com.szakdolgozat.repository;

import org.springframework.data.repository.CrudRepository;

import com.szakdolgozat.domain.Employee;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {

}
