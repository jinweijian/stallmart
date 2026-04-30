package com.stallmart.service;

import com.stallmart.model.entity.Product;
import java.util.List;

/**
 * 商品服务接口
 */
public interface ProductService {
    Product findById(Long id);
    List<Product> findByStoreId(Long storeId);
    Product create(Product product);
    void update(Product product);
    void delete(Long id);
}
