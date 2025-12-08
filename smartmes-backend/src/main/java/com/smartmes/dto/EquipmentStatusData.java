package com.smartmes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 设备状态数据DTO
 * 用于展示设备的运行状态信息
 *
 * @author SmartMES Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentStatusData {

    /**
     * 设备列表
     */
    private List<EquipmentStatusItem> equipmentList;

    /**
     * 运行中设备数量
     */
    private Integer runningCount;

    /**
     * 空闲设备数量
     */
    private Integer idleCount;

    /**
     * 维护中设备数量
     */
    private Integer maintenanceCount;

    /**
     * 故障设备数量
     */
    private Integer faultCount;

    /**
     * 设备状态项DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EquipmentStatusItem {

        /**
         * 设备编号
         */
        private String equipmentId;

        /**
         * 设备名称
         */
        private String equipmentName;

        /**
         * 设备类型
         */
        private String equipmentType;

        /**
         * 所属产线
         */
        private String lineId;

        /**
         * 设备状态
         */
        private String status;

        /**
         * 设备状态中文描述
         */
        private String statusName;

        /**
         * 位置/区域
         */
        private String location;

        /**
         * 上次维护时间
         */
        private LocalDateTime lastMaintenanceTime;

        /**
         * 下次维护时间
         */
        private LocalDateTime nextMaintenanceTime;
    }
}
