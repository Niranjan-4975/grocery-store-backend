package com.grocery.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.grocery.common.JwtUtility;
import com.grocery.product.service.ProductService;

@RestController
@RequestMapping("api/products")
public class ProductController {

	private final ProductService productService;
	@Autowired
	private JwtUtility jwtUtility;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	// GET ACTIVE PRODUCTS (CUSTOMER)
	@GetMapping("/getProducts")
	public ResponseEntity<?> getActiveProducts(
			@RequestParam(defaultValue = "") String search,
			@RequestParam(required = false) Long categoryId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestHeader("user-payload") String token) {

		String email = jwtUtility.extractEmail(token);

		if (!jwtUtility.validateToken(token, email)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not Authorised to access this resouce");
		}

		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(productService.listActiveProducts(search, categoryId, pageable));
	}

	@GetMapping("/getProducts/{id}")
	public ResponseEntity<?> getProductById(@RequestParam Long id,
			@RequestHeader("user-payload") String token) {

		String email = jwtUtility.extractEmail(token);

		if (!jwtUtility.validateToken(token, email)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not Authorised to access this resouce");
		}
		return ResponseEntity.ok(productService.getProduct(id));
	}

}
