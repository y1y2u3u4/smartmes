package com.smartmes.repository;

import com.smartmes.entity.Product;
import com.smartmes.entity.Product.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 产品数据访问层接口
 * 提供产品数据的CRUD操作
 *
 * @author SmartMES Team
 * @version 1.0.0
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 根据产品编号查询产品
     * @param productCode 产品编号
     * @return 产品信息
     */
    Optional<Product> findByProductCode(String productCode);

    /**
     * 根据产品名称查询产品列表
     * @param productName 产品名称
     * @return 产品列表
     */
    List<Product> findByProductNameContaining(String productName);

    /**
     * 根据产品状态查询产品列表
     * @param status 产品状态
     * @return 产品列表
     */
    List<Product> findByStatus(ProductStatus status);

    /**
     * 根据产品类型查询产品列表
     * @param productType 产品类型
     * @return 产品列表
     */
    List<Product> findByProductType(String productType);

    /**
     * 查询所有正常使用的产品
     * @return 产品列表
     */
    List<Product> findByStatusOrderByProductCodeAsc(ProductStatus status);
}
