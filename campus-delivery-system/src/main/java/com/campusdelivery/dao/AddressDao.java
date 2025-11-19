package com.campusdelivery.dao;

import com.campusdelivery.model.Address;
import java.util.List;

public class AddressDao {

    public void addAddress(Address address) {
        // TODO: Implement SQL INSERT
        System.out.println("DAO: Adding address for user " + address.getUserId());
    }

    public Address getAddressById(int addressId) {
        // TODO: Implement SQL SELECT by ID
        System.out.println("DAO: Getting address by ID " + addressId);
        return null;
    }

    public List<Address> getAddressesByUserId(int userId) {
        // TODO: Implement SQL SELECT by user ID
        System.out.println("DAO: Getting addresses for user " + userId);
        return null;
    }

    public void updateAddress(Address address) {
        // TODO: Implement SQL UPDATE
        System.out.println("DAO: Updating address " + address.getAddressId());
    }

    public void deleteAddress(int addressId) {
        // TODO: Implement SQL DELETE
        System.out.println("DAO: Deleting address " + addressId);
    }
}
