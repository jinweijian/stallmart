package com.stallmart.store.internal.repository;

import com.stallmart.store.internal.model.CategoryModule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    List<CategoryEntity> findByStoreIdOrderBySortOrderAscIdAsc(Long storeId);

    List<CategoryEntity> findByStoreIdAndModuleOrderBySortOrderAscIdAsc(Long storeId, CategoryModule module);
}
