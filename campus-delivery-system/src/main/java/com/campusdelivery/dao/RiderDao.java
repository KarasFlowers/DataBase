package com.campusdelivery.dao;

import com.campusdelivery.entity.Rider;
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
public class RiderDao {

    @Autowired
    private DataSource dataSource;

    public void addRider(Rider rider) {
        String sql = "INSERT INTO riders (name, phone_number, status) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, rider.getName());
            pstmt.setString(2, rider.getPhoneNumber());
            pstmt.setString(3, rider.getStatus());
            pstmt.executeUpdate();
            System.out.println("DAO: Successfully added rider " + rider.getName());
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to add rider " + rider.getName());
            e.printStackTrace();
        }
    }

    public Rider getRiderById(int riderId) {
        String sql = "SELECT * FROM riders WHERE rider_id = ?";
        Rider rider = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, riderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    rider = new Rider();
                    rider.setRiderId(rs.getInt("rider_id"));
                    rider.setName(rs.getString("name"));
                    rider.setPhoneNumber(rs.getString("phone_number"));
                    rider.setStatus(rs.getString("status"));
                    System.out.println("DAO: Found rider by ID " + riderId);
                } else {
                    System.out.println("DAO: No rider found with ID " + riderId);
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to get rider by ID " + riderId);
            e.printStackTrace();
        }
        return rider;
    }

    public List<Rider> getAllRiders() {
        List<Rider> riders = new ArrayList<>();
        String sql = "SELECT * FROM riders";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Rider rider = new Rider();
                rider.setRiderId(rs.getInt("rider_id"));
                rider.setName(rs.getString("name"));
                rider.setPhoneNumber(rs.getString("phone_number"));
                rider.setStatus(rs.getString("status"));
                riders.add(rider);
            }
            System.out.println("DAO: Retrieved " + riders.size() + " riders.");
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to get all riders.");
            e.printStackTrace();
        }
        return riders;
    }

    public void updateRider(Rider rider) {
        String sql = "UPDATE riders SET name = ?, phone_number = ?, status = ? WHERE rider_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, rider.getName());
            pstmt.setString(2, rider.getPhoneNumber());
            pstmt.setString(3, rider.getStatus());
            pstmt.setInt(4, rider.getRiderId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("DAO: Successfully updated rider " + rider.getName());
            } else {
                System.out.println("DAO: No rider found with ID " + rider.getRiderId() + " to update.");
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to update rider " + rider.getName());
            e.printStackTrace();
        }
    }

    public void deleteRider(int riderId) {
        String sql = "DELETE FROM riders WHERE rider_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, riderId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("DAO: Successfully deleted rider with ID " + riderId);
            } else {
                System.out.println("DAO: No rider found with ID " + riderId + " to delete.");
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to delete rider with ID " + riderId);
            e.printStackTrace();
        }
    }
}