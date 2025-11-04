package com.example.evaluativas.lab.repository;

import com.example.evaluativas.lab.model.Lab;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LabRepository extends JpaRepository<Lab, Long> {
  Optional<Lab> findByCode(String code);
  boolean existsByCode(String code);
}
