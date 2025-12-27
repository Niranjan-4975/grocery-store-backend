package com.grocery.order.repository;

import com.grocery.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    // Advanced Admin Search
    @Query("SELECT o FROM Order o WHERE " +
            "(:search IS NULL OR LOWER(o.user.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "CAST(o.id AS string) LIKE CONCAT('%', :search, '%'))")
    Page<Order> searchOrders(@Param("search") String search, Pageable pageable);

    // ADMIN: For Stats and Reports
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status != 'CANCELLED'")
    long countActiveOrders();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'PENDING'")
    long countPendingOrders();

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'DELIVERED'")
    Double calculateTotalRevenue();


    // Standard Range Fix (7, 15, 30 days)
    @Query(value = "SELECT CAST(order_date AS DATE) as date, SUM(total_amount) as amount " +
            "FROM orders WHERE order_date >= CURRENT_DATE - (:days * INTERVAL '1 day') " +
            "AND status = 'DELIVERED' GROUP BY CAST(order_date AS DATE) ORDER BY date",
            nativeQuery = true)
    List<Object[]> getSalesTrend(@Param("days") Integer days);

    // Custom Range Fix (Start to End)
    @Query(value = "SELECT CAST(order_date AS DATE) as date, SUM(total_amount) as amount " +
            "FROM orders WHERE CAST(order_date AS DATE) BETWEEN CAST(:start AS DATE) AND CAST(:end AS DATE) " +
            "AND status = 'DELIVERED' GROUP BY CAST(order_date AS DATE) ORDER BY date",
            nativeQuery = true)
    List<Object[]> getSalesTrendCustom(@Param("start") String start, @Param("end") String end);


    // Best Seller: Top 5 Products
    @Query(value = "SELECT product_name, SUM(quantity) as total_qty " +
            "FROM order_items GROUP BY product_name ORDER BY total_qty DESC LIMIT 5",
            nativeQuery = true)
    List<Object[]> getBestSellers();
}
