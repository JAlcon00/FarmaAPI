package model;

import java.sql.Timestamp;

/**
 * Modelo para registros de auditoría
 * Registra todas las operaciones críticas del sistema
 */
public class AuditLog {
    private Long id;
    private Long usuarioId;
    private String usuarioEmail;
    private String accion; // CREATE, UPDATE, DELETE, LOGIN, LOGOUT
    private String entidad; // PRODUCTO, VENTA, COMPRA, USUARIO, etc.
    private Long entidadId;
    private String detalles; // JSON con información adicional
    private String ipAddress;
    private String userAgent;
    private Timestamp createdAt;

    // Constantes para acciones
    public static final String ACCION_CREATE = "CREATE";
    public static final String ACCION_UPDATE = "UPDATE";
    public static final String ACCION_DELETE = "DELETE";
    public static final String ACCION_LOGIN = "LOGIN";
    public static final String ACCION_LOGOUT = "LOGOUT";
    public static final String ACCION_LOGIN_FAILED = "LOGIN_FAILED";

    // Constantes para entidades
    public static final String ENTIDAD_PRODUCTO = "PRODUCTO";
    public static final String ENTIDAD_VENTA = "VENTA";
    public static final String ENTIDAD_COMPRA = "COMPRA";
    public static final String ENTIDAD_CLIENTE = "CLIENTE";
    public static final String ENTIDAD_PROVEEDOR = "PROVEEDOR";
    public static final String ENTIDAD_CATEGORIA = "CATEGORIA";
    public static final String ENTIDAD_USUARIO = "USUARIO";
    public static final String ENTIDAD_ROLE = "ROLE";

    // Constructores
    public AuditLog() {}

    public AuditLog(Long usuarioId, String usuarioEmail, String accion, String entidad, 
                    Long entidadId, String detalles, String ipAddress, String userAgent) {
        this.usuarioId = usuarioId;
        this.usuarioEmail = usuarioEmail;
        this.accion = accion;
        this.entidad = entidad;
        this.entidadId = entidadId;
        this.detalles = detalles;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioEmail() {
        return usuarioEmail;
    }

    public void setUsuarioEmail(String usuarioEmail) {
        this.usuarioEmail = usuarioEmail;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public Long getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(Long entidadId) {
        this.entidadId = entidadId;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "id=" + id +
                ", usuarioEmail='" + usuarioEmail + '\'' +
                ", accion='" + accion + '\'' +
                ", entidad='" + entidad + '\'' +
                ", entidadId=" + entidadId +
                ", ipAddress='" + ipAddress + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
