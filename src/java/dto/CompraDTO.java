package dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para crear/actualizar compras con validaciones
 */
public class CompraDTO {
    
    /**
     * ID del proveedor (requerido)
     */
    @NotNull(message = "El ID del proveedor es obligatorio")
    @Positive(message = "El ID del proveedor debe ser positivo")
    private Long proveedorId;
    
    /**
     * ID del usuario que registra la compra (requerido)
     */
    @NotNull(message = "El ID de usuario es obligatorio")
    @Positive(message = "El ID del usuario debe ser positivo")
    private Long usuarioId;
    
    /**
     * Subtotal antes de impuestos
     */
    @NotNull(message = "El subtotal es obligatorio")
    @DecimalMin(value = "0.01", message = "El subtotal debe ser mayor a 0")
    @DecimalMax(value = "9999999.99", message = "El subtotal no puede exceder $9,999,999.99")
    @Digits(integer = 7, fraction = 2, message = "El subtotal debe tener máximo 7 dígitos enteros y 2 decimales")
    private BigDecimal subtotal;
    
    /**
     * Impuestos (IVA) aplicados
     */
    @NotNull(message = "Los impuestos son obligatorios")
    @DecimalMin(value = "0.00", message = "Los impuestos no pueden ser negativos")
    @DecimalMax(value = "999999.99", message = "Los impuestos no pueden exceder $999,999.99")
    @Digits(integer = 6, fraction = 2, message = "Los impuestos deben tener máximo 6 dígitos enteros y 2 decimales")
    private BigDecimal impuestos;
    
    /**
     * Total final de la compra
     */
    @NotNull(message = "El total es obligatorio")
    @DecimalMin(value = "0.01", message = "El total debe ser mayor a 0")
    @DecimalMax(value = "9999999.99", message = "El total no puede exceder $9,999,999.99")
    @Digits(integer = 7, fraction = 2, message = "El total debe tener máximo 7 dígitos enteros y 2 decimales")
    private BigDecimal total;
    
    /**
     * Estado de la compra
     */
    @Pattern(regexp = "^(PENDIENTE|RECIBIDA|CANCELADA)$", 
             message = "Estado inválido. Usar: PENDIENTE, RECIBIDA o CANCELADA")
    private String estado = "PENDIENTE";
    
    /**
     * Observaciones adicionales (opcional)
     */
    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    private String observaciones;
    
    /**
     * Detalles de la compra (productos comprados)
     */
    @NotNull(message = "Los detalles de la compra son obligatorios")
    @NotEmpty(message = "Debe incluir al menos un producto en la compra")
    @Size(min = 1, max = 100, message = "La compra debe tener entre 1 y 100 productos")
    @Valid
    private List<DetalleCompraDTO> detalles;
    
    /**
     * DTO interno para detalles de compra
     */
    public static class DetalleCompraDTO {
        
        @NotNull(message = "El ID del producto es obligatorio")
        @Positive(message = "El ID del producto debe ser positivo")
        private Long productoId;
        
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad mínima es 1")
        @Max(value = 50000, message = "La cantidad máxima es 50,000")
        private Integer cantidad;
        
        @NotNull(message = "El precio unitario es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor a 0")
        @DecimalMax(value = "99999.99", message = "El precio unitario no puede exceder $99,999.99")
        @Digits(integer = 5, fraction = 2, message = "El precio debe tener máximo 5 dígitos enteros y 2 decimales")
        private BigDecimal precioUnitario;
        
        @NotNull(message = "El subtotal es obligatorio")
        @DecimalMin(value = "0.01", message = "El subtotal debe ser mayor a 0")
        @DecimalMax(value = "9999999.99", message = "El subtotal no puede exceder $9,999,999.99")
        @Digits(integer = 7, fraction = 2, message = "El subtotal debe tener máximo 7 dígitos enteros y 2 decimales")
        private BigDecimal subtotal;
        
        // Constructores
        public DetalleCompraDTO() {}
        
        public DetalleCompraDTO(Long productoId, Integer cantidad, BigDecimal precioUnitario, BigDecimal subtotal) {
            this.productoId = productoId;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
            this.subtotal = subtotal;
        }
        
        // Getters y Setters
        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }
        
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
        
        public BigDecimal getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
        
        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    }
    
    // Constructores de CompraDTO
    public CompraDTO() {
        this.estado = "PENDIENTE";
    }
    
    public CompraDTO(Long proveedorId, Long usuarioId, BigDecimal subtotal, BigDecimal impuestos,
                    BigDecimal total, String estado, String observaciones, List<DetalleCompraDTO> detalles) {
        this.proveedorId = proveedorId;
        this.usuarioId = usuarioId;
        this.subtotal = subtotal;
        this.impuestos = impuestos;
        this.total = total;
        this.estado = estado != null ? estado : "PENDIENTE";
        this.observaciones = observaciones;
        this.detalles = detalles;
    }
    
    // Getters y Setters de CompraDTO
    public Long getProveedorId() { return proveedorId; }
    public void setProveedorId(Long proveedorId) { this.proveedorId = proveedorId; }
    
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public BigDecimal getImpuestos() { return impuestos; }
    public void setImpuestos(BigDecimal impuestos) { this.impuestos = impuestos; }
    
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    
    public List<DetalleCompraDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleCompraDTO> detalles) { this.detalles = detalles; }
}
