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
    public Product updateProduct(String productCode, Product product) {
        log.info("更新产品: productCode={}", productCode);

        Product existing = productRepository.findById(productCode)
                .orElseThrow(() -> new RuntimeException("Product not found with code: " + productCode));

        // 更新字段
        existing.setProductName(product.getProductName());
        existing.setProductType(product.getProductType());
        existing.setStandardWorkTime(product.getStandardWorkTime());

        Product updated = productRepository.save(existing);
        log.info("产品更新成功: {}", updated.getProductCode());
        return updated;
    }

    @Override
    public void deleteProduct(String productCode) {
        log.info("删除产品: productCode={}", productCode);

        if (!productRepository.existsById(productCode)) {
            throw new RuntimeException("Product not found with code: " + productCode);
        }

        productRepository.deleteById(productCode);
        log.info("产品删除成功: productCode={}", productCode);
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(String productCode) {
        return productRepository.findById(productCode)
                .orElseThrow(() -> new RuntimeException("Product not found with code: " + productCode));
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
