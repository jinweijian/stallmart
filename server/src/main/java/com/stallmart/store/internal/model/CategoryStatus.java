package com.stallmart.store.internal.model;

import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;

public enum CategoryStatus {
    ACTIVE,
    INACTIVE;

    public static CategoryStatus from(String value) {
        try {
            return CategoryStatus.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new AppException(ErrorCode.INVALID_CATEGORY_STATUS);
        }
    }
}
