package com.campusdelivery.service;

import com.campusdelivery.dao.OrderDetailDao;
import com.campusdelivery.entity.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailService {

    @Autowired
    private OrderDetailDao orderDetailDao;

    public void addOrderDetail(OrderDetail detail) {
        orderDetailDao.addOrderDetail(detail);
    }

    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {
        return orderDetailDao.getOrderDetailsByOrderId(orderId);
    }
}
