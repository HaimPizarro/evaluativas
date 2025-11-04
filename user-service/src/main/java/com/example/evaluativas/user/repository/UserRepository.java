package com.example.evaluativas.user.repository;

import com.example.evaluativas.user.model.User;
import java.util.*;

/**
 * Contrato del repositorio. Implementación en memoria por ahora.
 * Cuando uses BD, reemplázalo por Spring Data JPA.
 */
public interface UserRepository {
    List<User> findAll();
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    User save(User u);
    void deleteById(Long id);
    boolean existsById(Long id);
}
