package com.grocery.order.controller;

import com.grocery.auth.UserRepository;
import com.grocery.order.Order;
import com.grocery.order.OrderStatus;
import com.grocery.order.dto.BulkStatusRequest;
import com.grocery.order.repository.OrderRepository;
import com.grocery.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;


    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrdersForAdmin(Authentication authentication){
        return ResponseEntity.ok(orderService.findAllOrders());
    }

    // ADMIN: update one order status
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status,
            Authentication authentication){

        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    @PatchMapping("/bulk-status")
    public ResponseEntity<?> bulkUpdateStatus(
            @RequestBody BulkStatusRequest request,
            Authentication authentication){
        orderService.updateMultipleOrdersStatus(request.getIds(), request.getStatus());
        return ResponseEntity.ok("Orders updated Successfully");
    }
}
