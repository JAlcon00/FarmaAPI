package model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * Modelo para la tabla ventas
 */
public class Venta {
    private Long id;
    private Timestamp fecha;
    private Long clienteId;
    private Long usuarioId;
    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal impuestos;
    private BigDecimal total;
    private String metodoPago;
    private String estado;
    private String observaciones;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Relaciones (opcional, para joins)
    private Cliente cliente;
    private Usuario usuario;
    private List<DetalleVenta> detalles;

    // Constructor vacío
    public Venta() {
    }

    // Constructor con parámetros principales
    public Venta(Long clienteId, Long usuarioId, BigDecimal subtotal, BigDecimal descuento, 
                 BigDecimal impuestos, BigDecimal total, String metodoPago) {
        this.clienteId = clienteId;
        this.usuarioId = usuarioId;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.impuestos = impuestos;
        this.total = total;
        this.metodoPago = metodoPago;
        this.estado = "COMPLETADA";
    }

    // Constructor completo
    public Venta(Long id, Timestamp fecha, Long clienteId, Long usuarioId, BigDecimal subtotal, 
                 BigDecimal descuento, BigDecimal impuestos, BigDecimal total, String metodoPago, 
                 String estado, String observaciones, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.fecha = fecha;
        this.clienteId = clienteId;
        this.usuarioId = usuarioId;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.impuestos = impuestos;
        this.total = total;
        this.metodoPago = metodoPago;
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

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
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

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
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

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
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

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }

    @Override
    public String toString() {
        return "Venta{" +
                "id=" + id +
                ", fecha=" + fecha +
                ", clienteId=" + clienteId +
                ", usuarioId=" + usuarioId +
                ", total=" + total +
                ", metodoPago='" + metodoPago + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
