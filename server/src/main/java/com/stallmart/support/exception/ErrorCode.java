package com.stallmart.support.exception;

public enum ErrorCode {
    BAD_REQUEST(10000, "bad_request"),
    UNAUTHORIZED(10001, "unauthorized"),
    TOKEN_INVALID(10002, "token_invalid"),
    TOKEN_EXPIRED(10003, "token_expired"),
    NOT_FOUND(10004, "not_found"),
    FORBIDDEN(10005, "forbidden"),
    INVALID_CREDENTIALS(10006, "invalid_credentials"),
    CAPTCHA_REQUIRED(10007, "captcha_required"),
    CAPTCHA_INVALID(10008, "captcha_invalid"),
    INVALID_ORDER_STATUS(30001, "invalid_order_status"),
    INVALID_STORE_STATUS(30002, "invalid_store_status"),
    INVALID_PRODUCT_STATUS(30003, "invalid_product_status"),
    INVALID_SKU_STATUS(30004, "invalid_sku_status"),
    INVALID_CATEGORY_STATUS(30005, "invalid_category_status"),
    INVALID_USER_STATUS(30006, "invalid_user_status"),
    INVALID_USER_ROLE(30007, "invalid_user_role"),
    INVALID_ADMIN_ACCOUNT_STATUS(30008, "invalid_admin_account_status"),
    INVALID_CART_STATUS(30009, "invalid_cart_status"),
    INVALID_STORE_STYLE_STATUS(30010, "invalid_store_style_status"),
    INVALID_CATEGORY_MODULE(30011, "invalid_category_module"),
    INVALID_SPEC_TYPE(30012, "invalid_spec_type");

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
