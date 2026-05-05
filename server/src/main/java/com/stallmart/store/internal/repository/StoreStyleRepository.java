package com.stallmart.store.internal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreStyleRepository extends JpaRepository<StoreStyleEntity, Long> {
    List<StoreStyleEntity> findAllByOrderByIdAsc();

    List<StoreStyleEntity> findByStatusOrderByIdAsc(String status);
}
