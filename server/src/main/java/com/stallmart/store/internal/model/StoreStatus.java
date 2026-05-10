package com.stallmart.store.internal.model;

import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;

public enum StoreStatus {
    OPEN,
    CLOSED;

    public static StoreStatus from(String value) {
        try {
            return StoreStatus.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new AppException(ErrorCode.INVALID_STORE_STATUS);
        }
    }
}
