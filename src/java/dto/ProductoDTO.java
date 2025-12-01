package dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO para operaciones de Producto con validación
 */
public class ProductoDTO {
    
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @DecimalMax(value = "999999.99", message = "El precio es demasiado alto")
    @Digits(integer = 6, fraction = 2, message = "Formato de precio inválido (máx: 999999.99)")
    private BigDecimal precio;
    
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Max(value = 999999, message = "El stock es demasiado alto")
    private Integer stock;
    
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;
    
    @NotNull(message = "La categoría es obligatoria")
    @Positive(message = "ID de categoría inválido")
    private Long categoriaId;
    
    @NotNull(message = "El proveedor es obligatorio")
    @Positive(message = "ID de proveedor inválido")
    private Long proveedorId;
    
    private String codigoBarras;
    
    @Pattern(regexp = "^(activo|inactivo)$", message = "Estado debe ser 'activo' o 'inactivo'")
    private String estado;
    
    // Constructors
    public ProductoDTO() {}
    
    public ProductoDTO(Long id, String nombre, String descripcion, BigDecimal precio, 
                      Integer stock, Integer stockMinimo, Long categoriaId, Long proveedorId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.categoriaId = categoriaId;
        this.proveedorId = proveedorId;
        this.estado = "activo";
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    
    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
    
    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
    
    public Long getProveedorId() { return proveedorId; }
    public void setProveedorId(Long proveedorId) { this.proveedorId = proveedorId; }
    
    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
