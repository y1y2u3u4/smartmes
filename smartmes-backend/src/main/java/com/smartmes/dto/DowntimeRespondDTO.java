package com.smartmes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

/**
 * Downtime Respond DTO
 * Data transfer object for responding to downtime reports
 * 异常响应数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DowntimeRespondDTO {
    /**
     * Responder ID (required)
     * 响应人（必填）
     */
    @NotBlank(message = "Responder ID cannot be empty")
    private String responderId;

    /**
     * Initial response notes
     * 响应备注
     */
    private String responseNotes;
}
