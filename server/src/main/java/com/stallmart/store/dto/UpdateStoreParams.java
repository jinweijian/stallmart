package com.stallmart.store.dto;

import com.stallmart.store.internal.model.StoreStatus;

public record UpdateStoreParams(
        String name,
        String description,
        String logoUrl,
        String coverUrl,
        StoreStatus status
) {
}
