package com.smartmes.service;

import com.smartmes.common.PageResult;
import com.smartmes.dto.*;
import com.smartmes.entity.DowntimeReport;

/**
 * Downtime Service Interface
 * Business logic for downtime reports
 * 异常停机业务接口
 */
public interface DowntimeService {
    /**
     * Report downtime incident
     * 上报异常停机
     *
     * @param reportDTO Downtime report data
     * @return Created downtime report
     */
    DowntimeReport reportDowntime(DowntimeReportDTO reportDTO);

    /**
     * Query downtime reports with pagination
     * 分页查询异常停机列表
     *
     * @param queryDTO Query parameters
     * @return Paginated result
     */
    PageResult<DowntimeReport> queryReports(DowntimeQueryDTO queryDTO);

    /**
     * Get downtime report details by ID
     * 根据ID查询异常详情
     *
     * @param reportId Report ID
     * @return Downtime report
     */
    DowntimeReport getReportById(Long reportId);

    /**
     * Respond to downtime report
     * 响应异常停机
     *
     * @param reportId Report ID
     * @param respondDTO Response data
     * @return Updated downtime report
     */
    DowntimeReport respondToDowntime(Long reportId, DowntimeRespondDTO respondDTO);

    /**
     * Resolve downtime report
     * 解决异常停机
     *
     * @param reportId Report ID
     * @param resolveDTO Resolution data
     * @return Updated downtime report
     */
    DowntimeReport resolveDowntime(Long reportId, DowntimeResolveDTO resolveDTO);

    /**
     * Get downtime statistics
     * 获取异常统计数据
     *
     * @return Statistics data
     */
    DowntimeStatisticsDTO getStatistics();

    /**
     * Delete downtime report
     * 删除异常停机记录
     *
     * @param reportId Report ID
     * @return Success flag
     */
    boolean deleteReport(Long reportId);
}
