package com.campusdelivery.controller;

import com.campusdelivery.entity.Rider;
import com.campusdelivery.service.RiderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/riders")
public class RiderController {

    @Autowired
    private RiderService riderService;

    @PostMapping
    public ResponseEntity<String> addRider(@RequestBody Rider rider) {
        riderService.addRider(rider);
        return new ResponseEntity<>("Rider added successfully", HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rider> getRiderById(@PathVariable int id) {
        Rider rider = riderService.getRiderById(id);
        if (rider != null) {
            return new ResponseEntity<>(rider, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public ResponseEntity<List<Rider>> getAllRiders() {
        List<Rider> riders = riderService.getAllRiders();
        return new ResponseEntity<>(riders, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> updateRider(@RequestBody Rider rider) {
        riderService.updateRider(rider);
        return new ResponseEntity<>("Rider updated successfully", HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRider(@PathVariable int id) {
        riderService.deleteRider(id);
        return new ResponseEntity<>("Rider deleted successfully", HttpStatus.NO_CONTENT);
    }
}
