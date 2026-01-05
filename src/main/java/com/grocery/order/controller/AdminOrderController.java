package com.grocery.order.controller;

import com.grocery.common.ApiResponse;
import com.grocery.order.Order;
import com.grocery.order.OrderStatus;
import com.grocery.order.dto.BulkStatusRequest;
import com.grocery.order.repository.OrderRepository;
import com.grocery.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private OrderRepository orderRepository;


    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<Order>>> getAllOrdersForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Authentication authentication){
        Pageable pageable = PageRequest.of(page,size, Sort.by("orderDate").descending());
        Page<Order> data = (search.isBlank())
                ? orderService.findAllOrders(pageable)
                : orderRepository.searchOrders(search, pageable);
        return ResponseEntity.ok(ApiResponse.success(null, data));
    }

    // ADMIN: update one order status
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Order>> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status,
            Authentication authentication){
        Order updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Order status updated to " + status, updatedOrder));
    }

    // ADMIN: Update Orders Multiple Status
    @PatchMapping("/bulk-status")
    public ResponseEntity<ApiResponse<Void>> bulkUpdateStatus(
            @RequestBody BulkStatusRequest request,
            Authentication authentication){
        orderService.updateMultipleOrdersStatus(request.getIds(), request.getStatus());
        return ResponseEntity.ok(ApiResponse.success(request.getIds().size()+ " Orders updated Successfully", null));
    }

    @GetMapping("user/{id}")
    public ResponseEntity<ApiResponse<List<Order>>> getOrdersByUserId(
            @PathVariable Long id,
            Authentication authentication){
        List<Order> userOrders = orderService.getOrdersById(id);
        return ResponseEntity.ok(ApiResponse.success(null,userOrders));
    }
}
