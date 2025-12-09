package com.smartmes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 设备实体类
 * 用于管理生产设备的基本信息和状态
 *
 * @author SmartMES Team
 * @version 1.0.0
 */
@Entity
@Table(name = "equipment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Equipment {

    /**
     * 设备编号（主键）
     */
    @Id
    @Column(name = "equipment_id", nullable = false, length = 50)
    private String equipmentId;

    /**
     * 设备名称
     */
    @Column(name = "equipment_name", nullable = false, length = 100)
    private String equipmentName;

    /**
     * 设备类型（如：注塑机、冲压机、装配线等）
     */
    @Column(name = "equipment_type", length = 50)
    private String equipmentType;

    /**
     * 所属产线ID
     */
    @Column(name = "line_id", length = 50)
    private String lineId;

    /**
     * 设备状态
     * RUNNING - 运行中
     * IDLE - 空闲
     * MAINTENANCE - 维护中
     * FAULT - 故障
     */
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EquipmentStatus status;

    /**
     * 上次维护时间
     */
    @Column(name = "last_maintenance_time")
    private LocalDateTime lastMaintenanceTime;

    /**
     * 下次计划维护时间
     */
    @Column(name = "next_maintenance_time")
    private LocalDateTime nextMaintenanceTime;

    /**
     * 设备位置/区域
     */
    @Transient
    private String location;

    /**
     * 创建时间
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updateTime;

    /**
     * 备注信息
     */
    @Transient
    private String remarks;

    /**
     * 设备状态枚举
     */
    public enum EquipmentStatus {
        RUNNING,      // 运行中
        IDLE,         // 空闲
        MAINTENANCE,  // 维护中
        FAULT         // 故障
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
    }
}
