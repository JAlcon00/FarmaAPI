package dto;

import jakarta.validation.constraints.*;

/**
 * DTO para crear/actualizar clientes con validaciones
 */
public class ClienteDTO {
    
    /**
     * ID del cliente (solo para actualización)
     */
    private Long id;
    
    /**
     * Nombre del cliente
     */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", 
             message = "El nombre solo puede contener letras y espacios")
    private String nombre;
    
    /**
     * Apellido del cliente
     */
    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", 
             message = "El apellido solo puede contener letras y espacios")
    private String apellido;
    
    /**
     * Email del cliente (opcional pero debe ser válido si se proporciona)
     */
    @Email(message = "Email inválido")
    @Size(max = 150, message = "El email no puede exceder 150 caracteres")
    private String email;
    
    /**
     * Teléfono del cliente (opcional)
     */
    @Pattern(regexp = "^[0-9]{10}$", 
             message = "El teléfono debe tener exactamente 10 dígitos numéricos")
    private String telefono;
    
    /**
     * Dirección del cliente (opcional)
     */
    @Size(max = 300, message = "La dirección no puede exceder 300 caracteres")
    private String direccion;
    
    /**
     * RFC del cliente (opcional pero debe ser válido si se proporciona)
     */
    @Pattern(regexp = "^[A-ZÑ&]{3,4}[0-9]{6}[A-Z0-9]{3}$", 
             message = "RFC inválido. Formato: AAA000000XXX (persona moral) o AAAA000000XXX (persona física)")
    @Size(min = 12, max = 13, message = "El RFC debe tener entre 12 y 13 caracteres")
    private String rfc;
    
    /**
     * Fecha de nacimiento (opcional, formato ISO 8601)
     */
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private java.time.LocalDate fechaNacimiento;
    
    /**
     * Tipo de cliente (opcional)
     */
    @Pattern(regexp = "^(FRECUENTE|OCASIONAL|MAYORISTA|VIP)$", 
             message = "Tipo de cliente inválido. Usar: FRECUENTE, OCASIONAL, MAYORISTA o VIP")
    private String tipo = "OCASIONAL";
    
    /**
     * Estado del cliente
     */
    @Pattern(regexp = "^(ACTIVO|INACTIVO|SUSPENDIDO)$", 
             message = "Estado inválido. Usar: ACTIVO, INACTIVO o SUSPENDIDO")
    private String estado = "ACTIVO";
    
    /**
     * Notas adicionales sobre el cliente (opcional)
     */
    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private String notas;
    
    // Constructores
    public ClienteDTO() {
        this.tipo = "OCASIONAL";
        this.estado = "ACTIVO";
    }
    
    public ClienteDTO(Long id, String nombre, String apellido, String email, String telefono, 
                     String direccion, String rfc, java.time.LocalDate fechaNacimiento, 
                     String tipo, String estado, String notas) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.rfc = rfc;
        this.fechaNacimiento = fechaNacimiento;
        this.tipo = tipo != null ? tipo : "OCASIONAL";
        this.estado = estado != null ? estado : "ACTIVO";
        this.notas = notas;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }
    
    public java.time.LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(java.time.LocalDate fechaNacimiento) { 
        this.fechaNacimiento = fechaNacimiento; 
    }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
