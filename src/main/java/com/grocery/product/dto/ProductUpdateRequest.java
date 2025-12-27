package com.grocery.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Getter
@Setter
public class ProductUpdateRequest {

	private String name;
	private String description;
	private Double price;
	private Integer stock;
	private Long categoryId;
	private String status;
	private String imageUrl;
    private boolean featured;
}
