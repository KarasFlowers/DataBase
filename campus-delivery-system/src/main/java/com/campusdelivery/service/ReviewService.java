package com.campusdelivery.service;

import com.campusdelivery.dao.ReviewDao;
import com.campusdelivery.entity.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewDao reviewDao;

    public void addReview(Review review) {
        reviewDao.addReview(review);
    }

    public Review getReviewByOrderId(int orderId) {
        return reviewDao.getReviewByOrderId(orderId);
    }

    public List<Review> getReviewsByUserId(int userId) {
        return reviewDao.getReviewsByUserId(userId);
    }

    public void deleteReview(int reviewId) {
        reviewDao.deleteReview(reviewId);
    }

    public List<Review> getReviewsByMerchantId(int merchantId) {
        return reviewDao.getReviewsByMerchantId(merchantId);
    }

    public List<Review> getAllReviews() {
        return reviewDao.getAllReviews();
    }

    public Review getReviewById(int reviewId) {
        return reviewDao.getReviewById(reviewId);
    }

    public void updateReview(Review review) {
        reviewDao.updateReview(review);
    }

    public int countUnseenReviewsByMerchant(int merchantId) {
        return reviewDao.countUnseenReviewsByMerchant(merchantId);
    }

    public void markReviewsAsSeenByMerchant(int merchantId) {
        reviewDao.markReviewsAsSeenByMerchant(merchantId);
    }
}
