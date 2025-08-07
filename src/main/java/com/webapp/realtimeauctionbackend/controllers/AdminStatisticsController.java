package com.webapp.realtimeauctionbackend.controllers;

import com.webapp.realtimeauctionbackend.DTOs.AdminDashboardStatsDto;
import com.webapp.realtimeauctionbackend.services.AdminStatisticsService;
import com.webapp.realtimeauctionbackend.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/statistics")
public class AdminStatisticsController {

    @Autowired
    private AdminStatisticsService adminStatisticsService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardStatsDto> getDashboardStatistics() {
        return ResponseEntity.ok(adminStatisticsService.getDashboardStatistics());
    }
} 