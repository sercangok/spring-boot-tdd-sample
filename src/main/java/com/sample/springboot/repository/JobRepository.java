package com.sample.springboot.repository;

import com.sample.springboot.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {
    Optional<Job> findByIdAndCompanyId(UUID id, UUID companyId);
}
