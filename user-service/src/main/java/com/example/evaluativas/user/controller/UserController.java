package com.example.evaluativas.user.controller;

import com.example.evaluativas.user.model.User;
import com.example.evaluativas.user.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private static final String MESSAGE_KEY = "message";

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("service", "user-service", "status", "ok");
    }

    @GetMapping
    public ResponseEntity<List<User>> listar() {
        log.info("[USERS][GET] listar");
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> obtenerPorId(@PathVariable("id") Long id) {
        log.info("[USERS][GET] id={}", id);
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> crear(@Valid @RequestBody User user) {
        log.info("[USERS][POST] crear {}", user.getEmail());
        User nuevo = service.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> login(@RequestBody User cred) {
        log.info("[USERS][POST] login {}", cred.getEmail());
        User usuario = service.findByEmail(cred.getEmail());

        boolean ok = service.authenticate(cred.getEmail(), cred.getPassword());

        if (!ok) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(MESSAGE_KEY, "Credenciales inválidas"));
        }

        return ResponseEntity.ok(usuario);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> actualizar(@PathVariable("id") Long id,
                                                          @Valid @RequestBody User user) {
        var actualizado = service.update(id, user);
        return ResponseEntity.ok(
                Map.of(
                        MESSAGE_KEY, "Usuario actualizado",
                        "user", actualizado
                )
        );
    }

    @DeleteMapping(value = "/{id}", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.ok(
                Map.of(
                        MESSAGE_KEY, "Usuario eliminado",
                        "id", id
                )
        );
    }

    @PostMapping(value = "/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String newPassword = body.get("newPassword");

        log.info("[USERS][POST] reset-password para email={}", email);

        if (email == null || email.isBlank() || newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body(
                    Map.of(MESSAGE_KEY, "Email y nueva contraseña son obligatorios")
            );
        }

        User updated = service.resetPassword(email, newPassword);

        return ResponseEntity.ok(
                Map.of(
                        MESSAGE_KEY, "Contraseña actualizada correctamente",
                        "user", updated
                )
        );
    }
}
