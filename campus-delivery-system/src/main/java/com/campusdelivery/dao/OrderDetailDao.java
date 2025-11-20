package com.campusdelivery.dao;

import com.campusdelivery.entity.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderDetailDao {

    @Autowired
    private DataSource dataSource;

    public void addOrderDetail(OrderDetail detail) {
        String sql = "INSERT INTO order_details (order_id, dish_id, quantity, price_per_item) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, detail.getOrderId());
            pstmt.setInt(2, detail.getDishId());
            pstmt.setInt(3, detail.getQuantity());
            pstmt.setBigDecimal(4, detail.getPricePerItem());
            pstmt.executeUpdate();
            System.out.println("DAO: Successfully added order detail for order ID " + detail.getOrderId());
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to add order detail for order ID " + detail.getOrderId());
            e.printStackTrace();
        }
    }

    public OrderDetail getOrderDetailById(int orderDetailId) {
        String sql = "SELECT * FROM order_details WHERE order_detail_id = ?";
        OrderDetail detail = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderDetailId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    detail = new OrderDetail();
                    detail.setOrderDetailId(rs.getInt("order_detail_id"));
                    detail.setOrderId(rs.getInt("order_id"));
                    detail.setDishId(rs.getInt("dish_id"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setPricePerItem(rs.getBigDecimal("price_per_item"));
                    System.out.println("DAO: Found order detail by ID " + orderDetailId);
                } else {
                    System.out.println("DAO: No order detail found with ID " + orderDetailId);
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to get order detail by ID " + orderDetailId);
            e.printStackTrace();
        }
        return detail;
    }

    public List<OrderDetail> getAllOrderDetails() {
        String sql = "SELECT * FROM order_details";
        List<OrderDetail> orderDetails = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                OrderDetail detail = new OrderDetail();
                detail.setOrderDetailId(rs.getInt("order_detail_id"));
                detail.setOrderId(rs.getInt("order_id"));
                detail.setDishId(rs.getInt("dish_id"));
                detail.setQuantity(rs.getInt("quantity"));
                detail.setPricePerItem(rs.getBigDecimal("price_per_item"));
                orderDetails.add(detail);
            }
            System.out.println("DAO: Retrieved " + orderDetails.size() + " order details.");
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to get all order details.");
            e.printStackTrace();
        }
        return orderDetails;
    }

    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        String sql = "SELECT * FROM order_details WHERE order_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrderDetail detail = new OrderDetail();
                    detail.setOrderDetailId(rs.getInt("order_detail_id"));
                    detail.setOrderId(rs.getInt("order_id"));
                    detail.setDishId(rs.getInt("dish_id"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setPricePerItem(rs.getBigDecimal("price_per_item"));
                    orderDetails.add(detail);
                }
                System.out.println("DAO: Retrieved " + orderDetails.size() + " order details for order ID " + orderId);
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to get order details for order ID " + orderId);
            e.printStackTrace();
        }
        return orderDetails;
    }

    public void updateOrderDetail(OrderDetail detail) {
        String sql = "UPDATE order_details SET order_id = ?, dish_id = ?, quantity = ?, price_per_item = ? WHERE order_detail_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, detail.getOrderId());
            pstmt.setInt(2, detail.getDishId());
            pstmt.setInt(3, detail.getQuantity());
            pstmt.setBigDecimal(4, detail.getPricePerItem());
            pstmt.setInt(5, detail.getOrderDetailId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("DAO: Successfully updated order detail with ID " + detail.getOrderDetailId());
            } else {
                System.out.println("DAO: No order detail found with ID " + detail.getOrderDetailId() + " to update.");
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to update order detail with ID " + detail.getOrderDetailId());
            e.printStackTrace();
        }
    }

    public void deleteOrderDetail(int orderDetailId) {
        String sql = "DELETE FROM order_details WHERE order_detail_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderDetailId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("DAO: Successfully deleted order detail with ID " + orderDetailId);
            } else {
                System.out.println("DAO: No order detail found with ID " + orderDetailId + " to delete.");
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to delete order detail with ID " + orderDetailId);
            e.printStackTrace();
        }
    }
}