package com.smartmes.dto;

import com.smartmes.enums.DowntimeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Downtime Report DTO
 * Data transfer object for creating downtime reports
 * 异常停机上报数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DowntimeReportDTO {
    /**
     * Associated order ID (required)
     * 关联工单号（必填）
     */
    @NotBlank(message = "Order ID cannot be empty")
    private String orderId;

    /**
     * Equipment ID (required)
     * 设备ID（必填）
     */
    @NotBlank(message = "Equipment ID cannot be empty")
    private String equipmentId;

    /**
     * Downtime type (required)
     * 停机类型（必填）
     */
    @NotNull(message = "Downtime type cannot be null")
    private DowntimeType downtimeType;

    /**
     * Description of the issue (required)
     * 异常描述（必填）
     */
    @NotBlank(message = "Description cannot be empty")
    private String description;

    /**
     * Downtime start time (required)
     * 停机开始时间（必填）
     */
    @NotNull(message = "Start time cannot be null")
    private LocalDateTime startTime;

    /**
     * Downtime end time (optional, can be set later)
     * 停机结束时间（可选）
     */
    private LocalDateTime endTime;

    /**
     * Reporter ID (required)
     * 上报人（必填）
     */
    @NotBlank(message = "Reporter ID cannot be empty")
    private String reporterId;

    /**
     * Attachment file paths (comma-separated)
     * 附件路径（逗号分隔）
     */
    private String attachments;
}
