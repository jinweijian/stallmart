package com.stallmart.support.web;

import java.time.Instant;

public record Result<T>(int code, String message, T data, long timestamp) {

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data, Instant.now().getEpochSecond());
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null, Instant.now().getEpochSecond());
    }
}
