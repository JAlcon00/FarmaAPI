package model;

import java.sql.Timestamp;

/**
 * Modelo para la tabla usuarios
 */
public class Usuario {
    private Long id;
    private String email;
    private String passwordHash;
    private String nombre;
    private String apellido;
    private Integer rolId;
    private Boolean activo;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Relación con Role (opcional, para joins)
    private Role role;

    // Constructor vacío
    public Usuario() {
    }

    // Constructor con parámetros principales
    public Usuario(String email, String passwordHash, String nombre, String apellido, Integer rolId) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rolId = rolId;
        this.activo = true;
    }

    // Constructor completo
    public Usuario(Long id, String email, String passwordHash, String nombre, String apellido, 
                   Integer rolId, Boolean activo, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rolId = rolId;
        this.activo = activo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Integer getRolId() {
        return rolId;
    }

    public void setRolId(Integer rolId) {
        this.rolId = rolId;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", rolId=" + rolId +
                ", activo=" + activo +
                '}';
    }
}
