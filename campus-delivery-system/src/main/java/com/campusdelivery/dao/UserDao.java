package com.campusdelivery.dao;

import com.campusdelivery.model.User;
import java.util.List;

public class UserDao {

    public void addUser(User user) {
        // TODO: Implement SQL INSERT for user using JDBC
        System.out.println("DAO: Adding user " + user.getUsername());
    }

    public User getUserById(int userId) {
        // TODO: Implement SQL SELECT for user by ID
        System.out.println("DAO: Getting user by ID " + userId);
        return null;
    }

    public User findByUsername(String username) {
        // TODO: Implement SQL SELECT for user by username
        System.out.println("DAO: Getting user by username " + username);
        return null;
    }

    public List<User> getAllUsers() {
        // TODO: Implement SQL SELECT for all users
        System.out.println("DAO: Getting all users");
        return null;
    }

    public void updateUser(User user) {
        // TODO: Implement SQL UPDATE for user
        System.out.println("DAO: Updating user " + user.getUsername());
    }

    public void deleteUser(int userId) {
        // TODO: Implement SQL DELETE for user
        System.out.println("DAO: Deleting user by ID " + userId);
    }
}
