package services;

import config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Categoria;
import model.Producto;

/**
 * Service para gestión de productos
 */
public class ProductoService {
    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);
    private final DatabaseConfig dbConfig;
    
    public ProductoService() {
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    /**
     * Obtener todos los productos
     */
    public List<Producto> findAll() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.*, c.nombre as categoria_nombre, c.descripcion as categoria_descripcion " +
                     "FROM productos p " +
                     "LEFT JOIN categorias c ON p.categoria_id = c.id " +
                     "WHERE p.activo = TRUE " +
                     "ORDER BY p.nombre";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                productos.add(mapResultSetToProducto(rs));
            }
        }
        
        return productos;
    }
    
    /**
     * Buscar producto por ID
     */
    public Producto findById(Long id) throws SQLException {
        String sql = "SELECT p.*, c.nombre as categoria_nombre, c.descripcion as categoria_descripcion " +
                     "FROM productos p " +
                     "LEFT JOIN categorias c ON p.categoria_id = c.id " +
                     "WHERE p.id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProducto(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Buscar productos por categoría
     */
    public List<Producto> findByCategoria(Long categoriaId) throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.*, c.nombre as categoria_nombre, c.descripcion as categoria_descripcion " +
                     "FROM productos p " +
                     "LEFT JOIN categorias c ON p.categoria_id = c.id " +
                     "WHERE p.categoria_id = ? AND p.activo = TRUE " +
                     "ORDER BY p.nombre";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, categoriaId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapResultSetToProducto(rs));
                }
            }
        }
        
        return productos;
    }
    
    /**
     * Buscar productos con stock bajo
     */
    public List<Producto> findLowStock() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.*, c.nombre as categoria_nombre, c.descripcion as categoria_descripcion " +
                     "FROM productos p " +
                     "LEFT JOIN categorias c ON p.categoria_id = c.id " +
                     "WHERE p.stock <= p.stock_minimo AND p.activo = TRUE " +
                     "ORDER BY p.stock ASC";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                productos.add(mapResultSetToProducto(rs));
            }
        }
        
        return productos;
    }
    
    /**
     * Buscar productos por nombre, descripción o código de barras
     */
    public List<Producto> search(String query) throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.*, c.nombre as categoria_nombre, c.descripcion as categoria_descripcion " +
                     "FROM productos p " +
                     "LEFT JOIN categorias c ON p.categoria_id = c.id " +
                     "WHERE (LOWER(p.nombre) LIKE LOWER(?) " +
                     "   OR LOWER(p.descripcion) LIKE LOWER(?) " +
                     "   OR p.codigo_barras LIKE ?) " +
                     "AND p.activo = TRUE " +
                     "ORDER BY p.nombre";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + query + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapResultSetToProducto(rs));
                }
            }
        }
        
        return productos;
    }
    
    /**
     * Crear un nuevo producto
     */
    public Producto create(Producto producto) throws SQLException {
        String sql = "INSERT INTO productos (nombre, descripcion, categoria_id, precio, stock, " +
                     "stock_minimo, codigo_barras, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getDescripcion());
            stmt.setLong(3, producto.getCategoriaId());
            stmt.setBigDecimal(4, producto.getPrecio());
            stmt.setInt(5, producto.getStock());
            stmt.setInt(6, producto.getStockMinimo());
            stmt.setString(7, producto.getCodigoBarras());
            stmt.setBoolean(8, producto.getActivo() != null ? producto.getActivo() : true);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        producto.setId(generatedKeys.getLong(1));
                        return producto;
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Actualizar un producto
     */
    public boolean update(Producto producto) throws SQLException {
        String sql = "UPDATE productos SET nombre = ?, descripcion = ?, categoria_id = ?, " +
                     "precio = ?, stock = ?, stock_minimo = ?, codigo_barras = ?, activo = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getDescripcion());
            stmt.setLong(3, producto.getCategoriaId());
            stmt.setBigDecimal(4, producto.getPrecio());
            stmt.setInt(5, producto.getStock());
            stmt.setInt(6, producto.getStockMinimo());
            stmt.setString(7, producto.getCodigoBarras());
            stmt.setBoolean(8, producto.getActivo());
            stmt.setLong(9, producto.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Actualizar stock del producto
     */
    public boolean updateStock(Long id, Integer newStock) throws SQLException {
        String sql = "UPDATE productos SET stock = ? WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, newStock);
            stmt.setLong(2, id);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Eliminar (soft delete) un producto
     */
    public boolean delete(Long id) throws SQLException {
        String sql = "UPDATE productos SET activo = FALSE WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Mapear ResultSet a objeto Producto
     */
    private Producto mapResultSetToProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto(
            rs.getLong("id"),
            rs.getString("nombre"),
            rs.getString("descripcion"),
            rs.getLong("categoria_id"),
            rs.getBigDecimal("precio"),
            rs.getInt("stock"),
            rs.getInt("stock_minimo"),
            rs.getString("codigo_barras"),
            rs.getBoolean("activo"),
            rs.getTimestamp("created_at"),
            rs.getTimestamp("updated_at")
        );
        
        // Mapear la categoría si está disponible en el JOIN
        try {
            String categoriaNombre = rs.getString("categoria_nombre");
            if (categoriaNombre != null) {
                Categoria categoria = new Categoria();
                categoria.setId(rs.getLong("categoria_id"));
                categoria.setNombre(categoriaNombre);
                categoria.setDescripcion(rs.getString("categoria_descripcion"));
                producto.setCategoria(categoria);
            }
        } catch (SQLException e) {
            // Si no hay JOIN, ignorar
        }
        
        return producto;
    }
}
