package com.campusdelivery.service;

import com.campusdelivery.dao.AnalyticsDao;
import com.campusdelivery.dao.MerchantDao;
import com.campusdelivery.entity.AdminDashboardStats;
import com.campusdelivery.entity.MerchantDashboardStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {

    @Autowired
    private AnalyticsDao analyticsDao;

    @Autowired
    private MerchantDao merchantDao;

    public AdminDashboardStats getAdminDashboardStats() {
        AdminDashboardStats stats = new AdminDashboardStats();

        stats.setTotalUsers(analyticsDao.getTotalUsers());
        stats.setTotalMerchants(analyticsDao.getTotalMerchants());
        stats.setTotalRiders(analyticsDao.getTotalRiders());
        stats.setTotalOrders(analyticsDao.getTotalOrders());
        stats.setTotalRevenue(analyticsDao.getTotalRevenue());
        stats.setOrdersToday(analyticsDao.getOrdersToday());
        stats.setRevenueToday(analyticsDao.getRevenueToday());
        
        return stats;
    }

    public MerchantDashboardStats getMerchantDashboardStats(int merchantId) {
        MerchantDashboardStats stats = new MerchantDashboardStats();

        // Fetch merchant details for name
        com.campusdelivery.entity.Merchant merchant = merchantDao.getMerchantById(merchantId);
        if (merchant != null) {
            stats.setMerchantId(merchantId);
            stats.setMerchantName(merchant.getName());
        } else {
            // Handle case where merchant is not found
            return null;
        }

        stats.setTotalOrders(analyticsDao.getTotalOrdersByMerchant(merchantId));
        stats.setTotalRevenue(analyticsDao.getTotalRevenueByMerchant(merchantId));
        stats.setOrdersToday(analyticsDao.getOrdersTodayByMerchant(merchantId));
        stats.setRevenueToday(analyticsDao.getRevenueTodayByMerchant(merchantId));
        stats.setTopSellingDishes(analyticsDao.getTopSellingDishesByMerchant(merchantId));

        return stats;
    }
}
