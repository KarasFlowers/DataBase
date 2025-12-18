package com.campusdelivery.entity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map; // For top selling dishes

public class MerchantDashboardStats {
    private int merchantId;
    private String merchantName;
    private long totalOrders;
    private BigDecimal totalRevenue;
    private long ordersToday;
    private BigDecimal revenueToday;
    private List<Map<String, Object>> topSellingDishes; // List of maps, each map for a dish {name, count, revenue}

    // Constructor
    public MerchantDashboardStats() {
    }

    // Getters and Setters
    public int getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getOrdersToday() {
        return ordersToday;
    }

    public void setOrdersToday(long ordersToday) {
        this.ordersToday = ordersToday;
    }

    public BigDecimal getRevenueToday() {
        return revenueToday;
    }

    public void setRevenueToday(BigDecimal revenueToday) {
        this.revenueToday = revenueToday;
    }

    public List<Map<String, Object>> getTopSellingDishes() {
        return topSellingDishes;
    }

    public void setTopSellingDishes(List<Map<String, Object>> topSellingDishes) {
        this.topSellingDishes = topSellingDishes;
    }
}
