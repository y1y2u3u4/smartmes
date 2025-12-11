package com.smartmes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 工单实体类
 * 用于管理生产工单信息
 * 注意：字段与数据库表 work_order 完全匹配
 *
 * @author SmartMES Team
 * @version 1.0.0
 */
@Entity
@Table(name = "work_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrder {

    /**
     * 工单号（主键）
     */
    @Id
    @Column(name = "order_id", length = 50)
    private String id;

    /**
     * 产品编号
     */
    @Column(name = "product_code", nullable = false, length = 50)
    private String productCode;

    /**
     * 批次号
     */
    @Column(name = "batch_no", length = 50)
    private String batchNo;

    /**
     * 计划产量
     */
    @Column(name = "plan_qty", nullable = false)
    private Integer planQty;

    /**
     * 实际产量
     */
    @Column(name = "actual_qty")
    private Integer actualQty = 0;

    /**
     * 开始时间
     */
    @Column(name = "start_time")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /**
     * 工单状态
     */
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private WorkOrderStatus status;

    /**
     * 产线ID
     */
    @Column(name = "line_id", length = 50)
    private String lineId;

    /**
     * 设备ID
     */
    @Column(name = "equipment_id", length = 50)
    private String equipmentId;

    /**
     * 操作员ID
     */
    @Column(name = "operator_id", length = 50)
    private String operatorId;

    /**
     * 创建人
     */
    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 完成率 (actualQty / planQty) - 非持久化字段
     */
    @Transient
    private Double completionRate;

    /**
     * 工单号别名 - 用于兼容前端字段命名
     */
    @Transient
    public String getWorkOrderNo() {
        return this.id;
    }

    /**
     * 创建时自动设置时间
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    /**
     * 更新时自动设置时间
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 获取完成率
     */
    @PostLoad
    public void calculateCompletionRate() {
        if (planQty != null && planQty > 0) {
            this.completionRate = (double) (actualQty != null ? actualQty : 0) / planQty;
        } else {
            this.completionRate = 0.0;
        }
    }

    /**
     * 工单状态枚举
     */
    public enum WorkOrderStatus {
        PENDING,      // 待开始
        IN_PROGRESS,  // 进行中
        COMPLETED,    // 已完成
        ABNORMAL,     // 异常
        CANCELLED,    // 已取消
        CLOSED        // 已关闭
    }
}
