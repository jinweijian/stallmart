package com.stallmart.style.dto;

import com.stallmart.store.internal.model.StoreStyleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StyleUpsertParams(
        @NotBlank String name,
        @NotBlank String code,
        String previewUrl,
        StoreStyleStatus status,
        @NotNull StorefrontThemeDTO theme
) {
}
