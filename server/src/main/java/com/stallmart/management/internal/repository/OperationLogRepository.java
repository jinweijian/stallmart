package com.stallmart.management.internal.repository;

import com.stallmart.management.internal.model.OperationLogScope;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationLogRepository extends JpaRepository<OperationLogEntity, Long> {

    List<OperationLogEntity> findTop100ByScopeOrderByCreatedAtDesc(OperationLogScope scope);

    List<OperationLogEntity> findTop100ByStoreIdOrderByCreatedAtDesc(Long storeId);
}
