package com.szakdolgozat.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.szakdolgozat.domain.Product;

public interface ProductService {

	List<Product> findAll() throws Exception;
	
	void addOrEditProduct(Product product, String pathToFile);

	String deleteProduct(long id);

	void addOrEditProduct(Product product, MultipartFile file, String path);
	
	Product getProductById(long id);

	Product getProductForCart(long productId);
	
	void saveProduct(Product product);

	void updateProductOnStock(Product product, int diff);
	
	String recycleProduct(long id);

}
