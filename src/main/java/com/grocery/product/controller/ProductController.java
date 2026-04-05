package com.grocery.product.controller;

import com.grocery.auth.User;
import com.grocery.auth.UserService;
import com.grocery.common.ApiResponse;
import com.grocery.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.grocery.product.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final ProductService productService;
    private final UserService userService;
	public ProductController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService= userService;
	}


    // 🌟 1. GET FEATURED PRODUCTS (Home Page Slider)
    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<Product>>> getFeaturedProducts(Authentication authentication) {
        Long userId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.getByEmail(authentication.getName());
            userId = user.getId();
        }
        List<Product> featured = productService.getFeaturedProducts(userId);
        return ResponseEntity.ok(ApiResponse.success(null, featured));
    }

	// GET ACTIVE PRODUCTS (CUSTOMER)
	@GetMapping("/getProducts")
	public ResponseEntity<ApiResponse<Page<Product>>> getActiveProducts(
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(required = false) Long categoryId,
            Pageable pageable, Authentication authentication) {
        Long userId = null;
        if (authentication !=null && authentication.isAuthenticated()){
            User user = userService.getByEmail(authentication.getName()); // AppKit existing logic
            userId = user.getId();
        }
        Page<Product> products = productService.listActiveProducts(search, categoryId, pageable, userId);
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
