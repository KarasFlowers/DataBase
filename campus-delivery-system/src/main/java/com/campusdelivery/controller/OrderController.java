package com.campusdelivery.controller;

import com.campusdelivery.entity.Order;
import com.campusdelivery.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody Order order) {
        int orderId = orderService.createOrder(order);
        if (orderId > 0) {
            return new ResponseEntity<>("Order created successfully with ID: " + orderId, HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Failed to create order", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable int id) {
        Order order = orderService.getOrderById(id);
        if (order != null) {
            return new ResponseEntity<>(order, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable int userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<List<Order>> getOrdersByMerchantId(@PathVariable int merchantId) {
        List<Order> orders = orderService.getOrdersByMerchantId(merchantId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PutMapping("/{orderId}/status/{status}")
    public ResponseEntity<String> updateOrderStatus(@PathVariable int orderId, @PathVariable String status) {
        orderService.updateOrderStatus(orderId, status);
        return new ResponseEntity<>("Order status updated successfully", HttpStatus.OK);
    }
}
