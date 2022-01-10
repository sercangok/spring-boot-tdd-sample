package com.sample.springboot.exception.handler;

import com.sample.springboot.dto.ResponseDto;
import com.sample.springboot.dto.StatusValue;
import com.sample.springboot.exception.BaseException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@AllArgsConstructor
public class ContollerExceptionHandler extends ResponseEntityExceptionHandler {


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        final List<String> errors = new ArrayList<String>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        ResponseDto resp = ResponseDto.builder()
                .status(StatusValue.ERROR)
                .responseDescription(errors.toString())
                .responseCode(HttpStatus.BAD_REQUEST.toString())
                .build();
        return handleExceptionInternal(ex, resp, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ResponseDto resp = ResponseDto.builder()
                .status(StatusValue.ERROR)
                .responseDescription(ex.getMessage())
                .responseCode(HttpStatus.BAD_REQUEST.toString())
                .build();
        return handleExceptionInternal(ex, resp, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {BaseException.class})
    protected ResponseEntity<Object> handleBaseException(RuntimeException ex, WebRequest request) {
        ResponseDto resp = ResponseDto.builder()
                .status(StatusValue.ERROR)
                .responseDescription(ex.getMessage())
                .responseCode(HttpStatus.BAD_REQUEST.toString())
                .build();
        return handleExceptionInternal(ex, resp, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
