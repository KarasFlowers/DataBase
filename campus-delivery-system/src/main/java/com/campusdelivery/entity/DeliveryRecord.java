package com.campusdelivery.entity;

import java.util.Date;

public class DeliveryRecord {
    private int deliveryId;
    private int orderId;
    private int riderId;
    private String status;
    private Date pickupTime;
    private Date deliveryTime;

    public DeliveryRecord() {}

    // Getters and Setters
    public int getDeliveryId() { return deliveryId; }
    public void setDeliveryId(int deliveryId) { this.deliveryId = deliveryId; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getRiderId() { return riderId; }
    public void setRiderId(int riderId) { this.riderId = riderId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getPickupTime() { return pickupTime; }
    public void setPickupTime(Date pickupTime) { this.pickupTime = pickupTime; }
    public Date getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(Date deliveryTime) { this.deliveryTime = deliveryTime; }
}
