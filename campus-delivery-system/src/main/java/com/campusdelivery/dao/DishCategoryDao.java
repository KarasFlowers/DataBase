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

    private int getMaxDisplayOrder(Connection conn, int merchantId) throws SQLException {
        String sql = "SELECT MAX(display_order) FROM dish_categories WHERE merchant_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, merchantId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public void addCategory(DishCategory category) {
        String sql = "INSERT INTO dish_categories (merchant_id, category_name, display_order) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection()) {
            int maxOrder = getMaxDisplayOrder(conn, category.getMerchantId());
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, category.getMerchantId());
                pstmt.setString(2, category.getCategoryName());
                pstmt.setInt(3, maxOrder + 1);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Error adding category, possible duplicate entry.", e);
        }
    }

    private DishCategory mapRowToCategory(ResultSet rs) throws SQLException {
        DishCategory category = new DishCategory();
        category.setCategoryId(rs.getInt("category_id"));
        category.setMerchantId(rs.getInt("merchant_id"));
        category.setCategoryName(rs.getString("category_name"));
        category.setDisplayOrder(rs.getInt("display_order"));
        return category;
    }

    public List<DishCategory> getCategoriesByMerchantId(int merchantId) {
        List<DishCategory> categories = new ArrayList<>();
        String sql = "SELECT * FROM dish_categories WHERE merchant_id = ? ORDER BY display_order ASC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, merchantId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapRowToCategory(rs));
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

    public void updateCategoryOrder(List<Integer> categoryIds) throws SQLException {
        String sql = "UPDATE dish_categories SET display_order = ? WHERE category_id = ?";
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false); // Start transaction
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < categoryIds.size(); i++) {
                    pstmt.setInt(1, i); // Set display_order to the index
                    pstmt.setInt(2, categoryIds.get(i));
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                conn.commit(); // Commit transaction
            } catch (SQLException e) {
                conn.rollback(); // Rollback on error
                throw e;
            }
        }
    }

    public void deleteCategory(int categoryId) {
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