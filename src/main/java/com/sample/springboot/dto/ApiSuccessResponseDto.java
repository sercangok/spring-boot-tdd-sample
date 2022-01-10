package com.sample.springboot.dto;

public class ApiSuccessResponseDto extends ResponseDto {

    public ApiSuccessResponseDto() {
        setStatus(StatusValue.SUCCESS);
        setResponseCode("0");
    }
}
