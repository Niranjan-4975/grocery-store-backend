package com.grocery.product.repository;

import com.grocery.common.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.grocery.product.Product;

import java.util.List;

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

    //ADMIN: Stats and Reports
    //Spring Data JPA magic method (Automatic Query)
    List<Product> findByStockLessThan(Integer stock);

    // ✅ FEATURED: Home page featured section ke liye
    List<Product> findByIsFeaturedTrueAndStatus(Status status);

    // ✅ PUBLIC: Home page main grid ke liye (bina search ke)
    Page<Product> findByStatus(Status status, Pageable pageable);
}
