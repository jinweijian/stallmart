package com.stallmart.style.dto;

import java.util.List;
import java.util.Map;

public record StorefrontThemeDTO(
        String code,
        String name,
        String layoutVersion,
        Map<String, String> colors,
        Map<String, String> iconNames,
        Map<String, String> iconUrls,
        Map<String, String> imageUrls,
        Map<String, String> copywriting,
        List<StorefrontCategoryDTO> categories
) {
}
