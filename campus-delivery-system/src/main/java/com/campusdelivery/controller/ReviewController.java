package com.campusdelivery.controller;

import com.campusdelivery.entity.Review;
import com.campusdelivery.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<String> addReview(@RequestBody Review review) {
        reviewService.addReview(review);
        return new ResponseEntity<>("Review added successfully", HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<String> updateReview(@RequestBody Review review) {
        reviewService.updateReview(review);
        return new ResponseEntity<>("Review updated successfully", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> reviews = reviewService.getAllReviews();
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable int id) {
        Review review = reviewService.getReviewById(id);
        if (review != null) {
            return new ResponseEntity<>(review, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Review> getReviewByOrderId(@PathVariable int orderId) {
        Review review = reviewService.getReviewByOrderId(orderId);
        if (review != null) {
            return new ResponseEntity<>(review, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getReviewsByUserId(@PathVariable int userId) {
        List<Review> reviews = reviewService.getReviewsByUserId(userId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<List<Review>> getReviewsByMerchantId(@PathVariable int merchantId) {
        List<Review> reviews = reviewService.getReviewsByMerchantId(merchantId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable int reviewId) {
        reviewService.deleteReview(reviewId);
        return new ResponseEntity<>("Review deleted successfully", HttpStatus.NO_CONTENT);
    }

    @GetMapping("/merchant/{merchantId}/unseen/count")
    public ResponseEntity<Integer> countUnseenReviewsByMerchant(@PathVariable int merchantId) {
        int count = reviewService.countUnseenReviewsByMerchant(merchantId);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @PostMapping("/merchant/{merchantId}/mark-as-seen")
    public ResponseEntity<String> markReviewsAsSeenByMerchant(@PathVariable int merchantId) {
        reviewService.markReviewsAsSeenByMerchant(merchantId);
        return new ResponseEntity<>("Reviews marked as seen", HttpStatus.OK);
    }
}
