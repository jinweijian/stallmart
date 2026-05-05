package com.stallmart.style.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StyleUpsertParams(
        @NotBlank String name,
        @NotBlank String code,
        String previewUrl,
        String status,
        @NotNull StorefrontThemeDTO theme
) {
}
