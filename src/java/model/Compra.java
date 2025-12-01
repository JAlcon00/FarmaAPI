package model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * Modelo para la tabla compras
 */
public class Compra {
    private Long id;
    private Timestamp fecha;
    private Long proveedorId;
    private Long usuarioId;
    private BigDecimal subtotal;
    private BigDecimal impuestos;
    private BigDecimal total;
    private String estado;
    private String observaciones;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Relaciones (opcional, para joins)
    private Proveedor proveedor;
    private Usuario usuario;
    private List<DetalleCompra> detalles;

    // Constructor vacío
    public Compra() {
    }

    // Constructor con parámetros principales
    public Compra(Long proveedorId, Long usuarioId, BigDecimal subtotal, 
                  BigDecimal impuestos, BigDecimal total) {
        this.proveedorId = proveedorId;
        this.usuarioId = usuarioId;
        this.subtotal = subtotal;
        this.impuestos = impuestos;
        this.total = total;
        this.estado = "PENDIENTE";
    }

    // Constructor completo
    public Compra(Long id, Timestamp fecha, Long proveedorId, Long usuarioId, 
                  BigDecimal subtotal, BigDecimal impuestos, BigDecimal total, String estado, 
                  String observaciones, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.fecha = fecha;
        this.proveedorId = proveedorId;
        this.usuarioId = usuarioId;
        this.subtotal = subtotal;
        this.impuestos = impuestos;
        this.total = total;
        this.estado = estado;
        this.observaciones = observaciones;
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

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public Long getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(Long proveedorId) {
        this.proveedorId = proveedorId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(BigDecimal impuestos) {
        this.impuestos = impuestos;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
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

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<DetalleCompra> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleCompra> detalles) {
        this.detalles = detalles;
    }

    @Override
    public String toString() {
        return "Compra{" +
                "id=" + id +
                ", fecha=" + fecha +
                ", proveedorId=" + proveedorId +
                ", usuarioId=" + usuarioId +
                ", total=" + total +
                ", estado='" + estado + '\'' +
                '}';
    }
}
