package com.farmacontrol.service;

import config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.farmacontrol.model.Role;

/**
 * DAO para la tabla roles
 */
public class RoleService {
    private final DatabaseConfig dbConfig;
    
    public RoleService() {
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    /**
     * Obtener todos los roles
     */
    public List<Role> findAll() throws SQLException {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM roles WHERE activo = TRUE ORDER BY nombre";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                roles.add(mapResultSetToRole(rs));
            }
        }
        
        return roles;
    }
    
    /**
     * Buscar role por ID
     */
    public Role findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM roles WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRole(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Crear un nuevo role
     */
    public Role create(Role role) throws SQLException {
        String sql = "INSERT INTO roles (nombre, descripcion, activo) VALUES (?, ?, ?)";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, role.getNombre());
            stmt.setString(2, role.getDescripcion());
            stmt.setBoolean(3, role.getActivo() != null ? role.getActivo() : true);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        role.setId(generatedKeys.getInt(1));
                        return role;
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Actualizar un role
     */
    public boolean update(Role role) throws SQLException {
        String sql = "UPDATE roles SET nombre = ?, descripcion = ?, activo = ? WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, role.getNombre());
            stmt.setString(2, role.getDescripcion());
            stmt.setBoolean(3, role.getActivo());
            stmt.setInt(4, role.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Eliminar (soft delete) un role
     */
    public boolean delete(Integer id) throws SQLException {
        String sql = "UPDATE roles SET activo = FALSE WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Mapear ResultSet a objeto Role
     */
    private Role mapResultSetToRole(ResultSet rs) throws SQLException {
        return new Role(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("descripcion"),
            rs.getBoolean("activo"),
            rs.getTimestamp("created_at"),
            rs.getTimestamp("updated_at")
        );
    }
}
