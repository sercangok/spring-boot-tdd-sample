package com.sample.springboot.test.service;

import com.sample.springboot.entity.Job;
import com.sample.springboot.entity.Shift;
import com.sample.springboot.exception.*;
import com.sample.springboot.repository.ShiftRepository;
import com.sample.springboot.service.impl.ShiftServiceImpl;
import org.junit.Before;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
public class ShiftServiceTest {

    @Mock
    ShiftRepository shiftRepository;
    ShiftServiceImpl shiftService;

    @Before
    public void setUp() {
        shiftService = new ShiftServiceImpl(shiftRepository);
    }

    @Test
    public void getShifts_whenExists() {
        UUID jobId = UUID.randomUUID();
        given(shiftRepository.findAllByJob_Id(jobId)).willReturn(Arrays.asList(Shift.builder().build()));

        List<Shift> shiftList = shiftService.getShifts(jobId);

        assertNotNull(shiftList);
        assertTrue(shiftList.size() == 1);
    }

    @Test
    public void getShifts_whenShiftNotExist() {
        given(shiftRepository.findAllByJob_Id(any())).willReturn(Arrays.asList());
        assertThrows(ShiftNotExistException.class, () -> shiftService.getShifts(UUID.randomUUID()));
    }

    @Test
    public void bookTalent_whenShiftExist() {
        UUID shiftId = UUID.randomUUID();
        UUID talentId = UUID.randomUUID();
        given(shiftRepository.findById(shiftId)).willReturn(Optional.of(Shift.builder().id(shiftId).build()));

        shiftService.bookTalent(shiftId, talentId);

        ArgumentCaptor<Shift> shiftArgumentCaptor = ArgumentCaptor.forClass(Shift.class);
        verify(shiftRepository).save(shiftArgumentCaptor.capture());
        Shift bookedShift = shiftArgumentCaptor.getValue();

        assertNotNull(bookedShift);
        assertEquals(bookedShift.getId(), shiftId);
        assertEquals(bookedShift.getTalentId(), talentId);
    }

    @Test
    public void bookTalent_whenShiftNotExist() {
        UUID shiftId = UUID.randomUUID();
        UUID talentId = UUID.randomUUID();
        given(shiftRepository.findById(shiftId)).willReturn(Optional.empty());

        assertThrows(ShiftNotExistException.class, () -> shiftService.bookTalent(shiftId, talentId));
    }

    @Test
    public void cancelShift_whenShiftExist() {
        UUID shiftId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();
        given(shiftRepository.findAllByIdAndJob_CompanyId(shiftId, companyId))
                .willReturn(Optional.of(Shift.builder().id(shiftId).job(Job.builder().companyId(companyId).build()).build()));

        shiftService.cancelShift(shiftId, companyId);

        ArgumentCaptor<Shift> shiftArgumentCaptor = ArgumentCaptor.forClass(Shift.class);
        verify(shiftRepository).delete(shiftArgumentCaptor.capture());
        Shift capturedShift = shiftArgumentCaptor.getValue();

        assertNotNull(capturedShift);
        assertEquals(capturedShift.getId(), shiftId);
        assertEquals(capturedShift.getJob().getCompanyId(), companyId);
    }

    @Test
    public void cancelShift_whenShiftNotExist() {
        UUID shiftId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();
        given(shiftRepository.findAllByIdAndJob_CompanyId(shiftId, companyId)).willReturn(Optional.empty());

        assertThrows(ShiftNotExistException.class, () -> shiftService.cancelShift(shiftId, companyId));
    }

    @Test
    public void cancelTalentShifts_whenExists() {
        UUID talentId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();
        given(shiftRepository.findAllByTalentIdAndJob_CompanyId(talentId, companyId))
                .willReturn(Arrays.asList(Shift.builder().
                        talentId(talentId)
                        .job(Job.builder().companyId(companyId).build())
                        .build()));

        shiftService.cancelTalentShifts(talentId, companyId);

        ArgumentCaptor<List<Shift>> shiftArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(shiftRepository).saveAll(shiftArgumentCaptor.capture());
        List<Shift> capturedShifts = shiftArgumentCaptor.getValue();

        assertNotNull(capturedShifts);
        assertEquals(capturedShifts.size(), 1);
        assertEquals(capturedShifts.get(0).getTalentId(), null);
        assertNotNull(capturedShifts.get(0).getJob());
        assertEquals(capturedShifts.get(0).getJob().getCompanyId(), companyId);
    }

    @Test
    public void cancelTalentShifts_whenShiftNotExist() {
        UUID talentId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();
        given(shiftRepository.findAllByTalentIdAndJob_CompanyId(talentId, companyId)).willReturn(Arrays.asList());

        assertThrows(ShiftNotExistException.class, () -> shiftService.cancelTalentShifts(talentId, companyId));
    }
}
