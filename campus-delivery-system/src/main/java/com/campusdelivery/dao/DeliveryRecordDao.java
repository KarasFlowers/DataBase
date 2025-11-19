package com.campusdelivery.dao;

import com.campusdelivery.model.DeliveryRecord;

public class DeliveryRecordDao {

    public void addDeliveryRecord(DeliveryRecord record) {
        // TODO: Implement SQL INSERT
        System.out.println("DAO: Adding delivery record for order " + record.getOrderId());
    }

    public DeliveryRecord getDeliveryRecordByOrderId(int orderId) {
        // TODO: Implement SQL SELECT by order ID
        System.out.println("DAO: Getting delivery record for order " + orderId);
        return null;
    }

    public void updateDeliveryRecord(DeliveryRecord record) {
        // TODO: Implement SQL UPDATE
        System.out.println("DAO: Updating delivery record for order " + record.getOrderId());
    }
}
