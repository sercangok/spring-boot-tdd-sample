package com.sample.springboot.service;

import com.sample.springboot.entity.Shift;

import java.util.List;
import java.util.UUID;

public interface ShiftService {

    List<Shift> getShifts(UUID jobId);

    void bookTalent(UUID shiftId, UUID talent);

    void cancelShift(UUID shiftId, UUID companyId);

    void cancelTalentShifts(UUID talentId, UUID companyId);
}
