package com.smartmes.repository;

import com.smartmes.entity.WorkOrder;
import com.smartmes.entity.WorkOrder.WorkOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 工单数据访问层接口
 * 提供工单数据的CRUD操作
 *
 * @author SmartMES Team
 * @version 1.0.0
 */
@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long>, JpaSpecificationExecutor<WorkOrder> {

    /**
     * 检查工单号是否存在
     * @param workOrderNo 工单号
     * @return 是否存在
     */
    boolean existsByWorkOrderNo(String workOrderNo);

    /**
     * 根据工单号查询工单
     * @param workOrderNo 工单号
     * @return 工单信息
     */
    Optional<WorkOrder> findByWorkOrderNo(String workOrderNo);

    /**
     * 根据工单状态查询工单列表
     * @param status 工单状态
     * @return 工单列表
     */
    List<WorkOrder> findByStatus(WorkOrderStatus status);

    /**
     * 根据产线ID查询工单列表
     * @param lineId 产线ID
     * @return 工单列表
     */
    List<WorkOrder> findByLineId(String lineId);

    /**
     * 统计指定状态的工单数量
     * @param status 工单状态
     * @return 工单数量
     */
    Long countByStatus(WorkOrderStatus status);

    /**
     * 查询指定时间范围内的工单
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 工单列表
     */
    @Query("SELECT w FROM WorkOrder w WHERE w.createTime BETWEEN :startTime AND :endTime")
    List<WorkOrder> findByCreateTimeBetween(@Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 查询今日工单
     * @param startOfDay 当天开始时间
     * @param endOfDay 当天结束时间
     * @return 工单列表
     */
    @Query("SELECT w FROM WorkOrder w WHERE w.createTime >= :startOfDay AND w.createTime <= :endOfDay")
    List<WorkOrder> findTodayWorkOrders(@Param("startOfDay") LocalDateTime startOfDay,
                                        @Param("endOfDay") LocalDateTime endOfDay);

    /**
     * 统计今日指定状态的工单数量
     * @param status 工单状态
     * @param startOfDay 当天开始时间
     * @param endOfDay 当天结束时间
     * @return 工单数量
     */
    @Query("SELECT COUNT(w) FROM WorkOrder w WHERE w.status = :status AND w.createTime >= :startOfDay AND w.createTime <= :endOfDay")
    Long countTodayWorkOrdersByStatus(@Param("status") WorkOrderStatus status,
                                      @Param("startOfDay") LocalDateTime startOfDay,
                                      @Param("endOfDay") LocalDateTime endOfDay);

    /**
     * 统计今日计划产量总数
     * @param startOfDay 当天开始时间
     * @param endOfDay 当天结束时间
     * @return 计划产量总数
     */
    @Query("SELECT COALESCE(SUM(w.planQty), 0) FROM WorkOrder w WHERE w.createTime >= :startOfDay AND w.createTime <= :endOfDay")
    Integer sumTodayPlanQty(@Param("startOfDay") LocalDateTime startOfDay,
                           @Param("endOfDay") LocalDateTime endOfDay);

    /**
     * 统计今日实际产量总数
     * @param startOfDay 当天开始时间
     * @param endOfDay 当天结束时间
     * @return 实际产量总数
     */
    @Query("SELECT COALESCE(SUM(w.actualQty), 0) FROM WorkOrder w WHERE w.createTime >= :startOfDay AND w.createTime <= :endOfDay")
    Integer sumTodayActualQty(@Param("startOfDay") LocalDateTime startOfDay,
                             @Param("endOfDay") LocalDateTime endOfDay);

    /**
     * 查询进行中的工单（按优先级和计划开始时间排序）
     * @return 工单列表
     */
    @Query("SELECT w FROM WorkOrder w WHERE w.status = 'IN_PROGRESS' ORDER BY w.priority DESC, w.planStartTime ASC")
    List<WorkOrder> findInProgressWorkOrders();
}
