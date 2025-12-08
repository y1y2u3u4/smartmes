package com.smartmes.service.impl;

import com.smartmes.entity.Product;
import com.smartmes.repository.ProductRepository;
import com.smartmes.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 产品管理服务实现类
 * 实现产品的CRUD等基础管理功能
 *
 * @author SmartMES Team
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Product createProduct(Product product) {
        log.info("创建产品: {}", product.getProductCode());

        // 检查产品编号是否已存在
        if (productRepository.findByProductCode(product.getProductCode()).isPresent()) {
            throw new RuntimeException("Product code already exists: " + product.getProductCode());
        }

        Product saved = productRepository.save(product);
        log.info("产品创建成功: {}", saved.getProductCode());
        return saved;
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        log.info("更新产品: ID={}", id);

        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        // 更新字段
        existing.setProductName(product.getProductName());
        existing.setSpecification(product.getSpecification());
        existing.setProductType(product.getProductType());
        existing.setUnit(product.getUnit());
        existing.setStandardWorkTime(product.getStandardWorkTime());
        existing.setStatus(product.getStatus());
        existing.setRemarks(product.getRemarks());

        Product updated = productRepository.save(existing);
        log.info("产品更新成功: {}", updated.getProductCode());
        return updated;
    }

    @Override
    public void deleteProduct(Long id) {
        log.info("删除产品: ID={}", id);

        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with ID: " + id);
        }

        productRepository.deleteById(id);
        log.info("产品删除成功: ID={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductByCode(String productCode) {
        return productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new RuntimeException("Product not found with code: " + productCode));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> searchProductsByName(String productName) {
        return productRepository.findByProductNameContaining(productName);
    }
}
