package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Modelo para la tabla detalle_ventas
 */
public class DetalleVenta {
    private Long id;
    private Long ventaId;
    private Long productoId;
    private String nombreProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private Timestamp createdAt;
    
    // Relación con Producto (opcional, para joins)
    private Producto producto;

    // Constructor vacío
    public DetalleVenta() {
    }

    // Constructor con parámetros principales
    public DetalleVenta(Long ventaId, Long productoId, String nombreProducto, 
                        Integer cantidad, BigDecimal precioUnitario) {
        this.ventaId = ventaId;
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
    }

    // Constructor completo
    public DetalleVenta(Long id, Long ventaId, Long productoId, String nombreProducto, 
                        Integer cantidad, BigDecimal precioUnitario, BigDecimal subtotal, 
                        Timestamp createdAt) {
        this.id = id;
        this.ventaId = ventaId;
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
        this.createdAt = createdAt;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVentaId() {
        return ventaId;
    }

    public void setVentaId(Long ventaId) {
        this.ventaId = ventaId;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    @Override
    public String toString() {
        return "DetalleVenta{" +
                "id=" + id +
                ", ventaId=" + ventaId +
                ", productoId=" + productoId +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + subtotal +
                '}';
    }
}
