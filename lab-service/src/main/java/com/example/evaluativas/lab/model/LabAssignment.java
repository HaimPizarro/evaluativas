package com.example.evaluativas.lab.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "LAB_ASSIGNMENTS")
public class LabAssignment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "LAB_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_ASSIGN_LAB"))
  private Lab lab;

  @NotNull
  @Column(name = "USER_ID", nullable = false)
  private Long userId; // id del usuario de user-service (sin FK entre esquemas)

  @NotNull
  @Column(name = "START_TIME", nullable = false)
  private LocalDateTime startTime;

  @NotNull
  @Column(name = "END_TIME", nullable = false)
  private LocalDateTime endTime;

  @Size(max = 200)
  @Column(name = "NOTES", length = 200)
  private String notes;

  @Size(max = 20)
  @Column(name = "STATUS", length = 20)
  private String status = "RESERVED";

  // getters/setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Lab getLab() { return lab; }
  public void setLab(Lab lab) { this.lab = lab; }
  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }
  public LocalDateTime getStartTime() { return startTime; }
  public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
  public LocalDateTime getEndTime() { return endTime; }
  public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
  public String getNotes() { return notes; }
  public void setNotes(String notes) { this.notes = notes; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
}
