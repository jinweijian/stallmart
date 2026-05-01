package com.stallmart.store.dto;

public record UpdateStoreParams(
        String name,
        String description,
        String logoUrl,
        String coverUrl,
        String status
) {
}
