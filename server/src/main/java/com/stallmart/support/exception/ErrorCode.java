package com.stallmart.support.exception;

public enum ErrorCode {
    BAD_REQUEST(10000, "bad_request"),
    UNAUTHORIZED(10001, "unauthorized"),
    NOT_FOUND(10004, "not_found"),
    INVALID_ORDER_STATUS(30001, "invalid_order_status");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
