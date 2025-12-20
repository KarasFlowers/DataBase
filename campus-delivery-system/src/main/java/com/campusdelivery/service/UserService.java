package com.campusdelivery.service;

import com.campusdelivery.controller.dto.RegistrationRequest;
import com.campusdelivery.dao.UserDao;
import com.campusdelivery.entity.Merchant;
import com.campusdelivery.entity.Rider;
import com.campusdelivery.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private RiderService riderService;

    @Transactional
    public User registerUser(RegistrationRequest request) throws Exception {
        // Check if username already exists
        if (userDao.findByUsername(request.getUsername()) != null) {
            throw new DataIntegrityViolationException("Username '" + request.getUsername() + "' already exists.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(request.getPassword()); // Plain text, as requested
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(request.getRole());

        switch (request.getRole()) {
            case "merchant":
                Merchant merchant = new Merchant();
                merchant.setName(request.getMerchantName());
                merchant.setAddress(request.getMerchantAddress());
                merchant.setPhoneNumber(request.getPhoneNumber());
                merchant.setRating(BigDecimal.valueOf(0.0));
                int merchantId = merchantService.addMerchant(merchant);
                user.setEntityId(merchantId);
                break;
            case "rider":
                Rider rider = new Rider();
                rider.setName(request.getRiderName());
                rider.setPhoneNumber(request.getPhoneNumber());
                rider.setStatus("offline");
                int riderId = riderService.addRider(rider);
                user.setEntityId(riderId);
                break;
            case "user":
            case "admin":
                // No associated entity needed
                user.setEntityId(null);
                break;
            default:
                throw new Exception("Invalid role specified: " + request.getRole());
        }

        userDao.addUser(user);
        // Return the newly created user (without password)
        user.setPasswordHash(null);
        return user;
    }

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
