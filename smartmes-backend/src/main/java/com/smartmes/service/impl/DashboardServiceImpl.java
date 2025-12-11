package com.smartmes.service.impl;

import com.smartmes.dto.*;
import com.smartmes.dto.DowntimeStatistics.EquipmentFaultDTO;
import com.smartmes.dto.EquipmentStatusData.EquipmentStatusItem;
import com.smartmes.dto.WorkOrderProgress.WorkOrderProgressItem;
import com.smartmes.entity.Downtime.DowntimeType;
import com.smartmes.entity.Equipment;
import com.smartmes.entity.Equipment.EquipmentStatus;
import com.smartmes.entity.WorkOrder;
import com.smartmes.entity.WorkOrder.WorkOrderStatus;
import com.smartmes.repository.DowntimeRepository;
import com.smartmes.repository.EquipmentRepository;
import com.smartmes.repository.WorkOrderRepository;
import com.smartmes.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据看板服务实现类
 * 实现看板数据的统计和聚合逻辑
 *
 * @author SmartMES Team
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final WorkOrderRepository workOrderRepository;
    private final EquipmentRepository equipmentRepository;
    private final DowntimeRepository downtimeRepository;

    /**
     * 获取今天的开始时间
     */
    private LocalDateTime getStartOfToday() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
    }

    /**
     * 获取今天的结束时间
     */
    private LocalDateTime getEndOfToday() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
    }

    @Override
    public ProductionOverview getProductionOverview() {
        log.info("开始获取生产概览数据");

        LocalDateTime startOfDay = getStartOfToday();
        LocalDateTime endOfDay = getEndOfToday();

        // 统计今日工单数量（各状态）
        Long todayTotal = workOrderRepository.countTodayWorkOrdersByStatus(null, startOfDay, endOfDay);
        if (todayTotal == null) {
            List<WorkOrder> allTodayOrders = workOrderRepository.findTodayWorkOrders(startOfDay, endOfDay);
            todayTotal = (long) allTodayOrders.size();
        }

        Long completedCount = workOrderRepository.countTodayWorkOrdersByStatus(WorkOrderStatus.COMPLETED, startOfDay, endOfDay);
        Long inProgressCount = workOrderRepository.countTodayWorkOrdersByStatus(WorkOrderStatus.IN_PROGRESS, startOfDay, endOfDay);
        Long abnormalCount = workOrderRepository.countTodayWorkOrdersByStatus(WorkOrderStatus.ABNORMAL, startOfDay, endOfDay);

        // 统计今日产量
        Integer planQtyTotal = workOrderRepository.sumTodayPlanQty(startOfDay, endOfDay);
        Integer actualQtyTotal = workOrderRepository.sumTodayActualQty(startOfDay, endOfDay);

        // 计算完成率
        Double completionRate = ProductionOverview.calculateCompletionRate(planQtyTotal, actualQtyTotal);

        // 统计设备状态
        Long runningCount = equipmentRepository.countByStatus(EquipmentStatus.RUNNING);
        Long idleCount = equipmentRepository.countByStatus(EquipmentStatus.IDLE);
        Long faultCount = equipmentRepository.countByStatus(EquipmentStatus.FAULT);

        ProductionOverview overview = ProductionOverview.builder()
                .todayWorkOrderTotal(todayTotal != null ? todayTotal.intValue() : 0)
                .todayCompleted(completedCount != null ? completedCount.intValue() : 0)
                .todayInProgress(inProgressCount != null ? inProgressCount.intValue() : 0)
                .todayAbnormal(abnormalCount != null ? abnormalCount.intValue() : 0)
                .planQtyTotal(planQtyTotal != null ? planQtyTotal : 0)
                .actualQtyTotal(actualQtyTotal != null ? actualQtyTotal : 0)
                .completionRate(completionRate)
                .equipmentRunning(runningCount != null ? runningCount.intValue() : 0)
                .equipmentIdle(idleCount != null ? idleCount.intValue() : 0)
                .equipmentFault(faultCount != null ? faultCount.intValue() : 0)
                .build();

        log.info("生产概览数据获取完成: {}", overview);
        return overview;
    }

    @Override
    public DowntimeStatistics getDowntimeStatistics() {
        log.info("开始获取异常统计数据");

        LocalDateTime startOfDay = getStartOfToday();
        LocalDateTime endOfDay = getEndOfToday();

        // 统计今日异常次数
        Long downtimeCount = downtimeRepository.countTodayDowntime(startOfDay, endOfDay);

        // 统计今日总停机时长
        Integer totalMinutes = downtimeRepository.sumTodayDowntimeMinutes(startOfDay, endOfDay);

        // 按异常类型统计
        List<Object[]> typeStats = downtimeRepository.countTodayDowntimeByType(startOfDay, endOfDay);
        Map<String, Integer> downtimeByType = new HashMap<>();
        for (Object[] row : typeStats) {
            DowntimeType type = (DowntimeType) row[0];
            Long count = (Long) row[1];
            downtimeByType.put(getDowntimeTypeName(type), count.intValue());
        }

        // 获取故障TOP5设备
        List<Object[]> faultyEquipmentData = downtimeRepository.findTopFaultyEquipmentToday(startOfDay, endOfDay);
        List<EquipmentFaultDTO> topFaultyEquipment = new ArrayList<>();

        int limit = Math.min(5, faultyEquipmentData.size());
        for (int i = 0; i < limit; i++) {
            Object[] row = faultyEquipmentData.get(i);
            EquipmentFaultDTO dto = EquipmentFaultDTO.builder()
                    .equipmentCode((String) row[0])
                    .equipmentName((String) row[1])
                    .faultCount(((Long) row[2]).intValue())
                    .totalDowntimeMinutes(((Number) row[3]).intValue())
                    .build();
            topFaultyEquipment.add(dto);
        }

        DowntimeStatistics statistics = DowntimeStatistics.builder()
                .todayDowntimeCount(downtimeCount != null ? downtimeCount.intValue() : 0)
                .totalDowntimeMinutes(totalMinutes != null ? totalMinutes : 0)
                .downtimeByType(downtimeByType)
                .topFaultyEquipment(topFaultyEquipment)
                .build();

        log.info("异常统计数据获取完成: {}", statistics);
        return statistics;
    }

    @Override
    public WorkOrderProgress getWorkOrderProgress() {
        log.info("开始获取工单进度数据");

        LocalDateTime startOfDay = getStartOfToday();
        LocalDateTime endOfDay = getEndOfToday();

        // 获取今日所有工单
        List<WorkOrder> todayWorkOrders = workOrderRepository.findTodayWorkOrders(startOfDay, endOfDay);

        List<WorkOrderProgressItem> items = todayWorkOrders.stream()
                .map(this::convertToProgressItem)
                .collect(Collectors.toList());

        WorkOrderProgress progress = WorkOrderProgress.builder()
                .workOrders(items)
                .build();

        log.info("工单进度数据获取完成，共{}条工单", items.size());
        return progress;
    }

    @Override
    public EquipmentStatusData getEquipmentStatus() {
        log.info("开始获取设备状态数据");

        // 获取所有设备
        List<Equipment> equipmentList = equipmentRepository.findAll();

        // 转换为DTO
        List<EquipmentStatusItem> items = equipmentList.stream()
                .map(this::convertToStatusItem)
                .collect(Collectors.toList());

        // 统计各状态数量
        long runningCount = equipmentList.stream()
                .filter(e -> e.getStatus() == EquipmentStatus.RUNNING)
                .count();
        long idleCount = equipmentList.stream()
                .filter(e -> e.getStatus() == EquipmentStatus.IDLE)
                .count();
        long maintenanceCount = equipmentList.stream()
                .filter(e -> e.getStatus() == EquipmentStatus.MAINTENANCE)
                .count();
        long faultCount = equipmentList.stream()
                .filter(e -> e.getStatus() == EquipmentStatus.FAULT)
                .count();

        EquipmentStatusData statusData = EquipmentStatusData.builder()
                .equipmentList(items)
                .runningCount((int) runningCount)
                .idleCount((int) idleCount)
                .maintenanceCount((int) maintenanceCount)
                .faultCount((int) faultCount)
                .build();

        log.info("设备状态数据获取完成，共{}台设备", items.size());
        return statusData;
    }

    @Override
    public DashboardData getCompleteDashboardData() {
        log.info("开始获取完整看板数据");

        DashboardData dashboardData = new DashboardData();
        dashboardData.setProductionOverview(getProductionOverview());
        dashboardData.setDowntimeStatistics(getDowntimeStatistics());
        dashboardData.setWorkOrderProgress(getWorkOrderProgress());
        dashboardData.setEquipmentStatus(getEquipmentStatus());

        log.info("完整看板数据获取完成");
        return dashboardData;
    }

    /**
     * 将工单实体转换为工单进度项DTO
     */
    private WorkOrderProgressItem convertToProgressItem(WorkOrder workOrder) {
        return WorkOrderProgressItem.builder()
                .workOrderNo(workOrder.getWorkOrderNo())
                .productCode(workOrder.getProductCode())
                .productName(workOrder.getProductCode()) // Use productCode as productName since entity doesn't have productName
                .lineId(workOrder.getLineId())
                .status(workOrder.getStatus().name())
                .statusName(getWorkOrderStatusName(workOrder.getStatus()))
                .planQty(workOrder.getPlanQty())
                .actualQty(workOrder.getActualQty())
                .completionRate(workOrder.getCompletionRate())
                .planStartTime(null) // Entity doesn't have planStartTime
                .planEndTime(null) // Entity doesn't have planEndTime
                .actualStartTime(workOrder.getStartTime()) // Use startTime instead
                .actualEndTime(workOrder.getEndTime()) // Use endTime instead
                .priority(0) // Entity doesn't have priority
                .build();
    }

    /**
     * 将设备实体转换为设备状态项DTO
     */
    private EquipmentStatusItem convertToStatusItem(Equipment equipment) {
        return EquipmentStatusItem.builder()
                .equipmentId(equipment.getEquipmentId())
                .equipmentName(equipment.getEquipmentName())
                .equipmentType(equipment.getEquipmentType())
                .lineId(equipment.getLineId())
                .status(equipment.getStatus().name())
                .statusName(getEquipmentStatusName(equipment.getStatus()))
                .location(equipment.getLocation())
                .lastMaintenanceTime(equipment.getLastMaintenanceTime())
                .nextMaintenanceTime(equipment.getNextMaintenanceTime())
                .build();
    }

    /**
     * 获取工单状态中文名称
     */
    private String getWorkOrderStatusName(WorkOrderStatus status) {
        return switch (status) {
            case PENDING -> "Pending";
            case IN_PROGRESS -> "In Progress";
            case COMPLETED -> "Completed";
            case ABNORMAL -> "Abnormal";
            case CLOSED -> "Closed";
            case CANCELLED -> "Cancelled";
            case CLOSED -> "Closed";
        };
    }

    /**
     * 获取设备状态中文名称
     */
    private String getEquipmentStatusName(EquipmentStatus status) {
        return switch (status) {
            case RUNNING -> "Running";
            case IDLE -> "Idle";
            case MAINTENANCE -> "Maintenance";
            case FAULT -> "Fault";
        };
    }

    /**
     * 获取异常类型中文名称
     */
    private String getDowntimeTypeName(DowntimeType type) {
        return switch (type) {
            case EQUIPMENT_FAULT -> "Equipment Fault";
            case QUALITY_ISSUE -> "Quality Issue";
            case MATERIAL_SHORTAGE -> "Material Shortage";
            case TOOL_CHANGE -> "Tool Change";
            case OTHER -> "Other";
        };
    }
}
