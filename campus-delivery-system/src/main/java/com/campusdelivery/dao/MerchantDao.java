package com.campusdelivery.dao;

import com.campusdelivery.entity.Merchant;
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
public class MerchantDao {

    @Autowired
    private DataSource dataSource;

    public void addMerchant(Merchant merchant) {
        String sql = "INSERT INTO merchants (name, address, phone_number, rating) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, merchant.getName());
            pstmt.setString(2, merchant.getAddress());
            pstmt.setString(3, merchant.getPhoneNumber());
            pstmt.setBigDecimal(4, merchant.getRating());
            pstmt.executeUpdate();
            System.out.println("DAO: Successfully added merchant " + merchant.getName());
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to add merchant " + merchant.getName());
            e.printStackTrace();
        }
    }

    public Merchant getMerchantById(int merchantId) {
        String sql = "SELECT * FROM merchants WHERE merchant_id = ?";
        Merchant merchant = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, merchantId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    merchant = new Merchant();
                    merchant.setMerchantId(rs.getInt("merchant_id"));
                    merchant.setName(rs.getString("name"));
                    merchant.setAddress(rs.getString("address"));
                    merchant.setPhoneNumber(rs.getString("phone_number"));
                    merchant.setRating(rs.getBigDecimal("rating"));
                    System.out.println("DAO: Found merchant by ID " + merchantId);
                } else {
                    System.out.println("DAO: No merchant found with ID " + merchantId);
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to get merchant by ID " + merchantId);
            e.printStackTrace();
        }
        return merchant;
    }

    public List<Merchant> getAllMerchants() {
        List<Merchant> merchants = new ArrayList<>();
        String sql = "SELECT * FROM merchants";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Merchant merchant = new Merchant();
                merchant.setMerchantId(rs.getInt("merchant_id"));
                merchant.setName(rs.getString("name"));
                merchant.setAddress(rs.getString("address"));
                merchant.setPhoneNumber(rs.getString("phone_number"));
                merchant.setRating(rs.getBigDecimal("rating"));
                merchants.add(merchant);
            }
            System.out.println("DAO: Retrieved " + merchants.size() + " merchants.");
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to get all merchants.");
            e.printStackTrace();
        }
        return merchants;
    }

    public void updateMerchant(Merchant merchant) {
        String sql = "UPDATE merchants SET name = ?, address = ?, phone_number = ?, rating = ? WHERE merchant_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, merchant.getName());
            pstmt.setString(2, merchant.getAddress());
            pstmt.setString(3, merchant.getPhoneNumber());
            pstmt.setBigDecimal(4, merchant.getRating());
            pstmt.setInt(5, merchant.getMerchantId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("DAO: Successfully updated merchant " + merchant.getName());
            } else {
                System.out.println("DAO: No merchant found with ID " + merchant.getMerchantId() + " to update.");
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to update merchant " + merchant.getName());
            e.printStackTrace();
        }
    }

    public void deleteMerchant(int merchantId) {
        String sql = "DELETE FROM merchants WHERE merchant_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, merchantId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("DAO: Successfully deleted merchant with ID " + merchantId);
            } else {
                System.out.println("DAO: No merchant found with ID " + merchantId + " to delete.");
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to delete merchant with ID " + merchantId);
            e.printStackTrace();
        }
    }
}