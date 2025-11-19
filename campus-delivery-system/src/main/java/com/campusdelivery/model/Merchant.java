package com.campusdelivery.model;

import java.math.BigDecimal;

public class Merchant {
    private int merchantId;
    private String name;
    private String address;
    private String phoneNumber;
    private BigDecimal rating;

    public Merchant() {}

    // Getters and Setters
    public int getMerchantId() { return merchantId; }
    public void setMerchantId(int merchantId) { this.merchantId = merchantId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
}
