package com.smartmes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 生产概览数据DTO
 * 用于展示今日生产的整体概况
 *
 * @author SmartMES Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductionOverview {

    /**
     * 今日工单总数
     */
    private Integer todayWorkOrderTotal;

    /**
     * 今日已完成工单数
     */
    private Integer todayCompleted;

    /**
     * 今日进行中工单数
     */
    private Integer todayInProgress;

    /**
     * 今日异常工单数
     */
    private Integer todayAbnormal;

    /**
     * 计划产量总数
     */
    private Integer planQtyTotal;

    /**
     * 实际产量总数
     */
    private Integer actualQtyTotal;

    /**
     * 完成率（百分比，如：85.5 表示 85.5%）
     */
    private Double completionRate;

    /**
     * 运行中的设备数量
     */
    private Integer equipmentRunning;

    /**
     * 空闲设备数量
     */
    private Integer equipmentIdle;

    /**
     * 故障设备数量
     */
    private Integer equipmentFault;

    /**
     * 计算完成率
     * @param planQty 计划产量
     * @param actualQty 实际产量
     * @return 完成率百分比
     */
    public static Double calculateCompletionRate(Integer planQty, Integer actualQty) {
        if (planQty == null || planQty == 0) {
            return 0.0;
        }
        if (actualQty == null) {
            return 0.0;
        }
        return Math.round((actualQty * 100.0 / planQty) * 100.0) / 100.0;
    }
}
