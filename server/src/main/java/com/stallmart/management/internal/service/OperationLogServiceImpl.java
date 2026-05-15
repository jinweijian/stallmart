package com.stallmart.management.internal.service;

import com.stallmart.management.OperationLogService;
import com.stallmart.management.dto.OperationLogDTO;
import com.stallmart.management.dto.OperationLogRecordParams;
import com.stallmart.management.internal.model.OperationLogScope;
import com.stallmart.management.internal.repository.OperationLogEntity;
import com.stallmart.management.internal.repository.OperationLogRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    private static final int MAX_TEXT_LENGTH = 300;

    private final OperationLogRepository operationLogRepository;

    public OperationLogServiceImpl(OperationLogRepository operationLogRepository) {
        this.operationLogRepository = operationLogRepository;
    }

    @Override
    @Transactional
    public void record(OperationLogRecordParams params) {
        OperationLogEntity entity = new OperationLogEntity();
        entity.scope = params.scope();
        entity.storeId = params.storeId();
        entity.actorUserId = params.actorUserId();
        entity.actorAccount = truncate(params.actorAccount(), 80);
        entity.actorRole = params.actorRole();
        entity.action = truncate(params.action(), 80);
        entity.resourceType = truncate(params.resourceType(), 80);
        entity.resourceId = truncate(params.resourceId(), 80);
        entity.description = truncate(params.description(), MAX_TEXT_LENGTH);
        entity.result = params.result();
        entity.ipAddress = truncate(params.ipAddress(), 80);
        entity.userAgent = truncate(params.userAgent(), MAX_TEXT_LENGTH);
        entity.createdAt = Instant.now();
        operationLogRepository.save(entity);
    }

    @Override
    public List<OperationLogDTO> listPlatformLogs() {
        return operationLogRepository.findTop100ByScopeOrderByCreatedAtDesc(OperationLogScope.PLATFORM).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<OperationLogDTO> listVendorLogs(long storeId) {
        return operationLogRepository.findTop100ByStoreIdOrderByCreatedAtDesc(storeId).stream()
                .map(this::toDto)
                .toList();
    }

    private OperationLogDTO toDto(OperationLogEntity entity) {
        return new OperationLogDTO(
                entity.id,
                entity.scope,
                entity.storeId,
                entity.actorUserId,
                entity.actorAccount,
                entity.actorRole,
                entity.action,
                entity.resourceType,
                entity.resourceId,
                entity.description,
                entity.result,
                entity.ipAddress,
                entity.userAgent,
                entity.createdAt
        );
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
