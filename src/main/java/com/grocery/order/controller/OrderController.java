package com.grocery.order.controller;

import com.grocery.auth.User;
import com.grocery.auth.UserRepository;
import com.grocery.order.Order;
import com.grocery.order.dto.OrderRequest;
import com.grocery.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserRepository userRepository;


    // CUSTOMER: Place a new order
    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(
            @RequestBody OrderRequest request,
            Authentication authentication){
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        Order order = orderService.placeOrder(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<Order>> getMyOrders(Authentication authentication){
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return ResponseEntity.ok(orderService.myOrders(user.getId()));
    }
}
