package com.sample.springboot.service;

import com.sample.springboot.entity.Job;

import java.time.LocalDate;
import java.util.UUID;

public interface JobService {

    Job createJob(UUID companyId, LocalDate startDate, LocalDate endDate);

    void cancelJob(UUID jobId, UUID companyId);
}
