package com.sample.springboot.service.impl;

import com.sample.springboot.entity.Job;
import com.sample.springboot.entity.Shift;
import com.sample.springboot.exception.NoJobFoundToCancelException;
import com.sample.springboot.repository.JobRepository;
import com.sample.springboot.service.JobService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@AllArgsConstructor
@Service
@Transactional
public class JobServiceImpl implements JobService {

    private JobRepository jobRepository;

    public Job createJob(UUID companyId, LocalDate startDate, LocalDate endDate) {
        Job job = Job.builder()
                .id(UUID.randomUUID())
                .companyId(companyId)
                .startTime(startDate.atTime(8, 0, 0).toInstant(ZoneOffset.UTC))
                .endTime(endDate.atTime(17, 0, 0).toInstant(ZoneOffset.UTC))
                .build();
        job.setShifts(LongStream.range(0, ChronoUnit.DAYS.between(startDate, endDate))
                .mapToObj(idx -> startDate.plus(idx, ChronoUnit.DAYS))
                .map(date -> Shift.builder()
                        .id(UUID.randomUUID())
                        .job(job)
                        .startTime(date.atTime(8, 0, 0).toInstant(ZoneOffset.UTC))
                        .endTime(date.atTime(17, 0, 0).toInstant(ZoneOffset.UTC))
                        .build())
                .collect(Collectors.toList()));
        return jobRepository.save(job);
    }

    public void cancelJob(UUID jobId, UUID companyId) {
        Optional<Job> optionalJob = jobRepository.findByIdAndCompanyId(jobId, companyId);
        Job jobWillBeDeleted = optionalJob.orElseThrow(NoJobFoundToCancelException::new);
        jobRepository.delete(jobWillBeDeleted);
    }
}
