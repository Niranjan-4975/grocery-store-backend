package com.grocery.category;

import java.util.List;

import com.grocery.exception.CategoryAlreadyExistsException;
import com.grocery.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.grocery.category.dto.CategoryCreateRequest;
import com.grocery.category.dto.CategoryUpdateRequest;
import com.grocery.common.Status;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	// Create Category
	public Category createCategory(CategoryCreateRequest request) {
		
		if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
			throw new CategoryAlreadyExistsException("Category '"+request.getName()+"' Already Exists");
		}
		Category category = new Category();
		category.setName(request.getName());
		category.setDescription(request.getDescription());
		category.setStatus(request.getStatus() !=null ? request.getStatus() : Status.ACTIVE);
		return categoryRepository.save(category);
	}

	// Update Category
    @Transactional
	public Category updateCategory(Long id, CategoryUpdateRequest request) {
		
		Category category = categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Category not found with ID: " + id));

        if (categoryRepository.existsByNameIgnoreCaseAndNotId(request.getName(), id)) {
            throw new CategoryAlreadyExistsException("Category name '" + request.getName() + "' is already taken");
        }
		category.setName(request.getName());
		category.setDescription(request.getDescription());
		if (request.getStatus() != null)
			category.setStatus(request.getStatus());
		return categoryRepository.save(category);
	}
	
	// Admin: get all categories
	public Page<Category> listAll(String search, Status status, Pageable pageable){
		
		if (search == null) search = "";
		return categoryRepository.searchCategories(search, status, pageable);
	}

	// Customer: get only Active categories
	public List<Category> listActive(){
		return categoryRepository.findByStatus(Status.ACTIVE);
	}

    @Transactional
    public void deleteCategory(Long id) {
        // Soft Delete: Sets status to DELETED
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Cannot delete: Category not found with ID: " + id));
        category.setStatus(Status.DELETED); // Assuming Status enum includes DELETED
        categoryRepository.save(category);
    }
}





