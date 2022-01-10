package com.sample.springboot.exception;

import com.sample.springboot.constant.ErrorMessages;

public class ShiftNotExistException extends BaseException {
    public ShiftNotExistException() {
        super(ErrorMessages.SHIFT_NOT_EXIST_ERR);
    }
}
