package com.grocery.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.grocery.product.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
	
	
	// BASIC Search + Category filter + Status = ACTIVE
    @Query("""
        SELECT p FROM Product p
        WHERE
            (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
        AND (:categoryId IS NULL OR p.category.id = :categoryId)
        AND p.status = com.grocery.common.Status.ACTIVE
   """)
    Page<Product> searchActiveProducts(
            @Param("search") String search,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );
    
    
    // ADMIN: All products (both ACTIVE & INACTIVE)
    @Query("""
        SELECT p FROM Product p
        WHERE 
            (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
        AND (:categoryId IS NULL OR p.category.id = :categoryId)
    """)
    Page<Product> searchAllProducts(
            @Param("search") String search,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );
}
