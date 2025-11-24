package com.example.evaluativas.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "USERS",
    uniqueConstraints = @UniqueConstraint(name = "UK_USERS_EMAIL", columnNames = "EMAIL")
)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @NotBlank(message = "nombre es obligatorio")
    @Size(max = 100, message = "nombre: máx 100 caracteres")
    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @Email(message = "email inválido")
    @NotBlank(message = "email es obligatorio")
    @Size(max = 150, message = "email: máx 150 caracteres")
    @Column(name = "EMAIL", nullable = false, length = 150)
    private String email;

    // 👇 OJO: quitamos las validaciones de Bean Validation para manejarlo a mano en el servicio
    @Column(name = "PASSWORD", nullable = false, length = 200)
    private String password;

    @Column(name = "ACTIVO", nullable = false)
    private Boolean activo = Boolean.TRUE;

    // IS_ADMIN (NUMBER(1) en Oracle, Boolean en Java)
    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "IS_ADMIN", nullable = false)
    private Boolean admin = Boolean.FALSE;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    public User() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public Boolean getAdmin() { return admin; }
    public void setAdmin(Boolean admin) { this.admin = admin; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.activo == null) {
            this.activo = Boolean.TRUE;
        }
        if (this.admin == null) {
            this.admin = Boolean.FALSE;
        }
    }
}
