package com.smartmes.dto;

import com.smartmes.enums.DowntimeStatus;
import com.smartmes.enums.DowntimeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Downtime Query DTO
 * Data transfer object for querying downtime reports with filters
 * 异常停机查询数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DowntimeQueryDTO {
    /**
     * Order ID filter
     * 工单号筛选
     */
    private String orderId;

    /**
     * Equipment ID filter
     * 设备ID筛选
     */
    private String equipmentId;

    /**
     * Downtime type filter
     * 停机类型筛选
     */
    private DowntimeType downtimeType;

    /**
     * Status filter
     * 状态筛选
     */
    private DowntimeStatus status;

    /**
     * Start time range - from
     * 开始时间范围-起始
     */
    private LocalDateTime startTimeFrom;

    /**
     * Start time range - to
     * 开始时间范围-结束
     */
    private LocalDateTime startTimeTo;

    /**
     * Reporter ID filter
     * 上报人筛选
     */
    private String reporterId;

    /**
     * Page number (default: 1)
     * 页码（默认第1页）
     */
    private Integer pageNum = 1;

    /**
     * Page size (default: 10)
     * 每页大小（默认10条）
     */
    private Integer pageSize = 10;

    /**
     * Sort field (default: createdAt)
     * 排序字段（默认按创建时间）
     */
    private String sortBy = "createdAt";

    /**
     * Sort direction (default: DESC)
     * 排序方向（默认降序）
     */
    private String sortOrder = "DESC";
}
