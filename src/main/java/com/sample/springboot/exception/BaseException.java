package com.sample.springboot.exception;

public class BaseException extends RuntimeException {
    public BaseException(String s) {
        super(s);
    }
}
