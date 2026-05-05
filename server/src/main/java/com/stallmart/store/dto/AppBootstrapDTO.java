package com.stallmart.store.dto;

public record AppBootstrapDTO(
        Long storeId,
        Long styleId,
        String styleCode,
        int styleVersion,
        StorefrontDTO storefront
) {
}
