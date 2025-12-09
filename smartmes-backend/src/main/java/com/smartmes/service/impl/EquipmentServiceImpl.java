package com.smartmes.service.impl;

import com.smartmes.entity.Equipment;
import com.smartmes.repository.EquipmentRepository;
import com.smartmes.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 设备管理服务实现类
 * 实现设备的CRUD等基础管理功能
 *
 * @author SmartMES Team
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;

    @Override
    public Equipment createEquipment(Equipment equipment) {
        log.info("创建设备: {}", equipment.getEquipmentId());

        // 检查设备编号是否已存在
        if (equipmentRepository.findByEquipmentId(equipment.getEquipmentId()).isPresent()) {
            throw new RuntimeException("Equipment ID already exists: " + equipment.getEquipmentId());
        }

        Equipment saved = equipmentRepository.save(equipment);
        log.info("设备创建成功: {}", saved.getEquipmentId());
        return saved;
    }

    @Override
    public Equipment updateEquipment(String id, Equipment equipment) {
        log.info("更新设备: ID={}", id);

        Equipment existing = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found with ID: " + id));

        // 更新字段
        existing.setEquipmentName(equipment.getEquipmentName());
        existing.setEquipmentType(equipment.getEquipmentType());
        existing.setLineId(equipment.getLineId());
        existing.setStatus(equipment.getStatus());
        existing.setLocation(equipment.getLocation());
        existing.setLastMaintenanceTime(equipment.getLastMaintenanceTime());
        existing.setNextMaintenanceTime(equipment.getNextMaintenanceTime());
        existing.setRemarks(equipment.getRemarks());

        Equipment updated = equipmentRepository.save(existing);
        log.info("设备更新成功: {}", updated.getEquipmentId());
        return updated;
    }

    @Override
    public void deleteEquipment(String id) {
        log.info("删除设备: ID={}", id);

        if (!equipmentRepository.existsById(id)) {
            throw new RuntimeException("Equipment not found with ID: " + id);
        }

        equipmentRepository.deleteById(id);
        log.info("设备删除成功: ID={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Equipment getEquipmentById(String id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Equipment getEquipmentByCode(String equipmentId) {
        return equipmentRepository.findByEquipmentId(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found with code: " + equipmentId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Equipment> getAllEquipments() {
        return equipmentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Equipment> getEquipmentsByLineId(String lineId) {
        return equipmentRepository.findByLineId(lineId);
    }
}
