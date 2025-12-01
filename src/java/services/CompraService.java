package services;

import config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Compra;
import model.DetalleCompra;
import model.Proveedor;
import model.Usuario;

/**
 * Service para gestión de compras con soporte transaccional
 */
public class CompraService {
    private static final Logger log = LoggerFactory.getLogger(CompraService.class);
    private final DatabaseConfig dbConfig;
    
    public CompraService() {
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    /**
     * Obtener todas las compras
     */
    public List<Compra> findAll() throws SQLException {
        List<Compra> compras = new ArrayList<>();
        String sql = "SELECT c.*, " +
                     "p.nombre as proveedor_nombre, " +
                     "u.nombre as usuario_nombre, u.apellido as usuario_apellido " +
                     "FROM compras c " +
                     "LEFT JOIN proveedores p ON c.proveedor_id = p.id " +
                     "LEFT JOIN usuarios u ON c.usuario_id = u.id " +
                     "ORDER BY c.fecha DESC";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                compras.add(mapResultSetToCompra(rs));
            }
        }
        
        return compras;
    }
    
    /**
     * Buscar compra por ID
     */
    public Compra findById(Long id) throws SQLException {
        String sql = "SELECT c.*, " +
                     "p.nombre as proveedor_nombre, " +
                     "u.nombre as usuario_nombre, u.apellido as usuario_apellido " +
                     "FROM compras c " +
                     "LEFT JOIN proveedores p ON c.proveedor_id = p.id " +
                     "LEFT JOIN usuarios u ON c.usuario_id = u.id " +
                     "WHERE c.id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCompra(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Buscar compras por proveedor
     */
    public List<Compra> findByProveedor(Long proveedorId) throws SQLException {
        List<Compra> compras = new ArrayList<>();
        String sql = "SELECT c.*, " +
                     "p.nombre as proveedor_nombre, " +
                     "u.nombre as usuario_nombre, u.apellido as usuario_apellido " +
                     "FROM compras c " +
                     "LEFT JOIN proveedores p ON c.proveedor_id = p.id " +
                     "LEFT JOIN usuarios u ON c.usuario_id = u.id " +
                     "WHERE c.proveedor_id = ? " +
                     "ORDER BY c.fecha DESC";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, proveedorId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    compras.add(mapResultSetToCompra(rs));
                }
            }
        }
        
        return compras;
    }
    
    /**
     * Crear una nueva compra con sus detalles (Transacción atómica)
     * Si algo falla, se hace rollback automático de TODA la operación
     */
    public Compra createConDetalles(Compra compra, List<DetalleCompra> detalles) throws SQLException {
        log.info("Iniciando creación de compra. Proveedor: {}, Items: {}", 
            compra.getProveedorId(), detalles.size());
        
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Insertar la compra
            String sqlCompra = "INSERT INTO compras (fecha, proveedor_id, usuario_id, subtotal, " +
                              "impuestos, total, estado, observaciones) " +
                              "VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, ?, ?)";
            
            long compraId;
            try (PreparedStatement stmt = conn.prepareStatement(sqlCompra, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setLong(1, compra.getProveedorId());
                stmt.setLong(2, compra.getUsuarioId());
                stmt.setBigDecimal(3, compra.getSubtotal());
                stmt.setBigDecimal(4, compra.getImpuestos());
                stmt.setBigDecimal(5, compra.getTotal());
                stmt.setString(6, compra.getEstado() != null ? compra.getEstado() : "PENDIENTE");
                stmt.setString(7, compra.getObservaciones());
                
                stmt.executeUpdate();
                
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        compraId = generatedKeys.getLong(1);
                        compra.setId(compraId);
                    } else {
                        throw new SQLException("Error al crear compra, no se obtuvo ID");
                    }
                }
            }
            
            // 2. Insertar los detalles (esto activa el trigger de aumento de stock)
            String sqlDetalle = "INSERT INTO detalle_compras (compra_id, producto_id, cantidad, " +
                               "precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(sqlDetalle)) {
                for (DetalleCompra detalle : detalles) {
                    stmt.setLong(1, compraId);
                    stmt.setLong(2, detalle.getProductoId());
                    stmt.setInt(3, detalle.getCantidad());
                    stmt.setBigDecimal(4, detalle.getPrecioUnitario());
                    stmt.setBigDecimal(5, detalle.getSubtotal());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
            
            conn.commit();
            
            log.info("Compra creada exitosamente. ID: {}, Total: ${}", compraId, compra.getTotal());
            return compra;
            
        } catch (SQLException e) {
            log.error("Error al crear compra. Proveedor: {}, Error: {}", 
                compra.getProveedorId(), e.getMessage(), e);
            if (conn != null) {
                try {
                    conn.rollback();
                    log.warn("Rollback exitoso para compra fallida");
                } catch (SQLException rollbackEx) {
                    log.error("Error en rollback", rollbackEx);
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                    log.error("Error al restaurar autocommit", ex);
                }
            }
        }
    }
    
    /**
     * Obtener detalles de una compra
     */
    public List<DetalleCompra> getDetalles(Long compraId) throws SQLException {
        List<DetalleCompra> detalles = new ArrayList<>();
        String sql = "SELECT dc.*, p.nombre as producto_nombre " +
                     "FROM detalle_compras dc " +
                     "LEFT JOIN productos p ON dc.producto_id = p.id " +
                     "WHERE dc.compra_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, compraId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    detalles.add(mapResultSetToDetalleCompra(rs));
                }
            }
        }
        
        return detalles;
    }
    
    /**
     * Actualizar estado de una compra
     */
    public boolean updateEstado(Long id, String nuevoEstado) throws SQLException {
        String sql = "UPDATE compras SET estado = ? WHERE id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nuevoEstado);
            stmt.setLong(2, id);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Cancelar una compra (elimina detalles para revertir stock)
     */
    public boolean cancelar(Long id) throws SQLException {
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Eliminar detalles (esto activa el trigger de reversión de stock)
            String sqlDeleteDetalles = "DELETE FROM detalle_compras WHERE compra_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlDeleteDetalles)) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }
            
            // 2. Actualizar estado de la compra
            String sqlUpdateCompra = "UPDATE compras SET estado = 'CANCELADA' WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateCompra)) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        }
    }
    
    /**
     * Mapear ResultSet a objeto Compra
     */
    private Compra mapResultSetToCompra(ResultSet rs) throws SQLException {
        Compra compra = new Compra(
            rs.getLong("id"),
            rs.getTimestamp("fecha"),
            rs.getLong("proveedor_id"),
            rs.getLong("usuario_id"),
            rs.getBigDecimal("subtotal"),
            rs.getBigDecimal("impuestos"),
            rs.getBigDecimal("total"),
            rs.getString("estado"),
            rs.getString("observaciones"),
            rs.getTimestamp("created_at"),
            rs.getTimestamp("updated_at")
        );
        
        // Mapear proveedor
        try {
            String proveedorNombre = rs.getString("proveedor_nombre");
            if (proveedorNombre != null) {
                Proveedor proveedor = new Proveedor();
                proveedor.setId(rs.getLong("proveedor_id"));
                proveedor.setNombre(proveedorNombre);
                compra.setProveedor(proveedor);
            }
        } catch (SQLException e) {
            // Si no hay JOIN, ignorar
        }
        
        // Mapear usuario
        try {
            String usuarioNombre = rs.getString("usuario_nombre");
            if (usuarioNombre != null) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getLong("usuario_id"));
                usuario.setNombre(usuarioNombre);
                usuario.setApellido(rs.getString("usuario_apellido"));
                compra.setUsuario(usuario);
            }
        } catch (SQLException e) {
            // Si no hay JOIN, ignorar
        }
        
        return compra;
    }
    
    /**
     * Mapear ResultSet a objeto DetalleCompra
     */
    private DetalleCompra mapResultSetToDetalleCompra(ResultSet rs) throws SQLException {
        return new DetalleCompra(
            rs.getLong("id"),
            rs.getLong("compra_id"),
            rs.getLong("producto_id"),
            rs.getInt("cantidad"),
            rs.getBigDecimal("precio_unitario"),
            rs.getBigDecimal("subtotal"),
            rs.getTimestamp("created_at")
        );
    }
}
