package com.stallmart.common.result;

import lombok.Getter;

/**
 * 错误码枚举
 * 分段规则：
 * 1xxxx  - 通用错误
 * 2xxxx  - 用户模块错误
 * 3xxxx  - 订单模块错误
 * 4xxxx  - 商品模块错误
 * 5xxxx  - 店铺模块错误
 * 9xxxx  - 第三方服务错误
 */
@Getter
public enum ErrorCode {

    // 通用错误（10000-19999）
    SUCCESS(200, "操作成功"),
    VALIDATION_ERROR(10001, "参数校验失败"),
    UNAUTHORIZED(10002, "未授权访问"),
    FORBIDDEN(10003, "无权限访问"),
    NOT_FOUND(10004, "资源不存在"),
    SYSTEM_ERROR(10005, "系统异常"),
    TOKEN_EXPIRED(10006, "Token已过期"),
    TOKEN_INVALID(10007, "Token无效"),

    // 用户模块（20000-29999）
    USER_NOT_FOUND(20001, "用户不存在"),
    USER_ALREADY_EXISTS(20002, "用户已存在"),
    INVALID_PASSWORD(20003, "密码错误"),

    // 订单模块（30000-39999）
    ORDER_NOT_FOUND(30001, "订单不存在"),
    ORDER_STATUS_ERROR(30002, "订单状态异常"),
    ORDER_CREATE_FAILED(30003, "订单创建失败"),

    // 商品模块（40000-49999）
    PRODUCT_NOT_FOUND(40001, "商品不存在"),
    PRODUCT_OUT_OF_STOCK(40002, "商品已售罄"),

    // 店铺模块（50000-59999）
    STORE_NOT_FOUND(50001, "店铺不存在"),
    STORE_CLOSED(50002, "店铺已歇业");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
