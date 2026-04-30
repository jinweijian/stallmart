package com.stallmart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stallmart.common.exception.BusinessException;
import com.stallmart.common.result.ErrorCode;
import com.stallmart.model.entity.ProductSpec;
import com.stallmart.repository.ProductSpecMapper;
import com.stallmart.service.ProductSpecService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSpecServiceImpl implements ProductSpecService {

    private final ProductSpecMapper productSpecMapper;

    @Override
    public List<ProductSpec> findByStyleId(Long styleId) {
        return productSpecMapper.selectList(
            new LambdaQueryWrapper<ProductSpec>()
                .eq(ProductSpec::getStyleId, styleId)
                .eq(ProductSpec::getDeleted, false)
                .orderByAsc(ProductSpec::getSortOrder)
        );
    }

    @Override
    public ProductSpec findById(Long id) {
        ProductSpec spec = productSpecMapper.selectById(id);
        if (spec == null || spec.getDeleted()) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        return spec;
    }

    @Override
    public ProductSpec create(ProductSpec spec) {
        spec.setCreatedAt(LocalDateTime.now());
        spec.setUpdatedAt(LocalDateTime.now());
        spec.setDeleted(false);
        productSpecMapper.insert(spec);
        return spec;
    }

    @Override
    public void update(ProductSpec spec) {
        spec.setUpdatedAt(LocalDateTime.now());
        productSpecMapper.updateById(spec);
    }

    @Override
    public void delete(Long id) {
        ProductSpec spec = findById(id);
        spec.setDeleted(true);
        spec.setUpdatedAt(LocalDateTime.now());
        productSpecMapper.updateById(spec);
    }
}
