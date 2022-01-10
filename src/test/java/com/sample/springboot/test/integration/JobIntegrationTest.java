package com.sample.springboot.test.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sample.springboot.dto.*;
import com.sample.springboot.constant.ErrorMessages;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JobIntegrationTest extends BaseIntegrationTest {

    @Test
    public void createJob_Ok_withValidShifts() throws JsonProcessingException {
        CreateJobRequestDto jobRequest = getCreateJobRequestDto();
        ResponseDto<CreateJobResponse> responseResponseDto = post("/job", jobRequest, CreateJobResponse.class);
        assertThat(responseResponseDto.getStatus()).isEqualTo(StatusValue.SUCCESS);
        assertThat(responseResponseDto.getResponseCode()).isEqualTo("0");
        assertThat(responseResponseDto.getData().getJobId()).isNotNull();
    }

    @Test
    public void createJob_Err_withSameShift() throws JsonProcessingException {
        UUID companyId = UUID.randomUUID();
        LocalDate jobStartDate = LocalDate.of(2021, Month.JANUARY, 1);
        LocalDate jobEndDate = LocalDate.of(2021, Month.JANUARY, 1);
        CreateJobRequestDto jobRequest = new CreateJobRequestDto(companyId, jobStartDate, jobEndDate);
        ResponseDto<CreateJobResponse> responseDto = post("/job", jobRequest, CreateJobResponse.class);

        assertThat(responseDto.getStatus()).isEqualTo(StatusValue.ERROR);
        assertThat(responseDto.getResponseCode()).isNotEqualTo("0");
        assertThat(responseDto.getResponseDescription()).isEqualTo(ErrorMessages.INVALID_SHIFT_PERIOD_ERR);
    }

    @Test
    public void cancelJob_Ok_whenItExist() throws JsonProcessingException {
        CreateJobRequestDto jobRequest = getCreateJobRequestDto();
        ResponseDto<CreateJobResponse> responseResponseDto = post("/job", jobRequest, CreateJobResponse.class);

        CancelJobRequestDto cancelJobRequestDto = new CancelJobRequestDto(jobRequest.getCompanyId());
        ResponseDto responseDto = delete("/job/" + responseResponseDto.getData().getJobId() + "/cancel", cancelJobRequestDto);

        assertThat(responseDto.getStatus()).isEqualTo(StatusValue.SUCCESS);
        assertThat(responseDto.getResponseCode()).isEqualTo("0");
    }

    @Test
    public void cancelJob_Err_whenJobExistButCompanyNot() throws JsonProcessingException {
        CreateJobRequestDto jobRequest = getCreateJobRequestDto();
        ResponseDto<CreateJobResponse> responseResponseDto = post("/job", jobRequest, CreateJobResponse.class);

        CancelJobRequestDto cancelJobRequestDto = new CancelJobRequestDto(UUID.randomUUID());
        ResponseDto responseDto = delete("/job/" + responseResponseDto.getData().getJobId() + "/cancel", cancelJobRequestDto);

        assertThat(responseDto.getStatus()).isEqualTo(StatusValue.ERROR);
        assertThat(responseDto.getResponseCode()).isNotEqualTo("0");
        assertThat(responseDto.getResponseDescription()).isEqualTo(ErrorMessages.NO_JOB_FOUND_TO_CANCEL_ERR);
    }

    @Test
    public void cancelJob_Err_whenJobNotExist() {
        CancelJobRequestDto cancelJobRequestDto = new CancelJobRequestDto(UUID.randomUUID());
        ResponseDto responseDto = delete("/job/" + UUID.randomUUID() + "/cancel", cancelJobRequestDto);
        assertThat(responseDto.getStatus()).isEqualTo(StatusValue.ERROR);
        assertThat(responseDto.getResponseCode()).isNotEqualTo("0");
        assertThat(responseDto.getResponseDescription()).isEqualTo(ErrorMessages.NO_JOB_FOUND_TO_CANCEL_ERR);
    }
}
