package com.smartmes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 工单进度数据DTO
 * 用于展示工单的执行进度信息
 *
 * @author SmartMES Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkOrderProgress {

    /**
     * 工单列表
     */
    private List<WorkOrderProgressItem> workOrders;

    /**
     * 工单进度项DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WorkOrderProgressItem {

        /**
         * 工单号
         */
        private String workOrderNo;

        /**
         * 产品编号
         */
        private String productCode;

        /**
         * 产品名称
         */
        private String productName;

        /**
         * 产线ID
         */
        private String lineId;

        /**
         * 工单状态
         */
        private String status;

        /**
         * 工单状态中文描述
         */
        private String statusName;

        /**
         * 计划产量
         */
        private Integer planQty;

        /**
         * 实际产量
         */
        private Integer actualQty;

        /**
         * 完成率（百分比）
         */
        private Double completionRate;

        /**
         * 计划开始时间
         */
        private LocalDateTime planStartTime;

        /**
         * 计划完成时间
         */
        private LocalDateTime planEndTime;

        /**
         * 实际开始时间
         */
        private LocalDateTime actualStartTime;

        /**
         * 实际完成时间
         */
        private LocalDateTime actualEndTime;

        /**
         * 优先级
         */
        private Integer priority;
    }
}
