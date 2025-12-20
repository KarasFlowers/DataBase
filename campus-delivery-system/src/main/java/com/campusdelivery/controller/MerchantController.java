package com.campusdelivery.controller;

import com.campusdelivery.entity.Merchant;
import com.campusdelivery.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchants")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    @PostMapping
    public ResponseEntity<String> addMerchant(@RequestBody Merchant merchant) {
        merchantService.addMerchant(merchant);
        return new ResponseEntity<>("Merchant added successfully", HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Merchant> getMerchantById(@PathVariable int id) {
        Merchant merchant = merchantService.getMerchantById(id);
        if (merchant != null) {
            return new ResponseEntity<>(merchant, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public ResponseEntity<List<Merchant>> getAllMerchants(
            @RequestParam(required = false, defaultValue = "default") String sortBy) {
        List<Merchant> merchants = merchantService.getAllMerchants(sortBy);
        return new ResponseEntity<>(merchants, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> updateMerchant(@RequestBody Merchant merchant) {
        merchantService.updateMerchant(merchant);
        return new ResponseEntity<>("Merchant updated successfully", HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMerchant(@PathVariable int id) {
        merchantService.deleteMerchant(id);
        return new ResponseEntity<>("Merchant deleted successfully", HttpStatus.NO_CONTENT);
    }
}
