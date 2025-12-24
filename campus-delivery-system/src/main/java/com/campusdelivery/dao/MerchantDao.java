package com.campusdelivery.dao;

import com.campusdelivery.entity.Merchant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MerchantDao {

    @Autowired
    private DataSource dataSource;

    // Define a constant for the complex SELECT statement to avoid repetition
    private static final String SQL_SELECT_FIELDS_WITH_OPEN_STATUS =
        "m.merchant_id, m.name, m.address, m.phone_number, m.rating, m.sales_count, m.average_order_price, " +
        "m.open_time, m.close_time, m.is_manually_closed, " +
        "(CASE " +
            "WHEN m.is_manually_closed = TRUE THEN FALSE " +
            "WHEN m.open_time IS NULL OR m.close_time IS NULL THEN TRUE " +
            "WHEN m.open_time > m.close_time THEN (CURTIME() >= m.open_time OR CURTIME() <= m.close_time) " +
            "ELSE (CURTIME() BETWEEN m.open_time AND m.close_time) " +
        "END) AS is_open";

    public int addMerchant(Merchant merchant) {
        String sql = "INSERT INTO merchants (name, address, phone_number, rating) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, merchant.getName());
            pstmt.setString(2, merchant.getAddress());
            pstmt.setString(3, merchant.getPhoneNumber());
            pstmt.setBigDecimal(4, merchant.getRating());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        System.out.println("DAO: Successfully added merchant " + merchant.getName());
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error: Failed to add merchant " + merchant.getName());
            e.printStackTrace();
        }
        return 0;
    }

    private Merchant mapRowToMerchant(ResultSet rs) throws SQLException {
        Merchant merchant = new Merchant();
        merchant.setMerchantId(rs.getInt("merchant_id"));
        merchant.setName(rs.getString("name"));
        merchant.setAddress(rs.getString("address"));
        merchant.setPhoneNumber(rs.getString("phone_number"));
        merchant.setRating(rs.getBigDecimal("rating"));
        merchant.setSalesCount(rs.getInt("sales_count"));
        merchant.setAverageOrderPrice(rs.getBigDecimal("average_order_price"));
        merchant.setOpenTime(rs.getTime("open_time"));
        merchant.setCloseTime(rs.getTime("close_time"));
        merchant.setManuallyClosed(rs.getBoolean("is_manually_closed"));
        merchant.setOpen(rs.getBoolean("is_open"));
        return merchant;
    }

    public Merchant getMerchantById(int merchantId) {
        String sql = "SELECT " + SQL_SELECT_FIELDS_WITH_OPEN_STATUS + " FROM merchants m WHERE m.merchant_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, merchantId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToMerchant(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Merchant> getAllMerchants(String name, String sortBy, BigDecimal minPrice, BigDecimal maxPrice) {
        List<Merchant> merchants = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT ").append(SQL_SELECT_FIELDS_WITH_OPEN_STATUS).append(" FROM merchants m");
        List<Object> params = new ArrayList<>();
        boolean hasWhere = false;

        if (name != null && !name.isEmpty()) {
            sql.append(" WHERE m.name LIKE ?");
            params.add("%" + name + "%");
            hasWhere = true;
        }

        if (minPrice != null) {
            sql.append(hasWhere ? " AND" : " WHERE").append(" m.average_order_price >= ?");
            params.add(minPrice);
            hasWhere = true;
        }

        if (maxPrice != null) {
            sql.append(hasWhere ? " AND" : " WHERE").append(" m.average_order_price <= ?");
            params.add(maxPrice);
        }

        // Always sort by open status first, then by the selected option
        sql.append(" ORDER BY is_open DESC");

        switch (sortBy) {
            case "rating_desc": sql.append(", m.rating DESC"); break;
            case "sales_desc": sql.append(", m.sales_count DESC"); break;
            case "aov_asc": sql.append(", m.average_order_price ASC"); break;
            case "aov_desc": sql.append(", m.average_order_price DESC"); break;
            default: sql.append(", m.merchant_id"); break; // Default secondary sort
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    merchants.add(mapRowToMerchant(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return merchants;
    }

    public void updateMerchant(Merchant merchant) {
        String sql = "UPDATE merchants SET name = ?, address = ?, phone_number = ? WHERE merchant_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, merchant.getName());
            pstmt.setString(2, merchant.getAddress());
            pstmt.setString(3, merchant.getPhoneNumber());
            pstmt.setInt(4, merchant.getMerchantId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateBusinessHours(int merchantId, Time openTime, Time closeTime) {
        String sql = "UPDATE merchants SET open_time = ?, close_time = ? WHERE merchant_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTime(1, openTime);
            pstmt.setTime(2, closeTime);
            pstmt.setInt(3, merchantId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateManualStatus(int merchantId, boolean isClosed) {
        String sql = "UPDATE merchants SET is_manually_closed = ? WHERE merchant_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, isClosed);
            pstmt.setInt(2, merchantId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteMerchant(int merchantId) {
        String sql = "DELETE FROM merchants WHERE merchant_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, merchantId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Merchant> search(String query) {
        List<Merchant> merchants = new ArrayList<>();
        String sql = "SELECT " + SQL_SELECT_FIELDS_WITH_OPEN_STATUS +
                     " FROM merchants m WHERE m.name LIKE ? " +
                     "UNION " +
                     "SELECT " + SQL_SELECT_FIELDS_WITH_OPEN_STATUS +
                     " FROM merchants m WHERE m.merchant_id IN " +
                     "(SELECT DISTINCT merchant_id FROM dishes WHERE name LIKE ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String likeQuery = "%" + query + "%";
            pstmt.setString(1, likeQuery);
            pstmt.setString(2, likeQuery);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    merchants.add(mapRowToMerchant(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return merchants;
    }
}