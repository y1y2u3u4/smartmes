package com.smartmes.service;

import com.smartmes.common.PageResult;
import com.smartmes.entity.WorkOrder;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 工单服务接口
 *
 * @author SmartMES Team
 * @version 1.0.0
 */
public interface WorkOrderService {

    /**
     * 创建工单
     *
     * @param workOrder 工单信息
     * @return 创建的工单
     */
    WorkOrder createWorkOrder(WorkOrder workOrder);

    /**
     * 根据ID查询工单
     *
     * @param id 工单ID
     * @return 工单信息
     */
    WorkOrder getWorkOrderById(String id);

    /**
     * 根据工单号查询工单
     *
     * @param workOrderNo 工单号
     * @return 工单信息
     */
    WorkOrder getWorkOrderByNo(String workOrderNo);

    /**
     * 更新工单信息
     *
     * @param id 工单ID
     * @param workOrder 更新的工单信息
     * @return 更新后的工单
     */
    WorkOrder updateWorkOrder(String id, WorkOrder workOrder);

    /**
     * 删除工单
     *
     * @param id 工单ID
     */
    void deleteWorkOrder(String id);

    /**
     * 分页查询工单列表
     *
     * @param pageable 分页参数
     * @return 工单分页结果
     */
    PageResult<WorkOrder> listWorkOrders(Pageable pageable);

    /**
     * 根据条件查询工单列表
     *
     * @param productCode 产品编号（可选）
     * @param status 工单状态（可选）
     * @param lineId 产线ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param pageable 分页参数
     * @return 工单分页结果
     */
    PageResult<WorkOrder> searchWorkOrders(
            String productCode,
            WorkOrder.WorkOrderStatus status,
            String lineId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Pageable pageable
    );

    /**
     * 开始工单
     *
     * @param id 工单ID
     * @return 更新后的工单
     */
    WorkOrder startWorkOrder(String id);

    /**
     * 完成工单
     *
     * @param id 工单ID
     * @param actualQty 实际产量
     * @return 更新后的工单
     */
    WorkOrder completeWorkOrder(String id, Integer actualQty);

    /**
     * 取消工单
     *
     * @param id 工单ID
     * @return 更新后的工单
     */
    WorkOrder cancelWorkOrder(String id);

    /**
     * 标记工单异常
     *
     * @param id 工单ID
     * @return 更新后的工单
     */
    WorkOrder markAbnormal(String id);

    /**
     * 更新工单进度
     *
     * @param id 工单ID
     * @param actualQty 实际产量
     * @return 更新后的工单
     */
    WorkOrder updateProgress(String id, Integer actualQty);

    /**
     * 查询指定状态的工单数量
     *
     * @param status 工单状态
     * @return 工单数量
     */
    Long countByStatus(WorkOrder.WorkOrderStatus status);

    /**
     * 查询指定产线的进行中工单
     *
     * @param lineId 产线ID
     * @return 进行中的工单列表
     */
    List<WorkOrder> getInProgressWorkOrdersByLine(String lineId);

    /**
     * 检查工单号是否存在
     *
     * @param workOrderNo 工单号
     * @return true-存在，false-不存在
     */
    boolean existsByWorkOrderNo(String workOrderNo);
}
