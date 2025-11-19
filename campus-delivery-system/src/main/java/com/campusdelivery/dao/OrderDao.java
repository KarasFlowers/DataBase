package com.campusdelivery.dao;

import com.campusdelivery.model.Order;
import java.util.List;

public class OrderDao {

    public int createOrder(Order order) {
        // TODO: Implement SQL INSERT and return generated order_id
        System.out.println("DAO: Creating order for user " + order.getUserId());
        return 0; // Return the new order ID
    }

    public Order getOrderById(int orderId) {
        // TODO: Implement SQL SELECT by ID
        System.out.println("DAO: Getting order by ID " + orderId);
        return null;
    }

    public List<Order> getOrdersByUserId(int userId) {
        // TODO: Implement SQL SELECT by user ID
        System.out.println("DAO: Getting orders for user " + userId);
        return null;
    }
    
    public List<Order> getOrdersByMerchantId(int merchantId) {
        // TODO: Implement SQL SELECT by merchant ID
        System.out.println("DAO: Getting orders for merchant " + merchantId);
        return null;
    }

    public void updateOrderStatus(int orderId, String status) {
        // TODO: Implement SQL UPDATE for status
        System.out.println("DAO: Updating order status for " + orderId);
    }
}
