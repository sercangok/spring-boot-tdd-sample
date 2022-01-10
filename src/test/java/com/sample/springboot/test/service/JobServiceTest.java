package com.sample.springboot.test.service;

import com.sample.springboot.entity.Job;
import com.sample.springboot.exception.NoJobFoundToCancelException;
import com.sample.springboot.repository.JobRepository;
import com.sample.springboot.service.impl.JobServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JobServiceTest {

    JobServiceImpl jobService;
    @Mock
    JobRepository jobRepository;

    @Before
    public void setUp() {
        jobService = new JobServiceImpl(jobRepository);
    }

    @Test
    public void createJob_withValidShifts() {
        UUID companyId = UUID.randomUUID();
        LocalDate jobStartDate = LocalDate.of(2021, Month.JANUARY, 1);
        LocalDate jobEndDate = LocalDate.of(2021, Month.JANUARY, 2);

        jobService.createJob(companyId, jobStartDate, jobEndDate);
        ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
        verify(jobRepository).save(jobArgumentCaptor.capture());
        Job capturedJob = jobArgumentCaptor.getValue();

        Instant jobStartDateInstant = jobStartDate.atTime(8, 0, 0).toInstant(ZoneOffset.UTC);
        Instant jobEndDateInstant = jobEndDate.atTime(17, 0, 0).toInstant(ZoneOffset.UTC);

        assertThat(capturedJob).isNotNull();
        assertThat(capturedJob.getCompanyId()).isEqualTo(companyId);
        assertThat(capturedJob.getStartTime()).isEqualTo(jobStartDateInstant);
        assertThat(capturedJob.getEndTime()).isEqualTo(jobEndDateInstant);
        assertThat(capturedJob.getShifts()).hasSize(1);
    }

    @Test
    public void cancelJob_whenJobExist() {
        UUID jobId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();
        given(jobRepository.findByIdAndCompanyId(jobId, companyId)).willReturn(Optional.of(Job.builder().id(jobId).companyId(companyId).build()));

        jobService.cancelJob(jobId, companyId);

        verify(jobRepository, times(1)).delete(any());
    }

    @Test(expected = NoJobFoundToCancelException.class)
    public void cancelJob_whenJobOrCompanyNotExist() {
        UUID jobId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();
        given(jobRepository.findByIdAndCompanyId(jobId, companyId)).willReturn(Optional.empty());

        jobService.cancelJob(jobId, companyId);
    }
}
