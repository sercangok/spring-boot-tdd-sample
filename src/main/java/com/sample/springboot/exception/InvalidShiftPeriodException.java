package com.sample.springboot.exception;

import com.sample.springboot.constant.ErrorMessages;

public class InvalidShiftPeriodException extends BaseException {
    public InvalidShiftPeriodException() {
        super(ErrorMessages.INVALID_SHIFT_PERIOD_ERR);
    }
}
