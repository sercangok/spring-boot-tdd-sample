package com.sample.springboot.exception;


import com.sample.springboot.constant.ErrorMessages;

public class NoJobFoundToCancelException extends BaseException {
    public NoJobFoundToCancelException() {
        super(ErrorMessages.NO_JOB_FOUND_TO_CANCEL_ERR);
    }
}
