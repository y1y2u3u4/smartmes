package com.smartmes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 异常统计数据DTO
 * 用于展示停机和异常的统计信息
 *
 * @author SmartMES Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DowntimeStatistics {

    /**
     * 今日异常次数
     */
    private Integer todayDowntimeCount;

    /**
     * 总停机时长（分钟）
     */
    private Integer totalDowntimeMinutes;

    /**
     * 按类型统计异常次数
     * Key: 异常类型名称
     * Value: 该类型的异常次数
     */
    private Map<String, Integer> downtimeByType;

    /**
     * 故障TOP5设备列表
     */
    private List<EquipmentFaultDTO> topFaultyEquipment;

    /**
     * 设备故障统计DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EquipmentFaultDTO {

        /**
         * 设备编号
         */
        private String equipmentCode;

        /**
         * 设备名称
         */
        private String equipmentName;

        /**
         * 故障次数
         */
        private Integer faultCount;

        /**
         * 总停机时长（分钟）
         */
        private Integer totalDowntimeMinutes;
    }
}
