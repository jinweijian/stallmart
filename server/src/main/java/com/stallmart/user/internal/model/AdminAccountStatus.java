package com.stallmart.user.internal.model;

import com.stallmart.support.exception.AppException;
import com.stallmart.support.exception.ErrorCode;

public enum AdminAccountStatus {
    ACTIVE,
    DISABLED;

    public static AdminAccountStatus from(String value) {
        try {
            return AdminAccountStatus.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new AppException(ErrorCode.INVALID_ADMIN_ACCOUNT_STATUS);
        }
    }
}
