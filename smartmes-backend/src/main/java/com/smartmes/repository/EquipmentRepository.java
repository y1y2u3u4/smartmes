package com.smartmes.repository;

import com.smartmes.entity.Equipment;
import com.smartmes.entity.Equipment.EquipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 设备数据访问层接口
 * 提供设备数据的CRUD操作
 *
 * @author SmartMES Team
 * @version 1.0.0
 */
@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    /**
     * 根据设备编号查询设备
     * @param equipmentId 设备编号
     * @return 设备信息
     */
    Optional<Equipment> findByEquipmentId(String equipmentId);

    /**
     * 根据设备状态查询设备列表
     * @param status 设备状态
     * @return 设备列表
     */
    List<Equipment> findByStatus(EquipmentStatus status);

    /**
     * 根据产线ID查询设备列表
     * @param lineId 产线ID
     * @return 设备列表
     */
    List<Equipment> findByLineId(String lineId);

    /**
     * 统计指定状态的设备数量
     * @param status 设备状态
     * @return 设备数量
     */
    Long countByStatus(EquipmentStatus status);

    /**
     * 根据设备类型查询设备列表
     * @param equipmentType 设备类型
     * @return 设备列表
     */
    List<Equipment> findByEquipmentType(String equipmentType);

    /**
     * 查询所有设备并按状态排序
     * @return 设备列表
     */
    List<Equipment> findAllByOrderByStatusAsc();
}
