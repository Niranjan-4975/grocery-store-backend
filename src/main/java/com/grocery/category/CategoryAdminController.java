package com.grocery.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grocery.category.dto.CategoryCreateRequest;
import com.grocery.category.dto.CategoryUpdateRequest;

@RestController
@RequestMapping("/admin/categories")
public class CategoryAdminController {

	
	@Autowired
	private CategoryService categoryService;
	
	
	// Admin create
	@PostMapping("/create")
	public ResponseEntity<Category> create(
			@RequestBody CategoryCreateRequest request,
			@RequestHeader("user-payload") String token){
		
		return ResponseEntity.ok(categoryService.createCategory(request));
	}
	
	
	@PutMapping("/update/{id}")
	public ResponseEntity<Category> update(
			@PathVariable Long id,
			@RequestBody CategoryUpdateRequest request){
		
		return ResponseEntity.ok(categoryService.updateCategory(id, request));
	}
}
