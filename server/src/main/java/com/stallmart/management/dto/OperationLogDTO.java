package com.stallmart.management.dto;

import com.stallmart.management.internal.model.OperationLogResult;
import com.stallmart.management.internal.model.OperationLogScope;
import com.stallmart.user.internal.model.UserRole;
import java.time.Instant;

public record OperationLogDTO(
        Long id,
        OperationLogScope scope,
        Long storeId,
        Long actorUserId,
        String actorAccount,
        UserRole actorRole,
        String action,
        String resourceType,
        String resourceId,
        String description,
        OperationLogResult result,
        String ipAddress,
        String userAgent,
        Instant createdAt
) {
}
