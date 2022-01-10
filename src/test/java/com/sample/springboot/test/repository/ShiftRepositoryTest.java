package com.sample.springboot.test.repository;

import com.sample.springboot.entity.Job;
import com.sample.springboot.entity.Shift;
import com.sample.springboot.repository.ShiftRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@DataJpaTest
class ShiftRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;
    @Autowired
    ShiftRepository shiftRepository;

    @Test
    void findAllByJob_Id() {
        UUID shiftId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        Job job = Job.builder().id(jobId).companyId(UUID.randomUUID()).build();
        testEntityManager.persistAndFlush(job);
        Shift shift = Shift.builder().id(shiftId).job(job).build();
        testEntityManager.persistAndFlush(shift);

        List<Shift> shiftList = shiftRepository.findAllByJob_Id(jobId);

        assertNotNull(shiftList);
        assertEquals(shiftList.size(), 1);
        assertEquals(shiftList.get(0).getId(), shiftId);
        assertNotNull(shiftList.get(0).getJob());
        assertEquals(shiftList.get(0).getJob().getId(), jobId);
    }

    @Test
    void findAllByJob_Id_whenNotFound() {
        UUID shiftId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        Job job = Job.builder().id(jobId).companyId(UUID.randomUUID()).build();
        testEntityManager.persistAndFlush(job);
        Shift shift = Shift.builder().id(shiftId).job(job).build();
        testEntityManager.persistAndFlush(shift);

        List<Shift> shiftList = shiftRepository.findAllByJob_Id(UUID.randomUUID());

        assertNotNull(shiftList);
        assertEquals(shiftList.size(), 0);
    }

    @Test
    void findAllByIdAndJob_CompanyId() {
        UUID shiftId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        Job job = Job.builder().id(jobId).companyId(UUID.randomUUID()).build();
        testEntityManager.persistAndFlush(job);
        Shift shift = Shift.builder().id(shiftId).job(job).talentId(UUID.randomUUID()).build();
        testEntityManager.persistAndFlush(shift);

        Optional<Shift> shiftOptional = shiftRepository.findAllByIdAndJob_CompanyId(shift.getId(), job.getCompanyId());

        assertTrue(shiftOptional.isPresent());
    }

    @Test
    void findAllByIdAndJob_CompanyId_WhenNotFound() {
        UUID shiftId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        Job job = Job.builder().id(jobId).companyId(UUID.randomUUID()).build();
        testEntityManager.persistAndFlush(job);
        Shift shift = Shift.builder().id(shiftId).job(job).talentId(UUID.randomUUID()).build();
        testEntityManager.persistAndFlush(shift);

        Optional<Shift> shiftOptional = shiftRepository.findAllByIdAndJob_CompanyId(shift.getId(), UUID.randomUUID());

        assertFalse(shiftOptional.isPresent());
    }

    @Test
    void findAllByTalentIdAndJob_CompanyId() {
        UUID shiftId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        Job job = Job.builder().id(jobId).companyId(UUID.randomUUID()).build();
        testEntityManager.persistAndFlush(job);
        Shift shift = Shift.builder().id(shiftId).job(job).talentId(UUID.randomUUID()).build();
        testEntityManager.persistAndFlush(shift);

        List<Shift> shiftList = shiftRepository.findAllByTalentIdAndJob_CompanyId(shift.getTalentId(), job.getCompanyId());
        assertFalse(shiftList.isEmpty());
        assertTrue(shiftList.size() == 1);
    }

    @Test
    void findAllByTalentIdAndJob_CompanyId_WhenNotFound() {
        UUID shiftId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        Job job = Job.builder().id(jobId).companyId(UUID.randomUUID()).build();
        testEntityManager.persistAndFlush(job);
        Shift shift = Shift.builder().id(shiftId).job(job).talentId(UUID.randomUUID()).build();
        testEntityManager.persistAndFlush(shift);

        List<Shift> shiftList = shiftRepository.findAllByTalentIdAndJob_CompanyId(shift.getTalentId(), UUID.randomUUID());
        assertTrue(shiftList.isEmpty());
    }
}
