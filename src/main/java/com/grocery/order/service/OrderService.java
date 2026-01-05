package com.grocery.order.service;

import com.grocery.auth.User;
import com.grocery.exception.InsufficientStockException;
import com.grocery.exception.ResourceNotFoundException;
import com.grocery.order.Order;
import com.grocery.order.OrderItem;
import com.grocery.order.OrderStatus;
import com.grocery.order.dto.OrderRequest;
import com.grocery.order.repository.OrderItemRepository;
import com.grocery.order.repository.OrderRepository;
import com.grocery.product.Product;
import com.grocery.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ProductRepository productRepository;


    // CUSTOMER: Place an order
    @Transactional
    public Order placeOrder(OrderRequest request, User user) {

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(request.getShippingAddress());
        order.setPaymentMethod(request.getPaymentMethod());

        double totalAmount = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        //1. Process each item in the request
        for (OrderRequest.OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + itemReq.getProductId()));
            //2. Check and update stock
            if (product.getStock() < itemReq.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName()
                +". Available: "+ product.getStock());
            }
            product.setStock(product.getStock() - itemReq.getQuantity());
            productRepository.save(product);
            //3. Create OrderItem (Snap of price/name)
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setPrice(product.getPrice());

            totalAmount += product.getPrice() * itemReq.getQuantity();
            orderItems.add(orderItem);
        }
        order.setTotalAmount(totalAmount);
        //4. Save order first to generate ID, then save items
        Order savedOrder = orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);

        return savedOrder;
    }

    // Customer: view my own order history
    public List<Order> getOrdersById(Long id) {
        return orderRepository.findByUserId(id);
    }

    // ADMIN: Find all orders
    public Page<Order> findAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    // ADMIN: Update Order Status
    public Order updateOrderStatus(Long id,OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: "+id));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    // ADMIN: Update Multiple Orders Status
    @Transactional
    public void updateMultipleOrdersStatus(List<Long> ids, OrderStatus status){
        List<Order> orders = orderRepository.findAllById(ids);
        orders.forEach(order -> order.setStatus(status));
        orderRepository.saveAll(orders);
    }
}
