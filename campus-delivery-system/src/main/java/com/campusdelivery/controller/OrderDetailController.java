package com.campusdelivery.controller;

import com.campusdelivery.entity.OrderDetail;
import com.campusdelivery.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orderdetails")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping
    public ResponseEntity<String> addOrderDetail(@RequestBody OrderDetail orderDetail) {
        orderDetailService.addOrderDetail(orderDetail);
        return new ResponseEntity<>("Order detail added successfully", HttpStatus.CREATED);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderDetail>> getOrderDetailsByOrderId(@PathVariable int orderId) {
        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailsByOrderId(orderId);
        return new ResponseEntity<>(orderDetails, HttpStatus.OK);
    }
}
