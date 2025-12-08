package com.smartmes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Downtime Statistics DTO
 * Data transfer object for downtime statistics
 * 异常统计数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DowntimeStatisticsDTO {
    /**
     * Total number of downtime reports
     * 异常总数
     */
    private Long totalReports;

    /**
     * Total downtime duration in minutes
     * 总停机时长（分钟）
     */
    private Long totalDurationMinutes;

    /**
     * Pending reports count
     * 待处理数量
     */
    private Long pendingCount;

    /**
     * Processing reports count
     * 处理中数量
     */
    private Long processingCount;

    /**
     * Resolved reports count
     * 已解决数量
     */
    private Long resolvedCount;

    /**
     * Distribution by downtime type
     * 按类型分布
     * Key: DowntimeType, Value: count
     */
    private Map<String, Long> typeDistribution;

    /**
     * Top 5 equipment with most downtime incidents
     * 设备故障TOP5
     */
    private List<EquipmentDowntimeStats> topEquipmentByIncidents;

    /**
     * Top 5 equipment with longest downtime duration
     * 停机时长TOP5设备
     */
    private List<EquipmentDowntimeStats> topEquipmentByDuration;

    /**
     * Equipment downtime statistics
     * 设备停机统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EquipmentDowntimeStats {
        /**
         * Equipment ID
         * 设备ID
         */
        private String equipmentId;

        /**
         * Number of incidents
         * 故障次数
         */
        private Long incidentCount;

        /**
         * Total duration in minutes
         * 总停机时长（分钟）
         */
        private Long totalDurationMinutes;
    }
}
