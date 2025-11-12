package com.travelplanner.exception;

import com.travelplanner.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 全局异常处理器
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public Result<?> handleBusinessException(BusinessException e, HttpServletResponse response) {
        log.error("Business exception: {}", e.getMessage(), e);
        // 确保响应内容类型为JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<?> handle404Exception(NoHandlerFoundException e, HttpServletResponse response) {
        log.error("Not found: {}", e.getMessage(), e);
        // 确保响应内容类型为JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return Result.error(ErrorCode.NOT_FOUND);
    }

    /**
     * 处理参数错误异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletResponse response) {
        log.error("Method argument not valid: {}", e.getMessage(), e);
        // 确保响应内容类型为JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return Result.error(ErrorCode.PARAM_ERROR, e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    /**
     * 处理请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public Result<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletResponse response) {
        log.error("Http request method not supported: {}", e.getMessage(), e);
        // 确保响应内容类型为JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return Result.error(ErrorCode.FORBIDDEN);
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public Result<?> handleNullPointerException(NullPointerException e, HttpServletResponse response) {
        log.error("Null pointer exception: {}", e.getMessage(), e);
        // 确保响应内容类型为JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return Result.error(ErrorCode.SYSTEM_ERROR, "空指针异常");
    }

    /**
     * 处理系统异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e, HttpServletResponse response) {
        log.error("System exception: {}", e.getMessage(), e);
        // 确保响应内容类型为JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return Result.error(ErrorCode.SYSTEM_ERROR);
    }
}