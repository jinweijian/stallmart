package com.stallmart.user.internal.model;

import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;

public enum UserRole {
    CUSTOMER,
    VENDOR,
    ADMIN;

    public static UserRole from(String value) {
        try {
            return UserRole.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new AppException(ErrorCode.INVALID_USER_ROLE);
        }
    }
}
