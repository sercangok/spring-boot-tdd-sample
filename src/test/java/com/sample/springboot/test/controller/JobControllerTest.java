package com.sample.springboot.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.springboot.controller.JobController;
import com.sample.springboot.dto.*;
import com.sample.springboot.entity.Job;
import com.sample.springboot.exception.InvalidShiftPeriodException;
import com.sample.springboot.exception.NoJobFoundToCancelException;
import com.sample.springboot.constant.ErrorMessages;
import com.sample.springboot.service.impl.JobServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.*;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(JobController.class)
public class JobControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    JobServiceImpl jobService;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void createJob_Ok_withValidShifts() throws Exception {
        UUID companyId = UUID.randomUUID();
        Instant jobStartDate = LocalDate.of(2021, Month.JANUARY, 1).atTime(8, 17).toInstant(ZoneOffset.UTC);
        Instant jobEndDate = LocalDate.of(2021, Month.JANUARY, 2).atTime(8, 17).toInstant(ZoneOffset.UTC);

        given(jobService.createJob(any(), any(), any())).willReturn(Job.builder()
                .id(UUID.randomUUID())
                .companyId(companyId)
                .startTime(jobStartDate)
                .endTime(jobEndDate)
                .build());

        CreateJobRequestDto jobRequest = new CreateJobRequestDto(companyId, LocalDate.of(2021, Month.JANUARY, 1), LocalDate.of(2021, Month.JANUARY, 2));

        mockMvc.perform(MockMvcRequestBuilders.post("/job")
                        .content(objectMapper.writeValueAsString(jobRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value(StatusValue.SUCCESS.toString()))
                .andExpect(jsonPath("responseCode").value("0"))
                .andExpect(jsonPath("data.jobId").exists());
    }

    @Test
    public void createJob_Err_withSameShift() throws Exception {
        UUID companyId = UUID.randomUUID();
        LocalDate jobStartDate = LocalDate.of(2021, Month.JANUARY, 1);
        LocalDate jobEndDate = LocalDate.of(2021, Month.JANUARY, 1);

        given(jobService.createJob(any(), any(), any())).willThrow(new InvalidShiftPeriodException());

        CreateJobRequestDto jobRequest = new CreateJobRequestDto(companyId, jobStartDate, jobEndDate);
        mockMvc.perform(MockMvcRequestBuilders.post("/job")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(StatusValue.ERROR.toString()))
                .andExpect(jsonPath("responseCode").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("responseDescription").value(ErrorMessages.INVALID_SHIFT_PERIOD_ERR));
    }

    @Test
    public void createJob_Err_withInvalidShiftEndDate() throws Exception {

        UUID companyId = UUID.randomUUID();
        LocalDate jobStartDate = LocalDate.of(2021, Month.JANUARY, 1);
        String invalidJobEnDate = "2021-01-77";

        given(jobService.createJob(any(), any(), any())).willThrow(InvalidShiftPeriodException.class);

        String jobRequestAsStr = objectMapper.createObjectNode()
                .put("companyId", companyId.toString())
                .put("start", jobStartDate.toString())
                .put("end", invalidJobEnDate)
                .toString();

        mockMvc.perform(MockMvcRequestBuilders.post("/job")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jobRequestAsStr))
                .andExpect(status().isBadRequest())
                .andExpect(mockMvc -> assertTrue(mockMvc.getResolvedException() instanceof HttpMessageNotReadableException))
                .andExpect(jsonPath("status").value(StatusValue.ERROR.toString()))
                .andExpect(jsonPath("responseCode").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("responseDescription", containsString(invalidJobEnDate)));
    }

    @Test
    public void cancelJob_Ok_whenItExist() throws Exception {
        doNothing().when(jobService).cancelJob(any(), any());
        CancelJobRequestDto cancelJobRequestDto = new CancelJobRequestDto(UUID.randomUUID());
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/job/" + UUID.randomUUID() + "/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelJobRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value(StatusValue.SUCCESS.toString()))
                .andExpect(jsonPath("responseCode").value("0"));
    }

    @Test
    public void cancelJob_Err_whenJobExistButCompanyNot() throws Exception {
        doThrow(new NoJobFoundToCancelException()).when(jobService).cancelJob(any(), any());
        CancelJobRequestDto cancelJobRequestDto = new CancelJobRequestDto(UUID.randomUUID());
        mockMvc.perform(MockMvcRequestBuilders.delete("/job/" + UUID.randomUUID() + "/cancel")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelJobRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(StatusValue.ERROR.toString()))
                .andExpect(jsonPath("responseCode").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("responseDescription").value(ErrorMessages.NO_JOB_FOUND_TO_CANCEL_ERR));
    }

    @Test
    public void cancelJob_Err_whenJobNotExist() throws Exception {
        doThrow(new NoJobFoundToCancelException()).when(jobService).cancelJob(any(), any());
        CancelJobRequestDto cancelJobRequestDto = new CancelJobRequestDto(UUID.randomUUID());
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/job/" + UUID.randomUUID() + "/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelJobRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(StatusValue.ERROR.toString()))
                .andExpect(jsonPath("responseCode").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("responseDescription").value(ErrorMessages.NO_JOB_FOUND_TO_CANCEL_ERR));
    }
}
