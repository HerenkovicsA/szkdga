package com.szakdolgozat.repository;

import org.springframework.data.repository.CrudRepository;

import com.szakdolgozat.domain.Order;

public interface OrderRepository extends CrudRepository<Order, Long> {

}
