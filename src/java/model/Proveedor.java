package model;

import java.sql.Timestamp;

/**
 * Modelo para la tabla proveedores
 */
public class Proveedor {
    private Long id;
    private String nombre;
    private String rfc;
    private String telefono;
    private String email;
    private String direccion;
    private String ciudad;
    private Boolean activo;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Constructor vacío
    public Proveedor() {
    }

    // Constructor con parámetros principales
    public Proveedor(String nombre, String rfc, String telefono, String email, 
                     String direccion, String ciudad) {
        this.nombre = nombre;
        this.rfc = rfc;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.activo = true;
    }

    // Constructor completo
    public Proveedor(Long id, String nombre, String rfc, String telefono, String email, 
                     String direccion, String ciudad, Boolean activo, Timestamp createdAt, 
                     Timestamp updatedAt) {
        this.id = id;
        this.nombre = nombre;
        this.rfc = rfc;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.ciudad = ciudad;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
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

    @Override
    public String toString() {
        return "Proveedor{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", rfc='" + rfc + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", activo=" + activo +
                '}';
    }
}
