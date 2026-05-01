package com.stallmart.support.exception;

public enum ErrorCode {
    BAD_REQUEST(10000, "bad_request"),
    UNAUTHORIZED(10001, "unauthorized"),
    TOKEN_INVALID(10002, "token_invalid"),
    TOKEN_EXPIRED(10003, "token_expired"),
    NOT_FOUND(10004, "not_found"),
    FORBIDDEN(10005, "forbidden"),
    INVALID_CREDENTIALS(10006, "invalid_credentials"),
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
