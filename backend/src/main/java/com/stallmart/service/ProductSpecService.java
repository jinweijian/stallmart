package com.stallmart.service;

import com.stallmart.model.entity.ProductSpec;
import java.util.List;

public interface ProductSpecService {
    List<ProductSpec> findByStyleId(Long styleId);
    ProductSpec findById(Long id);
    ProductSpec create(ProductSpec spec);
    void update(ProductSpec spec);
    void delete(Long id);
}
