package com.stallmart.cart.internal.model;

import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;

public enum CartStatus {
    ACTIVE,
    CHECKED_OUT;

    public static CartStatus from(String value) {
        try {
            return CartStatus.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new AppException(ErrorCode.INVALID_CART_STATUS);
        }
    }
}
