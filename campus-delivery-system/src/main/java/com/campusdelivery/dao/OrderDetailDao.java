package com.campusdelivery.dao;

import com.campusdelivery.model.OrderDetail;
import java.util.List;

public class OrderDetailDao {

    public void addOrderDetail(OrderDetail detail) {
        // TODO: Implement SQL INSERT
        System.out.println("DAO: Adding detail for order " + detail.getOrderId());
    }

    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {
        // TODO: Implement SQL SELECT by order ID
        System.out.println("DAO: Getting details for order " + orderId);
        return null;
    }
}
