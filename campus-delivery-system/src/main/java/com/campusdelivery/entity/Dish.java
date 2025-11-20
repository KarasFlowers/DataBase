package com.campusdelivery.entity;

import java.math.BigDecimal;

public class Dish {
    private int dishId;
    private int merchantId;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean isAvailable;

    public Dish() {}

    // Getters and Setters
    public int getDishId() { return dishId; }
    public void setDishId(int dishId) { this.dishId = dishId; }
    public int getMerchantId() { return merchantId; }
    public void setMerchantId(int merchantId) { this.merchantId = merchantId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean isAvailable) { this.isAvailable = isAvailable; }
}
