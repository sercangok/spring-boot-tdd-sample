package com.sample.springboot.test.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.springboot.dto.CreateJobRequestDto;
import com.sample.springboot.dto.ResponseDto;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

import static org.springframework.http.HttpMethod.*;

public class BaseIntegrationTest {

    @Autowired
    TestRestTemplate testRestTemplate;
    @Autowired
    ObjectMapper objectMapper;

    protected CreateJobRequestDto getCreateJobRequestDto() {
        UUID companyId = UUID.randomUUID();
        LocalDate jobStartDate = LocalDate.of(2021, Month.JANUARY, 1);
        LocalDate jobEndDate = LocalDate.of(2021, Month.JANUARY, 3);
        CreateJobRequestDto jobRequest = new CreateJobRequestDto(companyId, jobStartDate, jobEndDate);
        return jobRequest;
    }

    public <T, K> ResponseDto post(String path, T requestObject, Class<K> respType) throws JsonProcessingException {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ResponseDto.class, respType);
        String respAsStr = testRestTemplate.postForObject(path, requestObject, String.class);
        return objectMapper.readValue(respAsStr, javaType);
    }

    public <T> ResponseDto get(String path, Class<T> respType) throws JsonProcessingException {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ResponseDto.class, respType);
        String respAsStr = testRestTemplate.getForObject(path, String.class);
        return objectMapper.readValue(respAsStr, javaType);
    }

    public <T> ResponseDto put(String path, T requestObject) {
        HttpEntity<T> reqHttpEntity = new HttpEntity<>(requestObject);
        return testRestTemplate.exchange(path, PUT, reqHttpEntity, ResponseDto.class).getBody();
    }

    public <T> ResponseDto patch(String path, T requestObject) {
        HttpEntity<T> reqHttpEntity = new HttpEntity<>(requestObject);
        return testRestTemplate.exchange(path, PATCH, reqHttpEntity, ResponseDto.class).getBody();
    }

    public <T> ResponseDto delete(String path, T requestObject) {
        HttpEntity<T> reqHttpEntity = new HttpEntity<>(requestObject);
        return testRestTemplate.exchange(path, DELETE, reqHttpEntity, ResponseDto.class).getBody();
    }
}
