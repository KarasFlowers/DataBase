package com.campusdelivery.entity;

import java.math.BigDecimal;
import java.sql.Time;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Merchant {
    private int merchantId;
    private String name;
    private String address;
    private String phoneNumber;
    private BigDecimal rating;
    private int salesCount;
    private BigDecimal averageOrderPrice;
    private Time openTime;
    private Time closeTime;
    private boolean isManuallyClosed;
    private boolean isOpen; // This will be calculated by the DAO

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
    public int getSalesCount() { return salesCount; }
    public void setSalesCount(int salesCount) { this.salesCount = salesCount; }
    public BigDecimal getAverageOrderPrice() { return averageOrderPrice; }
    public void setAverageOrderPrice(BigDecimal averageOrderPrice) { this.averageOrderPrice = averageOrderPrice; }
    public Time getOpenTime() { return openTime; }
    public void setOpenTime(Time openTime) { this.openTime = openTime; }
    public Time getCloseTime() { return closeTime; }
    public void setCloseTime(Time closeTime) { this.closeTime = closeTime; }
    @JsonProperty("isManuallyClosed") // Ensure JSON property name is exact
    public boolean isManuallyClosed() { return isManuallyClosed; }
    public void setManuallyClosed(boolean manuallyClosed) { this.isManuallyClosed = manuallyClosed; }
    public boolean isOpen() { return isOpen; }
    public void setOpen(boolean open) { isOpen = open; }
}
