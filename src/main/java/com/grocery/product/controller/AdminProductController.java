package com.grocery.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.grocery.auth.Role;
import com.grocery.common.JwtUtility;
import com.grocery.product.Product;
import com.grocery.product.dto.ProductCreateRequest;
import com.grocery.product.dto.ProductUpdateRequest;
import com.grocery.product.service.FileStorageService;
import com.grocery.product.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

	@Autowired
	private ProductService productService;
	@Autowired
	private JwtUtility jwtUtility;
	@Autowired
	private FileStorageService fileStorageService;

	@PostMapping
	public ResponseEntity<?> createProduct(
            @RequestBody @Valid ProductCreateRequest request,
            Authentication authentication){
        System.out.println("Post called");
        // Note: We are creating the product without an image initially.
		Product created = productService.createProduct(request, null);
		return ResponseEntity.ok(created);
	}


	@PutMapping("/{id}")
	public ResponseEntity<?> updateProduct(
			@PathVariable Long id,
            @RequestBody ProductUpdateRequest request,
            Authentication authentication){
        System.out.println("Put called");
		Product updated = productService.updateProduct(id, request, null);
		return ResponseEntity.ok(updated);
	}


	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteProduct(
            @PathVariable Long id,
            Authentication authentication) {

		productService.deleteProduct(id);
		return ResponseEntity.ok("Product deleted successfully");
	}

	// GET ALL PRODUCTS (ADMIN)
    @GetMapping
    public ResponseEntity<?> getAllProducts(
    		@RequestParam(defaultValue = "") String search,
    		@RequestParam(required = false) Long categoryId,
    		@RequestParam(defaultValue = "0") int page,
    		@RequestParam(defaultValue = "10") int size,
    		Authentication authentication) {
    	Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.listAllProducts(search, categoryId, pageable));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@RequestParam Long id,
    		@RequestHeader("user-payload") String token) {
    	
    	var roles = jwtUtility.extractRoles(token);

		if (roles == null || !roles.contains(Role.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only Admins can access this data");
		}
    	
    	return ResponseEntity.ok(productService.getProduct(id));
    }

    // AdminProductController.java (CORRECTED uploadProductImage)
    @PostMapping("/images/product/{id}")
    public ResponseEntity<?> uploadProductImage(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file,
            Authentication authentication){

        String imageUrl = fileStorageService.saveFile(file);
        Product updated = productService.updateImage(id, imageUrl);
        return ResponseEntity.ok(updated);
    }
    
}
