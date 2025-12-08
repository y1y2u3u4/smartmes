package com.smartmes.service.impl;

import com.smartmes.common.PageResult;
import com.smartmes.dto.*;
import com.smartmes.entity.DowntimeReport;
import com.smartmes.enums.DowntimeStatus;
import com.smartmes.mapper.DowntimeMapper;
import com.smartmes.service.DowntimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Downtime Service Implementation
 * 异常停机业务实现类
 */
@Slf4j
@Service
public class DowntimeServiceImpl implements DowntimeService {

    @Autowired
    private DowntimeMapper downtimeMapper;

    /**
     * Report downtime incident
     * 上报异常停机
     */
    @Override
    @Transactional
    public DowntimeReport reportDowntime(DowntimeReportDTO reportDTO) {
        log.info("Reporting downtime for order: {}, equipment: {}",
                reportDTO.getOrderId(), reportDTO.getEquipmentId());

        // Create entity from DTO
        DowntimeReport report = new DowntimeReport();
        BeanUtils.copyProperties(reportDTO, report);

        // Initialize defaults
        report.initDefaults();

        // Calculate duration if end time is provided
        if (reportDTO.getEndTime() != null) {
            report.calculateDuration();
        }

        // Insert into database
        int rows = downtimeMapper.insert(report);
        if (rows == 0) {
            throw new RuntimeException("Failed to insert downtime report");
        }

        log.info("Downtime report created with ID: {}", report.getReportId());
        return report;
    }

    /**
     * Query downtime reports with pagination
     * 分页查询异常停机列表
     */
    @Override
    public PageResult<DowntimeReport> queryReports(DowntimeQueryDTO queryDTO) {
        log.info("Querying downtime reports with filters: {}", queryDTO);

        // Calculate offset for pagination
        int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
        queryDTO.setPageNum(offset);

        // Query data
        List<DowntimeReport> records = downtimeMapper.findByConditions(queryDTO);
        Long total = downtimeMapper.countByConditions(queryDTO);

        log.info("Found {} downtime reports", total);
        return PageResult.of(records, total, queryDTO.getPageNum() / queryDTO.getPageSize() + 1, queryDTO.getPageSize());
    }

    /**
     * Get downtime report details by ID
     * 根据ID查询异常详情
     */
    @Override
    public DowntimeReport getReportById(Long reportId) {
        log.info("Getting downtime report by ID: {}", reportId);

        DowntimeReport report = downtimeMapper.findById(reportId);
        if (report == null) {
            throw new RuntimeException("Downtime report not found: " + reportId);
        }

        return report;
    }

    /**
     * Respond to downtime report
     * 响应异常停机
     */
    @Override
    @Transactional
    public DowntimeReport respondToDowntime(Long reportId, DowntimeRespondDTO respondDTO) {
        log.info("Responding to downtime report: {}", reportId);

        // Check if report exists
        DowntimeReport report = getReportById(reportId);

        // Validate current status
        if (report.getStatus() != DowntimeStatus.PENDING) {
            throw new RuntimeException("Only pending reports can be responded to");
        }

        // Update responder and status
        downtimeMapper.updateResponder(reportId, respondDTO.getResponderId());
        downtimeMapper.updateStatus(reportId, DowntimeStatus.PROCESSING);

        // Get updated report
        report = getReportById(reportId);
        log.info("Downtime report {} responded by {}", reportId, respondDTO.getResponderId());

        return report;
    }

    /**
     * Resolve downtime report
     * 解决异常停机
     */
    @Override
    @Transactional
    public DowntimeReport resolveDowntime(Long reportId, DowntimeResolveDTO resolveDTO) {
        log.info("Resolving downtime report: {}", reportId);

        // Check if report exists
        DowntimeReport report = getReportById(reportId);

        // Validate current status
        if (report.getStatus() == DowntimeStatus.RESOLVED) {
            throw new RuntimeException("Report is already resolved");
        }

        // Validate end time is after start time
        if (resolveDTO.getEndTime().isBefore(report.getStartTime())) {
            throw new RuntimeException("End time cannot be before start time");
        }

        // Update resolution info
        report.setEndTime(resolveDTO.getEndTime());
        report.setSolution(resolveDTO.getSolution());
        report.setStatus(DowntimeStatus.RESOLVED);
        report.calculateDuration();
        report.setUpdatedAt(LocalDateTime.now());

        int rows = downtimeMapper.updateResolution(report);
        if (rows == 0) {
            throw new RuntimeException("Failed to update downtime report");
        }

        log.info("Downtime report {} resolved. Duration: {} minutes",
                reportId, report.getDurationMinutes());

        return report;
    }

    /**
     * Get downtime statistics
     * 获取异常统计数据
     */
    @Override
    public DowntimeStatisticsDTO getStatistics() {
        log.info("Getting downtime statistics");

        // Get basic statistics
        Long totalReports = downtimeMapper.countTotal();
        Long totalDuration = downtimeMapper.sumTotalDuration();
        Long pendingCount = downtimeMapper.countByStatus(DowntimeStatus.PENDING);
        Long processingCount = downtimeMapper.countByStatus(DowntimeStatus.PROCESSING);
        Long resolvedCount = downtimeMapper.countByStatus(DowntimeStatus.RESOLVED);

        // Get type distribution
        List<Map<String, Object>> typeList = downtimeMapper.countByType();
        Map<String, Long> typeDistribution = new HashMap<>();
        for (Map<String, Object> item : typeList) {
            String type = (String) item.get("type");
            Long count = ((Number) item.get("count")).longValue();
            typeDistribution.put(type, count);
        }

        // Get top equipment statistics
        List<DowntimeStatisticsDTO.EquipmentDowntimeStats> topByIncidents =
                downtimeMapper.getTopEquipmentByIncidents(5);
        List<DowntimeStatisticsDTO.EquipmentDowntimeStats> topByDuration =
                downtimeMapper.getTopEquipmentByDuration(5);

        // Build statistics DTO
        DowntimeStatisticsDTO statistics = DowntimeStatisticsDTO.builder()
                .totalReports(totalReports)
                .totalDurationMinutes(totalDuration)
                .pendingCount(pendingCount)
                .processingCount(processingCount)
                .resolvedCount(resolvedCount)
                .typeDistribution(typeDistribution)
                .topEquipmentByIncidents(topByIncidents)
                .topEquipmentByDuration(topByDuration)
                .build();

        log.info("Statistics retrieved: {} total reports, {} total minutes",
                totalReports, totalDuration);

        return statistics;
    }

    /**
     * Delete downtime report
     * 删除异常停机记录
     */
    @Override
    @Transactional
    public boolean deleteReport(Long reportId) {
        log.info("Deleting downtime report: {}", reportId);

        // Check if report exists
        DowntimeReport report = getReportById(reportId);
        if (report == null) {
            throw new RuntimeException("Downtime report not found: " + reportId);
        }

        // Delete report
        int rows = downtimeMapper.delete(reportId);
        if (rows > 0) {
            log.info("Downtime report {} deleted successfully", reportId);
            return true;
        }

        return false;
    }
}
