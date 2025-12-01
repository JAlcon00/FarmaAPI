package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Modelo para la tabla productos
 */
public class Producto {
    private Long id;
    private String nombre;
    private String descripcion;
    private Long categoriaId;
    private BigDecimal precio;
    private Integer stock;
    private Integer stockMinimo;
    private String codigoBarras;
    private Boolean activo;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Relación con Categoria (opcional, para joins)
    private Categoria categoria;

    // Constructor vacío
    public Producto() {
    }

    // Constructor con parámetros principales
    public Producto(String nombre, String descripcion, Long categoriaId, BigDecimal precio, 
                    Integer stock, Integer stockMinimo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoriaId = categoriaId;
        this.precio = precio;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.activo = true;
    }

    // Constructor completo
    public Producto(Long id, String nombre, String descripcion, Long categoriaId, 
                    BigDecimal precio, Integer stock, Integer stockMinimo, String codigoBarras, 
                    Boolean activo, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoriaId = categoriaId;
        this.precio = precio;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.codigoBarras = codigoBarras;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
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

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", categoriaId=" + categoriaId +
                ", precio=" + precio +
                ", stock=" + stock +
                ", stockMinimo=" + stockMinimo +
                ", activo=" + activo +
                '}';
    }
}
