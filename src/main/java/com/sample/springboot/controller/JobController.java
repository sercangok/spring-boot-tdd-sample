package com.sample.springboot.controller;

import com.sample.springboot.dto.*;
import com.sample.springboot.entity.Job;
import com.sample.springboot.exception.InvalidShiftPeriodException;
import com.sample.springboot.service.impl.JobServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@Controller
@RequestMapping(path = "/job")
@AllArgsConstructor
public class JobController {

    private JobServiceImpl jobService;

    @PostMapping
    @ResponseBody
    public ResponseDto<CreateJobResponse> createJob(@RequestBody @Valid CreateJobRequestDto dto) {
        if (dto.getStart().getYear() == dto.getEnd().getYear() && dto.getStart().getDayOfYear() == dto.getEnd().getDayOfYear()) {
            throw new InvalidShiftPeriodException();
        }

        Job job = jobService.createJob(dto.getCompanyId(), dto.getStart(), dto.getEnd());
        return ApiSuccessResponseDto.<CreateJobResponse>builder()
                .status(StatusValue.SUCCESS)
                .responseCode("0")
                .data(CreateJobResponse.builder()
                        .jobId(job.getId())
                        .build())
                .build();
    }

    @DeleteMapping("/{jobId}/cancel")
    @ResponseBody
    public ResponseDto cancelJob(@PathVariable UUID jobId, @RequestBody @Valid CancelJobRequestDto cancelJobRequestDto) {
        jobService.cancelJob(jobId, cancelJobRequestDto.getCompanyId());
        return new ApiSuccessResponseDto();
    }
}
