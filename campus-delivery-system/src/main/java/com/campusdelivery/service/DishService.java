package com.campusdelivery.service;

import com.campusdelivery.dao.DishDao;
import com.campusdelivery.entity.Dish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishService {

    @Autowired
    private DishDao dishDao;

    public void addDish(Dish dish) {
        dishDao.addDish(dish);
    }

    public Dish getDishById(int dishId) {
        return dishDao.getDishById(dishId);
    }

    public List<Dish> getDishesByMerchantId(int merchantId) {
        return dishDao.getDishesByMerchantId(merchantId);
    }

    public void updateDish(Dish dish) {
        dishDao.updateDish(dish);
    }

    public void deleteDish(int dishId) {
        dishDao.deleteDish(dishId);
    }
}
