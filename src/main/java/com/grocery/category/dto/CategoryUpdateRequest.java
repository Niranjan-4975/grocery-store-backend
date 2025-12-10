package com.grocery.category.dto;

import com.grocery.common.Status;
import jakarta.validation.constraints.NotBlank;

public class CategoryUpdateRequest {

	@NotBlank
	private String name;
	private String description;
	private Status status;
	
	
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
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
		
}
