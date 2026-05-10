package com.stallmart.store.internal.model;

import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;

public enum SkuStatus {
    ACTIVE,
    INACTIVE,
    SOLD_OUT;

    public static SkuStatus from(String value) {
        try {
            return SkuStatus.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new AppException(ErrorCode.INVALID_SKU_STATUS);
        }
    }
}
