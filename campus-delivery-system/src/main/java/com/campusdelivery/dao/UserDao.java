package com.campusdelivery.dao;

import com.campusdelivery.entity.User;
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
public class UserDao {

    @Autowired
    private DataSource dataSource;

    public void addUser(User user) {
        String sql = "INSERT INTO users (username, password_hash, phone_number) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getPhoneNumber());
            pstmt.executeUpdate();
            System.out.println("DAO: Successfully added user " + user.getUsername());

        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to add user " + user.getUsername());
            e.printStackTrace();
        }
    }

    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        User user = null;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setPhoneNumber(rs.getString("phone_number"));
                    user.setRegistrationDate(rs.getTimestamp("registration_date"));
                    System.out.println("DAO: Found user by ID " + userId);
                } else {
                    System.out.println("DAO: No user found with ID " + userId);
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to get user by ID " + userId);
            e.printStackTrace();
        }
        return user;
    }

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        User user = null;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setPhoneNumber(rs.getString("phone_number"));
                    user.setRegistrationDate(rs.getTimestamp("registration_date"));
                    System.out.println("DAO: Found user by username " + username);
                } else {
                    System.out.println("DAO: No user found with username " + username);
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to find user by username " + username);
            e.printStackTrace();
        }
        return user;
    }

    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setRegistrationDate(rs.getTimestamp("registration_date"));
                users.add(user);
            }
            System.out.println("DAO: Retrieved " + users.size() + " users.");

        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to get all users.");
            e.printStackTrace();
        }
        return users;
    }

    public void updateUser(User user) {
        String sql = "UPDATE users SET username = ?, password_hash = ?, phone_number = ? WHERE user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getPhoneNumber());
            pstmt.setInt(4, user.getUserId());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("DAO: Successfully updated user " + user.getUsername());
            } else {
                System.out.println("DAO: No user found with ID " + user.getUserId() + " to update.");
            }

        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to update user " + user.getUsername());
            e.printStackTrace();
        }
    }

    public void deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("DAO: Successfully deleted user with ID " + userId);
            } else {
                System.out.println("DAO: No user found with ID " + userId + " to delete.");
            }

        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to delete user with ID " + userId);
            e.printStackTrace();
        }
    }
}