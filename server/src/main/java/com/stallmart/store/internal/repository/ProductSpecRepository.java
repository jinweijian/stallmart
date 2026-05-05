package com.stallmart.store.internal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSpecRepository extends JpaRepository<ProductSpecEntity, Long> {
    List<ProductSpecEntity> findByStyleIdOrderByIdAsc(Long styleId);
}
