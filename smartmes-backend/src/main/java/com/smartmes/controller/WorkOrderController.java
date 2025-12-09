package com.smartmes.controller;

import com.smartmes.common.PageResult;
import com.smartmes.common.Result;
import com.smartmes.entity.WorkOrder;
import com.smartmes.entity.WorkOrder.WorkOrderStatus;
import com.smartmes.service.WorkOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 工单管理控制器
 *
 * @author SmartMES Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/workorders")
@RequiredArgsConstructor
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    /**
     * 创建工单
     *
     * @param workOrder 工单信息
     * @return 创建的工单
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Result<WorkOrder> createWorkOrder(@Valid @RequestBody WorkOrder workOrder) {
        log.info("REST request to create work order: {}", workOrder.getId());
        try {
            WorkOrder created = workOrderService.createWorkOrder(workOrder);
            return Result.success("Work order created successfully", created);
        } catch (IllegalArgumentException e) {
            log.error("Failed to create work order: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("Failed to create work order", e);
            return Result.error("Failed to create work order: " + e.getMessage());
        }
    }

    /**
     * 查询工单详情
     *
     * @param id 工单ID
     * @return 工单信息
     */
    @GetMapping("/{id}")
    public Result<WorkOrder> getWorkOrder(@PathVariable String id) {
        log.info("REST request to get work order: {}", id);
        try {
            WorkOrder workOrder = workOrderService.getWorkOrderById(id);
            return Result.success(workOrder);
        } catch (IllegalArgumentException e) {
            log.error("Work order not found: {}", id);
            return Result.error(404, e.getMessage());
        } catch (Exception e) {
            log.error("Failed to get work order", e);
            return Result.error("Failed to get work order: " + e.getMessage());
        }
    }

    /**
     * 根据工单号查询工单
     *
     * @param workOrderNo 工单号
     * @return 工单信息
     */
    @GetMapping("/no/{workOrderNo}")
    public Result<WorkOrder> getWorkOrderByNo(@PathVariable String workOrderNo) {
        log.info("REST request to get work order by number: {}", workOrderNo);
        try {
            WorkOrder workOrder = workOrderService.getWorkOrderByNo(workOrderNo);
            return Result.success(workOrder);
        } catch (IllegalArgumentException e) {
            log.error("Work order not found: {}", workOrderNo);
            return Result.error(404, e.getMessage());
        } catch (Exception e) {
            log.error("Failed to get work order", e);
            return Result.error("Failed to get work order: " + e.getMessage());
        }
    }

    /**
     * 查询工单列表
     *
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页记录数
     * @param sortBy 排序字段
     * @param sortDir 排序方向（ASC/DESC）
     * @return 工单分页列表
     */
    @GetMapping
    public Result<PageResult<WorkOrder>> listWorkOrders(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

        log.info("REST request to list work orders - page: {}, size: {}", pageNum, pageSize);
        try {
            Sort.Direction direction = "ASC".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(direction, sortBy));
            PageResult<WorkOrder> result = workOrderService.listWorkOrders(pageable);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to list work orders", e);
            return Result.error("Failed to list work orders: " + e.getMessage());
        }
    }

    /**
     * 条件查询工单列表
     *
     * @param productCode 产品编号（可选）
     * @param status 工单状态（可选）
     * @param lineId 产线ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 工单分页列表
     */
    @GetMapping("/search")
    public Result<PageResult<WorkOrder>> searchWorkOrders(
            @RequestParam(required = false) String productCode,
            @RequestParam(required = false) WorkOrderStatus status,
            @RequestParam(required = false) String lineId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

        log.info("REST request to search work orders - productCode: {}, status: {}, lineId: {}",
                 productCode, status, lineId);
        try {
            Sort.Direction direction = "ASC".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(direction, sortBy));
            PageResult<WorkOrder> result = workOrderService.searchWorkOrders(
                    productCode, status, lineId, startTime, endTime, pageable);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to search work orders", e);
            return Result.error("Failed to search work orders: " + e.getMessage());
        }
    }

    /**
     * 更新工单
     *
     * @param id 工单ID
     * @param workOrder 更新的工单信息
     * @return 更新后的工单
     */
    @PutMapping("/{id}")
    public Result<WorkOrder> updateWorkOrder(@PathVariable String id, @RequestBody WorkOrder workOrder) {
        log.info("REST request to update work order: {}", id);
        try {
            WorkOrder updated = workOrderService.updateWorkOrder(id, workOrder);
            return Result.success("Work order updated successfully", updated);
        } catch (IllegalArgumentException e) {
            log.error("Work order not found: {}", id);
            return Result.error(404, e.getMessage());
        } catch (Exception e) {
            log.error("Failed to update work order", e);
            return Result.error("Failed to update work order: " + e.getMessage());
        }
    }

    /**
     * 删除工单
     *
     * @param id 工单ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteWorkOrder(@PathVariable String id) {
        log.info("REST request to delete work order: {}", id);
        try {
            workOrderService.deleteWorkOrder(id);
            return Result.success("Work order deleted successfully", null);
        } catch (IllegalArgumentException e) {
            log.error("Work order not found: {}", id);
            return Result.error(404, e.getMessage());
        } catch (IllegalStateException e) {
            log.error("Cannot delete work order: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("Failed to delete work order", e);
            return Result.error("Failed to delete work order: " + e.getMessage());
        }
    }

    /**
     * 开始工单
     *
     * @param id 工单ID
     * @return 更新后的工单
     */
    @PutMapping("/{id}/start")
    public Result<WorkOrder> startWorkOrder(@PathVariable String id) {
        log.info("REST request to start work order: {}", id);
        try {
            WorkOrder workOrder = workOrderService.startWorkOrder(id);
            return Result.success("Work order started successfully", workOrder);
        } catch (IllegalArgumentException e) {
            log.error("Work order not found: {}", id);
            return Result.error(404, e.getMessage());
        } catch (IllegalStateException e) {
            log.error("Cannot start work order: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("Failed to start work order", e);
            return Result.error("Failed to start work order: " + e.getMessage());
        }
    }

    /**
     * 完成工单
     *
     * @param id 工单ID
     * @param request 完成请求参数（actualQty）
     * @return 更新后的工单
     */
    @PutMapping("/{id}/complete")
    public Result<WorkOrder> completeWorkOrder(@PathVariable String id, @RequestBody Map<String, Integer> request) {
        log.info("REST request to complete work order: {}", id);
        try {
            Integer actualQty = request.get("actualQty");
            WorkOrder workOrder = workOrderService.completeWorkOrder(id, actualQty);
            return Result.success("Work order completed successfully", workOrder);
        } catch (IllegalArgumentException e) {
            log.error("Work order not found: {}", id);
            return Result.error(404, e.getMessage());
        } catch (IllegalStateException e) {
            log.error("Cannot complete work order: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("Failed to complete work order", e);
            return Result.error("Failed to complete work order: " + e.getMessage());
        }
    }

    /**
     * 取消工单
     *
     * @param id 工单ID
     * @return 更新后的工单
     */
    @PutMapping("/{id}/cancel")
    public Result<WorkOrder> cancelWorkOrder(@PathVariable String id) {
        log.info("REST request to cancel work order: {}", id);
        try {
            WorkOrder workOrder = workOrderService.cancelWorkOrder(id);
            return Result.success("Work order cancelled successfully", workOrder);
        } catch (IllegalArgumentException e) {
            log.error("Work order not found: {}", id);
            return Result.error(404, e.getMessage());
        } catch (IllegalStateException e) {
            log.error("Cannot cancel work order: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("Failed to cancel work order", e);
            return Result.error("Failed to cancel work order: " + e.getMessage());
        }
    }

    /**
     * 标记工单异常
     *
     * @param id 工单ID
     * @return 更新后的工单
     */
    @PutMapping("/{id}/abnormal")
    public Result<WorkOrder> markAbnormal(@PathVariable String id) {
        log.info("REST request to mark work order as abnormal: {}", id);
        try {
            WorkOrder workOrder = workOrderService.markAbnormal(id);
            return Result.success("Work order marked as abnormal successfully", workOrder);
        } catch (IllegalArgumentException e) {
            log.error("Work order not found: {}", id);
            return Result.error(404, e.getMessage());
        } catch (IllegalStateException e) {
            log.error("Cannot mark work order as abnormal: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("Failed to mark work order as abnormal", e);
            return Result.error("Failed to mark work order as abnormal: " + e.getMessage());
        }
    }

    /**
     * 更新工单进度
     *
     * @param id 工单ID
     * @param request 进度请求参数（actualQty）
     * @return 更新后的工单
     */
    @PutMapping("/{id}/progress")
    public Result<WorkOrder> updateProgress(@PathVariable String id, @RequestBody Map<String, Integer> request) {
        log.info("REST request to update work order progress: {}", id);
        try {
            Integer actualQty = request.get("actualQty");
            WorkOrder workOrder = workOrderService.updateProgress(id, actualQty);
            return Result.success("Work order progress updated successfully", workOrder);
        } catch (IllegalArgumentException e) {
            log.error("Work order not found: {}", id);
            return Result.error(404, e.getMessage());
        } catch (IllegalStateException e) {
            log.error("Cannot update work order progress: {}", e.getMessage());
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("Failed to update work order progress", e);
            return Result.error("Failed to update work order progress: " + e.getMessage());
        }
    }

    /**
     * 统计指定状态的工单数量
     *
     * @param status 工单状态
     * @return 工单数量
     */
    @GetMapping("/count/{status}")
    public Result<Long> countByStatus(@PathVariable WorkOrderStatus status) {
        log.info("REST request to count work orders by status: {}", status);
        try {
            Long count = workOrderService.countByStatus(status);
            return Result.success(count);
        } catch (Exception e) {
            log.error("Failed to count work orders by status", e);
            return Result.error("Failed to count work orders by status: " + e.getMessage());
        }
    }

    /**
     * 查询指定产线的进行中工单
     *
     * @param lineId 产线ID
     * @return 进行中的工单列表
     */
    @GetMapping("/line/{lineId}/in-progress")
    public Result<List<WorkOrder>> getInProgressWorkOrdersByLine(@PathVariable String lineId) {
        log.info("REST request to get in-progress work orders for line: {}", lineId);
        try {
            List<WorkOrder> workOrders = workOrderService.getInProgressWorkOrdersByLine(lineId);
            return Result.success(workOrders);
        } catch (Exception e) {
            log.error("Failed to get in-progress work orders", e);
            return Result.error("Failed to get in-progress work orders: " + e.getMessage());
        }
    }

    /**
     * 检查工单号是否存在
     *
     * @param workOrderNo 工单号
     * @return 是否存在
     */
    @GetMapping("/exists/{workOrderNo}")
    public Result<Boolean> checkWorkOrderNoExists(@PathVariable String workOrderNo) {
        log.info("REST request to check if work order number exists: {}", workOrderNo);
        try {
            boolean exists = workOrderService.existsByWorkOrderNo(workOrderNo);
            return Result.success(exists);
        } catch (Exception e) {
            log.error("Failed to check work order number existence", e);
            return Result.error("Failed to check work order number existence: " + e.getMessage());
        }
    }
}
