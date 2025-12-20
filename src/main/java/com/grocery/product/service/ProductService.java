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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class ProductService {
	
	@Autowired
	private  ProductRepository productRepository;
	@Autowired
	private  CategoryRepository categoryRepository;
    @Autowired
    private FileStorageService fileStorageService;
	
	
	// Add Product (Admin only)
    @Transactional
	public Product createProduct(ProductCreateRequest request, MultipartFile file) {
		
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

        String imageUrl = null;
        if (file !=null && !file.isEmpty()){
            imageUrl = fileStorageService.saveFile(file);
        }
        product.setImageUrl(imageUrl);
        return productRepository.save(product);
    }
	
	
	// ➤ Update Product (Admin only)
    public Product updateProduct(Long id, ProductUpdateRequest request, MultipartFile file) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        // --- Image Handling Logic ---
        String imageUrl = product.getImageUrl();
        // 1. If a new file is uploaded, process it
        if (file != null && !file.isEmpty()) {
            imageUrl = fileStorageService.saveFile(file);
        }
        // 2. If the request explicitly removes the image (by setting imageUrl to null)
        else if (request.getImageUrl() == null) {
            imageUrl = null; // Clear image if requested
        }
        // Admin can update all fields
        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getStock() != null) product.setStock(request.getStock());
        if (request.getStatus() != null) product.setStatus(Status.valueOf(request.getStatus()));
        if (request.getImageUrl() != null) product.setImageUrl(imageUrl);

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
    	if (categoryId != null && categoryId == 0L) categoryId = null;
    	return productRepository.searchActiveProducts(search, categoryId, pageable);
    }
    
    
    //List Products (Admin)
    public Page<Product> listAllProducts(String search, Long categoryId, Pageable pageable){
    	
    	if (search == null) search = "";
    	return productRepository.searchAllProducts(search, categoryId, pageable);
    }
	
    
    public Product updateImage(Long id, String imageUrl) {
    	
    	Product product = productRepository.getReferenceById(id);
    	product.setImageUrl(imageUrl);
    	return productRepository.save(product);
    }
}
