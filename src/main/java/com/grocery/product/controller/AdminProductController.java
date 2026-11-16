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

	@PostMapping(consumes = {"multipart/form-data"})
	public ResponseEntity<ApiResponse<Product>> createProductMultipart(
            @ModelAttribute @Valid ProductCreateRequest request,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Authentication authentication){
        MultipartFile uploadFile = (file != null && !file.isEmpty()) ? file : image;
        System.out.println(">>> [AdminProductController] POST /api/admin/products (multipart) createProduct with file: " + (uploadFile != null ? uploadFile.getOriginalFilename() : "none"));
		Product created = productService.createProduct(request, uploadFile);
		return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product added successfully!", created));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Product>> createProduct(
            @RequestBody @Valid ProductCreateRequest request,
            Authentication authentication){
        System.out.println(">>> [AdminProductController] POST /api/admin/products (json) createProduct with imageUrl: " + request.getImageUrl());
		Product created = productService.createProduct(request, null);
		return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product added successfully!", created));
	}

	@PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
	public ResponseEntity<ApiResponse<Product>> updateProductMultipart(
			@PathVariable Long id,
            @ModelAttribute ProductUpdateRequest request,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Authentication authentication){
        MultipartFile uploadFile = (file != null && !file.isEmpty()) ? file : image;
        System.out.println(">>> [AdminProductController] PUT /api/admin/products/" + id + " (multipart) updateProduct with file: " + (uploadFile != null ? uploadFile.getOriginalFilename() : "none"));
		Product updated = productService.updateProduct(id, request, uploadFile);
		return ResponseEntity.ok(ApiResponse.success("Product updated successfully!", updated));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<Product>> updateProduct(
			@PathVariable Long id,
            @RequestBody ProductUpdateRequest request,
            Authentication authentication){
        System.out.println(">>> [AdminProductController] PUT /api/admin/products/" + id + " (json) updateProduct with imageUrl: " + request.getImageUrl());
		Product updated = productService.updateProduct(id, request, null);
		return ResponseEntity.ok(ApiResponse.success("Product updated successfully!", updated));
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
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable Long id){
    	Product product = productService.getProduct(id);
    	return ResponseEntity.ok(ApiResponse.success(null,product));
    }

    @PostMapping("/images/product/{id}")
    public ResponseEntity<ApiResponse<Product>> uploadProductImage(
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Authentication authentication){

        MultipartFile uploadFile = (file != null && !file.isEmpty()) ? file : image;
        if (uploadFile == null || uploadFile.isEmpty()) {
            throw new com.grocery.exception.FileStorageException("Please select an image file to upload (param name 'file' or 'image').");
        }
        System.out.println(">>> [AdminProductController] POST /images/product/" + id + " received file: " + uploadFile.getOriginalFilename());
        String imageUrl = fileStorageService.saveFile(uploadFile);
        Product updated = productService.updateImage(id, imageUrl);
        return ResponseEntity.ok(ApiResponse.success("Product image added successfully!", updated));
    }

    @PostMapping("/upload-image")
    public ResponseEntity<ApiResponse<String>> uploadStandaloneImage(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "image", required = false) MultipartFile image,
            Authentication authentication){

        MultipartFile uploadFile = (file != null && !file.isEmpty()) ? file : image;
        if (uploadFile == null || uploadFile.isEmpty()) {
            throw new com.grocery.exception.FileStorageException("Please select an image file to upload (param name 'file' or 'image').");
        }
        System.out.println(">>> [AdminProductController] POST /upload-image received file: " + uploadFile.getOriginalFilename());
        String imageUrl = fileStorageService.saveFile(uploadFile);
        return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully!", imageUrl));
    }
    
}
