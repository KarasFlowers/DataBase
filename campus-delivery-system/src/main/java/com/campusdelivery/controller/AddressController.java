package com.campusdelivery.controller;

import com.campusdelivery.entity.Address;
import com.campusdelivery.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping
    public ResponseEntity<String> addAddress(@RequestBody Address address) {
        addressService.addAddress(address);
        return new ResponseEntity<>("Address added successfully", HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Address> getAddressById(@PathVariable int id) {
        Address address = addressService.getAddressById(id);
        if (address != null) {
            return new ResponseEntity<>(address, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Address>> getAddressesByUserId(@PathVariable int userId) {
        List<Address> addresses = addressService.getAddressesByUserId(userId);
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> updateAddress(@RequestBody Address address) {
        addressService.updateAddress(address);
        return new ResponseEntity<>("Address updated successfully", HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAddress(@PathVariable int id) {
        addressService.deleteAddress(id);
        return new ResponseEntity<>("Address deleted successfully", HttpStatus.NO_CONTENT);
    }
}
