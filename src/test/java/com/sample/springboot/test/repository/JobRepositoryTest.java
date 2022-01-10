package com.sample.springboot.test.repository;

import com.sample.springboot.entity.Job;
import com.sample.springboot.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
class JobRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;
    @Autowired
    JobRepository jobRepository;

    @Test
    void findAllByIdAndCompanyId() {
        Job job = Job.builder().id(UUID.randomUUID()).companyId(UUID.randomUUID()).build();
        testEntityManager.persistAndFlush(job);

        Optional<Job> optionalJob = jobRepository.findByIdAndCompanyId(job.getId(), job.getCompanyId());

        assertTrue(optionalJob.isPresent());
    }

    @Test
    void findAllByIdAndCompanyId_whenNotFound() {
        Optional<Job> optionalJob = jobRepository.findByIdAndCompanyId(UUID.randomUUID(), UUID.randomUUID());

        assertFalse(optionalJob.isPresent());
    }
}
