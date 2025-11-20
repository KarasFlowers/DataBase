package com.campusdelivery.controller;

import com.campusdelivery.entity.Dish;
import com.campusdelivery.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dishes")
public class DishController {

    @Autowired
    private DishService dishService;

    @PostMapping
    public ResponseEntity<String> addDish(@RequestBody Dish dish) {
        dishService.addDish(dish);
        return new ResponseEntity<>("Dish added successfully", HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dish> getDishById(@PathVariable int id) {
        Dish dish = dishService.getDishById(id);
        if (dish != null) {
            return new ResponseEntity<>(dish, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<List<Dish>> getDishesByMerchantId(@PathVariable int merchantId) {
        List<Dish> dishes = dishService.getDishesByMerchantId(merchantId);
        return new ResponseEntity<>(dishes, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> updateDish(@RequestBody Dish dish) {
        dishService.updateDish(dish);
        return new ResponseEntity<>("Dish updated successfully", HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDish(@PathVariable int id) {
        dishService.deleteDish(id);
        return new ResponseEntity<>("Dish deleted successfully", HttpStatus.NO_CONTENT);
    }
}
