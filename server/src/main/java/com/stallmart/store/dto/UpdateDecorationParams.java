package com.stallmart.store.dto;

import java.util.List;
import java.util.Map;

public record UpdateDecorationParams(
        Long styleId,
        String logoUrl,
        String coverUrl,
        List<String> banners,
        String description,
        Map<String, String> colors,
        Map<String, String> iconUrls,
        Map<String, String> imageUrls,
        Map<String, String> copywriting
) {
}
