package com.stallmart.store.internal.model;

import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;

public enum ProductStatus {
    ACTIVE,
    INACTIVE,
    SOLD_OUT;

    public static ProductStatus from(String value) {
        try {
            return ProductStatus.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new AppException(ErrorCode.INVALID_PRODUCT_STATUS);
        }
    }
}
