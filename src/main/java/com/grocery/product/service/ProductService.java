package com.grocery.product.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.grocery.category.Category;
import com.grocery.category.CategoryRepository;
import com.grocery.common.Status;
import com.grocery.product.Product;
import com.grocery.product.dto.ProductCreateRequest;
import com.grocery.product.dto.ProductUpdateRequest;
import com.grocery.product.repository.ProductRepository;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class ProductService {
	
	@Autowired
	private  ProductRepository productRepository;
	@Autowired
	private  CategoryRepository categoryRepository;
	
	
	// Add Product (Admin only)
	public Product createProduct(ProductCreateRequest request) {
		
		//validate Category
		Category category = categoryRepository.findById(request.getCategoryId())
				.orElseThrow(() -> new RuntimeException("Invalid Category Id"));
		
		
		Product product = new Product();
		product.setName(request.getName());
		product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category);
        product.setStatus(Status.valueOf(request.getStatus()));
        // Image handling → storage path (we'll implement in Step 5)
        product.setImageUrl(request.getImageUrl());

        return productRepository.save(product);
    }
	
	
	// ➤ Update Product (Admin only)
    public Product updateProduct(Long id, ProductUpdateRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Admin can update all fields
        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getStock() != null) product.setStock(request.getStock());
        if (request.getStatus() != null) product.setStatus(Status.valueOf(request.getStatus()));
        if (request.getImageUrl() != null) product.setImageUrl(request.getImageUrl());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Invalid category"));
            product.setCategory(category);
        }

        return productRepository.save(product);
    }
    
    
    // Delete Product
    public void deleteProduct(Long id) {
    	if (!productRepository.existsById(id)) {
    		throw new RuntimeException("Product not found");
    	}
    	productRepository.deleteById(id);
    }
    
    
    // ➤ Get Single Product by Id 
    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
    
    
    // List Products (Customer)
    // ➤ Get All Products with Search + Filter + Pagination
    public Page<Product> listActiveProducts(String search, Long categoryId, Pageable pageable) {
    	
    	if (search == null) search = "";
    	if (categoryId == null) categoryId = 0L;
    	return productRepository.searchActiveProducts(search, categoryId, pageable);
    }
    
    
    //List Products (Admin)
    public Page<Product> listAllProducts(String search, Long categoryId, Pageable pageable){
    	
    	if (search == null) search = "";
    	if (categoryId == null) categoryId = 0L;
    	return productRepository.searchAllProducts(search, categoryId, pageable);
    }
	
    
    public Product updateImage(Long id, String imageUrl) {
    	
    	Product product = productRepository.getReferenceById(id);
    	product.setImageUrl(imageUrl);
    	return productRepository.save(product);
    }
}
