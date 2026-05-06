package com.stallmart.store.internal.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<StoreEntity, Long> {
    Optional<StoreEntity> findByQrCode(String qrCode);

    Optional<StoreEntity> findByAppId(String appId);

    List<StoreEntity> findByOwnerIdOrderByIdAsc(Long ownerId);

    List<StoreEntity> findAllByOrderByIdAsc();

    boolean existsByStyleId(Long styleId);
}
