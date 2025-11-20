package com.campusdelivery.dao;

import com.campusdelivery.entity.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReviewDao {

    @Autowired
    private DataSource dataSource;

    public void addReview(Review review) {
        String sql = "INSERT INTO reviews (order_id, user_id, rating, comment) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, review.getOrderId());
            pstmt.setInt(2, review.getUserId());
            pstmt.setInt(3, review.getRating());
            pstmt.setString(4, review.getComment());
            pstmt.executeUpdate();
            System.out.println("DAO: Successfully added review for order ID " + review.getOrderId());
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to add review for order ID " + review.getOrderId());
            e.printStackTrace();
        }
    }

    public Review getReviewById(int reviewId) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        Review review = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reviewId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    review = new Review();
                    review.setReviewId(rs.getInt("review_id"));
                    review.setOrderId(rs.getInt("order_id"));
                    review.setUserId(rs.getInt("user_id"));
                    review.setRating(rs.getInt("rating"));
                    review.setComment(rs.getString("comment"));
                    review.setReviewTime(rs.getTimestamp("review_time"));
                    System.out.println("DAO: Found review by ID " + reviewId);
                } else {
                    System.out.println("DAO: No review found with ID " + reviewId);
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to get review by ID " + reviewId);
            e.printStackTrace();
        }
        return review;
    }

    public Review getReviewByOrderId(int orderId) {
        String sql = "SELECT * FROM reviews WHERE order_id = ?";
        Review review = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    review = new Review();
                    review.setReviewId(rs.getInt("review_id"));
                    review.setOrderId(rs.getInt("order_id"));
                    review.setUserId(rs.getInt("user_id"));
                    review.setRating(rs.getInt("rating"));
                    review.setComment(rs.getString("comment"));
                    review.setReviewTime(rs.getTimestamp("review_time"));
                    System.out.println("DAO: Found review for order ID " + orderId);
                } else {
                    System.out.println("DAO: No review found for order ID " + orderId);
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to get review by order ID " + orderId);
            e.printStackTrace();
        }
        return review;
    }

    public List<Review> getAllReviews() {
        String sql = "SELECT * FROM reviews";
        List<Review> reviews = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Review review = new Review();
                review.setReviewId(rs.getInt("review_id"));
                review.setOrderId(rs.getInt("order_id"));
                review.setUserId(rs.getInt("user_id"));
                review.setRating(rs.getInt("rating"));
                review.setComment(rs.getString("comment"));
                review.setReviewTime(rs.getTimestamp("review_time"));
                reviews.add(review);
            }
            System.out.println("DAO: Retrieved " + reviews.size() + " reviews.");
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to get all reviews.");
            e.printStackTrace();
        }
        return reviews;
    }

    public List<Review> getReviewsByUserId(int userId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM reviews WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Review review = new Review();
                    review.setReviewId(rs.getInt("review_id"));
                    review.setOrderId(rs.getInt("order_id"));
                    review.setUserId(rs.getInt("user_id"));
                    review.setRating(rs.getInt("rating"));
                    review.setComment(rs.getString("comment"));
                    review.setReviewTime(rs.getTimestamp("review_time"));
                    reviews.add(review);
                }
                System.out.println("DAO: Retrieved " + reviews.size() + " reviews for user ID " + userId);
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to get reviews for user ID " + userId);
            e.printStackTrace();
        }
        return reviews;
    }

    public void updateReview(Review review) {
        String sql = "UPDATE reviews SET order_id = ?, user_id = ?, rating = ?, comment = ? WHERE review_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, review.getOrderId());
            pstmt.setInt(2, review.getUserId());
            pstmt.setInt(3, review.getRating());
            pstmt.setString(4, review.getComment());
            pstmt.setInt(5, review.getReviewId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("DAO: Successfully updated review with ID " + review.getReviewId());
            } else {
                System.out.println("DAO: No review found with ID " + review.getReviewId() + " to update.");
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to update review with ID " + review.getReviewId());
            e.printStackTrace();
        }
    }

    public void deleteReview(int reviewId) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reviewId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("DAO: Successfully deleted review with ID " + reviewId);
            } else {
                System.out.println("DAO: No review found with ID " + reviewId + " to delete.");
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to delete review with ID " + reviewId);
            e.printStackTrace();
        }
    }
}
