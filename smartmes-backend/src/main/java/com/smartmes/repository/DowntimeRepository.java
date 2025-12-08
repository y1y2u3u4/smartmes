package com.smartmes.repository;

import com.smartmes.entity.Downtime;
import com.smartmes.entity.Downtime.DowntimeStatus;
import com.smartmes.entity.Downtime.DowntimeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 停机/异常记录数据访问层接口
 * 提供停机异常数据的CRUD操作
 *
 * @author SmartMES Team
 * @version 1.0.0
 */
@Repository
public interface DowntimeRepository extends JpaRepository<Downtime, Long> {

    /**
     * 根据设备ID查询停机记录
     * @param equipmentId 设备ID
     * @return 停机记录列表
     */
    List<Downtime> findByEquipmentId(Long equipmentId);

    /**
     * 根据工单ID查询停机记录
     * @param workOrderId 工单ID
     * @return 停机记录列表
     */
    List<Downtime> findByWorkOrderId(Long workOrderId);

    /**
     * 根据异常状态查询停机记录
     * @param status 异常状态
     * @return 停机记录列表
     */
    List<Downtime> findByStatus(DowntimeStatus status);

    /**
     * 查询指定时间范围内的停机记录
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 停机记录列表
     */
    @Query("SELECT d FROM Downtime d WHERE d.startTime >= :startTime AND d.startTime <= :endTime")
    List<Downtime> findByStartTimeBetween(@Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime);

    /**
     * 统计今日异常次数
     * @param startOfDay 当天开始时间
     * @param endOfDay 当天结束时间
     * @return 异常次数
     */
    @Query("SELECT COUNT(d) FROM Downtime d WHERE d.startTime >= :startOfDay AND d.startTime <= :endOfDay")
    Long countTodayDowntime(@Param("startOfDay") LocalDateTime startOfDay,
                           @Param("endOfDay") LocalDateTime endOfDay);

    /**
     * 统计今日总停机时长（分钟）
     * @param startOfDay 当天开始时间
     * @param endOfDay 当天结束时间
     * @return 总停机时长
     */
    @Query("SELECT COALESCE(SUM(d.durationMinutes), 0) FROM Downtime d WHERE d.startTime >= :startOfDay AND d.startTime <= :endOfDay")
    Integer sumTodayDowntimeMinutes(@Param("startOfDay") LocalDateTime startOfDay,
                                   @Param("endOfDay") LocalDateTime endOfDay);

    /**
     * 按异常类型统计今日异常次数
     * @param startOfDay 当天开始时间
     * @param endOfDay 当天结束时间
     * @return 异常类型和对应次数的列表
     */
    @Query("SELECT d.downtimeType, COUNT(d) FROM Downtime d WHERE d.startTime >= :startOfDay AND d.startTime <= :endOfDay GROUP BY d.downtimeType")
    List<Object[]> countTodayDowntimeByType(@Param("startOfDay") LocalDateTime startOfDay,
                                           @Param("endOfDay") LocalDateTime endOfDay);

    /**
     * 查询今日故障TOP5设备
     * @param startOfDay 当天开始时间
     * @param endOfDay 当天结束时间
     * @return 设备故障统计列表（设备编号、设备名称、故障次数、总停机时长）
     */
    @Query("SELECT d.equipmentCode, d.equipmentName, COUNT(d), COALESCE(SUM(d.durationMinutes), 0) " +
           "FROM Downtime d " +
           "WHERE d.startTime >= :startOfDay AND d.startTime <= :endOfDay " +
           "GROUP BY d.equipmentCode, d.equipmentName " +
           "ORDER BY COUNT(d) DESC")
    List<Object[]> findTopFaultyEquipmentToday(@Param("startOfDay") LocalDateTime startOfDay,
                                               @Param("endOfDay") LocalDateTime endOfDay);

    /**
     * 根据异常类型查询停机记录
     * @param downtimeType 异常类型
     * @return 停机记录列表
     */
    List<Downtime> findByDowntimeType(DowntimeType downtimeType);

    /**
     * 查询进行中的异常（未解决的）
     * @return 停机记录列表
     */
    @Query("SELECT d FROM Downtime d WHERE d.status = 'ONGOING' ORDER BY d.startTime DESC")
    List<Downtime> findOngoingDowntime();
}
