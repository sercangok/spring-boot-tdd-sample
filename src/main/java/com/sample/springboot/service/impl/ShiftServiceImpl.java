package com.sample.springboot.service.impl;

import com.sample.springboot.entity.Shift;
import com.sample.springboot.exception.*;
import com.sample.springboot.repository.ShiftRepository;
import com.sample.springboot.service.ShiftService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional
public class ShiftServiceImpl implements ShiftService {

    private ShiftRepository shiftRepository;

    @Override
    public List<Shift> getShifts(UUID jobId) {
        List<Shift> shiftList =  shiftRepository.findAllByJob_Id(jobId);
        shiftList.stream().findAny().orElseThrow(ShiftNotExistException::new);
        return shiftList;
    }

    @Override
    public void bookTalent(UUID shiftId, UUID talentId) {
        Shift shift = shiftRepository.findById(shiftId).orElseThrow(ShiftNotExistException::new);
        shiftRepository.save(shift.setTalentId(talentId));
    }

    @Override
    public void cancelShift(UUID shiftId, UUID companyId) {
        Optional<Shift> optionalShift = shiftRepository.findAllByIdAndJob_CompanyId(shiftId, companyId);
        Shift shiftThatWillBeDeleted = optionalShift.orElseThrow(ShiftNotExistException::new);
        shiftRepository.delete(shiftThatWillBeDeleted);
    }

    @Override
    public void cancelTalentShifts(UUID talentId, UUID companyId) {
        List<Shift> talentShiftsForCompany = shiftRepository.findAllByTalentIdAndJob_CompanyId(talentId, companyId);
        talentShiftsForCompany.stream().findAny().orElseThrow(ShiftNotExistException::new);

        talentShiftsForCompany = talentShiftsForCompany.stream().map(shift -> shift.setTalentId(null)).collect(Collectors.toList());

        shiftRepository.saveAll(talentShiftsForCompany);
    }
}
