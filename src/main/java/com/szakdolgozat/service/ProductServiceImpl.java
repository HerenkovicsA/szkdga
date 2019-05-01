package com.szakdolgozat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.szakdolgozat.domain.Product;
import com.szakdolgozat.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

	private ProductRepository pr;
	private StoreFileService sfs;
	
	@Autowired
	public ProductServiceImpl(ProductRepository pr, StoreFileService sfs) {
		this.pr = pr;
		this.sfs = sfs;
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
	public void addOrEditProduct(Product product, MultipartFile file, String path) {
		Optional<Product> optProd = pr.findById(product.getId());
		Product productToSave;
		if(optProd.isPresent()) {
			productToSave = optProd.get();
			if(!file.isEmpty()) {
				productToSave.setPathToPicture(sfs.store(file,productToSave.getPathToPicture(), path));
			}
		} else {
			productToSave = new Product();
			productToSave.setPathToPicture(sfs.store(file, false, path));
		}
		productToSave.setHeight(product.getHeight());
		productToSave.setWidth(product.getWidth());
		productToSave.setLength(product.getLength());
		productToSave.setName(product.getName());
		productToSave.setOnStock(product.getOnStock());
		productToSave.setPrice(product.getPrice());
		pr.save(productToSave);
	}

	
	@Override
	public String deleteProduct(long id) {
		Optional<Product> optProduct = pr.findById(id);
		if(optProduct.isPresent()) {
			Product product = optProduct.get();
			product.setDeleted(true);
			pr.save(product);
			return "deleted";
		}else {
			return "not exists";
		}
	}
	
	@Override
	public String recycleProduct(long id) {
		Optional<Product> optProduct = pr.findById(id);
		if(optProduct.isPresent()) {
			Product product = optProduct.get();
			product.setDeleted(false);
			pr.save(product);
			return "recycled";
		}else {
			return "not exists";
		}
	}

	@Override
	public Product getProductById(long id) {
		Optional<Product> prodOpt = pr.findById(id);
		if(prodOpt.isPresent()) {
			return prodOpt.get();
		}
		return null;
	}

	@Override
	public Product getProductForCart(long productId) {
		Product product = getProductById(productId);
		if(product != null && product.getOnStock() > 0 ) {
			pr.save(product);
			return product;
		}
		return null;
	}

	@Override
	public void saveProduct(Product product) {
		pr.save(product);		
	}

	@Override
	public void updateProductOnStock(Product product, int diff) {
		product.setOnStock(product.getOnStock() + diff);
		saveProduct(product);
	}


}
