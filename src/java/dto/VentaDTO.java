package dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para crear/actualizar ventas con validaciones
 */
public class VentaDTO {
    
    /**
     * ID del cliente (opcional para ventas sin cliente registrado)
     */
    @Positive(message = "El ID del cliente debe ser positivo")
    private Long clienteId;
    
    /**
     * ID del usuario que registra la venta (requerido)
     */
    @NotNull(message = "El ID de usuario es obligatorio")
    @Positive(message = "El ID del usuario debe ser positivo")
    private Long usuarioId;
    
    /**
     * Subtotal antes de descuentos e impuestos
     */
    @NotNull(message = "El subtotal es obligatorio")
    @DecimalMin(value = "0.01", message = "El subtotal debe ser mayor a 0")
    @DecimalMax(value = "999999.99", message = "El subtotal no puede exceder $999,999.99")
    @Digits(integer = 6, fraction = 2, message = "El subtotal debe tener máximo 6 dígitos enteros y 2 decimales")
    private BigDecimal subtotal;
    
    /**
     * Descuento aplicado (0 si no hay descuento)
     */
    @NotNull(message = "El descuento es obligatorio (usar 0 si no aplica)")
    @DecimalMin(value = "0.00", message = "El descuento no puede ser negativo")
    @DecimalMax(value = "99999.99", message = "El descuento no puede exceder $99,999.99")
    @Digits(integer = 5, fraction = 2, message = "El descuento debe tener máximo 5 dígitos enteros y 2 decimales")
    private BigDecimal descuento;
    
    /**
     * Impuestos (IVA) aplicados
     */
    @NotNull(message = "Los impuestos son obligatorios")
    @DecimalMin(value = "0.00", message = "Los impuestos no pueden ser negativos")
    @DecimalMax(value = "99999.99", message = "Los impuestos no pueden exceder $99,999.99")
    @Digits(integer = 5, fraction = 2, message = "Los impuestos deben tener máximo 5 dígitos enteros y 2 decimales")
    private BigDecimal impuestos;
    
    /**
     * Total final de la venta
     */
    @NotNull(message = "El total es obligatorio")
    @DecimalMin(value = "0.01", message = "El total debe ser mayor a 0")
    @DecimalMax(value = "999999.99", message = "El total no puede exceder $999,999.99")
    @Digits(integer = 6, fraction = 2, message = "El total debe tener máximo 6 dígitos enteros y 2 decimales")
    private BigDecimal total;
    
    /**
     * Método de pago usado
     */
    @NotBlank(message = "El método de pago es obligatorio")
    @Pattern(regexp = "^(EFECTIVO|TARJETA|TRANSFERENCIA|CREDITO)$", 
             message = "Método de pago inválido. Usar: EFECTIVO, TARJETA, TRANSFERENCIA o CREDITO")
    private String metodoPago;
    
    /**
     * Estado de la venta
     */
    @Pattern(regexp = "^(COMPLETADA|PENDIENTE|CANCELADA)$", 
             message = "Estado inválido. Usar: COMPLETADA, PENDIENTE o CANCELADA")
    private String estado = "COMPLETADA";
    
    /**
     * Observaciones adicionales (opcional)
     */
    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    private String observaciones;
    
    /**
     * Detalles de la venta (productos vendidos)
     */
    @NotNull(message = "Los detalles de la venta son obligatorios")
    @NotEmpty(message = "Debe incluir al menos un producto en la venta")
    @Size(min = 1, max = 100, message = "La venta debe tener entre 1 y 100 productos")
    @Valid
    private List<DetalleVentaDTO> detalles;
    
    /**
     * DTO interno para detalles de venta
     */
    public static class DetalleVentaDTO {
        
        @NotNull(message = "El ID del producto es obligatorio")
        @Positive(message = "El ID del producto debe ser positivo")
        private Long productoId;
        
        @NotBlank(message = "El nombre del producto es obligatorio")
        @Size(min = 3, max = 200, message = "El nombre del producto debe tener entre 3 y 200 caracteres")
        private String nombreProducto;
        
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad mínima es 1")
        @Max(value = 10000, message = "La cantidad máxima es 10,000")
        private Integer cantidad;
        
        @NotNull(message = "El precio unitario es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor a 0")
        @DecimalMax(value = "99999.99", message = "El precio unitario no puede exceder $99,999.99")
        @Digits(integer = 5, fraction = 2, message = "El precio debe tener máximo 5 dígitos enteros y 2 decimales")
        private BigDecimal precioUnitario;
        
        @NotNull(message = "El subtotal es obligatorio")
        @DecimalMin(value = "0.01", message = "El subtotal debe ser mayor a 0")
        @DecimalMax(value = "999999.99", message = "El subtotal no puede exceder $999,999.99")
        @Digits(integer = 6, fraction = 2, message = "El subtotal debe tener máximo 6 dígitos enteros y 2 decimales")
        private BigDecimal subtotal;
        
        // Constructores
        public DetalleVentaDTO() {}
        
        public DetalleVentaDTO(Long productoId, String nombreProducto, Integer cantidad, 
                              BigDecimal precioUnitario, BigDecimal subtotal) {
            this.productoId = productoId;
            this.nombreProducto = nombreProducto;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
            this.subtotal = subtotal;
        }
        
        // Getters y Setters
        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }
        
        public String getNombreProducto() { return nombreProducto; }
        public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
        
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
        
        public BigDecimal getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
        
        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    }
    
    // Constructores de VentaDTO
    public VentaDTO() {
        this.estado = "COMPLETADA";
    }
    
    public VentaDTO(Long clienteId, Long usuarioId, BigDecimal subtotal, BigDecimal descuento,
                   BigDecimal impuestos, BigDecimal total, String metodoPago, String estado,
                   String observaciones, List<DetalleVentaDTO> detalles) {
        this.clienteId = clienteId;
        this.usuarioId = usuarioId;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.impuestos = impuestos;
        this.total = total;
        this.metodoPago = metodoPago;
        this.estado = estado != null ? estado : "COMPLETADA";
        this.observaciones = observaciones;
        this.detalles = detalles;
    }
    
    // Getters y Setters de VentaDTO
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }
    
    public BigDecimal getImpuestos() { return impuestos; }
    public void setImpuestos(BigDecimal impuestos) { this.impuestos = impuestos; }
    
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    
    public List<DetalleVentaDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVentaDTO> detalles) { this.detalles = detalles; }
}
