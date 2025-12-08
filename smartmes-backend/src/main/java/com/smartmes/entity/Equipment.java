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
     * 设备主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 设备编号（唯一标识）
     */
    @Column(nullable = false, unique = true, length = 50)
    private String equipmentId;

    /**
     * 设备名称
     */
    @Column(nullable = false, length = 100)
    private String equipmentName;

    /**
     * 设备类型（如：注塑机、冲压机、装配线等）
     */
    @Column(length = 50)
    private String equipmentType;

    /**
     * 所属产线ID
     */
    @Column(length = 50)
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
    @Column
    private LocalDateTime lastMaintenanceTime;

    /**
     * 下次计划维护时间
     */
    @Column
    private LocalDateTime nextMaintenanceTime;

    /**
     * 设备位置/区域
     */
    @Column(length = 100)
    private String location;

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
     * 备注信息
     */
    @Column(length = 500)
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
