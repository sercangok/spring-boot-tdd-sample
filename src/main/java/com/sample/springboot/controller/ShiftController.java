package com.sample.springboot.controller;

import com.sample.springboot.dto.*;
import com.sample.springboot.service.impl.ShiftServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "/shift")
@AllArgsConstructor
public class ShiftController {

    private ShiftServiceImpl shiftService;

    @GetMapping(path = "/{jobId}")
    @ResponseBody
    public ResponseDto<GetShiftsResponse> getShifts(@PathVariable("jobId") UUID jobId) {
        List<ShiftResponse> shiftResponses = shiftService.getShifts(jobId).stream()
                .map(shift -> ShiftResponse.builder()
                        .id(shift.getId())
                        .talentId(shift.getTalentId())
                        .jobId(shift.getJob().getId())
                        .start(shift.getStartTime())
                        .end(shift.getEndTime())
                        .build())
                .collect(Collectors.toList());
        return ApiSuccessResponseDto.<GetShiftsResponse>builder()
                .status(StatusValue.SUCCESS)
                .responseCode("0")
                .data(GetShiftsResponse.builder()
                        .shifts(shiftResponses)
                        .build())
                .build();
    }

    @PatchMapping(path = "/{shiftId}/book")
    @ResponseBody
    public ResponseDto bookTalent(@PathVariable("shiftId") UUID shiftId, @RequestBody @Valid BookTalentRequestDto dto) {
        shiftService.bookTalent(shiftId, dto.getTalent());
        return new ApiSuccessResponseDto();
    }

    @DeleteMapping(path = "/{shiftId}/cancel")
    @ResponseBody
    public ResponseDto cancelShift(@PathVariable("shiftId") UUID shiftId, @RequestBody @Valid CancelShiftRequestDto dto) {
        shiftService.cancelShift(shiftId, dto.getCompanyId());
        return new ApiSuccessResponseDto();
    }

    @PatchMapping(path = "/cancel/{talentId}")
    @ResponseBody
    public ResponseDto cancelTalentShifts(@PathVariable("talentId") UUID talentId, @RequestBody @Valid CancelShiftRequestDto dto) {
        shiftService.cancelTalentShifts(talentId, dto.getCompanyId());
        return new ApiSuccessResponseDto();
    }
}
