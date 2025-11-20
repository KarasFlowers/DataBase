package com.campusdelivery.service;

import com.campusdelivery.dao.DeliveryRecordDao;
import com.campusdelivery.entity.DeliveryRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryRecordService {

    @Autowired
    private DeliveryRecordDao deliveryRecordDao;

    public void addDeliveryRecord(DeliveryRecord record) {
        deliveryRecordDao.addDeliveryRecord(record);
    }

    public DeliveryRecord getDeliveryRecordByOrderId(int orderId) {
        return deliveryRecordDao.getDeliveryRecordByOrderId(orderId);
    }

    public void updateDeliveryRecord(DeliveryRecord record) {
        deliveryRecordDao.updateDeliveryRecord(record);
    }
}
