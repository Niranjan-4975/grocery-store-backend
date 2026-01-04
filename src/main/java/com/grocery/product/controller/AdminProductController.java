package com.grocery.product.controller;

import com.grocery.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
	private FileStorageService fileStorageService;

	@PostMapping
	public ResponseEntity<ApiResponse<Product>> createProduct(
            @RequestBody @Valid ProductCreateRequest request,
            Authentication authentication){
		Product created = productService.createProduct(request, null);
		return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product added successfully!", created));
	}


	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<Product>> updateProduct(
			@PathVariable Long id,
            @RequestBody ProductUpdateRequest request,
            Authentication authentication){
		Product updated = productService.updateProduct(id, request, null);
		return ResponseEntity.ok(ApiResponse.success("Product updated successfully!",updated));
	}


	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Long id,
            Authentication authentication) {
		productService.deleteProduct(id);
		return ResponseEntity.ok(ApiResponse.success("Product removed from inventory",null));
	}

	// GET ALL PRODUCTS (ADMIN)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Product>>> getAllProducts(
    		@RequestParam(defaultValue = "") String search,
    		@RequestParam(required = false) Long categoryId,
    		@RequestParam(defaultValue = "0") int page,
    		@RequestParam(defaultValue = "10") int size,
    		Authentication authentication) {
    	Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.listAllProducts(search, categoryId, pageable);
        return ResponseEntity.ok(ApiResponse.success(null,products));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductById(@RequestParam Long id){
    	Product product = productService.getProduct(id);
    	return ResponseEntity.ok(ApiResponse.success(null,product));
    }


    @PostMapping("/images/product/{id}")
    public ResponseEntity<ApiResponse<Product>> uploadProductImage(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file,
            Authentication authentication){

        String imageUrl = fileStorageService.saveFile(file);
        Product updated = productService.updateImage(id, imageUrl);
        return ResponseEntity.ok(ApiResponse.success("Product image added successfully!",updated));
    }
    
}
