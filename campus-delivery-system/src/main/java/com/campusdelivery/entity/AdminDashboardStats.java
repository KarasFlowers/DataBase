package com.campusdelivery.entity;

import java.math.BigDecimal;

public class AdminDashboardStats {
    private long totalUsers;
    private long totalAdminUsers; // New field
    private long totalCustomerUsers; // New field
    private long totalMerchantUsersByRole; // New field
    private long totalRiderUsersByRole; // New field
    private long totalMerchants; // Entity count
    private long totalRiders; // Entity count
    private long totalOrders;
    private BigDecimal totalRevenue;
    private long ordersToday;
    private BigDecimal revenueToday;

    // Getters and Setters
    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    // New Getters and Setters
    public long getTotalAdminUsers() { return totalAdminUsers; }
    public void setTotalAdminUsers(long totalAdminUsers) { this.totalAdminUsers = totalAdminUsers; }
    public long getTotalCustomerUsers() { return totalCustomerUsers; }
    public void setTotalCustomerUsers(long totalCustomerUsers) { this.totalCustomerUsers = totalCustomerUsers; }
    public long getTotalMerchantUsersByRole() { return totalMerchantUsersByRole; }
    public void setTotalMerchantUsersByRole(long totalMerchantUsersByRole) { this.totalMerchantUsersByRole = totalMerchantUsersByRole; }
    public long getTotalRiderUsersByRole() { return totalRiderUsersByRole; }
    public void setTotalRiderUsersByRole(long totalRiderUsersByRole) { this.totalRiderUsersByRole = totalRiderUsersByRole; }

    public long getTotalMerchants() {
        return totalMerchants;
    }

    public void setTotalMerchants(long totalMerchants) {
        this.totalMerchants = totalMerchants;
    }

    public long getTotalRiders() {
        return totalRiders;
    }

    public void setTotalRiders(long totalRiders) {
        this.totalRiders = totalRiders;
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
}
