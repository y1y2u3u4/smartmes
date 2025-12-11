package com.smartmes.controller;

import com.smartmes.dto.ApiResponse;
import com.smartmes.entity.Equipment;
import com.smartmes.entity.Product;
import com.smartmes.service.EquipmentService;
import com.smartmes.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 基础数据管理控制器
 * 提供设备和产品的基础管理API接口
 *
 * @author SmartMES Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/base-data")
@RequiredArgsConstructor
@Slf4j
public class BaseDataController {

    private final EquipmentService equipmentService;
    private final ProductService productService;

    // ==================== 设备管理 API ====================

    /**
     * 创建新设备
     * POST /api/base-data/equipment
     *
     * @param equipment 设备信息
     * @return 创建的设备
     */
    @PostMapping("/equipment")
    public ApiResponse<Equipment> createEquipment(@RequestBody Equipment equipment) {
        log.info("接收请求: 创建设备 - {}", equipment.getEquipmentId());
        try {
            Equipment created = equipmentService.createEquipment(equipment);
            return ApiResponse.success("Equipment created successfully", created);
        } catch (Exception e) {
            log.error("创建设备失败", e);
            return ApiResponse.error("Failed to create equipment: " + e.getMessage());
        }
    }

    /**
     * 更新设备信息
     * PUT /api/base-data/equipment/{id}
     *
     * @param id 设备ID
     * @param equipment 设备信息
     * @return 更新后的设备
     */
    @PutMapping("/equipment/{id}")
    public ApiResponse<Equipment> updateEquipment(@PathVariable String id, @RequestBody Equipment equipment) {
        log.info("接收请求: 更新设备 - ID={}", id);
        try {
            Equipment updated = equipmentService.updateEquipment(id, equipment);
            return ApiResponse.success("Equipment updated successfully", updated);
        } catch (Exception e) {
            log.error("更新设备失败", e);
            return ApiResponse.error("Failed to update equipment: " + e.getMessage());
        }
    }

    /**
     * 删除设备
     * DELETE /api/base-data/equipment/{id}
     *
     * @param id 设备ID
     * @return 操作结果
     */
    @DeleteMapping("/equipment/{id}")
    public ApiResponse<Void> deleteEquipment(@PathVariable String id) {
        log.info("接收请求: 删除设备 - ID={}", id);
        try {
            equipmentService.deleteEquipment(id);
            return ApiResponse.success("Equipment deleted successfully", null);
        } catch (Exception e) {
            log.error("删除设备失败", e);
            return ApiResponse.error("Failed to delete equipment: " + e.getMessage());
        }
    }

    /**
     * 获取设备详情
     * GET /api/base-data/equipment/{id}
     *
     * @param id 设备ID
     * @return 设备信息
     */
    @GetMapping("/equipment/{id}")
    public ApiResponse<Equipment> getEquipment(@PathVariable String id) {
        log.info("接收请求: 获取设备详情 - ID={}", id);
        try {
            Equipment equipment = equipmentService.getEquipmentById(id);
            return ApiResponse.success(equipment);
        } catch (Exception e) {
            log.error("获取设备详情失败", e);
            return ApiResponse.error("Failed to get equipment: " + e.getMessage());
        }
    }

    /**
     * 获取所有设备列表
     * GET /api/base-data/equipment
     *
     * @return 设备列表
     */
    @GetMapping("/equipment")
    public ApiResponse<List<Equipment>> getAllEquipments() {
        log.info("接收请求: 获取所有设备列表");
        try {
            List<Equipment> equipments = equipmentService.getAllEquipments();
            return ApiResponse.success(equipments);
        } catch (Exception e) {
            log.error("获取设备列表失败", e);
            return ApiResponse.error("Failed to get equipment list: " + e.getMessage());
        }
    }

