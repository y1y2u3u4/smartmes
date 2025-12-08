package com.smartmes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据看板整体数据DTO
 * 用于封装整个看板页面的所有数据
 *
 * @author SmartMES Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardData {

    /**
     * 生产概览数据
     */
    private ProductionOverview productionOverview;

    /**
     * 异常统计数据
     */
    private DowntimeStatistics downtimeStatistics;

    /**
     * 工单进度数据
     */
    private WorkOrderProgress workOrderProgress;

    /**
     * 设备状态数据
     */
    private EquipmentStatusData equipmentStatus;
}
