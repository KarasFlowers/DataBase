package com.campusdelivery.entity;

import java.math.BigDecimal;
import java.util.Date;

public class Order {
    private int orderId;
    private int userId;
    private int merchantId;
    private int addressId;
    private Date orderTime;
    private BigDecimal totalPrice;
    private String status;

    // Fields for joined data
    private String username;
    private String merchantName;
    private String addressDetails;


    public Order() {}

    // Getters and Setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getMerchantId() { return merchantId; }
    public void setMerchantId(int merchantId) { this.merchantId = merchantId; }
    public int getAddressId() { return addressId; }
    public void setAddressId(int addressId) { this.addressId = addressId; }
    public Date getOrderTime() { return orderTime; }
    public void setOrderTime(Date orderTime) { this.orderTime = orderTime; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }
    public String getAddressDetails() { return addressDetails; }
    public void setAddressDetails(String addressDetails) { this.addressDetails = addressDetails; }
}
