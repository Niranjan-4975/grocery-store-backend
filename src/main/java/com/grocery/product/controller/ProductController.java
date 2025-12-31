package com.grocery.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.grocery.common.JwtUtility;
import com.grocery.product.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final ProductService productService;
	public ProductController(ProductService productService) {
		this.productService = productService;
	}


    // 🌟 1. GET FEATURED PRODUCTS (Home Page Slider)
    @GetMapping("/featured")
    public ResponseEntity<?> getFeaturedProducts() {
        return ResponseEntity.ok(productService.getFeaturedProducts());
    }

	// GET ACTIVE PRODUCTS (CUSTOMER)
	@GetMapping("/getProducts")
	public ResponseEntity<?> getActiveProducts(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(productService.listActiveProducts(search, categoryId, pageable));
	}

	@GetMapping("/getProducts/{id}")
	public ResponseEntity<?> getProductById(
            @PathVariable Long id,
            Authentication authentication) {
		return ResponseEntity.ok(productService.getProduct(id));
	}

}
