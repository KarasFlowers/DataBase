package com.campusdelivery.service;

import com.campusdelivery.dao.OrderDao;
import com.campusdelivery.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    public int createOrder(Order order) {
        return orderDao.createOrder(order);
    }

    public Order getOrderById(int orderId) {
        return orderDao.getOrderById(orderId);
    }

    public List<Order> getOrdersByUserId(int userId) {
        return orderDao.getOrdersByUserId(userId);
    }

    public List<Order> getOrdersByMerchantId(int merchantId) {
        return orderDao.getOrdersByMerchantId(merchantId);
    }

    public void updateOrderStatus(int orderId, String status) {
        orderDao.updateOrderStatus(orderId, status);
    }
}
