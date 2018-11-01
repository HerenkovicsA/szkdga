package com.szakdolgozat.service;

import java.util.List;

import com.szakdolgozat.domain.Product;

public interface ProductService {

	List<Product> findAll() throws Exception;
	
	void addNewProduct(Product product, String pathToFile);

	String deleteProduct(long id);

}