    /**
     * 根据产线获取设备列表
     * GET /api/base-data/equipment/by-line/{lineId}
     *
     * @param lineId 产线ID
     * @return 设备列表
     */
    @GetMapping("/equipment/by-line/{lineId}")
    public ApiResponse<List<Equipment>> getEquipmentsByLine(@PathVariable String lineId) {
        log.info("接收请求: 获取产线设备列表 - LineID={}", lineId);
        try {
            List<Equipment> equipments = equipmentService.getEquipmentsByLineId(lineId);
            return ApiResponse.success(equipments);
        } catch (Exception e) {
            log.error("获取产线设备列表失败", e);
            return ApiResponse.error("Failed to get equipment list by line: " + e.getMessage());
        }
    }

    // ==================== 产品管理 API ====================

    /**
     * 创建新产品
     * POST /api/base-data/product
     *
     * @param product 产品信息
     * @return 创建的产品
     */
    @PostMapping("/product")
    public ApiResponse<Product> createProduct(@RequestBody Product product) {
        log.info("接收请求: 创建产品 - {}", product.getProductCode());
        try {
            Product created = productService.createProduct(product);
            return ApiResponse.success("Product created successfully", created);
        } catch (Exception e) {
            log.error("创建产品失败", e);
            return ApiResponse.error("Failed to create product: " + e.getMessage());
        }
    }

    /**
     * 更新产品信息
     * PUT /api/base-data/product/{productCode}
     *
     * @param productCode 产品编号
     * @param product 产品信息
     * @return 更新后的产品
     */
    @PutMapping("/product/{productCode}")
    public ApiResponse<Product> updateProduct(@PathVariable String productCode, @RequestBody Product product) {
        log.info("接收请求: 更新产品 - productCode={}", productCode);
        try {
            Product updated = productService.updateProduct(productCode, product);
            return ApiResponse.success("Product updated successfully", updated);
        } catch (Exception e) {
            log.error("更新产品失败", e);
            return ApiResponse.error("Failed to update product: " + e.getMessage());
        }
    }

    /**
     * 删除产品
     * DELETE /api/base-data/product/{productCode}
     *
     * @param productCode 产品编号
     * @return 操作结果
     */
    @DeleteMapping("/product/{productCode}")
    public ApiResponse<Void> deleteProduct(@PathVariable String productCode) {
        log.info("接收请求: 删除产品 - productCode={}", productCode);
        try {
            productService.deleteProduct(productCode);
            return ApiResponse.success("Product deleted successfully", null);
        } catch (Exception e) {
            log.error("删除产品失败", e);
            return ApiResponse.error("Failed to delete product: " + e.getMessage());
        }
    }

    /**
     * 获取产品详情
     * GET /api/base-data/product/{productCode}
     *
     * @param productCode 产品编号
     * @return 产品信息
     */
    @GetMapping("/product/{productCode}")
    public ApiResponse<Product> getProduct(@PathVariable String productCode) {
        log.info("接收请求: 获取产品详情 - productCode={}", productCode);
        try {
            Product product = productService.getProductById(productCode);
            return ApiResponse.success(product);
        } catch (Exception e) {
            log.error("获取产品详情失败", e);
            return ApiResponse.error("Failed to get product: " + e.getMessage());
        }
    }

    /**
     * 获取所有产品列表
     * GET /api/base-data/product
     *
     * @return 产品列表
     */
    @GetMapping("/product")
    public ApiResponse<List<Product>> getAllProducts() {
        log.info("接收请求: 获取所有产品列表");
        try {
            List<Product> products = productService.getAllProducts();
            return ApiResponse.success(products);
        } catch (Exception e) {
            log.error("获取产品列表失败", e);
            return ApiResponse.error("Failed to get product list: " + e.getMessage());
        }
    }

    /**
     * 搜索产品
     * GET /api/base-data/product/search?name=xxx
     *
     * @param name 产品名称关键字
     * @return 产品列表
     */
    @GetMapping("/product/search")
    public ApiResponse<List<Product>> searchProducts(@RequestParam String name) {
        log.info("接收请求: 搜索产品 - 关键字={}", name);
        try {
            List<Product> products = productService.searchProductsByName(name);
            return ApiResponse.success(products);
        } catch (Exception e) {
            log.error("搜索产品失败", e);
            return ApiResponse.error("Failed to search products: " + e.getMessage());
        }
    }
}
