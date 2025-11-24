package com.example.evaluativas.lab.service;

import com.example.evaluativas.lab.dto.AssignmentReq;
import com.example.evaluativas.lab.exception.ResourceNotFoundException;
import com.example.evaluativas.lab.model.Lab;
import com.example.evaluativas.lab.model.LabAssignment;
import com.example.evaluativas.lab.repository.LabAssignmentRepository;
import com.example.evaluativas.lab.repository.LabRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LabService {
  private static final Logger log = LoggerFactory.getLogger(LabService.class);

  private final LabRepository labRepo;
  private final LabAssignmentRepository assignRepo;

  public LabService(LabRepository labRepo, LabAssignmentRepository assignRepo) {
    this.labRepo = labRepo;
    this.assignRepo = assignRepo;

    // Semilla opcional de labs
    if (labRepo.count() == 0) {
      Lab l = new Lab();
      l.setCode("LAB-101");
      l.setName("Laboratorio Principal");
      l.setLocation("Piso 1");
      l.setCapacity(20);
      l.setActive(true);
      // Si quieres que la semilla tenga usuario asignado, puedes setearlo acá:
      // l.setUserId(1L);
      labRepo.save(l);
      log.info("Semilla: creado {}", l.getCode());
    }
  }

  // ---------- Labs ----------
  public List<Lab> listLabs() { return labRepo.findAll(); }

  public Lab getLab(Long id) {
    return labRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Lab no encontrado id=" + id));
  }

  public Lab createLab(Lab lab) {
    labRepo.findByCode(lab.getCode()).ifPresent(x -> {
      throw new IllegalArgumentException("Código de laboratorio ya existe");
    });
    // lab trae code, name, location, capacity, active y AHORA userId
    return labRepo.save(lab);
  }

  public Lab updateLab(Long id, Lab changes) {
    Lab db = getLab(id);

    if (!db.getCode().equalsIgnoreCase(changes.getCode())
        && labRepo.existsByCode(changes.getCode())) {
      throw new IllegalArgumentException("Código de laboratorio ya existe");
    }

    db.setCode(changes.getCode());
    db.setName(changes.getName());
    db.setLocation(changes.getLocation());
    db.setCapacity(changes.getCapacity());
    db.setActive(changes.getActive());

    // NUEVO: actualizar el USER_ID (responsable)
    db.setUserId(changes.getUserId());

    return labRepo.save(db);
  }

  public void deleteLab(Long id) {
    if (!labRepo.existsById(id)) {
      throw new ResourceNotFoundException("Lab no encontrado id=" + id);
    }
    labRepo.deleteById(id);
  }

  // ---------- Asignaciones ----------
  // (esto NO cambia, ya usas userId en LabAssignment)
  public List<LabAssignment> listAssignments(Long labId, Long userId) {
    if (labId != null) return assignRepo.findByLab_Id(labId);
    if (userId != null) return assignRepo.findByUserId(userId);
    return assignRepo.findAll();
  }

  public LabAssignment getAssignment(Long id) {
    return assignRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Asignación no encontrada id=" + id));
  }

  public LabAssignment createAssignment(AssignmentReq req) {
    Lab lab = getLab(req.labId);
    if (Boolean.FALSE.equals(lab.getActive())) {
      throw new IllegalStateException("El laboratorio está inactivo");
    }
    if (!req.endTime.isAfter(req.startTime)) {
      throw new IllegalArgumentException("endTime debe ser posterior a startTime");
    }
    boolean overlap = assignRepo.existsByLab_IdAndStartTimeLessThanAndEndTimeGreaterThan(
        req.labId, req.endTime, req.startTime);
    if (overlap) {
      throw new IllegalStateException("Ya existe una reserva que se solapa en ese horario");
    }
    LabAssignment a = new LabAssignment();
    a.setLab(lab);
    a.setUserId(req.userId);
    a.setStartTime(req.startTime);
    a.setEndTime(req.endTime);
    a.setNotes(req.notes);
    if (req.status != null && !req.status.isBlank()) a.setStatus(req.status);
    return assignRepo.save(a);
  }

  public LabAssignment updateAssignment(Long id, AssignmentReq req) {
    LabAssignment db = getAssignment(id);
    if (req.labId != null && !Objects.equals(db.getLab().getId(), req.labId)) {
      Lab lab = getLab(req.labId);
      db.setLab(lab);
    }
    if (req.startTime != null) db.setStartTime(req.startTime);
    if (req.endTime != null) db.setEndTime(req.endTime);
    if (req.userId != null) db.setUserId(req.userId);
    if (req.notes != null) db.setNotes(req.notes);
    if (req.status != null && !req.status.isBlank()) db.setStatus(req.status);

    if (!db.getEndTime().isAfter(db.getStartTime())) {
      throw new IllegalArgumentException("endTime debe ser posterior a startTime");
    }
    boolean overlap = assignRepo.existsByLab_IdAndStartTimeLessThanAndEndTimeGreaterThan(
        db.getLab().getId(), db.getEndTime(), db.getStartTime());
    if (overlap) {
      // comentario que ya tenías
    }
    return assignRepo.save(db);
  }

  public void deleteAssignment(Long id) {
    if (!assignRepo.existsById(id)) {
      throw new ResourceNotFoundException("Asignación no encontrada id=" + id);
    }
    assignRepo.deleteById(id);
  }

  
}
