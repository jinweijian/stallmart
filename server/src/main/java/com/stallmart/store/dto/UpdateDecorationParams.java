package com.stallmart.store.dto;

public record UpdateDecorationParams(
        Long styleId,
        String logoUrl,
        String coverUrl,
        String description
) {
}
