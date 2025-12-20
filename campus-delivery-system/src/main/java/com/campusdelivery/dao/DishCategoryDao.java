package com.campusdelivery.dao;

import com.campusdelivery.entity.DishCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DishCategoryDao {

    @Autowired
    private DataSource dataSource;

    public void addCategory(DishCategory category) {
        String sql = "INSERT INTO dish_categories (merchant_id, category_name) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, category.getMerchantId());
            pstmt.setString(2, category.getCategoryName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            // Re-throw as a Spring-translatable exception
            throw new DataIntegrityViolationException("Error adding category, possible duplicate entry.", e);
        }
    }

    public List<DishCategory> getCategoriesByMerchantId(int merchantId) {
        List<DishCategory> categories = new ArrayList<>();
        String sql = "SELECT * FROM dish_categories WHERE merchant_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, merchantId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DishCategory category = new DishCategory();
                    category.setCategoryId(rs.getInt("category_id"));
                    category.setMerchantId(rs.getInt("merchant_id"));
                    category.setCategoryName(rs.getString("category_name"));
                    categories.add(category);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public void updateCategory(DishCategory category) {
        String sql = "UPDATE dish_categories SET category_name = ? WHERE category_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category.getCategoryName());
            pstmt.setInt(2, category.getCategoryId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCategory(int categoryId) {
        // The foreign key in `dishes` is ON DELETE SET NULL,
        // so dishes in this category will become uncategorized.
        String sql = "DELETE FROM dish_categories WHERE category_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, categoryId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}