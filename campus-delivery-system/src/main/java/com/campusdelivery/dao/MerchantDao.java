package com.campusdelivery.dao;

import com.campusdelivery.entity.Merchant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MerchantDao {

    @Autowired
    private DataSource dataSource;

    public int addMerchant(Merchant merchant) {
        String sql = "INSERT INTO merchants (name, address, phone_number, rating) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, merchant.getName());
            pstmt.setString(2, merchant.getAddress());
            pstmt.setString(3, merchant.getPhoneNumber());
            pstmt.setBigDecimal(4, merchant.getRating());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        System.out.println("DAO: Successfully added merchant " + merchant.getName());
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to add merchant " + merchant.getName());
            e.printStackTrace();
        }
        return 0; // Return 0 or handle error appropriately
    }

    private Merchant mapRowToMerchant(ResultSet rs) throws SQLException {
        Merchant merchant = new Merchant();
        merchant.setMerchantId(rs.getInt("merchant_id"));
        merchant.setName(rs.getString("name"));
        merchant.setAddress(rs.getString("address"));
        merchant.setPhoneNumber(rs.getString("phone_number"));
        merchant.setRating(rs.getBigDecimal("rating"));
        merchant.setSalesCount(rs.getInt("sales_count"));
        return merchant;
    }

    public Merchant getMerchantById(int merchantId) {
        String sql = "SELECT * FROM merchants WHERE merchant_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, merchantId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToMerchant(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Merchant> getAllMerchants(String sortBy) {
        List<Merchant> merchants = new ArrayList<>();
        String sql = "SELECT * FROM merchants";

        switch (sortBy) {
            case "rating_desc":
                sql += " ORDER BY rating DESC";
                break;
            case "sales_desc":
                sql += " ORDER BY sales_count DESC";
                break;
            default:
                // No sorting or default sort by id
                sql += " ORDER BY merchant_id";
                break;
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                merchants.add(mapRowToMerchant(rs));
            }
        } catch (SQLException e) {
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