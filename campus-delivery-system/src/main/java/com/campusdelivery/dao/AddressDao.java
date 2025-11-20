package com.campusdelivery.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.campusdelivery.entity.Address;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AddressDao {

    @Autowired
    private DataSource dataSource;

    public void addAddress(Address address) {
        String sql = "INSERT INTO addresses (user_id, address_details, is_default) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, address.getUserId());
            pstmt.setString(2, address.getAddressDetails());
            pstmt.setBoolean(3, address.isDefault());
            pstmt.executeUpdate();
            System.out.println("DAO: Successfully added address for user " + address.getUserId());

        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to add address for user " + address.getUserId());
            e.printStackTrace();
        }
    }

    public Address getAddressById(int addressId) {
        String sql = "SELECT * FROM addresses WHERE address_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, addressId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Address address = new Address();
                address.setAddressId(rs.getInt("address_id"));
                address.setUserId(rs.getInt("user_id"));
                address.setAddressDetails(rs.getString("address_details"));
                address.setDefault(rs.getBoolean("is_default"));
                return address;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Address> getAddressesByUserId(int userId) {
        List<Address> addresses = new ArrayList<>();
        String sql = "SELECT * FROM addresses WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Address address = new Address();
                address.setAddressId(rs.getInt("address_id"));
                address.setUserId(rs.getInt("user_id"));
                address.setAddressDetails(rs.getString("address_details"));
                address.setDefault(rs.getBoolean("is_default"));
                addresses.add(address);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return addresses;
    }

    public void updateAddress(Address address) {
        String sql = "UPDATE addresses SET user_id = ?, address_details = ?, is_default = ? WHERE address_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, address.getUserId());
            pstmt.setString(2, address.getAddressDetails());
            pstmt.setBoolean(3, address.isDefault());
            pstmt.setInt(4, address.getAddressId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAddress(int addressId) {
        String sql = "DELETE FROM addresses WHERE address_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, addressId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}