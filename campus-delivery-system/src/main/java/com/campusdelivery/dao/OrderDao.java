package com.campusdelivery.dao;

import com.campusdelivery.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderDao {

    @Autowired
    private DataSource dataSource;

    // Helper method to map a ResultSet row to an Order object
    private Order mapRowToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        order.setUserId(rs.getInt("user_id"));
        order.setMerchantId(rs.getInt("merchant_id"));
        order.setAddressId(rs.getInt("address_id"));
        order.setOrderTime(rs.getTimestamp("order_time"));
        order.setTotalPrice(rs.getBigDecimal("total_price"));
        order.setStatus(rs.getString("status"));
        // Fields that might be joined, set them if they exist in the ResultSet
        try {
            order.setMerchantName(rs.getString("merchant_name"));
        } catch (SQLException e) { /* Column not in this ResultSet */ }
        try {
            order.setUsername(rs.getString("username"));
        } catch (SQLException e) { /* Column not in this ResultSet */ }
        try {
            order.setAddressDetails(rs.getString("address_details"));
        } catch (SQLException e) { /* Column not in this ResultSet */ }
        return order;
    }

    public int createOrder(Order order) {
        String sql = "INSERT INTO orders (user_id, merchant_id, address_id, total_price) VALUES (?, ?, ?, ?)";
        int orderId = 0;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, order.getUserId());
            pstmt.setInt(2, order.getMerchantId());
            pstmt.setInt(3, order.getAddressId());
            pstmt.setBigDecimal(4, order.getTotalPrice());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        orderId = generatedKeys.getInt(1);
                    }
                }
                System.out.println("DAO: Successfully created order with ID " + orderId + " (status: unpaid)");
            } else {
                System.out.println("DAO: Failed to create order for user ID " + order.getUserId());
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to create order for user ID " + order.getUserId());
            e.printStackTrace();
        }
        return orderId;
    }

    public Order getOrderById(int orderId) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToOrder(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.username, m.name as merchant_name " +
                     "FROM orders o " +
                     "JOIN users u ON o.user_id = u.user_id " +
                     "JOIN merchants m ON o.merchant_id = m.merchant_id " +
                     "ORDER BY o.order_time DESC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                orders.add(mapRowToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public List<Order> getOrdersByUserId(int userId, Integer merchantId) {
        List<Order> orders = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT o.*, m.name as merchant_name, u.username ")
                                        .append("FROM orders o ")
                                        .append("JOIN merchants m ON o.merchant_id = m.merchant_id ")
                                        .append("JOIN users u ON o.user_id = u.user_id ")
                                        .append("WHERE o.user_id = ?");

        if (merchantId != null) {
            sqlBuilder.append(" AND o.merchant_id = ?");
        }
        sqlBuilder.append(" ORDER BY o.order_time DESC");

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
            pstmt.setInt(1, userId);
            if (merchantId != null) {
                pstmt.setInt(2, merchantId);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapRowToOrder(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    public List<Order> getOrdersByMerchantId(int merchantId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, u.username FROM orders o JOIN users u ON o.user_id = u.user_id WHERE o.merchant_id = ? AND o.status NOT IN ('unpaid', 'cancelled')";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, merchantId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapRowToOrder(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public void updateOrder(Order order) {
        String sql = "UPDATE orders SET user_id = ?, merchant_id = ?, address_id = ?, total_price = ?, status = ? WHERE order_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, order.getUserId());
            pstmt.setInt(2, order.getMerchantId());
            pstmt.setInt(3, order.getAddressId());
            pstmt.setBigDecimal(4, order.getTotalPrice());
            pstmt.setString(5, order.getStatus());
            pstmt.setInt(6, order.getOrderId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("DAO: Successfully updated order with ID " + order.getOrderId());
            } else {
                System.out.println("DAO: No order found with ID " + order.getOrderId() + " to update.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("DAO: Successfully updated status for order ID " + orderId + " to " + status);
            } else {
                System.out.println("DAO: No order found with ID " + orderId + " to update status.");
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to update status for order ID " + orderId);
            e.printStackTrace();
        }
    }

    public void deleteOrder(int orderId) {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("DAO: Successfully deleted order with ID " + orderId);
            } else {
                System.out.println("DAO: No order found with ID " + orderId + " to delete.");
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to delete order with ID " + orderId);
            e.printStackTrace();
        }
    }

    public List<Order> getOrdersByUserAndDateRange(int userId, java.util.Date startDate, java.util.Date endDate) {
        List<Order> orders = new ArrayList<>();
        String sql = "{CALL get_user_orders_by_date_range(?, ?, ?)}";
        try (Connection conn = dataSource.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, userId);
            cstmt.setTimestamp(2, new Timestamp(startDate.getTime()));
            cstmt.setTimestamp(3, new Timestamp(endDate.getTime()));

            try (ResultSet rs = cstmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapRowToOrder(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public int countOrdersByMerchantIdAndStatus(int merchantId, String status) {
        String sql = "SELECT COUNT(*) FROM orders WHERE merchant_id = ? AND status = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, merchantId);
            pstmt.setString(2, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to count orders for merchant ID " + merchantId);
            e.printStackTrace();
        }
        return 0;
    }

    public List<Order> getOrdersByStatus(String status) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE status = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapRowToOrder(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public int countUnreviewedCompletedOrders(int userId) {
        String sql = "SELECT COUNT(*) FROM orders o LEFT JOIN reviews r ON o.order_id = r.order_id WHERE o.user_id = ? AND o.status = 'completed' AND r.review_id IS NULL";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to count unreviewed orders for user ID " + userId);
            e.printStackTrace();
        }
        return 0;
    }

    public List<Order> getUnreviewedCompletedOrders(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, m.name as merchant_name " +
                     "FROM orders o " +
                     "LEFT JOIN reviews r ON o.order_id = r.order_id " +
                     "JOIN merchants m ON o.merchant_id = m.merchant_id " +
                     "WHERE o.user_id = ? AND o.status = 'completed' AND r.review_id IS NULL";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapRowToOrder(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
}