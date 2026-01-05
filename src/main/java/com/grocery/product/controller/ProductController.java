package com.grocery.product.controller;

import com.grocery.common.ApiResponse;
import com.grocery.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.grocery.common.JwtUtility;
import com.grocery.product.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final ProductService productService;
	public ProductController(ProductService productService) {
        this.productService = productService;
	}


    // 🌟 1. GET FEATURED PRODUCTS (Home Page Slider)
    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<Product>>> getFeaturedProducts() {
        List<Product> featured = productService.getFeaturedProducts();
        return ResponseEntity.ok(ApiResponse.success(null, featured));
    }

	// GET ACTIVE PRODUCTS (CUSTOMER)
	@GetMapping("/getProducts")
	public ResponseEntity<ApiResponse<Page<Product>>> getActiveProducts(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
		Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.listActiveProducts(search, categoryId, pageable);
		return ResponseEntity.ok(ApiResponse.success(null,products));
	}

	@GetMapping("/getProducts/{id}")
	public ResponseEntity<ApiResponse<Product>> getProductById(
            @PathVariable Long id,
            Authentication authentication) {
        Product product = productService.getProduct(id);
		return ResponseEntity.ok(ApiResponse.success(null, product));
	}

}
