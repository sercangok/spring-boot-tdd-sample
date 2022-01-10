package com.sample.springboot.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.springboot.dto.CancelShiftRequestDto;
import com.sample.springboot.controller.ShiftController;
import com.sample.springboot.dto.BookTalentRequestDto;
import com.sample.springboot.dto.StatusValue;
import com.sample.springboot.entity.Job;
import com.sample.springboot.entity.Shift;
import com.sample.springboot.exception.ShiftNotExistException;
import com.sample.springboot.constant.ErrorMessages;
import com.sample.springboot.service.impl.ShiftServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ShiftController.class)
public class ShiftControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    ShiftServiceImpl shiftService;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void getShifts_Ok_whenJobExist() throws Exception {
        List<Shift> shiftList = new ArrayList<>();
        shiftList.add(new Shift().setJob(new Job()));
        shiftList.add(new Shift().setJob(new Job()));
        given(shiftService.getShifts(any())).willReturn(shiftList);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/shift/" + UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value(StatusValue.SUCCESS.toString()))
                .andExpect(jsonPath("responseCode").value("0"))
                .andExpect(jsonPath("data.shifts", hasSize(2)));
    }

    @Test
    public void getShifts_Err_whenShiftNotExist() throws Exception {
        given(shiftService.getShifts(any())).willThrow(new ShiftNotExistException());
        mockMvc.perform(MockMvcRequestBuilders.get("/shift/" + UUID.randomUUID()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(StatusValue.ERROR.toString()))
                .andExpect(jsonPath("responseCode").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("responseDescription").value(ErrorMessages.SHIFT_NOT_EXIST_ERR));
    }

    @Test
    public void bookTalent_Ok_whenShiftExist() throws Exception {
        BookTalentRequestDto bookTalentRequestDto = new BookTalentRequestDto();
        bookTalentRequestDto.setTalent(UUID.randomUUID());
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/shift/" + UUID.randomUUID() + "/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookTalentRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value(StatusValue.SUCCESS.toString()))
                .andExpect(jsonPath("responseCode").value("0"));
        verify(shiftService).bookTalent(any(), any());
    }

    @Test
    public void bookTalent_Err_whenShiftNotExist() throws Exception {
        doThrow(new ShiftNotExistException()).when(shiftService).bookTalent(any(), any());
        BookTalentRequestDto bookTalentRequestDto = new BookTalentRequestDto();
        bookTalentRequestDto.setTalent(UUID.randomUUID());
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/shift/" + UUID.randomUUID() + "/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookTalentRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(StatusValue.ERROR.toString()))
                .andExpect(jsonPath("responseCode").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("responseDescription").value(ErrorMessages.SHIFT_NOT_EXIST_ERR));
    }

    @Test
    public void cancelShift_Ok_whenShiftExist() throws Exception {
        CancelShiftRequestDto cancelShiftRequestDto = new CancelShiftRequestDto(UUID.randomUUID());
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/shift/" + UUID.randomUUID() + "/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelShiftRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value(StatusValue.SUCCESS.toString()))
                .andExpect(jsonPath("responseCode").value("0"));
        verify(shiftService).cancelShift(any(), any());
    }

    @Test
    public void cancelShift_Err_whenShiftNotExist() throws Exception {
        doThrow(new ShiftNotExistException()).when(shiftService).cancelShift(any(), any());
        CancelShiftRequestDto cancelShiftRequestDto = new CancelShiftRequestDto(UUID.randomUUID());
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/shift/" + UUID.randomUUID() + "/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelShiftRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(StatusValue.ERROR.toString()))
                .andExpect(jsonPath("responseCode").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("responseDescription").value(ErrorMessages.SHIFT_NOT_EXIST_ERR));
    }

    @Test
    public void cancelTalentShifts_Ok_whenExists() throws Exception {
        CancelShiftRequestDto cancelShiftRequestDto = new CancelShiftRequestDto(UUID.randomUUID());
        mockMvc.perform(MockMvcRequestBuilders.patch("/shift/cancel/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelShiftRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value(StatusValue.SUCCESS.toString()))
                .andExpect(jsonPath("responseCode").value("0"));
        verify(shiftService).cancelTalentShifts(any(), any());
    }

    @Test
    public void cancelTalentShifts_Err_whenShiftNotExists() throws Exception {
        doThrow(new ShiftNotExistException()).when(shiftService).cancelTalentShifts(any(), any());
        CancelShiftRequestDto cancelShiftRequestDto = new CancelShiftRequestDto(UUID.randomUUID());
        mockMvc.perform(MockMvcRequestBuilders.patch("/shift/cancel/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelShiftRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(StatusValue.ERROR.toString()))
                .andExpect(jsonPath("responseCode").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("responseDescription").value(ErrorMessages.SHIFT_NOT_EXIST_ERR));
    }
}
