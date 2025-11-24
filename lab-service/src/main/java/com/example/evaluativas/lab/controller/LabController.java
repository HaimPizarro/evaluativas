package com.example.evaluativas.lab.controller;

import com.example.evaluativas.lab.dto.AssignmentReq;
import com.example.evaluativas.lab.model.Lab;
import com.example.evaluativas.lab.model.LabAssignment;
import com.example.evaluativas.lab.service.LabService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/labs")
@CrossOrigin(origins = "http://localhost:4200")
public class LabController {

  private static final Logger log = LoggerFactory.getLogger(LabController.class);
  private final LabService service;

  public LabController(LabService service) { this.service = service; }

  // ---- health
  @GetMapping("/ping")
  public Map<String, String> ping() {
    return Map.of("service", "lab-service", "status", "ok");
  }

  // ---- labs CRUD
  @GetMapping
  public ResponseEntity<List<Lab>> list() {
    return ResponseEntity.ok(service.listLabs());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Lab> get(@PathVariable("id") Long id) {
    return ResponseEntity.ok(service.getLab(id));
  }

  @PostMapping
  public ResponseEntity<Lab> create(@Valid @RequestBody Lab lab) {
    Lab saved = service.createLab(lab);
    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Map<String, Object>> update(@PathVariable("id") Long id,
                                                    @Valid @RequestBody Lab lab) {
    Lab updated = service.updateLab(id, lab);
    return ResponseEntity.ok(Map.of("message", "Laboratorio actualizado exitosamente", "lab", updated));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Map<String, Object>> delete(@PathVariable("id") Long id) {
    service.deleteLab(id);
    return ResponseEntity.ok(Map.of("message", "Laboratorio eliminado exitosamente", "id", id));
  }

  // ---- assignments CRUD
  @GetMapping("/assignments")
  public ResponseEntity<List<LabAssignment>> listAssignments(
      @RequestParam(value = "labId", required = false) Long labId,
      @RequestParam(value = "userId", required = false) Long userId) {
    return ResponseEntity.ok(service.listAssignments(labId, userId));
  }

  @GetMapping("/assignments/{id}")
  public ResponseEntity<LabAssignment> getAssignment(@PathVariable("id") Long id) {
    return ResponseEntity.ok(service.getAssignment(id));
  }

  @PostMapping("/assignments")
  public ResponseEntity<LabAssignment> createAssignment(@Valid @RequestBody AssignmentReq req) {
    LabAssignment saved = service.createAssignment(req);
    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
  }

  @PutMapping("/assignments/{id}")
  public ResponseEntity<Map<String, Object>> updateAssignment(@PathVariable("id") Long id,
                                                              @Valid @RequestBody AssignmentReq req) {
    LabAssignment updated = service.updateAssignment(id, req);
    return ResponseEntity.ok(Map.of("message", "Asignación actualizada exitosamente", "assignment", updated));
  }

  @DeleteMapping("/assignments/{id}")
  public ResponseEntity<Map<String, Object>> deleteAssignment(@PathVariable("id") Long id) {
    service.deleteAssignment(id);
    return ResponseEntity.ok(Map.of("message", "Asignación eliminada exitosamente", "id", id));
  }
}
