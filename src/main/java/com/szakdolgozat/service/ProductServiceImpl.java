package com.szakdolgozat.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.szakdolgozat.domain.Product;
import com.szakdolgozat.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

	private ProductRepository pr;
	
	@Autowired
	public ProductServiceImpl(ProductRepository pr) {
		this.pr = pr;
	}

	@Override
	public List<Product> findAll() throws Exception {
		List<Product> productList = new ArrayList<Product>();
		for (Product product : pr.findAll()) {
			productList.add(product);
		}
		if(productList.isEmpty()) throw new Exception("No product found!");
		return productList;
	}

	@Override
	public void addOrEditProduct(Product product, String pathToFile) {
		Product productToSave;
		if(pathToFile != null) {
			productToSave = new Product();
			productToSave.setPathToPicture(pathToFile);
		}else {
			productToSave = pr.findById(product.getId()).get();
			productToSave.setPathToPicture(product.getPathToPicture());
		}		
		productToSave.setName(product.getName());
		productToSave.setOnStock(product.getOnStock());
		productToSave.setPrice(product.getPrice());
		pr.save(productToSave);
	}

	@Override
	public String deleteProduct(long id) {
		boolean exists = pr.findById(id).isPresent();
		if(exists) {
			pr.deleteById(id);
			return "deleted";
		}else {
			return "not exists";
		}
	}

}
