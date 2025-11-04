package com.example.evaluativas.user.controller;

import com.example.evaluativas.user.model.User;
import com.example.evaluativas.user.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Endpoints REST de Usuarios: CRUD, login y ping.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    /** GET /api/users/ping */
    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("service", "user-service", "status", "ok");
    }

    /** GET /api/users */
    @GetMapping
    public ResponseEntity<List<User>> listar() {
        log.info("[USERS][GET] listar");
        return ResponseEntity.ok(service.findAll());
    }

    /** GET /api/users/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<User> obtenerPorId(@PathVariable("id") Long id) {
        log.info("[USERS][GET] id={}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    /** GET /api/users/email/{email} */
    @GetMapping("/email/{email:.+}")
    public ResponseEntity<User> obtenerPorEmail(@PathVariable("email") String email) {
        log.info("[USERS][GET] email={}", email);
        return ResponseEntity.ok(service.findByEmail(email));
    }

    /** POST /api/users */
    @PostMapping
    public ResponseEntity<User> crear(@Valid @RequestBody User user) {
        log.info("[USERS][POST] crear {}", user.getEmail());
        User nuevo = service.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    /** PUT /api/users/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(@PathVariable("id") Long id,
                                                          @Valid @RequestBody User user) {
        var actualizado = service.update(id, user);
        return ResponseEntity.ok(
            Map.of(
                "message", "Usuario actualizado exitosamente",
                "user", actualizado
            )
        );
    }

    /** DELETE /api/users/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable("id") Long id) {
        log.info("[USERS][DELETE] id={}", id);
        service.delete(id);
        return ResponseEntity.ok(
            Map.of("message", "Usuario eliminado exitosamente", "id", id)
        );
    }

    /** POST /api/users/login */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User cred) {
        log.info("[USERS][POST] login {}", cred.getEmail());
        boolean ok = service.authenticate(cred.getEmail(), cred.getPassword());
        if (!ok) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Credenciales inválidas"));
        }
        return ResponseEntity.ok(Map.of("message", "Login OK"));
    }
}
