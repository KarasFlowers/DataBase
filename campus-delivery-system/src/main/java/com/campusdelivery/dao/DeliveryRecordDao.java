package com.campusdelivery.dao;

import com.campusdelivery.entity.DeliveryRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DeliveryRecordDao {

    @Autowired
    private DataSource dataSource;

    public void addDeliveryRecord(DeliveryRecord record) {
        String sql = "INSERT INTO delivery_records (order_id, rider_id, status, pickup_time, delivery_time) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, record.getOrderId());
            // rider_id can be null
            if (record.getRiderId() != 0) {
                pstmt.setInt(2, record.getRiderId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, record.getStatus());
            pstmt.setTimestamp(4, record.getPickupTime() != null ? new Timestamp(record.getPickupTime().getTime()) : null);
            pstmt.setTimestamp(5, record.getDeliveryTime() != null ? new Timestamp(record.getDeliveryTime().getTime()) : null);
            pstmt.executeUpdate();
            System.out.println("DAO: Successfully added delivery record for order ID " + record.getOrderId());
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to add delivery record for order ID " + record.getOrderId());
            e.printStackTrace();
        }
    }

    public DeliveryRecord getDeliveryRecordByOrderId(int orderId) {
        String sql = "SELECT * FROM delivery_records WHERE order_id = ?";
        DeliveryRecord record = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    record = new DeliveryRecord();
                    record.setDeliveryId(rs.getInt("delivery_id"));
                    record.setOrderId(rs.getInt("order_id"));
                    record.setRiderId(rs.getInt("rider_id"));
                    record.setStatus(rs.getString("status"));
                    record.setPickupTime(rs.getTimestamp("pickup_time"));
                    record.setDeliveryTime(rs.getTimestamp("delivery_time"));
                    System.out.println("DAO: Found delivery record for order ID " + orderId);
                } else {
                    System.out.println("DAO: No delivery record found for order ID " + orderId);
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to get delivery record by order ID " + orderId);
            e.printStackTrace();
        }
        return record;
    }
    
    public List<DeliveryRecord> getAllDeliveryRecords() {
        String sql = "SELECT * FROM delivery_records";
        List<DeliveryRecord> records = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                DeliveryRecord record = new DeliveryRecord();
                record.setDeliveryId(rs.getInt("delivery_id"));
                record.setOrderId(rs.getInt("order_id"));
                record.setRiderId(rs.getInt("rider_id"));
                record.setStatus(rs.getString("status"));
                record.setPickupTime(rs.getTimestamp("pickup_time"));
                record.setDeliveryTime(rs.getTimestamp("delivery_time"));
                records.add(record);
            }
            System.out.println("DAO: Retrieved " + records.size() + " delivery records.");

        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to get all delivery records.");
            e.printStackTrace();
        }
        return records;
    }

    public void updateDeliveryRecord(DeliveryRecord record) {
        String sql = "UPDATE delivery_records SET rider_id = ?, status = ?, pickup_time = ?, delivery_time = ? WHERE delivery_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // rider_id can be null
            if (record.getRiderId() != 0) {
                pstmt.setInt(1, record.getRiderId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            pstmt.setString(2, record.getStatus());
            pstmt.setTimestamp(3, record.getPickupTime() != null ? new Timestamp(record.getPickupTime().getTime()) : null);
            pstmt.setTimestamp(4, record.getDeliveryTime() != null ? new Timestamp(record.getDeliveryTime().getTime()) : null);
            pstmt.setInt(5, record.getDeliveryId());
            pstmt.executeUpdate();
            System.out.println("DAO: Successfully updated delivery record for ID " + record.getDeliveryId());
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to update delivery record for ID " + record.getDeliveryId());
            e.printStackTrace();
        }
    }

    public void deleteDeliveryRecord(int deliveryId) {
        String sql = "DELETE FROM delivery_records WHERE delivery_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, deliveryId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("DAO: Successfully deleted delivery record with ID " + deliveryId);
            } else {
                System.out.println("DAO: No delivery record found with ID " + deliveryId + " to delete.");
            }

        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to delete delivery record with ID " + deliveryId);
            e.printStackTrace();
        }
    }
}