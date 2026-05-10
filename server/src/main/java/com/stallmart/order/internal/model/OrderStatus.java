package com.stallmart.order.internal.model;

import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;

public enum OrderStatus {
    NEW,
    ACCEPTED,
    PREPARING,
    READY,
    COMPLETED,
    REJECTED;

    public static OrderStatus from(String value) {
        try {
            return OrderStatus.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS);
        }
    }
}
