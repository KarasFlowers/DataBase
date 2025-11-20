package com.campusdelivery.entity;

public class Address {
    private int addressId;
    private int userId;
    private String addressDetails;
    private boolean isDefault;

    public Address() {}

    // Getters and Setters
    public int getAddressId() { return addressId; }
    public void setAddressId(int addressId) { this.addressId = addressId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getAddressDetails() { return addressDetails; }
    public void setAddressDetails(String addressDetails) { this.addressDetails = addressDetails; }
    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }
}
