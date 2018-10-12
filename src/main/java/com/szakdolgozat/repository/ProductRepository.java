package com.szakdolgozat.repository;

import org.springframework.data.repository.CrudRepository;

import com.szakdolgozat.domain.Product;

public interface ProductRepository extends CrudRepository<Product, Long> {

}
