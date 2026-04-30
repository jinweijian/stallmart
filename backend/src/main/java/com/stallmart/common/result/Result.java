package com.stallmart.common.result;

import lombok.Data;
import java.time.Instant;

/**
 * 统一响应格式
 * {
 *   "code": 200,
 *   "message": "success",
 *   "data": {...},
 *   "timestamp": 1743443200
 * }
 */
@Data
public class Result<T> {

    private int code;
    private String message;
    private T data;
    private long timestamp;

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        result.setTimestamp(Instant.now().getEpochSecond());
        return result;
    }

    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        result.setTimestamp(Instant.now().getEpochSecond());
        return result;
    }

    public static <T> Result<T> error(ErrorCode errorCode) {
        return error(errorCode.getCode(), errorCode.getMessage());
    }
}
