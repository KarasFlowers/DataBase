package com.campusdelivery.service;

import com.campusdelivery.dao.UserDao;
import com.campusdelivery.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public void addUser(User user) {
        // In a real application, you would hash the password here before saving
        userDao.addUser(user);
    }

    public User getUserById(int userId) {
        return userDao.getUserById(userId);
    }

    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    public void updateUser(User user) {
        userDao.updateUser(user);
    }

    public void deleteUser(int userId) {
        userDao.deleteUser(userId);
    }
}
