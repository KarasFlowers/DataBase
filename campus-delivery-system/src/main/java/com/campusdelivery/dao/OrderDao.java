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

    public int createOrder(Order order) {
        String sql = "INSERT INTO orders (user_id, merchant_id, address_id, total_price, status) VALUES (?, ?, ?, ?, ?)";
        int orderId = 0;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, order.getUserId());
            pstmt.setInt(2, order.getMerchantId());
            pstmt.setInt(3, order.getAddressId());
            pstmt.setBigDecimal(4, order.getTotalPrice());
            pstmt.setString(5, order.getStatus());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        orderId = generatedKeys.getInt(1);
                    }
                }
                System.out.println("DAO: Successfully created order with ID " + orderId);
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
        Order order = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    order = new Order();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setUserId(rs.getInt("user_id"));
                    order.setMerchantId(rs.getInt("merchant_id"));
                    order.setAddressId(rs.getInt("address_id"));
                    order.setOrderTime(rs.getTimestamp("order_time"));
                    order.setTotalPrice(rs.getBigDecimal("total_price"));
                    order.setStatus(rs.getString("status"));
                    System.out.println("DAO: Found order by ID " + orderId);
                } else {
                    System.out.println("DAO: No order found with ID " + orderId);
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to get order by ID " + orderId);
            e.printStackTrace();
        }
        return order;
    }

    public List<Order> getAllOrders() {
        String sql = "SELECT * FROM orders";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setUserId(rs.getInt("user_id"));
                order.setMerchantId(rs.getInt("merchant_id"));
                order.setAddressId(rs.getInt("address_id"));
                order.setOrderTime(rs.getTimestamp("order_time"));
                order.setTotalPrice(rs.getBigDecimal("total_price"));
                order.setStatus(rs.getString("status"));
                orders.add(order);
            }
            System.out.println("DAO: Retrieved " + orders.size() + " orders.");
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to get all orders.");
            e.printStackTrace();
        }
        return orders;
    }

    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setUserId(rs.getInt("user_id"));
                    order.setMerchantId(rs.getInt("merchant_id"));
                    order.setAddressId(rs.getInt("address_id"));
                    order.setOrderTime(rs.getTimestamp("order_time"));
                    order.setTotalPrice(rs.getBigDecimal("total_price"));
                    order.setStatus(rs.getString("status"));
                    orders.add(order);
                }
                System.out.println("DAO: Retrieved " + orders.size() + " orders for user ID " + userId);
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to get orders for user ID " + userId);
            e.printStackTrace();
        }
        return orders;
    }
    
    public List<Order> getOrdersByMerchantId(int merchantId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE merchant_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, merchantId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setUserId(rs.getInt("user_id"));
                    order.setMerchantId(rs.getInt("merchant_id"));
                    order.setAddressId(rs.getInt("address_id"));
                    order.setOrderTime(rs.getTimestamp("order_time"));
                    order.setTotalPrice(rs.getBigDecimal("total_price"));
                    order.setStatus(rs.getString("status"));
                    orders.add(order);
                }
                System.out.println("DAO: Retrieved " + orders.size() + " orders for merchant ID " + merchantId);
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to get orders for merchant ID " + merchantId);
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
            System.err.println("DAO Error: Failed to update order with ID " + order.getOrderId());
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
                    Order order = new Order();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setUserId(rs.getInt("user_id"));
                    order.setMerchantId(rs.getInt("merchant_id"));
                    order.setAddressId(rs.getInt("address_id"));
                    order.setOrderTime(rs.getTimestamp("order_time"));
                    order.setTotalPrice(rs.getBigDecimal("total_price"));
                    order.setStatus(rs.getString("status"));
                    order.setMerchantName(rs.getString("merchant_name"));
                    order.setAddressDetails(rs.getString("delivery_address"));
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
}