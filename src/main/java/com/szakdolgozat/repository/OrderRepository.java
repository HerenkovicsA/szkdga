package com.szakdolgozat.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.szakdolgozat.domain.Delivery;
import com.szakdolgozat.domain.Order;

public interface OrderRepository extends CrudRepository<Order, Long> {

	List<Order> findAllByDelivery(Delivery delivery);

}
