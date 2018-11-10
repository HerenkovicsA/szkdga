package com.szakdolgozat.components;

public class ProductAndQuantityResponse {

	private Long id;
	private String name;
	private Integer quantity;
	private Double price;
	
	public ProductAndQuantityResponse() {
		
	}
	
	public ProductAndQuantityResponse(Long id, String name, Integer quantity, Double price) {
		this.id = id;
		this.name = name;
		this.quantity = quantity;
		this.price = price;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getQuantity() {
		return quantity;
	}
	
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	public Double getPrice() {
		return price;
	}
	
	public void setPrice(Double price) {
		this.price = price;
	}
}
