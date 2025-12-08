package com.smartmes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Downtime Resolve DTO
 * Data transfer object for resolving downtime reports
 * 异常解决数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DowntimeResolveDTO {
    /**
     * End time of downtime (required)
     * 停机结束时间（必填）
     */
    @NotNull(message = "End time cannot be null")
    private LocalDateTime endTime;

    /**
     * Solution description (required)
     * 处理措施（必填）
     */
    @NotBlank(message = "Solution cannot be empty")
    private String solution;
}
