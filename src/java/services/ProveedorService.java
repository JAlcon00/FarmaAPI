package services;

import config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Proveedor;

/**
 * DAO para la tabla proveedores
 */
public class ProveedorService {
    private final DatabaseConfig dbConfig;
    
    public ProveedorService() {
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    /**
     * Obtener todos los proveedores
     */
    public List<Proveedor> findAll() throws SQLException {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT * FROM proveedores WHERE activo = TRUE ORDER BY nombre";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                proveedores.add(mapResultSetToProveedor(rs));
            }
        }
        
        return proveedores;
    }
    
    /**
     * Buscar proveedor por ID
     */
    public Proveedor findById(Long id) throws SQLException {
        String sql = "SELECT * FROM proveedores WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProveedor(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Buscar proveedores por nombre
     */
    public List<Proveedor> findByNombre(String nombre) throws SQLException {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT * FROM proveedores WHERE nombre LIKE ? AND activo = TRUE ORDER BY nombre";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + nombre + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    proveedores.add(mapResultSetToProveedor(rs));
                }
            }
        }
        
        return proveedores;
    }
    
    /**
     * Crear un nuevo proveedor
     */
    public Proveedor create(Proveedor proveedor) throws SQLException {
        String sql = "INSERT INTO proveedores (nombre, rfc, telefono, email, direccion, ciudad, activo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, proveedor.getNombre());
            stmt.setString(2, proveedor.getRfc());
            stmt.setString(3, proveedor.getTelefono());
            stmt.setString(4, proveedor.getEmail());
            stmt.setString(5, proveedor.getDireccion());
            stmt.setString(6, proveedor.getCiudad());
            stmt.setBoolean(7, proveedor.getActivo() != null ? proveedor.getActivo() : true);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        proveedor.setId(generatedKeys.getLong(1));
                        return proveedor;
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Actualizar un proveedor
     */
    public boolean update(Proveedor proveedor) throws SQLException {
        String sql = "UPDATE proveedores SET nombre = ?, rfc = ?, telefono = ?, email = ?, " +
                     "direccion = ?, ciudad = ?, activo = ? WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, proveedor.getNombre());
            stmt.setString(2, proveedor.getRfc());
            stmt.setString(3, proveedor.getTelefono());
            stmt.setString(4, proveedor.getEmail());
            stmt.setString(5, proveedor.getDireccion());
            stmt.setString(6, proveedor.getCiudad());
            stmt.setBoolean(7, proveedor.getActivo());
            stmt.setLong(8, proveedor.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Eliminar (soft delete) un proveedor
     */
    public boolean delete(Long id) throws SQLException {
        String sql = "UPDATE proveedores SET activo = FALSE WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Mapear ResultSet a objeto Proveedor
     */
    private Proveedor mapResultSetToProveedor(ResultSet rs) throws SQLException {
        return new Proveedor(
            rs.getLong("id"),
            rs.getString("nombre"),
            rs.getString("rfc"),
            rs.getString("telefono"),
            rs.getString("email"),
            rs.getString("direccion"),
            rs.getString("ciudad"),
            rs.getBoolean("activo"),
            rs.getTimestamp("created_at"),
            rs.getTimestamp("updated_at")
        );
    }
}
