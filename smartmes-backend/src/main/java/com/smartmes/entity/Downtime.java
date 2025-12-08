package com.smartmes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 停机/异常记录实体类
 * 用于记录设备停机和生产异常信息
 *
 * @author SmartMES Team
 * @version 1.0.0
 */
@Entity
@Table(name = "downtime")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Downtime {

    /**
     * 停机记录主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 异常编号
     */
    @Column(unique = true, length = 50)
    private String downtimeNo;

    /**
     * 工单ID
     */
    @Column
    private Long workOrderId;

    /**
     * 工单号
     */
    @Column(length = 50)
    private String workOrderNo;

    /**
     * 设备ID
     */
    @Column(nullable = false)
    private Long equipmentId;

    /**
     * 设备编号
     */
    @Column(length = 50)
    private String equipmentCode;

    /**
     * 设备名称
     */
    @Column(length = 100)
    private String equipmentName;

    /**
     * 异常类型
     * EQUIPMENT_FAULT - 设备故障
     * QUALITY_ISSUE - 质量问题
     * MATERIAL_SHORTAGE - 物料短缺
     * TOOL_CHANGE - 换模换线
     * OTHER - 其他
     */
    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private DowntimeType downtimeType;

    /**
     * 异常原因描述
     */
    @Column(length = 500)
    private String reason;

    /**
     * 开始时间
     */
    @Column(nullable = false)
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Column
    private LocalDateTime endTime;

    /**
     * 停机时长（分钟）
     */
    @Column
    private Integer durationMinutes;

    /**
     * 状态
     * ONGOING - 进行中
     * RESOLVED - 已解决
     */
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private DowntimeStatus status;

    /**
     * 解决方案描述
     */
    @Column(length = 500)
    private String solution;

    /**
     * 责任人
     */
    @Column(length = 50)
    private String responsiblePerson;

    /**
     * 创建时间
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column
    private LocalDateTime updateTime;

    /**
     * 异常类型枚举
     */
    public enum DowntimeType {
        EQUIPMENT_FAULT,     // 设备故障
        QUALITY_ISSUE,       // 质量问题
        MATERIAL_SHORTAGE,   // 物料短缺
        TOOL_CHANGE,         // 换模换线
        OTHER                // 其他
    }

    /**
     * 异常状态枚举
     */
    public enum DowntimeStatus {
        ONGOING,   // 进行中
        RESOLVED   // 已解决
    }

    /**
     * 创建时自动设置时间
     */
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    /**
     * 更新时自动设置时间
     */
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
        // 如果结束时间存在且开始时间存在，自动计算停机时长
        if (endTime != null && startTime != null) {
            durationMinutes = (int) java.time.Duration.between(startTime, endTime).toMinutes();
        }
    }
}
