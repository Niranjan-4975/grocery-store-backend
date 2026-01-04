package com.grocery.category;

import com.grocery.common.ApiResponse;
import com.grocery.common.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.grocery.category.dto.CategoryCreateRequest;
import com.grocery.category.dto.CategoryUpdateRequest;

@RestController
@RequestMapping("/api/admin/categories")
public class CategoryAdminController {

	@Autowired
	private CategoryService categoryService;
	
	// Admin create
	@PostMapping
	public ResponseEntity<ApiResponse<Category>> create(@RequestBody CategoryCreateRequest request,
            Authentication authentication){
        Category category = categoryService.createCategory(request);
		return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category added successfully!", category));
	}

    // Admin read all (List with optional Search/Filter)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Category>>> listAllCategories(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> data = categoryService.listAll(search,status, pageable);
        return ResponseEntity.ok(ApiResponse.success(null, data));
    }

    // Admin update
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<Category>> updateCategory(
			@PathVariable Long id,
			@RequestBody CategoryUpdateRequest request,
            Authentication authentication){
        Category updated = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully!", updated));
	}

    // Admin soft delete
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteCategory(
            @PathVariable Long id,
            Authentication authentication) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully!", null));
    }
}
