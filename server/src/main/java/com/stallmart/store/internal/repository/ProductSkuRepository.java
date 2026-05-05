package com.stallmart.store.internal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSkuRepository extends JpaRepository<ProductSkuEntity, Long> {
    List<ProductSkuEntity> findByProductIdOrderByIdAsc(Long productId);

    void deleteByProductId(Long productId);
}
