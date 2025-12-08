package com.smartmes.entity;

import com.smartmes.enums.DowntimeStatus;
import com.smartmes.enums.DowntimeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Downtime Report Entity
 * Records downtime incidents during production
 * 异常停机上报实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DowntimeReport {
    /**
     * Report ID (Primary key, auto-increment)
     * 上报ID（主键，自增）
     */
    private Long reportId;

    /**
     * Associated order ID
     * 关联工单号
     */
    private String orderId;

    /**
     * Equipment ID
     * 设备ID
     */
    private String equipmentId;

    /**
     * Downtime type
     * 停机类型
     */
    private DowntimeType downtimeType;

    /**
     * Description of the issue
     * 异常描述
     */
    private String description;

    /**
     * Downtime start time
     * 停机开始时间
     */
    private LocalDateTime startTime;

    /**
     * Downtime end time
     * 停机结束时间
     */
    private LocalDateTime endTime;

    /**
     * Downtime duration in minutes (auto-calculated)
     * 停机时长（分钟，自动计算）
     */
    private Integer durationMinutes;

    /**
     * Reporter ID
     * 上报人
     */
    private String reporterId;

    /**
     * Responder ID
     * 响应人
     */
    private String responderId;

    /**
     * Solution description
     * 处理措施
     */
    private String solution;

    /**
     * Report status
     * 状态：PENDING/PROCESSING/RESOLVED
     */
    private DowntimeStatus status;

    /**
     * Attachment file paths (comma-separated)
     * 附件路径（逗号分隔）
     */
    private String attachments;

    /**
     * Creation time
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * Last update time
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * Calculate duration in minutes between start and end time
     * 计算停机时长（分钟）
     */
    public void calculateDuration() {
        if (startTime != null && endTime != null) {
            this.durationMinutes = (int) ChronoUnit.MINUTES.between(startTime, endTime);
        }
    }

    /**
     * Initialize default values
     * 初始化默认值
     */
    public void initDefaults() {
        if (this.status == null) {
            this.status = DowntimeStatus.PENDING;
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }
}
