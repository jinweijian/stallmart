package com.stallmart.store.internal.repository;

import com.stallmart.store.internal.model.StoreStyleStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreStyleRepository extends JpaRepository<StoreStyleEntity, Long> {
    List<StoreStyleEntity> findAllByOrderByIdAsc();

    List<StoreStyleEntity> findByStatusOrderByIdAsc(StoreStyleStatus status);
}
