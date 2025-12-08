package com.smartmes.service;

import com.smartmes.dto.DashboardData;
import com.smartmes.dto.DowntimeStatistics;
import com.smartmes.dto.EquipmentStatusData;
import com.smartmes.dto.ProductionOverview;
import com.smartmes.dto.WorkOrderProgress;

/**
 * 数据看板服务接口
 * 提供看板数据的业务逻辑处理
 *
 * @author SmartMES Team
 * @version 1.0.0
 */
public interface DashboardService {

    /**
     * 获取生产概览数据
     * @return 生产概览数据
     */
    ProductionOverview getProductionOverview();

    /**
     * 获取异常统计数据
     * @return 异常统计数据
     */
    DowntimeStatistics getDowntimeStatistics();

    /**
     * 获取工单进度数据
     * @return 工单进度数据
     */
    WorkOrderProgress getWorkOrderProgress();

    /**
     * 获取设备状态数据
     * @return 设备状态数据
     */
    EquipmentStatusData getEquipmentStatus();

    /**
     * 获取完整的看板数据
     * @return 完整看板数据
     */
    DashboardData getCompleteDashboardData();
}
