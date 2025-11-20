package com.campusdelivery.service;

import com.campusdelivery.dao.AddressDao;
import com.campusdelivery.entity.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressDao addressDao;

    public void addAddress(Address address) {
        addressDao.addAddress(address);
    }

    public Address getAddressById(int addressId) {
        return addressDao.getAddressById(addressId);
    }

    public List<Address> getAddressesByUserId(int userId) {
        return addressDao.getAddressesByUserId(userId);
    }

    public void updateAddress(Address address) {
        addressDao.updateAddress(address);
    }

    public void deleteAddress(int addressId) {
        addressDao.deleteAddress(addressId);
    }
}
