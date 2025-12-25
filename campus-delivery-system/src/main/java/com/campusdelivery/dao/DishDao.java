package com.campusdelivery.dao;

import com.campusdelivery.entity.Dish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DishDao {

    @Autowired
    private DataSource dataSource;

    public void addDish(Dish dish) {
        String sql = "INSERT INTO dishes (merchant_id, name, description, price, is_available, category_id, purchase_limit) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dish.getMerchantId());
            pstmt.setString(2, dish.getName());
            pstmt.setString(3, dish.getDescription());
            pstmt.setBigDecimal(4, dish.getPrice());
            pstmt.setBoolean(5, dish.isAvailable());
            pstmt.setObject(6, dish.getCategoryId(), Types.INTEGER);
            pstmt.setObject(7, dish.getPurchaseLimit(), Types.INTEGER);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to add dish " + dish.getName());
            e.printStackTrace();
        }
    }

    private Dish mapRowToDish(ResultSet rs) throws SQLException {
        Dish dish = new Dish();
        dish.setDishId(rs.getInt("dish_id"));
        dish.setMerchantId(rs.getInt("merchant_id"));
        dish.setName(rs.getString("name"));
        dish.setDescription(rs.getString("description"));
        dish.setPrice(rs.getBigDecimal("price"));
        dish.setAvailable(rs.getBoolean("is_available"));

        // Safely handle numeric type conversion from DB (can be Long or Integer)
        Object categoryIdObj = rs.getObject("category_id");
        if (categoryIdObj != null) {
            dish.setCategoryId(((Number) categoryIdObj).intValue());
        }

        Object purchaseLimitObj = rs.getObject("purchase_limit");
        if (purchaseLimitObj != null) {
            dish.setPurchaseLimit(((Number) purchaseLimitObj).intValue());
        }
        
        return dish;
    }

    public Dish getDishById(int dishId) {
        String sql = "SELECT * FROM dishes WHERE dish_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dishId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToDish(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Dish> getAllDishes() {
        String sql = "SELECT * FROM dishes";
        List<Dish> dishes = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                dishes.add(mapRowToDish(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dishes;
    }

    public List<Dish> getDishesByMerchantId(int merchantId) {
        List<Dish> dishes = new ArrayList<>();
        String sql = "SELECT * FROM dishes WHERE merchant_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, merchantId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    dishes.add(mapRowToDish(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dishes;
    }

    public void updateDish(Dish dish) {
        String sql = "UPDATE dishes SET name = ?, description = ?, price = ?, is_available = ?, category_id = ?, purchase_limit = ? WHERE dish_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dish.getName());
            pstmt.setString(2, dish.getDescription());
            pstmt.setBigDecimal(3, dish.getPrice());
            pstmt.setBoolean(4, dish.isAvailable());
            pstmt.setObject(5, dish.getCategoryId(), Types.INTEGER);
            pstmt.setObject(6, dish.getPurchaseLimit(), Types.INTEGER);
            pstmt.setInt(7, dish.getDishId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to update dish " + dish.getName());
            e.printStackTrace();
        }
    }

    public void deleteDish(int dishId) {
        String sql = "DELETE FROM dishes WHERE dish_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dishId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}