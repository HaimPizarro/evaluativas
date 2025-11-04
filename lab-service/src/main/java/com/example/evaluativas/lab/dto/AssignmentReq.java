package com.example.evaluativas.lab.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class AssignmentReq {
  @NotNull public Long labId;
  @NotNull public Long userId;
  @NotNull public LocalDateTime startTime;
  @NotNull public LocalDateTime endTime;
  public String notes;
  public String status;
}
