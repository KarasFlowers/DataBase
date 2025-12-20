package com.campusdelivery.service;

import com.campusdelivery.dao.DishCategoryDao;
import com.campusdelivery.entity.DishCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishCategoryService {

    @Autowired
    private DishCategoryDao dishCategoryDao;

    public void addCategory(DishCategory category) {
        dishCategoryDao.addCategory(category);
    }

    public List<DishCategory> getCategoriesByMerchantId(int merchantId) {
        return dishCategoryDao.getCategoriesByMerchantId(merchantId);
    }

    public void updateCategory(DishCategory category) {
        dishCategoryDao.updateCategory(category);
    }

    public void deleteCategory(int categoryId) {
        dishCategoryDao.deleteCategory(categoryId);
    }
}
