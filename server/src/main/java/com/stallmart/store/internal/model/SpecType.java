package com.stallmart.store.internal.model;

import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;

public enum SpecType {
    SIZE,
    SWEET,
    ICE,
    OTHER;

    public static SpecType from(String value) {
        try {
            return SpecType.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new AppException(ErrorCode.INVALID_SPEC_TYPE);
        }
    }
}
