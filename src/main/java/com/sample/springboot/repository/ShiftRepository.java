package com.sample.springboot.repository;

import com.sample.springboot.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, UUID> {

    Optional<Shift> findAllByIdAndJob_CompanyId(UUID id, UUID companyId);

    List<Shift> findAllByJob_Id(UUID jobId);

    List<Shift> findAllByTalentIdAndJob_CompanyId(UUID talentId, UUID companyId);
}
