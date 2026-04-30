package com.stallmart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stallmart.common.exception.BusinessException;
import com.stallmart.common.result.ErrorCode;
import com.stallmart.model.entity.Product;
import com.stallmart.repository.ProductMapper;
import com.stallmart.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品服务实现
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    @Override
    public Product findById(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null || product.getDeleted()) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return product;
    }

    @Override
    public List<Product> findByStoreId(Long storeId) {
        return productMapper.selectList(
            new LambdaQueryWrapper<Product>()
                .eq(Product::getStoreId, storeId)
                .eq(Product::getDeleted, false)
                .eq(Product::getStatus, "active")
                .orderByAsc(Product::getSortOrder)
        );
    }

    @Override
    public Product create(Product product) {
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        product.setDeleted(false);
        productMapper.insert(product);
        return product;
    }

    @Override
    public void update(Product product) {
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);
    }

    @Override
    public void delete(Long id) {
        Product product = findById(id);
        product.setDeleted(true);
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);
    }
}
