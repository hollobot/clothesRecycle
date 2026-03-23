package com.example.project.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import com.example.project.common.result.Result;
import com.example.project.common.result.ResultCode;
import com.example.project.utils.LogMaskUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常。
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", LogMaskUtil.mask(e.getMessage()));
        return Result.fail(e.getMessage());
    }

    /**
     * 处理 @RequestBody + @Valid 场景的参数校验异常。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .findFirst()
                .orElse("参数错误");
        log.warn("参数校验失败: {}", LogMaskUtil.mask(msg));
        return Result.fail(msg);
    }

    /**
     * 处理 @ModelAttribute 场景的参数校验异常。
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        String msg = e.getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .findFirst()
                .orElse("参数错误");
        log.warn("参数绑定失败: {}", LogMaskUtil.mask(msg));
        return Result.fail(msg);
    }

    /**
     * 处理未登录异常。
     */
    @ExceptionHandler(NotLoginException.class)
    public Result<Void> handleNotLoginException(NotLoginException e) {
        log.warn("未登录访问: {}", LogMaskUtil.mask(e.getMessage()));
        return Result.fail(ResultCode.UNAUTHORIZED);
    }

    /**
     * 处理无权限异常。
     */
    @ExceptionHandler(NotPermissionException.class)
    public Result<Void> handleNotPermissionException(NotPermissionException e) {
        log.warn("无权限访问: {}", LogMaskUtil.mask(e.getMessage()));
        return Result.fail(ResultCode.FORBIDDEN);
    }

    /**
     * 处理兜底异常。
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常: {}", LogMaskUtil.mask(e.getMessage()), e);
        return Result.fail(ResultCode.ERROR);
    }
}
