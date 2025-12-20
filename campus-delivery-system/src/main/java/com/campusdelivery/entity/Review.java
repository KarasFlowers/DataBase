package com.campusdelivery.entity;

import java.util.Date;

public class Review {
    private int reviewId;
    private int orderId;
    private int userId;
    private int rating;
    private String comment;
    private Date reviewTime;
    private Date lastModifiedTime;
    private boolean isSeenByMerchant;
    private String username; // To hold the username when joining tables
    private String merchantName; // To hold the merchant name when joining tables
    private int merchantId; // To hold the merchant id when joining tables

    public Review() {}

    // Getters and Setters
    public int getReviewId() { return reviewId; }
    public void setReviewId(int reviewId) { this.reviewId = reviewId; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Date getReviewTime() { return reviewTime; }
    public void setReviewTime(Date reviewTime) { this.reviewTime = reviewTime; }
    public Date getLastModifiedTime() { return lastModifiedTime; }
    public void setLastModifiedTime(Date lastModifiedTime) { this.lastModifiedTime = lastModifiedTime; }
    public boolean isSeenByMerchant() { return isSeenByMerchant; }
    public void setSeenByMerchant(boolean seenByMerchant) { isSeenByMerchant = seenByMerchant; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }
    public int getMerchantId() { return merchantId; }
    public void setMerchantId(int merchantId) { this.merchantId = merchantId; }
}
