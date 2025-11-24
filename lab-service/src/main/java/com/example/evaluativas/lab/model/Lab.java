package com.example.evaluativas.lab.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(
  name = "LABS",
  uniqueConstraints = @UniqueConstraint(name = "UK_LABS_CODE", columnNames = "CODE")
)
public class Lab {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Long id;

  @NotBlank
  @Size(max = 30)
  @Column(name = "CODE", nullable = false, length = 30)
  private String code;

  @NotBlank
  @Size(max = 100)
  @Column(name = "NAME", nullable = false, length = 100)
  private String name;

  @Size(max = 100)
  @Column(name = "LOCATION", length = 100)
  private String location;

  @Min(0)
  @Column(name = "CAPACITY")
  private Integer capacity;

  @Column(name = "ACTIVE", nullable = false, length = 1)
  @Convert(converter = BooleanYNConverter.class)
  private Boolean active = Boolean.TRUE;

  @Column(name = "USER_ID")
  private Long userId;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getLocation() { return location; }
  public void setLocation(String location) { this.location = location; }

  public Integer getCapacity() { return capacity; }
  public void setCapacity(Integer capacity) { this.capacity = capacity; }

  public Boolean getActive() { return active; }
  public void setActive(Boolean active) { this.active = active; }

  public Long getUserId() { return userId; }          
  public void setUserId(Long userId) { this.userId = userId; } 
}
