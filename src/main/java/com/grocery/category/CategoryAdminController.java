package com.grocery.category;

import com.grocery.common.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.grocery.category.dto.CategoryCreateRequest;
import com.grocery.category.dto.CategoryUpdateRequest;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
public class CategoryAdminController {

	@Autowired
	private CategoryService categoryService;
	
	// Admin create
	@PostMapping
	public ResponseEntity<Category> create(
            @RequestBody CategoryCreateRequest request,
            Authentication authentication){
		return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request));
	}

    // Admin read all (List with optional Search/Filter)
    @GetMapping
    public ResponseEntity<Page<Category>> listAllCategories(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "0") int page,      // ✅ New
            @RequestParam(defaultValue = "10") int size,     // ✅ New
            Authentication authentication) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(categoryService.listAll(search,status, pageable));
    }

    // Admin update
	@PutMapping("/{id}")
	public ResponseEntity<Category> updateCategory(
			@PathVariable Long id,
			@RequestBody CategoryUpdateRequest request,
            Authentication authentication){
		return ResponseEntity.ok(categoryService.updateCategory(id, request));
	}

    // Admin soft delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id,
            Authentication authentication) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }
}
