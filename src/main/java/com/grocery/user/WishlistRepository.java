package com.grocery.user;

import com.grocery.user.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    // to check product exist in wishlist or not
    Optional<Wishlist> findByUserIdAndProductId(Long userId, Long productId);
    // to fetch user wishlist
    List<Wishlist> findByUserId(Long userId);
    // to check exist with user and product
    boolean existsByUserIdAndProductId(Long userId, Long productId);
}
