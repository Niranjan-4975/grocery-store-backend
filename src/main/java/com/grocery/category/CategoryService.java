package com.grocery.category;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
			throw new RuntimeException("Category Already Exists");
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
				() -> new RuntimeException("Category Not Found"));
		
		//✅ Use the new repository method that checks name against all IDs EXCEPT the current one.
        if (categoryRepository.existsByNameIgnoreCaseAndNotId(request.getName(), id)) {
            throw new RuntimeException("Category name already exists");
        }
		category.setName(request.getName());
		category.setDescription(request.getDescription());
		if (request.getStatus() != null)
			category.setStatus(request.getStatus());
		return categoryRepository.save(category);
	}
	
	// Admin: get all categories
	public List<Category> listAll(String search, Status status){
		
		if (search == null) search = "";
		return categoryRepository.searchCategories(search, status);
	}

	// Customer: get only Active categories
	public List<Category> listActive(){
		return categoryRepository.findByStatus(Status.ACTIVE);
	}

    @Transactional
    public void deleteCategory(Long id) {
        // Soft Delete: Sets status to DELETED
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Category Not Found for deletion"));
        category.setStatus(Status.DELETED); // Assuming Status enum includes DELETED
        categoryRepository.save(category);
    }
}





