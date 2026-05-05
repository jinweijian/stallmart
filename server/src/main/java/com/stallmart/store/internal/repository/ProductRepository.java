package com.stallmart.store.internal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findByStoreIdOrderBySortOrderAscIdAsc(Long storeId);

    List<ProductEntity> findAllByOrderByIdAsc();
}
