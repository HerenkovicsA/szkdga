package com.szakdolgozat.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.szakdolgozat.domain.Delivery;

public interface DeliveryRepository extends CrudRepository<Delivery, Long> {

	Optional<Delivery> findTop1ByUserIsNullAndPriorityIsNotNullOrderByPriorityDesc();
	
	Optional<Delivery> findTop1ByUserIsNullOrderByDeliveryDateAsc();
	
}
