package com.campusdelivery.dao;

import com.campusdelivery.entity.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReviewDao {

    @Autowired
    private DataSource dataSource;

    // This helper ONLY maps columns from the 'reviews' table itself.
    private Review mapRowToReview(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getInt("review_id"));
        review.setOrderId(rs.getInt("order_id"));
        review.setUserId(rs.getInt("user_id"));
        review.setRating(rs.getInt("rating"));
        review.setComment(rs.getString("comment"));
        review.setReviewTime(rs.getTimestamp("review_time"));
        review.setLastModifiedTime(rs.getTimestamp("last_modified_time"));
        review.setSeenByMerchant(rs.getBoolean("is_seen_by_merchant"));
        return review;
    }

    public void addReview(Review review) {
        String sql = "INSERT INTO reviews (order_id, user_id, rating, comment) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, review.getOrderId());
            pstmt.setInt(2, review.getUserId());
            pstmt.setInt(3, review.getRating());
            pstmt.setString(4, review.getComment());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataIntegrityViolationException("Error adding review.", e);
        }
    }

    public Review getReviewById(int reviewId) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reviewId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToReview(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Review getReviewByOrderId(int orderId) {
        String sql = "SELECT * FROM reviews WHERE order_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToReview(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Review> getReviewsByUserId(int userId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM reviews WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapRowToReview(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public List<Review> getAllReviews() {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, u.username, o.merchant_id, m.name as merchant_name " +
                     "FROM reviews r " +
                     "JOIN users u ON r.user_id = u.user_id " +
                     "JOIN orders o ON r.order_id = o.order_id " +
                     "JOIN merchants m ON o.merchant_id = m.merchant_id " +
                     "ORDER BY r.review_time DESC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Review review = mapRowToReview(rs);
                // Set fields from JOINed tables separately
                review.setUsername(rs.getString("username"));
                review.setMerchantName(rs.getString("merchant_name"));
                review.setMerchantId(rs.getInt("merchant_id"));
                reviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public void updateReview(Review review) {
        String sql = "UPDATE reviews SET rating = ?, comment = ? WHERE review_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, review.getRating());
            pstmt.setString(2, review.getComment());
            pstmt.setInt(3, review.getReviewId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteReview(int reviewId) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reviewId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Review> getReviewsByMerchantId(int merchantId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, u.username " +
                     "FROM reviews r " +
                     "JOIN orders o ON r.order_id = o.order_id " +
                     "JOIN users u ON r.user_id = u.user_id " +
                     "WHERE o.merchant_id = ? " +
                     "ORDER BY r.review_time DESC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, merchantId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Review review = mapRowToReview(rs);
                    review.setUsername(rs.getString("username"));
                    reviews.add(review);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public int countUnseenReviewsByMerchant(int merchantId) {
        String sql = "SELECT COUNT(*) FROM reviews r JOIN orders o ON r.order_id = o.order_id WHERE o.merchant_id = ? AND r.is_seen_by_merchant = FALSE";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, merchantId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void markReviewsAsSeenByMerchant(int merchantId) {
        String sql = "UPDATE reviews r JOIN orders o ON r.order_id = o.order_id SET r.is_seen_by_merchant = TRUE WHERE o.merchant_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, merchantId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}