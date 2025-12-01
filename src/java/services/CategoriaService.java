package services;

import config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Categoria;

/**
 * DAO para la tabla categorias
 */
public class CategoriaService {
    private final DatabaseConfig dbConfig;
    
    public CategoriaService() {
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    /**
     * Obtener todas las categorías
     */
    public List<Categoria> findAll() throws SQLException {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categorias WHERE activo = TRUE ORDER BY nombre";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                categorias.add(mapResultSetToCategoria(rs));
            }
        }
        
        return categorias;
    }
    
    /**
     * Buscar categoría por ID
     */
    public Categoria findById(Long id) throws SQLException {
        String sql = "SELECT * FROM categorias WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategoria(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Crear una nueva categoría
     */
    public Categoria create(Categoria categoria) throws SQLException {
        String sql = "INSERT INTO categorias (nombre, descripcion, activo) VALUES (?, ?, ?)";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, categoria.getNombre());
            stmt.setString(2, categoria.getDescripcion());
            stmt.setBoolean(3, categoria.getActivo() != null ? categoria.getActivo() : true);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        categoria.setId(generatedKeys.getLong(1));
                        return categoria;
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Actualizar una categoría
     */
    public boolean update(Categoria categoria) throws SQLException {
        String sql = "UPDATE categorias SET nombre = ?, descripcion = ?, activo = ? WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categoria.getNombre());
            stmt.setString(2, categoria.getDescripcion());
            stmt.setBoolean(3, categoria.getActivo());
            stmt.setLong(4, categoria.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Eliminar (soft delete) una categoría
     */
    public boolean delete(Long id) throws SQLException {
        String sql = "UPDATE categorias SET activo = FALSE WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Mapear ResultSet a objeto Categoria
     */
    private Categoria mapResultSetToCategoria(ResultSet rs) throws SQLException {
        return new Categoria(
            rs.getLong("id"),
            rs.getString("nombre"),
            rs.getString("descripcion"),
            rs.getBoolean("activo"),
            rs.getTimestamp("created_at"),
            rs.getTimestamp("updated_at")
        );
    }
}
