package com.sample.springboot.test.integration;

import com.sample.springboot.dto.*;
import com.sample.springboot.constant.ErrorMessages;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShiftIntegrationTest extends BaseIntegrationTest {

    @Before
    public void setup() {
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    @Test
    public void getShifts_Ok_whenJobExist() throws Exception {
        CreateJobRequestDto jobRequest = getCreateJobRequestDto();
        ResponseDto<CreateJobResponse> responseResponseDto = post("/job", jobRequest, CreateJobResponse.class);

        ResponseDto<GetShiftsResponse> getShiftsResponseResponseDto = get("/shift/" + responseResponseDto.getData().getJobId(), GetShiftsResponse.class);

        assertThat(getShiftsResponseResponseDto.getStatus()).isEqualTo(StatusValue.SUCCESS);
        assertThat(getShiftsResponseResponseDto.getResponseCode()).isEqualTo("0");
        assertThat(getShiftsResponseResponseDto.getData().getShifts()).hasSizeGreaterThan(0);
        assertThat(getShiftsResponseResponseDto.getData().getShifts()
                .stream()
                .filter(shiftResponse -> shiftResponse.getJobId().equals(responseResponseDto.getData().getJobId()))
                .collect(Collectors.toList()))
                .hasSizeGreaterThan(0);
    }

    @Test
    public void getShifts_Err_whenShiftNotExist() throws Exception {
        ResponseDto<GetShiftsResponse> getShiftsResponseResponseDto = get("/shift/" + UUID.randomUUID(), GetShiftsResponse.class);
        assertThat(getShiftsResponseResponseDto.getStatus()).isEqualTo(StatusValue.ERROR);
        assertThat(getShiftsResponseResponseDto.getResponseCode()).isNotEqualTo("0");
        assertThat(getShiftsResponseResponseDto.getResponseDescription()).isEqualTo(ErrorMessages.SHIFT_NOT_EXIST_ERR);
    }

    @Test
    public void bookTalent_Ok_whenShiftExist() throws Exception {
        CreateJobRequestDto jobRequest = getCreateJobRequestDto();
        ResponseDto<CreateJobResponse> createJobResponseResponseDto = post("/job", jobRequest, CreateJobResponse.class);
        UUID jobId = createJobResponseResponseDto.getData().getJobId();
        ResponseDto<GetShiftsResponse> getShiftsResponseResponseDto = get("/shift/" + jobId, GetShiftsResponse.class);
        UUID shiftId = getShiftsResponseResponseDto.getData().getShifts().stream().findAny().get().getId();
        BookTalentRequestDto bookTalentRequestDto = new BookTalentRequestDto(UUID.randomUUID());

        ResponseDto bookTalentResponseDto = patch("/shift/" + shiftId + "/book", bookTalentRequestDto);

        assertThat(bookTalentResponseDto.getStatus()).isEqualTo(StatusValue.SUCCESS);
        assertThat(bookTalentResponseDto.getResponseCode()).isEqualTo("0");
    }

    @Test
    public void bookTalent_Err_whenShiftNotExist() throws Exception {
        BookTalentRequestDto bookTalentRequestDto = new BookTalentRequestDto(UUID.randomUUID());

        ResponseDto bookTalentResponseDto = patch("/shift/" + UUID.randomUUID() + "/book", bookTalentRequestDto);

        assertThat(bookTalentResponseDto.getStatus()).isEqualTo(StatusValue.ERROR);
        assertThat(bookTalentResponseDto.getResponseCode()).isNotEqualTo("0");
        assertThat(bookTalentResponseDto.getResponseDescription()).isEqualTo(ErrorMessages.SHIFT_NOT_EXIST_ERR);
    }

    @Test
    public void cancelShift_Ok_whenShiftExist() throws Exception {
        CreateJobRequestDto jobRequest = getCreateJobRequestDto();
        ResponseDto<CreateJobResponse> createJobResponseResponseDto = post("/job", jobRequest, CreateJobResponse.class);
        UUID jobId = createJobResponseResponseDto.getData().getJobId();
        ResponseDto<GetShiftsResponse> getShiftsResponseResponseDto = get("/shift/" + jobId, GetShiftsResponse.class);
        UUID shiftId = getShiftsResponseResponseDto.getData().getShifts().stream().findAny().get().getId();
        CancelShiftRequestDto cancelShiftRequestDto = new CancelShiftRequestDto(jobRequest.getCompanyId());

        ResponseDto cancelShiftResponseDto = delete("/shift/" + shiftId + "/cancel", cancelShiftRequestDto);

        assertThat(cancelShiftResponseDto.getStatus()).isEqualTo(StatusValue.SUCCESS);
        assertThat(cancelShiftResponseDto.getResponseCode()).isEqualTo("0");
    }

    @Test
    public void cancelShift_Err_whenShiftNotExist() {
        CancelShiftRequestDto cancelShiftRequestDto = new CancelShiftRequestDto(UUID.randomUUID());

        ResponseDto cancelShiftResponseDto = delete("/shift/" + UUID.randomUUID() + "/cancel", cancelShiftRequestDto);

        assertThat(cancelShiftResponseDto.getStatus()).isEqualTo(StatusValue.ERROR);
        assertThat(cancelShiftResponseDto.getResponseCode()).isNotEqualTo("0");
        assertThat(cancelShiftResponseDto.getResponseDescription()).isEqualTo(ErrorMessages.SHIFT_NOT_EXIST_ERR);
    }

    @Test
    public void cancelTalentShifts_Ok_whenExists() throws Exception {
        CreateJobRequestDto jobRequest = getCreateJobRequestDto();
        ResponseDto<CreateJobResponse> createJobResponseResponseDto = post("/job", jobRequest, CreateJobResponse.class);
        UUID jobId = createJobResponseResponseDto.getData().getJobId();
        ResponseDto<GetShiftsResponse> getShiftsResponseResponseDto = get("/shift/" + jobId, GetShiftsResponse.class);
        UUID shiftId = getShiftsResponseResponseDto.getData().getShifts().stream().findAny().get().getId();
        BookTalentRequestDto bookTalentRequestDto = new BookTalentRequestDto(UUID.randomUUID());
        patch("/shift/" + shiftId + "/book", bookTalentRequestDto);
        CancelShiftRequestDto cancelShiftRequestDto = new CancelShiftRequestDto(jobRequest.getCompanyId());

        ResponseDto cancelShiftResponseDto = patch("/shift/cancel/" + bookTalentRequestDto.getTalent(), cancelShiftRequestDto);

        assertThat(cancelShiftResponseDto.getStatus()).isEqualTo(StatusValue.SUCCESS);
        assertThat(cancelShiftResponseDto.getResponseCode()).isEqualTo("0");
    }

    @Test
    public void cancelTalentShifts_Err_whenShiftNotExist() throws Exception {
        CreateJobRequestDto jobRequest = getCreateJobRequestDto();
        post("/job", jobRequest, CreateJobResponse.class);
        CancelShiftRequestDto cancelShiftRequestDto = new CancelShiftRequestDto(UUID.randomUUID());

        ResponseDto cancelShiftResponseDto = patch("/shift/cancel/" + UUID.randomUUID(), cancelShiftRequestDto);

        assertThat(cancelShiftResponseDto.getStatus()).isEqualTo(StatusValue.ERROR);
        assertThat(cancelShiftResponseDto.getResponseCode()).isNotEqualTo("0");
        assertThat(cancelShiftResponseDto.getResponseDescription()).isEqualTo(ErrorMessages.SHIFT_NOT_EXIST_ERR);
    }
}
