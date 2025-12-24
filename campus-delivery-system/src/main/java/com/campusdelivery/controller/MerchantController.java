package com.campusdelivery.controller;

import com.campusdelivery.entity.Merchant;
import com.campusdelivery.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.util.List;
import java.util.Map;

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

    // Corrected endpoint with filtering and sorting
    @GetMapping
    public ResponseEntity<List<Merchant>> getAllMerchants(
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "default") String sortBy,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice) {
        List<Merchant> merchants = merchantService.getAllMerchants(name, sortBy, minPrice, maxPrice);
        return new ResponseEntity<>(merchants, HttpStatus.OK);
    }

    // New search endpoint
    @GetMapping("/search")
    public ResponseEntity<List<Merchant>> searchMerchants(@RequestParam String q) {
        List<Merchant> merchants = merchantService.search(q);
        return new ResponseEntity<>(merchants, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> updateMerchant(@RequestBody Merchant merchant) {
        merchantService.updateMerchant(merchant);
        return new ResponseEntity<>("Merchant updated successfully", HttpStatus.OK);
    }

    @PutMapping("/{id}/hours")
    public ResponseEntity<String> updateBusinessHours(@PathVariable int id, @RequestBody Map<String, String> hours) {
        try {
            Time openTime = Time.valueOf(hours.get("openTime") + ":00");
            Time closeTime = Time.valueOf(hours.get("closeTime") + ":00");
            merchantService.updateBusinessHours(id, openTime, closeTime);
            return new ResponseEntity<>("Business hours updated successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid time format. Please use HH:mm", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateManualStatus(@PathVariable int id, @RequestBody Map<String, Boolean> status) {
        Boolean isClosed = status.get("isClosed");
        if (isClosed == null) {
            return new ResponseEntity<>("isClosed field is required.", HttpStatus.BAD_REQUEST);
        }
        merchantService.updateManualStatus(id, isClosed);
        return new ResponseEntity<>("Manual status updated successfully", HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMerchant(@PathVariable int id) {
        merchantService.deleteMerchant(id);
        return new ResponseEntity<>("Merchant deleted successfully", HttpStatus.NO_CONTENT);
    }
}