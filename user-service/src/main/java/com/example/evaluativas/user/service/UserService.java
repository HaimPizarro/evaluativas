package com.example.evaluativas.user.service;

import com.example.evaluativas.user.exception.ResourceNotFoundException;
import com.example.evaluativas.user.model.User;
import com.example.evaluativas.user.repository.UserJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserJpaRepository repo;

    public UserService(UserJpaRepository repo) {
        this.repo = repo;

        // Usuario ADMIN semilla
        if (repo.findByEmail("admin@demo.cl").isEmpty()) {
            User admin = new User();
            admin.setNombre("Admin");
            admin.setEmail("admin@demo.cl");
            admin.setPassword("1234");
            admin.setActivo(true);
            admin.setAdmin(true);
            repo.save(admin);
            log.debug("Usuario semilla creado (ADMIN): {}", admin.getEmail());
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

        // Validar email duplicado
        repo.findByEmail(user.getEmail()).ifPresent(u -> {
            log.warn("Intento de registrar email duplicado: {}", user.getEmail());
            throw new IllegalArgumentException("Email ya registrado");
        });

        // ✅ Validación MANUAL de password SOLO para creación
        if (user.getPassword() == null || user.getPassword().isBlank() || user.getPassword().length() < 4) {
            throw new IllegalArgumentException("Password debe tener al menos 4 caracteres");
        }

        User saved = repo.save(user);
        log.info("Usuario creado id={} email={}", saved.getId(), saved.getEmail());
        return saved;
    }

    public User update(Long id, User cambios) {
        log.debug("Actualizando usuario id={}", id);
        User actual = findById(id);

        // Nombre y correo siempre vienen validados por Bean Validation
        actual.setNombre(cambios.getNombre());
        actual.setEmail(cambios.getEmail());

        // ✅ Solo cambiar password si viene NO nula y NO en blanco
        if (cambios.getPassword() != null && !cambios.getPassword().isBlank()) {
            // acá podrías volver a validar mínimo 4 si quieres seguridad extra
            if (cambios.getPassword().length() < 4) {
                throw new IllegalArgumentException("Password debe tener al menos 4 caracteres");
            }
            actual.setPassword(cambios.getPassword());
        }

        // ACTIVO opcional
        if (cambios.getActivo() != null) {
            actual.setActivo(cambios.getActivo());
        }

        // ADMIN opcional (IS_ADMIN en DB)
        if (cambios.getAdmin() != null) {
            actual.setAdmin(cambios.getAdmin());
        }

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
            // Verificamos password y que esté ACTIVO
            .map(u -> u.getPassword().equals(password) && Boolean.TRUE.equals(u.getActivo()))
            .orElse(false);
    }

    public User resetPassword(String email, String newPassword) {
        log.debug("Reseteando contraseña para email={}", email);

        if (newPassword == null || newPassword.isBlank() || newPassword.length() < 4) {
            throw new IllegalArgumentException("Password debe tener al menos 4 caracteres");
        }

        User user = repo.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado email=" + email));

        user.setPassword(newPassword);
        User updated = repo.save(user);

        log.info("Contraseña actualizada para email={}", updated.getEmail());
        return updated;
    }
}
