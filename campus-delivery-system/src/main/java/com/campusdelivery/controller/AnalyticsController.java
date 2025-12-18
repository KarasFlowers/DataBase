package com.campusdelivery.controller;

import com.campusdelivery.entity.AdminDashboardStats;
import com.campusdelivery.entity.MerchantDashboardStats; // Import new DTO
import com.campusdelivery.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // Import PathVariable
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/admin-dashboard")
    public ResponseEntity<AdminDashboardStats> getAdminDashboardStats() {
        AdminDashboardStats stats = analyticsService.getAdminDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/merchant/{merchantId}/dashboard")
    public ResponseEntity<MerchantDashboardStats> getMerchantDashboardStats(@PathVariable int merchantId) {
        MerchantDashboardStats stats = analyticsService.getMerchantDashboardStats(merchantId);
        if (stats != null) {
            return ResponseEntity.ok(stats);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
