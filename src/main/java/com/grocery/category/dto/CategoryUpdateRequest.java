package com.grocery.category.dto;

import com.grocery.common.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
public class CategoryUpdateRequest {

	@NotBlank
	private String name;
	private String description;
	private Status status;


}
