package com.campusdelivery.dao;

import com.campusdelivery.model.Dish;
import java.util.List;

public class DishDao {

    public void addDish(Dish dish) {
        // TODO: Implement SQL INSERT
        System.out.println("DAO: Adding dish " + dish.getName());
    }

    public Dish getDishById(int dishId) {
        // TODO: Implement SQL SELECT by ID
        System.out.println("DAO: Getting dish by ID " + dishId);
        return null;
    }

    public List<Dish> getDishesByMerchantId(int merchantId) {
        // TODO: Implement SQL SELECT by merchant ID
        System.out.println("DAO: Getting dishes for merchant " + merchantId);
        return null;
    }

    public void updateDish(Dish dish) {
        // TODO: Implement SQL UPDATE
        System.out.println("DAO: Updating dish " + dish.getDishId());
    }

    public void deleteDish(int dishId) {
        // TODO: Implement SQL DELETE
        System.out.println("DAO: Deleting dish " + dishId);
    }
}
