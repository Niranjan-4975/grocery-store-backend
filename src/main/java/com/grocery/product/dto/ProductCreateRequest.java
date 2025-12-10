package com.grocery.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ProductCreateRequest {
	
	@NotBlank(message = "Product name is required")
	private String name;
	private String description;
	
	@NotNull(message = "Price is required")
	@Positive(message = "Price must be greater than 0")
	private Double price;
	
	@NotNull(message = "Stock is required")
	@Positive(message = "Stock cannot be negative")
	private Integer stock;
	
	@NotNull(message = "Category ID is required")
	private Long categoryId;
	
	private String imageUrl;
	
	@NotNull
	private String status;
	

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	
}
