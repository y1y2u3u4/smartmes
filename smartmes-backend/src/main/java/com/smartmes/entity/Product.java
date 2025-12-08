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
     * 产品主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 产品编号（唯一标识）
     */
    @Column(nullable = false, unique = true, length = 50)
    private String productCode;

    /**
     * 产品名称
     */
    @Column(nullable = false, length = 100)
    private String productName;

    /**
     * 产品规格型号
     */
    @Column(length = 100)
    private String specification;

    /**
     * 产品类型/分类
     */
    @Column(length = 50)
    private String productType;

    /**
     * 计量单位（如：件、箱、台等）
     */
    @Column(length = 20)
    private String unit;

    /**
     * 标准工时（分钟）
     */
    @Column
    private Integer standardWorkTime;

    /**
     * 产品状态
     * ACTIVE - 正常使用
     * INACTIVE - 停用
     */
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

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
