package com.example.project.exception;

/**
 * 业务异常
 * <p>主动抛出业务错误时使用，由 GlobalExceptionHandler 统一捕获并返回给前端</p>
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
