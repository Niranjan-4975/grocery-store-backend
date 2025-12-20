package com.grocery.category.dto;

import com.grocery.common.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class CategoryCreateRequest {
	
	@NotBlank
	private String name;
	private String description;
	private Status status;
}
