package com.smartmes.mapper;

import com.smartmes.dto.DowntimeQueryDTO;
import com.smartmes.dto.DowntimeStatisticsDTO;
import com.smartmes.entity.DowntimeReport;
import com.smartmes.enums.DowntimeStatus;
import com.smartmes.enums.DowntimeType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Downtime Report Mapper
 * MyBatis Mapper interface for downtime reports
 * 异常停机Mapper接口
 */
@Mapper
public interface DowntimeMapper {
    /**
     * Insert downtime report
     * 插入异常停机记录
     *
     * @param report Downtime report
     * @return Number of affected rows
     */
    int insert(DowntimeReport report);

    /**
     * Update downtime report
     * 更新异常停机记录
     *
     * @param report Downtime report
     * @return Number of affected rows
     */
    int update(DowntimeReport report);

    /**
     * Find downtime report by ID
     * 根据ID查询异常停机记录
     *
     * @param reportId Report ID
     * @return Downtime report
     */
    DowntimeReport findById(@Param("reportId") Long reportId);

    /**
     * Query downtime reports with filters
     * 条件查询异常停机列表
     *
     * @param queryDTO Query parameters
     * @return List of downtime reports
     */
    List<DowntimeReport> findByConditions(DowntimeQueryDTO queryDTO);

    /**
     * Count downtime reports with filters
     * 条件查询异常停机总数
     *
     * @param queryDTO Query parameters
     * @return Total count
     */
    Long countByConditions(DowntimeQueryDTO queryDTO);

    /**
     * Update status
     * 更新状态
     *
     * @param reportId Report ID
     * @param status New status
     * @return Number of affected rows
     */
    int updateStatus(@Param("reportId") Long reportId, @Param("status") DowntimeStatus status);

    /**
     * Update responder
     * 更新响应人
     *
     * @param reportId Report ID
     * @param responderId Responder ID
     * @return Number of affected rows
     */
    int updateResponder(@Param("reportId") Long reportId, @Param("responderId") String responderId);

    /**
     * Update resolution
     * 更新解决信息
     *
     * @param report Downtime report with resolution info
     * @return Number of affected rows
     */
    int updateResolution(DowntimeReport report);

    /**
     * Count total reports
     * 统计异常总数
     *
     * @return Total count
     */
    Long countTotal();

    /**
     * Sum total duration
     * 统计总停机时长
     *
     * @return Total duration in minutes
     */
    Long sumTotalDuration();

    /**
     * Count by status
     * 按状态统计
     *
     * @param status Status
     * @return Count
     */
    Long countByStatus(@Param("status") DowntimeStatus status);

    /**
     * Count by type
     * 按类型统计
     *
     * @return Map of type and count
     */
    List<Map<String, Object>> countByType();

    /**
     * Get top equipment by incident count
     * 获取故障次数TOP设备
     *
     * @param limit Limit
     * @return List of equipment stats
     */
    List<DowntimeStatisticsDTO.EquipmentDowntimeStats> getTopEquipmentByIncidents(@Param("limit") Integer limit);

    /**
     * Get top equipment by duration
     * 获取停机时长TOP设备
     *
     * @param limit Limit
     * @return List of equipment stats
     */
    List<DowntimeStatisticsDTO.EquipmentDowntimeStats> getTopEquipmentByDuration(@Param("limit") Integer limit);

    /**
     * Delete downtime report
     * 删除异常停机记录
     *
     * @param reportId Report ID
     * @return Number of affected rows
     */
    int delete(@Param("reportId") Long reportId);
}
