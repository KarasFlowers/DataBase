package com.campusdelivery.controller;

import com.campusdelivery.entity.DishCategory;
import com.campusdelivery.service.DishCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class DishCategoryController {

    @Autowired
    private DishCategoryService dishCategoryService;

    @PostMapping
    public ResponseEntity<String> addCategory(@RequestBody DishCategory category) {
        try {
            dishCategoryService.addCategory(category);
            return new ResponseEntity<>("Category added successfully", HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>("Category name already exists for this merchant.", HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<List<DishCategory>> getCategoriesByMerchantId(@PathVariable int merchantId) {
        List<DishCategory> categories = dishCategoryService.getCategoriesByMerchantId(merchantId);
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> updateCategory(@RequestBody DishCategory category) {
        dishCategoryService.updateCategory(category);
        return new ResponseEntity<>("Category updated successfully", HttpStatus.OK);
    }

    @PutMapping("/reorder")
    public ResponseEntity<String> updateCategoryOrder(@RequestBody List<Integer> categoryIds) {
        try {
            dishCategoryService.updateCategoryOrder(categoryIds);
            return new ResponseEntity<>("Category order updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update category order.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable int id) {
        dishCategoryService.deleteCategory(id);
        return new ResponseEntity<>("Category deleted successfully", HttpStatus.NO_CONTENT);
    }
}
