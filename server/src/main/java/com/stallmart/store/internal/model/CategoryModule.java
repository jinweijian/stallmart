package com.stallmart.store.internal.model;

import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;

public enum CategoryModule {
    PRODUCT;

    public static CategoryModule from(String value) {
        try {
            return CategoryModule.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new AppException(ErrorCode.INVALID_CATEGORY_MODULE);
        }
    }
}
