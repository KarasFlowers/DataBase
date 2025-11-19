package com.campusdelivery.dao;

import com.campusdelivery.model.Review;
import java.util.List;

public class ReviewDao {

    public void addReview(Review review) {
        // TODO: Implement SQL INSERT
        System.out.println("DAO: Adding review for order " + review.getOrderId());
    }

    public Review getReviewByOrderId(int orderId) {
        // TODO: Implement SQL SELECT by order ID
        System.out.println("DAO: Getting review for order " + orderId);
        return null;
    }

    public List<Review> getReviewsByUserId(int userId) {
        // TODO: Implement SQL SELECT by user ID
        System.out.println("DAO: Getting reviews from user " + userId);
        return null;
    }

    public void deleteReview(int reviewId) {
        // TODO: Implement SQL DELETE
        System.out.println("DAO: Deleting review " + reviewId);
    }
}
