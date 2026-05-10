package com.stallmart.style.dto;

import com.stallmart.store.internal.model.StoreStyleStatus;

public record StyleDTO(
        Long id,
        String name,
        String code,
        String previewUrl,
        StorefrontThemeDTO theme,
        StoreStyleStatus status,
        int version
) {
}
