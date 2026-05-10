package com.stallmart.store.dto;

import com.stallmart.store.internal.model.StoreStatus;

public record StorefrontDTO(
        Long id,
        Long ownerId,
        Long styleId,
        String styleCode,
        String name,
        String category,
        String description,
        String avatarUrl,
        String coverUrl,
        String qrCode,
        String address,
        StoreStatus status,
        StoreDecorationDTO decoration
) {
    public static StorefrontDTO from(StoreDTO store, StoreDecorationDTO decoration) {
        return new StorefrontDTO(
                store.id(),
                store.ownerId(),
                store.styleId(),
                store.styleCode(),
                store.name(),
                store.category(),
                store.description(),
                store.avatarUrl(),
                store.coverUrl(),
                store.qrCode(),
                store.address(),
                store.status(),
                decoration
        );
    }
}
