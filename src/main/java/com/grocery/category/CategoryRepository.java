package com.grocery.category;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.grocery.common.Status;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{

	boolean existsByNameIgnoreCase(String name);

	//List only Active Categories (for customer)
	List<Category> findByStatus(Status status);


	// Search + Filter (for admin)
	@Query("""
			SELECT c FROM Category c
			WHERE
				(:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')))
			AND (:status IS NULL OR c.status = :status)
			""")
	List<Category> searchCategories(
			@Param("search") String search,
			@Param("status") Status status
	);

}
