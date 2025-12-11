package com.smartmes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 产品实体类
 * 用于管理产品的基本信息
 *
 * @author SmartMES Team
 * @version 1.0.0
 */
@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    /**
     * 产品编号（主键）
     */
    @Id
    @Column(name = "product_code", nullable = false, length = 50)
    private String productCode;

    /**
     * 产品名称
     */
    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    /**
     * 产品类型/分类
     */
    @Column(name = "product_type", length = 50)
    private String productType;

    /**
     * 标准工时（分钟）
     */
    @Column(name = "standard_time")
    private Integer standardWorkTime;

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
     * 产品规格型号（不在数据库中）
     */
    @Transient
    private String specification;

    /**
     * 计量单位（不在数据库中）
     */
    @Transient
    private String unit;

    /**
     * 产品状态（不在数据库中）
     */
    @Transient
    private ProductStatus status;

    /**
     * 备注信息（不在数据库中）
     */
    @Transient
    private String remarks;

    /**
     * 产品状态枚举
     */
    public enum ProductStatus {
        ACTIVE,    // 正常使用
        INACTIVE   // 停用
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
