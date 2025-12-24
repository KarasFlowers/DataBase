package com.campusdelivery.service;

import com.campusdelivery.dao.DishCategoryDao;
import com.campusdelivery.entity.DishCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(rollbackFor = Exception.class)
    public void updateCategoryOrder(List<Integer> categoryIds) throws Exception {
        try {
            dishCategoryDao.updateCategoryOrder(categoryIds);
        } catch (Exception e) {
            throw new Exception("Failed to update category order.", e);
        }
    }

    public void deleteCategory(int categoryId) {
        dishCategoryDao.deleteCategory(categoryId);
    }
}
