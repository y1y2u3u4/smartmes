package com.smartmes.service;

import com.smartmes.entity.Equipment;

import java.util.List;

/**
 * 设备管理服务接口
 * 提供设备的基础管理功能
 *
 * @author SmartMES Team
 * @version 1.0.0
 */
public interface EquipmentService {

    /**
     * 创建新设备
     * @param equipment 设备信息
     * @return 创建的设备
     */
    Equipment createEquipment(Equipment equipment);

    /**
     * 更新设备信息
     * @param id 设备ID
     * @param equipment 设备信息
     * @return 更新后的设备
     */
    Equipment updateEquipment(String id, Equipment equipment);

    /**
     * 删除设备
     * @param id 设备ID
     */
    void deleteEquipment(String id);

    /**
     * 根据ID获取设备
     * @param id 设备ID
     * @return 设备信息
     */
    Equipment getEquipmentById(String id);

    /**
     * 根据设备编号获取设备
     * @param equipmentId 设备编号
     * @return 设备信息
     */
    Equipment getEquipmentByCode(String equipmentId);

    /**
     * 获取所有设备列表
     * @return 设备列表
     */
    List<Equipment> getAllEquipments();

    /**
     * 根据产线ID获取设备列表
     * @param lineId 产线ID
     * @return 设备列表
     */
    List<Equipment> getEquipmentsByLineId(String lineId);
}
