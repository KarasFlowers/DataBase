package com.campusdelivery.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class AnalyticsDao {

    @Autowired
    private DataSource dataSource;

    // Generic method to get a single count (long)
    private long getCount(String query) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Generic method to get a single sum (BigDecimal)
    private BigDecimal getSum(String query) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                BigDecimal sum = rs.getBigDecimal(1);
                return sum == null ? BigDecimal.ZERO : sum;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
    
    public long getTotalUsers() {
        return getCount("SELECT COUNT(*) FROM users");
    }

    public long getTotalMerchants() {
        return getCount("SELECT COUNT(*) FROM merchants");
    }

    public long getTotalRiders() {
        return getCount("SELECT COUNT(*) FROM riders");
    }

    public long getTotalOrders() {
        return getCount("SELECT COUNT(*) FROM orders");
    }

    public BigDecimal getTotalRevenue() {
        return getSum("SELECT SUM(total_price) FROM orders WHERE status = 'completed'");
    }

    public long getOrdersToday() {
        return getCount("SELECT COUNT(*) FROM orders WHERE DATE(order_time) = CURDATE()");
    }

    public BigDecimal getRevenueToday() {
        return getSum("SELECT SUM(total_price) FROM orders WHERE status = 'completed' AND DATE(order_time) = CURDATE()");
    }

    // --- Merchant-specific Analytics ---

    public long getTotalOrdersByMerchant(int merchantId) {
        String query = "SELECT COUNT(*) FROM orders WHERE merchant_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, merchantId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public BigDecimal getTotalRevenueByMerchant(int merchantId) {
        String query = "SELECT SUM(total_price) FROM orders WHERE merchant_id = ? AND status = 'completed'";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, merchantId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal sum = rs.getBigDecimal(1);
                    return sum == null ? BigDecimal.ZERO : sum;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public long getOrdersTodayByMerchant(int merchantId) {
        String query = "SELECT COUNT(*) FROM orders WHERE merchant_id = ? AND DATE(order_time) = CURDATE()";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, merchantId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public BigDecimal getRevenueTodayByMerchant(int merchantId) {
        String query = "SELECT SUM(total_price) FROM orders WHERE merchant_id = ? AND status = 'completed' AND DATE(order_time) = CURDATE()";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, merchantId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal sum = rs.getBigDecimal(1);
                    return sum == null ? BigDecimal.ZERO : sum;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
    
    public java.util.List<java.util.Map<String, Object>> getTopSellingDishesByMerchant(int merchantId) {
        java.util.List<java.util.Map<String, Object>> topDishes = new java.util.ArrayList<>();
        String query = "SELECT d.name AS dish_name, SUM(od.quantity) AS total_quantity_sold, SUM(od.quantity * od.price_per_item) AS total_dish_revenue " +
                       "FROM orders o " +
                       "JOIN order_details od ON o.order_id = od.order_id " +
                       "JOIN dishes d ON od.dish_id = d.dish_id " +
                       "WHERE o.merchant_id = ? AND o.status = 'completed' " +
                       "GROUP BY d.name " +
                       "ORDER BY total_quantity_sold DESC, total_dish_revenue DESC " +
                       "LIMIT 5"; // Get top 5 dishes

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, merchantId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, Object> dishStats = new java.util.HashMap<>();
                    dishStats.put("dishName", rs.getString("dish_name"));
                    dishStats.put("totalQuantitySold", rs.getLong("total_quantity_sold"));
                    dishStats.put("totalDishRevenue", rs.getBigDecimal("total_dish_revenue"));
                    topDishes.add(dishStats);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topDishes;
    }
}
