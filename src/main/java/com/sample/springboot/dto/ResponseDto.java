package com.sample.springboot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
public class ResponseDto<K> {
    private StatusValue status;
    private String responseCode;
    private String responseDescription;
    private K data;

    public ResponseDto(){
        setStatus(StatusValue.SUCCESS);
    }
}
