package com.campusdelivery.entity;

import java.math.BigDecimal;

public class OrderDetail {
    private int orderDetailId;
    private int orderId;
    private int dishId;
    private int quantity;
    private BigDecimal pricePerItem;

    public OrderDetail() {}

    // Getters and Setters
    public int getOrderDetailId() { return orderDetailId; }
    public void setOrderDetailId(int orderDetailId) { this.orderDetailId = orderDetailId; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getDishId() { return dishId; }
    public void setDishId(int dishId) { this.dishId = dishId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getPricePerItem() { return pricePerItem; }
    public void setPricePerItem(BigDecimal pricePerItem) { this.pricePerItem = pricePerItem; }
}
