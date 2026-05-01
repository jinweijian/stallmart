package com.stallmart.style.dto;

public record StyleDTO(
        Long id,
        String name,
        String code,
        String previewUrl,
        StorefrontThemeDTO theme
) {
}
