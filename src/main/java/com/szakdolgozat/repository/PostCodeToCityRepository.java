package com.szakdolgozat.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.szakdolgozat.domain.PostCodeToCity;

public interface PostCodeToCityRepository extends CrudRepository<PostCodeToCity, Long> {
	
	List<PostCodeToCity> findByPostCode(int postCode);
	
	List<PostCodeToCity> findByCityName(String cityName);
	
	
}
