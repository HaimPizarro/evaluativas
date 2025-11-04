package com.example.evaluativas.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(
    name = "USERS",
    uniqueConstraints = @UniqueConstraint(name = "UK_USERS_EMAIL", columnNames = "EMAIL")
)
public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // Oracle 12c+ soporta IDENTITY
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

  @NotBlank(message = "password es obligatorio")
  @Size(min = 4, max = 200, message = "password debe tener al menos 4 caracteres")
  @Column(name = "PASSWORD", nullable = false, length = 200)
  private String password;

  // Se mapea a NUMBER(1) en Oracle (0/1). Asegúrate que la columna en BD sea NUMBER(1).
  @Column(name = "ACTIVO", nullable = false)
  private Boolean activo = Boolean.TRUE;

  public User() {
    // Constructor por defecto requerido por JPA.
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
}
