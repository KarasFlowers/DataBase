package com.campusdelivery.dao;

import com.campusdelivery.entity.Dish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DishDao {

    @Autowired
    private DataSource dataSource;

    public void addDish(Dish dish) {
        String sql = "INSERT INTO dishes (merchant_id, name, description, price, is_available) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dish.getMerchantId());
            pstmt.setString(2, dish.getName());
            pstmt.setString(3, dish.getDescription());
            pstmt.setBigDecimal(4, dish.getPrice());
            pstmt.setBoolean(5, dish.isAvailable());
            pstmt.executeUpdate();
            System.out.println("DAO: Successfully added dish " + dish.getName() + " for merchant ID " + dish.getMerchantId());
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to add dish " + dish.getName());
            e.printStackTrace();
        }
    }

    public Dish getDishById(int dishId) {
        String sql = "SELECT * FROM dishes WHERE dish_id = ?";
        Dish dish = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dishId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    dish = new Dish();
                    dish.setDishId(rs.getInt("dish_id"));
                    dish.setMerchantId(rs.getInt("merchant_id"));
                    dish.setName(rs.getString("name"));
                    dish.setDescription(rs.getString("description"));
                    dish.setPrice(rs.getBigDecimal("price"));
                    dish.setAvailable(rs.getBoolean("is_available"));
                    System.out.println("DAO: Found dish by ID " + dishId);
                } else {
                    System.out.println("DAO: No dish found with ID " + dishId);
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to get dish by ID " + dishId);
            e.printStackTrace();
        }
        return dish;
    }
    
    public List<Dish> getAllDishes() {
        String sql = "SELECT * FROM dishes";
        List<Dish> dishes = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Dish dish = new Dish();
                dish.setDishId(rs.getInt("dish_id"));
                dish.setMerchantId(rs.getInt("merchant_id"));
                dish.setName(rs.getString("name"));
                dish.setDescription(rs.getString("description"));
                dish.setPrice(rs.getBigDecimal("price"));
                dish.setAvailable(rs.getBoolean("is_available"));
                dishes.add(dish);
            }
            System.out.println("DAO: Retrieved " + dishes.size() + " dishes.");

        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to get all dishes.");
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
                    Dish dish = new Dish();
                    dish.setDishId(rs.getInt("dish_id"));
                    dish.setMerchantId(rs.getInt("merchant_id"));
                    dish.setName(rs.getString("name"));
                    dish.setDescription(rs.getString("description"));
                    dish.setPrice(rs.getBigDecimal("price"));
                    dish.setAvailable(rs.getBoolean("is_available"));
                    dishes.add(dish);
                }
                System.out.println("DAO: Retrieved " + dishes.size() + " dishes for merchant ID " + merchantId);
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to get dishes for merchant ID " + merchantId);
            e.printStackTrace();
        }
        return dishes;
    }

    public void updateDish(Dish dish) {
        String sql = "UPDATE dishes SET name = ?, description = ?, price = ?, is_available = ? WHERE dish_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dish.getName());
            pstmt.setString(2, dish.getDescription());
            pstmt.setBigDecimal(3, dish.getPrice());
            pstmt.setBoolean(4, dish.isAvailable());
            pstmt.setInt(5, dish.getDishId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("DAO: Successfully updated dish " + dish.getName());
            } else {
                System.out.println("DAO: No dish found with ID " + dish.getDishId() + " to update.");
            }
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
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("DAO: Successfully deleted dish with ID " + dishId);
            } else {
                System.out.println("DAO: No dish found with ID " + dishId + " to delete.");
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to delete dish with ID " + dishId);
            e.printStackTrace();
        }
    }
}