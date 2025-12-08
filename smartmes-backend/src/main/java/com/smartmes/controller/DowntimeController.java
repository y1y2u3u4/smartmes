package com.smartmes.controller;

import com.smartmes.common.ApiResponse;
import com.smartmes.common.PageResult;
import com.smartmes.dto.*;
import com.smartmes.entity.DowntimeReport;
import com.smartmes.service.DowntimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Downtime Report Controller
 * REST API for downtime management
 * 异常停机控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/downtime")
@Validated
public class DowntimeController {

    @Autowired
    private DowntimeService downtimeService;

    /**
     * Report downtime incident
     * POST /api/downtime/report
     * 上报异常停机
     *
     * @param reportDTO Downtime report data
     * @return API response with created report
     */
    @PostMapping("/report")
    public ApiResponse<DowntimeReport> reportDowntime(@Valid @RequestBody DowntimeReportDTO reportDTO) {
        log.info("API: Report downtime - Order: {}, Equipment: {}",
                reportDTO.getOrderId(), reportDTO.getEquipmentId());

        try {
            DowntimeReport report = downtimeService.reportDowntime(reportDTO);
            return ApiResponse.success("Downtime reported successfully", report);
        } catch (Exception e) {
            log.error("Failed to report downtime", e);
            return ApiResponse.error("Failed to report downtime: " + e.getMessage());
        }
    }

    /**
     * Query downtime reports with pagination and filters
     * GET /api/downtime/reports
     * 查询异常停机列表
     *
     * @param queryDTO Query parameters
     * @return API response with paginated reports
     */
    @GetMapping("/reports")
    public ApiResponse<PageResult<DowntimeReport>> queryReports(DowntimeQueryDTO queryDTO) {
        log.info("API: Query downtime reports - Page: {}, Size: {}",
                queryDTO.getPageNum(), queryDTO.getPageSize());

        try {
            PageResult<DowntimeReport> result = downtimeService.queryReports(queryDTO);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("Failed to query downtime reports", e);
            return ApiResponse.error("Failed to query reports: " + e.getMessage());
        }
    }

    /**
     * Get downtime report details by ID
     * GET /api/downtime/reports/{reportId}
     * 查询异常详情
     *
     * @param reportId Report ID
     * @return API response with report details
     */
    @GetMapping("/reports/{reportId}")
    public ApiResponse<DowntimeReport> getReportById(@PathVariable Long reportId) {
        log.info("API: Get downtime report by ID: {}", reportId);

        try {
            DowntimeReport report = downtimeService.getReportById(reportId);
            return ApiResponse.success(report);
        } catch (Exception e) {
            log.error("Failed to get downtime report: {}", reportId, e);
            return ApiResponse.error("Failed to get report: " + e.getMessage());
        }
    }

    /**
     * Respond to downtime report
     * PUT /api/downtime/reports/{reportId}/respond
     * 响应异常停机
     *
     * @param reportId Report ID
     * @param respondDTO Response data
     * @return API response with updated report
     */
    @PutMapping("/reports/{reportId}/respond")
    public ApiResponse<DowntimeReport> respondToDowntime(
            @PathVariable Long reportId,
            @Valid @RequestBody DowntimeRespondDTO respondDTO) {

        log.info("API: Respond to downtime report: {} by {}",
                reportId, respondDTO.getResponderId());

        try {
            DowntimeReport report = downtimeService.respondToDowntime(reportId, respondDTO);
            return ApiResponse.success("Response recorded successfully", report);
        } catch (Exception e) {
            log.error("Failed to respond to downtime report: {}", reportId, e);
            return ApiResponse.error("Failed to respond: " + e.getMessage());
        }
    }

    /**
     * Resolve downtime report
     * PUT /api/downtime/reports/{reportId}/resolve
     * 解决异常停机
     *
     * @param reportId Report ID
     * @param resolveDTO Resolution data
     * @return API response with updated report
     */
    @PutMapping("/reports/{reportId}/resolve")
    public ApiResponse<DowntimeReport> resolveDowntime(
            @PathVariable Long reportId,
            @Valid @RequestBody DowntimeResolveDTO resolveDTO) {

        log.info("API: Resolve downtime report: {}", reportId);

        try {
            DowntimeReport report = downtimeService.resolveDowntime(reportId, resolveDTO);
            return ApiResponse.success("Downtime resolved successfully", report);
        } catch (Exception e) {
            log.error("Failed to resolve downtime report: {}", reportId, e);
            return ApiResponse.error("Failed to resolve: " + e.getMessage());
        }
    }

    /**
     * Get downtime statistics
     * GET /api/downtime/statistics
     * 获取异常统计数据
     *
     * @return API response with statistics
     */
    @GetMapping("/statistics")
    public ApiResponse<DowntimeStatisticsDTO> getStatistics() {
        log.info("API: Get downtime statistics");

        try {
            DowntimeStatisticsDTO statistics = downtimeService.getStatistics();
            return ApiResponse.success(statistics);
        } catch (Exception e) {
            log.error("Failed to get downtime statistics", e);
            return ApiResponse.error("Failed to get statistics: " + e.getMessage());
        }
    }

    /**
     * Delete downtime report
     * DELETE /api/downtime/reports/{reportId}
     * 删除异常停机记录
     *
     * @param reportId Report ID
     * @return API response with success flag
     */
    @DeleteMapping("/reports/{reportId}")
    public ApiResponse<Boolean> deleteReport(@PathVariable Long reportId) {
        log.info("API: Delete downtime report: {}", reportId);

        try {
            boolean success = downtimeService.deleteReport(reportId);
            if (success) {
                return ApiResponse.success("Report deleted successfully", true);
            } else {
                return ApiResponse.error("Failed to delete report");
            }
        } catch (Exception e) {
            log.error("Failed to delete downtime report: {}", reportId, e);
            return ApiResponse.error("Failed to delete report: " + e.getMessage());
        }
    }
}
