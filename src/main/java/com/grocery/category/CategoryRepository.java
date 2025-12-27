package com.grocery.category;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.grocery.common.Status;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{

    // Check if any category exists with this name, but NOT this ID
    @Query("""
            SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END 
            FROM Category c 
            WHERE LOWER(c.name) = LOWER(:name) AND c.id <> :id
            """)
    boolean existsByNameIgnoreCaseAndNotId(@Param("name") String name, @Param("id") Long id);

    boolean existsByNameIgnoreCase(String name);
	//List only Active Categories (for customer)
	List<Category> findByStatus(Status status);

	// Search + Filter (for admin)
	@Query("""
			SELECT c FROM Category c
			WHERE
				(:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')))
			AND (:status IS NULL OR c.status = :status)
			AND c.status <> com.grocery.common.Status.DELETED
			""")
    Page<Category> searchCategories(
			@Param("search") String search,
			@Param("status") Status status,
            Pageable pageable
	);

}
