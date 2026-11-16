package com.grocery.user.controller;

import com.grocery.auth.User;
import com.grocery.auth.UserService;
import com.grocery.common.ApiResponse;
import com.grocery.product.Product;
import com.grocery.user.WishlistRepository;
import com.grocery.user.entity.Wishlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistRepository wishlistRepository;
    @Autowired
    private UserService userService;

    @PostMapping("/addWishList/{productId}")
    public ResponseEntity<ApiResponse<String>> addToWishlist(
            @PathVariable Long productId,
            Authentication authentication){

        User user = userService.getByEmail(authentication.getName());
        Optional<Wishlist> existing = wishlistRepository.findByUserIdAndProductId(user.getId(), productId);

        if (existing.isPresent()){
            wishlistRepository.delete(existing.get());
            return ResponseEntity.ok(ApiResponse.success("Removed from wishlist", "REMOVED"));
        } else {
            Wishlist newItem = new Wishlist();
            newItem.setUser(user);
            // Fetch product
            Product product = new Product();
            product.setId(productId);
            newItem.setProduct(product);

            wishlistRepository.save(newItem);
            return ResponseEntity.ok(ApiResponse.success("Added to wishlist!","ADDED"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<Wishlist>>> getMyWishlist(Authentication authentication){
        User user = userService.getByEmail(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(null, wishlistRepository.findByUserId(user.getId())));
    }
}
