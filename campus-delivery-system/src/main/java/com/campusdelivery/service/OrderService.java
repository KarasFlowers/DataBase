package com.campusdelivery.service;

import com.campusdelivery.dao.OrderDao;
import com.campusdelivery.entity.Merchant;
import com.campusdelivery.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private MerchantService merchantService;

    public int createOrder(Order order) {
        // Check if merchant is open before creating order
        Merchant merchant = merchantService.getMerchantById(order.getMerchantId());
        if (merchant == null || !merchant.isOpen()) {
            throw new IllegalStateException("商家已打烊，无法下单。");
        }
        return orderDao.createOrder(order);
    }

    public Order getOrderById(int orderId) {
        return orderDao.getOrderById(orderId);
    }

    public List<Order> getOrdersByUserId(int userId, Integer merchantId) {
        return orderDao.getOrdersByUserId(userId, merchantId);
    }

    public List<Order> getOrdersByMerchantId(int merchantId) {
        return orderDao.getOrdersByMerchantId(merchantId);
    }

    public List<Order> getAllOrders() {
        return orderDao.getAllOrders();
    }

    public void updateOrderStatus(int orderId, String status) {
        orderDao.updateOrderStatus(orderId, status);
    }

    public List<Order> getOrdersByUserAndDateRange(int userId, java.util.Date startDate, java.util.Date endDate) {
        return orderDao.getOrdersByUserAndDateRange(userId, startDate, endDate);
    }

    public int countOrdersByMerchantIdAndStatus(int merchantId, String status) {
        return orderDao.countOrdersByMerchantIdAndStatus(merchantId, status);
    }

    public List<Order> getAvailableOrdersForRider() {
        return orderDao.getOrdersByStatus("ready_for_pickup");
    }

    public void deleteOrder(int orderId) {
        orderDao.deleteOrder(orderId);
    }

    public int countUnreviewedCompletedOrders(int userId) {
        return orderDao.countUnreviewedCompletedOrders(userId);
    }

    public List<Order> getUnreviewedCompletedOrders(int userId) {
        return orderDao.getUnreviewedCompletedOrders(userId);
    }
}
