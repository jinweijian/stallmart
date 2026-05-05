package com.stallmart.store.internal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    List<CategoryEntity> findByStoreIdOrderBySortOrderAscIdAsc(Long storeId);

    List<CategoryEntity> findByStoreIdAndModuleIgnoreCaseOrderBySortOrderAscIdAsc(Long storeId, String module);
}
