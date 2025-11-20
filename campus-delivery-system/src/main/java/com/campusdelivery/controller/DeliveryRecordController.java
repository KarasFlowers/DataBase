package com.campusdelivery.controller;


import com.campusdelivery.entity.DeliveryRecord;
import com.campusdelivery.service.DeliveryRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deliveryrecords")
public class DeliveryRecordController {

    @Autowired
    private DeliveryRecordService deliveryRecordService;

    @PostMapping
    public ResponseEntity<String> addDeliveryRecord(@RequestBody DeliveryRecord deliveryRecord) {
        deliveryRecordService.addDeliveryRecord(deliveryRecord);
        return new ResponseEntity<>("Delivery record added successfully", HttpStatus.CREATED);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<DeliveryRecord> getDeliveryRecordByOrderId(@PathVariable int orderId) {
        DeliveryRecord deliveryRecord = deliveryRecordService.getDeliveryRecordByOrderId(orderId);
        if (deliveryRecord != null) {
            return new ResponseEntity<>(deliveryRecord, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping
    public ResponseEntity<String> updateDeliveryRecord(@RequestBody DeliveryRecord deliveryRecord) {
        deliveryRecordService.updateDeliveryRecord(deliveryRecord);
        return new ResponseEntity<>("Delivery record updated successfully", HttpStatus.OK);
    }
}
