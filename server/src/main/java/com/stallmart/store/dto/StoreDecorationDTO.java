package com.stallmart.store.dto;

import com.stallmart.style.dto.StyleDTO;

public record StoreDecorationDTO(
        Long storeId,
        String storeName,
        String logoUrl,
        String coverUrl,
        Long styleId,
        String styleCode,
        StyleDTO style
) {
}
