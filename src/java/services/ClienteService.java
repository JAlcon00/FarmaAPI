package services;

import config.DatabaseConfig;
import model.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla clientes
 */
public class ClienteService {
    private final DatabaseConfig dbConfig;
    
    public ClienteService() {
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    /**
     * Obtener todos los clientes
     */
    public List<Cliente> findAll() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE activo = TRUE ORDER BY nombre, apellido";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                clientes.add(mapResultSetToCliente(rs));
            }
        }
        
        return clientes;
    }
    
    /**
     * Buscar cliente por ID
     */
    public Cliente findById(Long id) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCliente(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Buscar clientes por nombre
     */
    public List<Cliente> findByNombre(String nombre) throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE (nombre LIKE ? OR apellido LIKE ?) AND activo = TRUE " +
                     "ORDER BY nombre, apellido";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + nombre + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapResultSetToCliente(rs));
                }
            }
        }
        
        return clientes;
    }
    
    /**
     * Crear un nuevo cliente
     */
    public Cliente create(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes (nombre, apellido, telefono, email, direccion, " +
                     "fecha_nacimiento, activo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getApellido());
            stmt.setString(3, cliente.getTelefono());
            stmt.setString(4, cliente.getEmail());
            stmt.setString(5, cliente.getDireccion());
            stmt.setDate(6, cliente.getFechaNacimiento());
            stmt.setBoolean(7, cliente.getActivo() != null ? cliente.getActivo() : true);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        cliente.setId(generatedKeys.getLong(1));
                        return cliente;
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Actualizar un cliente
     */
    public boolean update(Cliente cliente) throws SQLException {
        String sql = "UPDATE clientes SET nombre = ?, apellido = ?, telefono = ?, email = ?, " +
                     "direccion = ?, fecha_nacimiento = ?, activo = ? WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getApellido());
            stmt.setString(3, cliente.getTelefono());
            stmt.setString(4, cliente.getEmail());
            stmt.setString(5, cliente.getDireccion());
            stmt.setDate(6, cliente.getFechaNacimiento());
            stmt.setBoolean(7, cliente.getActivo());
            stmt.setLong(8, cliente.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Eliminar (soft delete) un cliente
     */
    public boolean delete(Long id) throws SQLException {
        String sql = "UPDATE clientes SET activo = FALSE WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Mapear ResultSet a objeto Cliente
     */
    private Cliente mapResultSetToCliente(ResultSet rs) throws SQLException {
        return new Cliente(
            rs.getLong("id"),
            rs.getString("nombre"),
            rs.getString("apellido"),
            rs.getString("telefono"),
            rs.getString("email"),
            rs.getString("direccion"),
            rs.getDate("fecha_nacimiento"),
            rs.getBoolean("activo"),
            rs.getTimestamp("created_at"),
            rs.getTimestamp("updated_at")
        );
    }
}
