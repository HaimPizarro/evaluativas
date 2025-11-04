package com.example.evaluativas.user.service;

import com.example.evaluativas.user.exception.ResourceNotFoundException;
import com.example.evaluativas.user.model.User;
import com.example.evaluativas.user.repository.UserJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Reglas de negocio de Usuarios con JPA.
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserJpaRepository repo;

    public UserService(UserJpaRepository repo) {
        this.repo = repo;

        // Datos semilla opcionales
        if (repo.findByEmail("admin@demo.cl").isEmpty()) {
            User admin = new User();
            admin.setNombre("Admin");
            admin.setEmail("admin@demo.cl");
            admin.setPassword("1234");
            admin.setActivo(true);
            repo.save(admin);
            log.debug("Usuario semilla creado: {}", admin.getEmail());
        }
    }

    public List<User> findAll() {
        log.debug("Consultando todos los usuarios");
        return repo.findAll();
    }

    public User findById(Long id) {
        log.debug("Buscando usuario por id={}", id);
        return repo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado id=" + id));
    }

    public User findByEmail(String email) {
        log.debug("Buscando usuario por email={}", email);
        return repo.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado email=" + email));
    }

    public User save(User user) {
        log.debug("Guardando usuario email={}", user.getEmail());
        repo.findByEmail(user.getEmail()).ifPresent(u -> {
            log.warn("Intento de registrar email duplicado: {}", user.getEmail());
            throw new IllegalArgumentException("Email ya registrado");
        });
        User saved = repo.save(user);
        log.info("Usuario creado id={} email={}", saved.getId(), saved.getEmail());
        return saved;
    }

    public User update(Long id, User cambios) {
        log.debug("Actualizando usuario id={}", id);
        User actual = findById(id);

        actual.setNombre(cambios.getNombre());
        actual.setEmail(cambios.getEmail());
        if (cambios.getPassword() != null && !cambios.getPassword().isBlank()) {
            actual.setPassword(cambios.getPassword());
        }
        actual.setActivo(cambios.getActivo() != null ? cambios.getActivo() : actual.getActivo());

        User updated = repo.save(actual);
        log.info("Usuario actualizado id={} email={}", updated.getId(), updated.getEmail());
        return updated;
    }

    public void delete(Long id) {
        log.debug("Eliminando usuario id={}", id);
        if (!repo.existsById(id)) {
            log.warn("No existe usuario para eliminar id={}", id);
            throw new ResourceNotFoundException("Usuario no encontrado id=" + id);
        }
        repo.deleteById(id);
        log.info("Usuario eliminado id={}", id);
    }

    public boolean authenticate(String email, String password) {
        log.debug("Autenticando email={}", email);
        return repo.findByEmail(email)
            .map(u -> u.getPassword().equals(password) && Boolean.TRUE.equals(u.getActivo()))
            .orElse(false);
    }
}
