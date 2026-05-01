package com.stallmart.store.dto;

public record StoreDTO(
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
        String status
) {
}
