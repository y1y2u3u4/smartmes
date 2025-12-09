package com.smartmes.service.impl;

import com.smartmes.common.PageResult;
import com.smartmes.entity.WorkOrder;
import com.smartmes.entity.WorkOrder.WorkOrderStatus;
import com.smartmes.repository.WorkOrderRepository;
import com.smartmes.service.WorkOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 工单服务实现类
 *
 * @author SmartMES Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkOrderServiceImpl implements WorkOrderService {

    private final WorkOrderRepository workOrderRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WorkOrder createWorkOrder(WorkOrder workOrder) {
        log.info("Creating work order: {}", workOrder.getId());

        // 检查工单号是否已存在
        if (workOrder.getId() != null && workOrderRepository.existsById(workOrder.getId())) {
            throw new IllegalArgumentException("Work order number already exists: " + workOrder.getId());
        }

        // 设置初始状态
        if (workOrder.getStatus() == null) {
            workOrder.setStatus(WorkOrderStatus.PENDING);
        }

        // 设置初始产量
        if (workOrder.getActualQty() == null) {
            workOrder.setActualQty(0);
        }

        WorkOrder saved = workOrderRepository.save(workOrder);
        log.info("Work order created successfully: {}", saved.getId());
        return saved;
    }

    @Override
    public WorkOrder getWorkOrderById(String id) {
        log.debug("Getting work order by id: {}", id);
        return workOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Work order not found: " + id));
    }

    @Override
    public WorkOrder getWorkOrderByNo(String workOrderNo) {
        log.debug("Getting work order by number: {}", workOrderNo);
        return workOrderRepository.findById(workOrderNo)
                .orElseThrow(() -> new IllegalArgumentException("Work order not found: " + workOrderNo));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WorkOrder updateWorkOrder(String id, WorkOrder workOrder) {
        log.info("Updating work order: {}", id);

        WorkOrder existing = getWorkOrderById(id);

        // 更新可修改的字段
        if (workOrder.getProductCode() != null) {
            existing.setProductCode(workOrder.getProductCode());
        }
        if (workOrder.getLineId() != null) {
            existing.setLineId(workOrder.getLineId());
        }
        if (workOrder.getPlanQty() != null) {
            existing.setPlanQty(workOrder.getPlanQty());
        }
        if (workOrder.getBatchNo() != null) {
            existing.setBatchNo(workOrder.getBatchNo());
        }
        if (workOrder.getEquipmentId() != null) {
            existing.setEquipmentId(workOrder.getEquipmentId());
        }
        if (workOrder.getOperatorId() != null) {
            existing.setOperatorId(workOrder.getOperatorId());
        }

        WorkOrder updated = workOrderRepository.save(existing);
        log.info("Work order updated successfully: {}", id);
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWorkOrder(String id) {
        log.info("Deleting work order: {}", id);

        WorkOrder workOrder = getWorkOrderById(id);

        // 只能删除待开始或已取消的工单
        if (workOrder.getStatus() == WorkOrderStatus.IN_PROGRESS ||
            workOrder.getStatus() == WorkOrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot delete work order in status: " + workOrder.getStatus());
        }

        workOrderRepository.deleteById(id);
        log.info("Work order deleted successfully: {}", id);
    }

    @Override
    public PageResult<WorkOrder> listWorkOrders(Pageable pageable) {
        log.debug("Listing work orders with page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<WorkOrder> page = workOrderRepository.findAll(pageable);
        return PageResult.of(
                page.getContent(),
                page.getTotalElements(),
                page.getNumber() + 1,
                page.getSize()
        );
    }

    @Override
    public PageResult<WorkOrder> searchWorkOrders(
            String productCode,
            WorkOrderStatus status,
            String lineId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Pageable pageable) {

        log.debug("Searching work orders with filters - productCode: {}, status: {}, lineId: {}",
                  productCode, status, lineId);

        Specification<WorkOrder> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (productCode != null && !productCode.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("productCode"), productCode));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (lineId != null && !lineId.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("lineId"), lineId));
            }

            if (startTime != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startTime));
            }

            if (endTime != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endTime));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<WorkOrder> page = workOrderRepository.findAll(spec, pageable);
        return PageResult.of(
                page.getContent(),
                page.getTotalElements(),
                page.getNumber() + 1,
                page.getSize()
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WorkOrder startWorkOrder(String id) {
        log.info("Starting work order: {}", id);

        WorkOrder workOrder = getWorkOrderById(id);

        // 只有待开始状态的工单才能启动
        if (workOrder.getStatus() != WorkOrderStatus.PENDING) {
            throw new IllegalStateException("Cannot start work order in status: " + workOrder.getStatus());
        }

        workOrder.setStatus(WorkOrderStatus.IN_PROGRESS);
        workOrder.setStartTime(LocalDateTime.now());

        WorkOrder updated = workOrderRepository.save(workOrder);
        log.info("Work order started successfully: {}", id);
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WorkOrder completeWorkOrder(String id, Integer actualQty) {
        log.info("Completing work order: {}", id);

        WorkOrder workOrder = getWorkOrderById(id);

        // 只有进行中或异常状态的工单才能完成
        if (workOrder.getStatus() != WorkOrderStatus.IN_PROGRESS &&
            workOrder.getStatus() != WorkOrderStatus.ABNORMAL) {
            throw new IllegalStateException("Cannot complete work order in status: " + workOrder.getStatus());
        }

        workOrder.setStatus(WorkOrderStatus.COMPLETED);
        workOrder.setEndTime(LocalDateTime.now());
        workOrder.setActualQty(actualQty != null ? actualQty : workOrder.getActualQty());

        WorkOrder updated = workOrderRepository.save(workOrder);
        log.info("Work order completed successfully: {}", id);
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WorkOrder cancelWorkOrder(String id) {
        log.info("Cancelling work order: {}", id);

        WorkOrder workOrder = getWorkOrderById(id);

        // 只有待开始和进行中状态的工单才能取消
        if (workOrder.getStatus() == WorkOrderStatus.COMPLETED ||
            workOrder.getStatus() == WorkOrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel work order in status: " + workOrder.getStatus());
        }

        workOrder.setStatus(WorkOrderStatus.CANCELLED);

        WorkOrder updated = workOrderRepository.save(workOrder);
        log.info("Work order cancelled successfully: {}", id);
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WorkOrder markAbnormal(String id) {
        log.info("Marking work order as abnormal: {}", id);

        WorkOrder workOrder = getWorkOrderById(id);

        // 只有进行中状态的工单才能标记为异常
        if (workOrder.getStatus() != WorkOrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot mark work order as abnormal in status: " + workOrder.getStatus());
        }

        workOrder.setStatus(WorkOrderStatus.ABNORMAL);

        WorkOrder updated = workOrderRepository.save(workOrder);
        log.info("Work order marked as abnormal successfully: {}", id);
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WorkOrder updateProgress(String id, Integer actualQty) {
        log.info("Updating work order progress: {}", id);

        WorkOrder workOrder = getWorkOrderById(id);

        // 只有进行中状态的工单才能更新进度
        if (workOrder.getStatus() != WorkOrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot update progress for work order in status: " + workOrder.getStatus());
        }

        if (actualQty != null) {
            workOrder.setActualQty(actualQty);
        }

        WorkOrder updated = workOrderRepository.save(workOrder);
        log.info("Work order progress updated successfully: {}", id);
        return updated;
    }

    @Override
    public Long countByStatus(WorkOrderStatus status) {
        log.debug("Counting work orders by status: {}", status);
        return workOrderRepository.countByStatus(status);
    }

    @Override
    public List<WorkOrder> getInProgressWorkOrdersByLine(String lineId) {
        log.debug("Getting in-progress work orders for line: {}", lineId);
        List<WorkOrder> allWorkOrders = workOrderRepository.findByLineId(lineId);
        return allWorkOrders.stream()
                .filter(wo -> wo.getStatus() == WorkOrderStatus.IN_PROGRESS)
                .toList();
    }

    @Override
    public boolean existsByWorkOrderNo(String workOrderNo) {
        return workOrderRepository.existsById(workOrderNo);
    }
}
