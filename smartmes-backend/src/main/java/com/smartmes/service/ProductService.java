package com.smartmes.service;

import com.smartmes.entity.Product;

import java.util.List;

/**
 * 产品管理服务接口
 * 提供产品的基础管理功能
 *
 * @author SmartMES Team
 * @version 1.0.0
 */
public interface ProductService {

    /**
     * 创建新产品
     * @param product 产品信息
     * @return 创建的产品
     */
    Product createProduct(Product product);

    /**
     * 更新产品信息
     * @param productCode 产品编号
     * @param product 产品信息
     * @return 更新后的产品
     */
    Product updateProduct(String productCode, Product product);

    /**
     * 删除产品
     * @param productCode 产品编号
     */
    void deleteProduct(String productCode);

    /**
     * 根据产品编号获取产品
     * @param productCode 产品编号
     * @return 产品信息
     */
    Product getProductById(String productCode);

    /**
     * 根据产品编号获取产品
     * @param productCode 产品编号
     * @return 产品信息
     */
    Product getProductByCode(String productCode);

    /**
     * 获取所有产品列表
     * @return 产品列表
     */
    List<Product> getAllProducts();

    /**
     * 根据产品名称搜索产品
     * @param productName 产品名称
     * @return 产品列表
     */
    List<Product> searchProductsByName(String productName);
}
