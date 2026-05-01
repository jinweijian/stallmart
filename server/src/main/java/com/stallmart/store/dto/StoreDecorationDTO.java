package com.stallmart.store.dto;

import com.stallmart.style.dto.StyleDTO;
import com.stallmart.style.dto.StorefrontCategoryDTO;
import com.stallmart.style.dto.StorefrontThemeDTO;
import java.util.List;
import java.util.Map;

public record StoreDecorationDTO(
        Long storeId,
        String storeName,
        String logoUrl,
        String coverUrl,
        List<String> banners,
        Long styleId,
        String styleCode,
        StyleDTO style,
        String layoutVersion,
        StorefrontThemeDTO theme,
        Map<String, String> colors,
        Map<String, String> iconNames,
        Map<String, String> iconUrls,
        Map<String, String> imageUrls,
        Map<String, String> copywriting,
        List<StorefrontCategoryDTO> categories
) {
}
