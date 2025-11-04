package com.example.evaluativas.lab.repository;

import com.example.evaluativas.lab.model.LabAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LabAssignmentRepository extends JpaRepository<LabAssignment, Long> {
  List<LabAssignment> findByLab_Id(Long labId);
  List<LabAssignment> findByUserId(Long userId);

  // solape: (start < existingEnd) AND (end > existingStart)
  boolean existsByLab_IdAndStartTimeLessThanAndEndTimeGreaterThan(
      Long labId, LocalDateTime endTime, LocalDateTime startTime);
}
