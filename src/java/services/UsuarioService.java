package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import config.DatabaseConfig;
import model.Role;
import model.Usuario;

/**
 * Servicio para gestión de usuarios
 */
public class UsuarioService {
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * Obtener todos los usuarios
     */
    public List<Usuario> getAllUsuarios() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = """
            SELECT u.id, u.email, u.password_hash, u.nombre, u.apellido, 
                   u.rol_id, u.activo, u.created_at, u.updated_at,
                   r.nombre as role_nombre, r.descripcion as role_descripcion
            FROM usuarios u
            LEFT JOIN roles r ON u.rol_id = r.id
            ORDER BY u.created_at DESC
        """;
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getLong("id"));
                usuario.setEmail(rs.getString("email"));
                usuario.setPasswordHash(rs.getString("password_hash"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setApellido(rs.getString("apellido"));
                usuario.setRolId(rs.getInt("rol_id"));
                usuario.setActivo(rs.getBoolean("activo"));
                usuario.setCreatedAt(rs.getTimestamp("created_at"));
                usuario.setUpdatedAt(rs.getTimestamp("updated_at"));
                
                // Agregar información del rol si existe
                if (rs.getString("role_nombre") != null) {
                    Role role = new Role();
                    role.setId(rs.getInt("rol_id"));
                    role.setNombre(rs.getString("role_nombre"));
                    role.setDescripcion(rs.getString("role_descripcion"));
                    usuario.setRole(role);
                }
                
                usuarios.add(usuario);
            }
        }
        
        return usuarios;
    }
    
    /**
     * Obtener usuario por ID
     */
    public Usuario getUsuarioById(Long id) throws SQLException {
        String sql = """
            SELECT u.id, u.email, u.password_hash, u.nombre, u.apellido, 
                   u.rol_id, u.activo, u.created_at, u.updated_at,
                   r.nombre as role_nombre, r.descripcion as role_descripcion
            FROM usuarios u
            LEFT JOIN roles r ON u.rol_id = r.id
            WHERE u.id = ?
        """;
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getLong("id"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setPasswordHash(rs.getString("password_hash"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setApellido(rs.getString("apellido"));
                    usuario.setRolId(rs.getInt("rol_id"));
                    usuario.setActivo(rs.getBoolean("activo"));
                    usuario.setCreatedAt(rs.getTimestamp("created_at"));
                    usuario.setUpdatedAt(rs.getTimestamp("updated_at"));
                    
                    // Agregar información del rol si existe
                    if (rs.getString("role_nombre") != null) {
                        Role role = new Role();
                        role.setId(rs.getInt("rol_id"));
                        role.setNombre(rs.getString("role_nombre"));
                        role.setDescripcion(rs.getString("role_descripcion"));
                        usuario.setRole(role);
                    }
                    
                    return usuario;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Obtener usuario por email
     */
    public Usuario getUsuarioByEmail(String email) throws SQLException {
        String sql = """
            SELECT u.id, u.email, u.password_hash, u.nombre, u.apellido, 
                   u.rol_id, u.activo, u.created_at, u.updated_at,
                   r.nombre as role_nombre, r.descripcion as role_descripcion
            FROM usuarios u
            LEFT JOIN roles r ON u.rol_id = r.id
            WHERE u.email = ?
        """;
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getLong("id"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setPasswordHash(rs.getString("password_hash"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setApellido(rs.getString("apellido"));
                    usuario.setRolId(rs.getInt("rol_id"));
                    usuario.setActivo(rs.getBoolean("activo"));
                    usuario.setCreatedAt(rs.getTimestamp("created_at"));
                    usuario.setUpdatedAt(rs.getTimestamp("updated_at"));
                    
                    // Agregar información del rol si existe
                    if (rs.getString("role_nombre") != null) {
                        Role role = new Role();
                        role.setId(rs.getInt("rol_id"));
                        role.setNombre(rs.getString("role_nombre"));
                        role.setDescripcion(rs.getString("role_descripcion"));
                        usuario.setRole(role);
                    }
                    
                    return usuario;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Crear nuevo usuario
     */
    public Usuario createUsuario(Usuario usuario) throws SQLException {
        String sql = """
            INSERT INTO usuarios (email, password_hash, nombre, apellido, rol_id, activo)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, usuario.getEmail());
            stmt.setString(2, hashPassword(usuario.getPasswordHash()));
            stmt.setString(3, usuario.getNombre());
            stmt.setString(4, usuario.getApellido());
            stmt.setInt(5, usuario.getRolId());
            stmt.setBoolean(6, usuario.getActivo() != null ? usuario.getActivo() : true);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al crear usuario, no se insertaron filas");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    usuario.setId(generatedKeys.getLong(1));
                    return getUsuarioById(usuario.getId());
                } else {
                    throw new SQLException("Error al crear usuario, no se obtuvo el ID");
                }
            }
        }
    }
    
    /**
     * Actualizar usuario existente
     */
    public Usuario updateUsuario(Long id, Usuario usuario) throws SQLException {
        String sql = """
            UPDATE usuarios 
            SET email = ?, nombre = ?, apellido = ?, rol_id = ?, activo = ?, updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
        """;
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getEmail());
            stmt.setString(2, usuario.getNombre());
            stmt.setString(3, usuario.getApellido());
            stmt.setInt(4, usuario.getRolId());
            stmt.setBoolean(5, usuario.getActivo() != null ? usuario.getActivo() : false);
            stmt.setLong(6, id);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al actualizar usuario, no se encontró el usuario con ID: " + id);
            }
            
            return getUsuarioById(id);
        }
    }
    
    /**
     * Actualizar contraseña del usuario
     */
    public boolean updatePassword(Long id, String newPassword) throws SQLException {
        String sql = "UPDATE usuarios SET password_hash = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, hashPassword(newPassword));
            stmt.setLong(2, id);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Eliminar usuario (desactivar)
     */
    public boolean deleteUsuario(Long id) throws SQLException {
        String sql = "UPDATE usuarios SET activo = false, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Verificar credenciales de usuario (para login)
     */
    public Usuario authenticateUser(String email, String password) throws SQLException {
        Usuario usuario = getUsuarioByEmail(email);
        
        if (usuario != null && usuario.getActivo() && verifyPassword(password, usuario.getPasswordHash())) {
            return usuario;
        }
        
        return null;
    }
    
    /**
     * Obtener usuarios por rol
     */
    public List<Usuario> getUsuariosByRole(Integer rolId) throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = """
            SELECT u.id, u.email, u.password_hash, u.nombre, u.apellido, 
                   u.rol_id, u.activo, u.created_at, u.updated_at,
                   r.nombre as role_nombre, r.descripcion as role_descripcion
            FROM usuarios u
            LEFT JOIN roles r ON u.rol_id = r.id
            WHERE u.rol_id = ? AND u.activo = true
            ORDER BY u.nombre, u.apellido
        """;
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, rolId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getLong("id"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setPasswordHash(rs.getString("password_hash"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setApellido(rs.getString("apellido"));
                    usuario.setRolId(rs.getInt("rol_id"));
                    usuario.setActivo(rs.getBoolean("activo"));
                    usuario.setCreatedAt(rs.getTimestamp("created_at"));
                    usuario.setUpdatedAt(rs.getTimestamp("updated_at"));
                    
                    // Agregar información del rol
                    if (rs.getString("role_nombre") != null) {
                        Role role = new Role();
                        role.setId(rs.getInt("rol_id"));
                        role.setNombre(rs.getString("role_nombre"));
                        role.setDescripcion(rs.getString("role_descripcion"));
                        usuario.setRole(role);
                    }
                    
                    usuarios.add(usuario);
                }
            }
        }
        
        return usuarios;
    }
    
    /**
     * Hash de contraseña usando BCrypt
     */
    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }
    
    /**
     * Verificar contraseña
     */
    private boolean verifyPassword(String password, String hashedPassword) {
        return passwordEncoder.matches(password, hashedPassword);
    }
}