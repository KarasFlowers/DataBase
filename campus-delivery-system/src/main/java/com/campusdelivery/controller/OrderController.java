package com.campusdelivery.controller;

import com.campusdelivery.entity.Order;
import com.campusdelivery.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable int userId, @RequestParam(required = false) Integer merchantId) {
        List<Order> orders = orderService.getOrdersByUserId(userId, merchantId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/bydate")
    public ResponseEntity<List<Order>> getOrdersByUserAndDateRange(
            @PathVariable int userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate) {
        List<Order> orders = orderService.getOrdersByUserAndDateRange(userId, startDate, endDate);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/to-review/count")
    public ResponseEntity<Integer> countUnreviewedCompletedOrders(@PathVariable int userId) {
        int count = orderService.countUnreviewedCompletedOrders(userId);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/to-review")
    public ResponseEntity<List<Order>> getUnreviewedCompletedOrders(@PathVariable int userId) {
        List<Order> orders = orderService.getUnreviewedCompletedOrders(userId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<List<Order>> getOrdersByMerchantId(@PathVariable int merchantId) {
        List<Order> orders = orderService.getOrdersByMerchantId(merchantId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/merchant/{merchantId}/count")
    public ResponseEntity<Integer> countOrdersByMerchantIdAndStatus(
            @PathVariable int merchantId,
            @RequestParam(required = false) String status) {
        int count = orderService.countOrdersByMerchantIdAndStatus(merchantId, status);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Order>> getAvailableOrders() {
        List<Order> orders = orderService.getAvailableOrdersForRider();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PutMapping("/{orderId}/status/{status}")
    public ResponseEntity<String> updateOrderStatus(@PathVariable int orderId, @PathVariable String status) {
        orderService.updateOrderStatus(orderId, status);
        return new ResponseEntity<>("Order status updated successfully", HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable int id) {
        orderService.deleteOrder(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
