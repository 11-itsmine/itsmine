package com.sparta.itsmine.global.exception;

import static com.sparta.itsmine.global.common.ResponseExceptionEnum.PRODUCT_IN_DATE;
import static com.sparta.itsmine.global.common.ResponseExceptionEnum.USER_ERROR;

import com.sparta.itsmine.global.common.HttpResponseDto;
import com.sparta.itsmine.global.common.ResponseUtils;
import com.sparta.itsmine.global.exception.product.ProductException;
import jdk.jshell.spi.ExecutionControl.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<HttpResponseDto> handleUserException(UserException e) {
        log.error("에러 메세지: ", e);
        return ResponseUtils.of(USER_ERROR);
    }

    @ExceptionHandler(ProductException.class)
    public ResponseEntity<HttpResponseDto> handlerProductException(ProductException e) {
        log.error("에러 메세지: ", e);
        return ResponseUtils.of(PRODUCT_IN_DATE);
    }
}