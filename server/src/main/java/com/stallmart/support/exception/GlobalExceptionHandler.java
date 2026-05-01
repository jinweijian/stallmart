package com.stallmart.support.exception;

import com.stallmart.support.exception.ErrorCode;
import com.stallmart.support.web.Result;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return Result.error(errorCode.code(), errorCode.message());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidationException(Exception exception) {
        return Result.error(ErrorCode.BAD_REQUEST.code(), ErrorCode.BAD_REQUEST.message());
    }
}
