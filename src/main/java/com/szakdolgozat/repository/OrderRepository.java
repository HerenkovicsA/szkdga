package com.szakdolgozat.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import com.szakdolgozat.domain.Delivery;
import com.szakdolgozat.domain.Order;

public interface OrderRepository extends CrudRepository<Order, Long> {

	List<Order> findAllByDelivery(Delivery delivery);

	List<Order> findTop20ByDoneFalseAndDeliveryIsNullOrderByDeadLineAsc();

	Set<Order> findAllByOrderByDeadLineAsc();

	List<Order> findByDoneFalseAndDeliveryIsNull();

}
