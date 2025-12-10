package com.grocery.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
@RequestMapping("/admin/products")
public class AdminProductController {

	@Autowired
	private ProductService productService;
	@Autowired
	private JwtUtility jwtUtility;
	@Autowired
	private FileStorageService fileStorageService;

	@PostMapping("/create")
	public ResponseEntity<?> createProduct(@Valid @RequestBody ProductCreateRequest request,
			@RequestHeader("user-payload") String token){

		var roles = jwtUtility.extractRoles(token);

		if (roles == null || !roles.contains(Role.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only Admins can access this data");
		}
		Product created = productService.createProduct(request);
		return ResponseEntity.ok(created);
	}


	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateProduct(
			@PathVariable Long id,
			@RequestBody ProductUpdateRequest request,
			@RequestHeader("user-payload") String token){

		var roles = jwtUtility.extractRoles(token);

		if (roles == null || !roles.contains(Role.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only Admins can access this data");
		}

		Product updated = productService.updateProduct(id, request);
		return ResponseEntity.ok(updated);
	}


	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteProduct(@PathVariable Long id, @RequestHeader("user-payload") String token) {

		var roles = jwtUtility.extractRoles(token);

		if (roles == null || !roles.contains(Role.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only Admins can access this data");
		}
		
		productService.deleteProduct(id);
		return ResponseEntity.ok("Product deleted successfully");
	}
	
	
	// GET ALL PRODUCTS (ADMIN)
    @GetMapping("/getproducts")
    public ResponseEntity<?> getAllProducts(
    		@RequestParam(defaultValue = "") String search,
    		@RequestParam(required = false) Long categoryId,
    		@RequestParam(defaultValue = "0") int page,
    		@RequestParam(defaultValue = "10") int size,
    		@RequestHeader("user-payload") String token) {
    	
    	var roles = jwtUtility.extractRoles(token);

		if (roles == null || !roles.contains(Role.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only Admins can access this data");
		}

    	Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.listAllProducts(search, categoryId, pageable));
    }
    
    @GetMapping("/getproducts/{id}")
    public ResponseEntity<?> getProductById(@RequestParam Long id,
    		@RequestHeader("user-payload") String token) {
    	
    	var roles = jwtUtility.extractRoles(token);

		if (roles == null || !roles.contains(Role.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only Admins can access this data");
		}
    	
    	return ResponseEntity.ok(productService.getProduct(id));
    }
    
    
    @PostMapping("/{id}/upload-image")
    public ResponseEntity<?> uploadProductImage(
    		@PathVariable Long id,
    		@RequestParam("file") MultipartFile file,
    		@RequestHeader("user-payload") String token){
    	
    	var roles = jwtUtility.extractRoles(token);

		if (roles == null || !roles.contains(Role.ROLE_ADMIN)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only Admins can access this data");
		}
    	
    	String imageUrl = fileStorageService.saveFile(file);
    	
    	Product updated = productService.updateImage(id, imageUrl);
    	
    	return ResponseEntity.ok(updated);
    }   
    
}
