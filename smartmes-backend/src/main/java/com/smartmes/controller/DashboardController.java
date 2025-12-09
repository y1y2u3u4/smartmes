package com.smartmes.controller;

import com.smartmes.dto.*;
import com.smartmes.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 数据看板控制器
 * 提供看板数据的REST API接口
 *
 * @author SmartMES Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 获取生产概览数据
     * GET /api/dashboard/overview
     *
     * @return 生产概览数据
     */
    @GetMapping("/overview")
    public ApiResponse<ProductionOverview> getProductionOverview() {
        log.info("接收请求: 获取生产概览数据");
        try {
            ProductionOverview overview = dashboardService.getProductionOverview();
            return ApiResponse.success(overview);
        } catch (Exception e) {
            log.error("获取生产概览数据失败", e);
            return ApiResponse.error("Failed to get production overview: " + e.getMessage());
        }
    }

    /**
     * 获取异常统计数据
     * GET /api/dashboard/downtime-stats
     *
     * @return 异常统计数据
     */
    @GetMapping("/downtime-stats")
    public ApiResponse<DowntimeStatistics> getDowntimeStatistics() {
        log.info("接收请求: 获取异常统计数据");
        try {
            DowntimeStatistics statistics = dashboardService.getDowntimeStatistics();
            return ApiResponse.success(statistics);
        } catch (Exception e) {
            log.error("获取异常统计数据失败", e);
            return ApiResponse.error("Failed to get downtime statistics: " + e.getMessage());
        }
    }

    /**
     * 获取工单进度数据
     * GET /api/dashboard/workorder-progress
     *
     * @return 工单进度数据
     */
    @GetMapping("/workorder-progress")
    public ApiResponse<WorkOrderProgress> getWorkOrderProgress() {
        log.info("接收请求: 获取工单进度数据");
        try {
            WorkOrderProgress progress = dashboardService.getWorkOrderProgress();
            return ApiResponse.success(progress);
        } catch (Exception e) {
            log.error("获取工单进度数据失败", e);
            return ApiResponse.error("Failed to get work order progress: " + e.getMessage());
        }
    }

    /**
     * 获取设备状态数据
     * GET /api/dashboard/equipment-status
     *
     * @return 设备状态数据
     */
    @GetMapping("/equipment-status")
    public ApiResponse<EquipmentStatusData> getEquipmentStatus() {
        log.info("接收请求: 获取设备状态数据");
        try {
            EquipmentStatusData statusData = dashboardService.getEquipmentStatus();
            return ApiResponse.success(statusData);
        } catch (Exception e) {
            log.error("获取设备状态数据失败", e);
            return ApiResponse.error("Failed to get equipment status: " + e.getMessage());
        }
    }

    /**
     * 获取完整看板数据
     * GET /api/dashboard/complete
     *
     * @return 完整看板数据
     */
    @GetMapping("/complete")
    public ApiResponse<DashboardData> getCompleteDashboard() {
        log.info("接收请求: 获取完整看板数据");
        try {
            DashboardData dashboardData = dashboardService.getCompleteDashboardData();
            return ApiResponse.success(dashboardData);
        } catch (Exception e) {
            log.error("获取完整看板数据失败", e);
            return ApiResponse.error("Failed to get complete dashboard data: " + e.getMessage());
        }
    }
}
