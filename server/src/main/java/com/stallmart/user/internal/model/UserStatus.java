package com.stallmart.user.internal.model;

import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;

public enum UserStatus {
    ACTIVE,
    DISABLED;

    public static UserStatus from(String value) {
        try {
            return UserStatus.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new AppException(ErrorCode.INVALID_USER_STATUS);
        }
    }
}
