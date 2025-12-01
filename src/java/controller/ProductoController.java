package controller;

import dto.ProductoDTO;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import model.Producto;
import services.CategoriaService;
import services.ProductoService;
import services.ValidationService;

/**
 * Controlador para productos
 */
public class ProductoController {
    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    
    public ProductoController() {
        this.productoService = new ProductoService();
        this.categoriaService = new CategoriaService();
    }
    
    /**
     * Obtener todos los productos
     */
    public List<Producto> getAllProductos() throws SQLException {
        return productoService.findAll();
    }
    
    /**
     * Obtener producto por ID
     */
    public Producto getProductoById(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de producto inválido");
        }
        
        Producto producto = productoService.findById(id);
        if (producto == null) {
            throw new SQLException("Producto no encontrado con ID: " + id);
        }
        
        return producto;
    }
    
    /**
     * Obtener productos por categoría
     */
    public List<Producto> getProductosByCategoria(Long categoriaId) throws SQLException {
        if (categoriaId == null || categoriaId <= 0) {
            throw new IllegalArgumentException("ID de categoría inválido");
        }
        
        // Verificar que la categoría existe
        if (categoriaService.findById(categoriaId) == null) {
            throw new SQLException("Categoría no encontrada con ID: " + categoriaId);
        }
        
        return productoService.findByCategoria(categoriaId);
    }
    
    /**
     * Obtener productos con stock bajo
     */
    public List<Producto> getProductosConStockBajo() throws SQLException {
        return productoService.findLowStock();
    }
    
    /**
     * Buscar productos por nombre, descripción o código de barras
     */
    public List<Producto> searchProductos(String query) throws SQLException {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Consulta de búsqueda requerida");
        }
        return productoService.search(query.trim());
    }
    
    /**
     * Crear un nuevo producto usando DTO con validaciones
     */
    public Producto createProducto(ProductoDTO productoDTO) throws SQLException {
        // Validar DTO usando Bean Validation
        ValidationService.validate(productoDTO);
        
        // Verificar que la categoría existe
        if (categoriaService.findById(productoDTO.getCategoriaId()) == null) {
            throw new SQLException("Categoría no encontrada con ID: " + productoDTO.getCategoriaId());
        }
        
        // Convertir DTO a Entity
        Producto producto = convertToEntity(productoDTO);
        
        return productoService.create(producto);
    }
    
    /**
     * Crear producto usando Entity (mantener compatibilidad)
     */
    public Producto createProducto(Producto producto) throws SQLException {
        // Convertir Entity a DTO para validar
        ProductoDTO dto = convertToDTO(producto);
        
        // Usar el método principal con DTO
        return createProducto(dto);
    }
    
    /**
     * Convertir DTO a Entity
     */
    private Producto convertToEntity(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setId(dto.getId());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setStockMinimo(dto.getStockMinimo());
        producto.setCategoriaId(dto.getCategoriaId());
        producto.setCodigoBarras(dto.getCodigoBarras());
        // Convertir estado String a activo Boolean
        producto.setActivo(dto.getEstado() == null || !"inactivo".equals(dto.getEstado()));
        return producto;
    }
    
    /**
     * Convertir Entity a DTO
     */
    private ProductoDTO convertToDTO(Producto entity) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setDescripcion(entity.getDescripcion());
        dto.setPrecio(entity.getPrecio());
        dto.setStock(entity.getStock());
        dto.setStockMinimo(entity.getStockMinimo());
        dto.setCategoriaId(entity.getCategoriaId());
        // ProveedorId: usar categoriaId si no está disponible (compatibilidad)
        dto.setProveedorId(entity.getCategoriaId());
        dto.setCodigoBarras(entity.getCodigoBarras());
        // Convertir activo Boolean a estado String
        dto.setEstado(entity.getActivo() != null && entity.getActivo() ? "activo" : "inactivo");
        return dto;
    }
    
    /**
     * Actualizar un producto usando DTO con validaciones
     */
    public boolean updateProducto(ProductoDTO productoDTO) throws SQLException {
        // Validar ID
        if (productoDTO.getId() == null || productoDTO.getId() <= 0) {
            throw new IllegalArgumentException("ID de producto inválido");
        }
        
        // Validar DTO usando Bean Validation
        ValidationService.validate(productoDTO);
        
        // Verificar que existe
        Producto existing = productoService.findById(productoDTO.getId());
        if (existing == null) {
            throw new SQLException("Producto no encontrado con ID: " + productoDTO.getId());
        }
        
        // Verificar que la categoría existe si se cambió
        if (productoDTO.getCategoriaId() != null && 
            !productoDTO.getCategoriaId().equals(existing.getCategoriaId())) {
            if (categoriaService.findById(productoDTO.getCategoriaId()) == null) {
                throw new SQLException("Categoría no encontrada con ID: " + productoDTO.getCategoriaId());
            }
        }
        
        // Convertir DTO a Entity
        Producto producto = convertToEntity(productoDTO);
        
        return productoService.update(producto);
    }
    
    /**
     * Actualizar producto usando Entity (mantener compatibilidad)
     */
    public boolean updateProducto(Producto producto) throws SQLException {
        // Convertir Entity a DTO para validar
        ProductoDTO dto = convertToDTO(producto);
        
        // Usar el método principal con DTO
        return updateProducto(dto);
    }
    
    /**
     * Actualizar stock de un producto
     */
    public boolean updateStock(Long id, Integer nuevoStock) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de producto inválido");
        }
        
        if (nuevoStock == null || nuevoStock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        
        // Verificar que existe
        Producto existing = productoService.findById(id);
        if (existing == null) {
            throw new SQLException("Producto no encontrado con ID: " + id);
        }
        
        return productoService.updateStock(id, nuevoStock);
    }
    
    /**
     * Eliminar (desactivar) un producto
     */
    public boolean deleteProducto(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de producto inválido");
        }
        
        // Verificar que existe
        Producto existing = productoService.findById(id);
        if (existing == null) {
            throw new SQLException("Producto no encontrado con ID: " + id);
        }
        
        return productoService.delete(id);
    }
}
