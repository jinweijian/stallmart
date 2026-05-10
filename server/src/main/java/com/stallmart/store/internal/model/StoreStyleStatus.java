package com.stallmart.store.internal.model;

import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;

public enum StoreStyleStatus {
    ACTIVE,
    INACTIVE;

    public static StoreStyleStatus from(String value) {
        try {
            return StoreStyleStatus.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new AppException(ErrorCode.INVALID_STORE_STYLE_STATUS);
        }
    }
}
