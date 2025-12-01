package services;

import config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Cliente;
import model.DetalleVenta;
import model.Usuario;
import model.Venta;

/**
 * Service para gestión de ventas con soporte transaccional
 */
public class VentaService {
    private static final Logger log = LoggerFactory.getLogger(VentaService.class);
    private final DatabaseConfig dbConfig;
    
    public VentaService() {
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    /**
     * Obtener todas las ventas
     */
    public List<Venta> findAll() throws SQLException {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT v.*, " +
                     "c.nombre as cliente_nombre, c.apellido as cliente_apellido, " +
                     "u.nombre as usuario_nombre, u.apellido as usuario_apellido " +
                     "FROM ventas v " +
                     "LEFT JOIN clientes c ON v.cliente_id = c.id " +
                     "LEFT JOIN usuarios u ON v.usuario_id = u.id " +
                     "ORDER BY v.fecha DESC";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                ventas.add(mapResultSetToVenta(rs));
            }
        }
        
        return ventas;
    }
    
    /**
     * Buscar venta por ID
     */
    public Venta findById(Long id) throws SQLException {
        String sql = "SELECT v.*, " +
                     "c.nombre as cliente_nombre, c.apellido as cliente_apellido, " +
                     "u.nombre as usuario_nombre, u.apellido as usuario_apellido " +
                     "FROM ventas v " +
                     "LEFT JOIN clientes c ON v.cliente_id = c.id " +
                     "LEFT JOIN usuarios u ON v.usuario_id = u.id " +
                     "WHERE v.id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVenta(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Buscar ventas por fecha
     */
    public List<Venta> findByFecha(Date fechaInicio, Date fechaFin) throws SQLException {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT v.*, " +
                     "c.nombre as cliente_nombre, c.apellido as cliente_apellido, " +
                     "u.nombre as usuario_nombre, u.apellido as usuario_apellido " +
                     "FROM ventas v " +
                     "LEFT JOIN clientes c ON v.cliente_id = c.id " +
                     "LEFT JOIN usuarios u ON v.usuario_id = u.id " +
                     "WHERE DATE(v.fecha) BETWEEN ? AND ? " +
                     "ORDER BY v.fecha DESC";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, fechaInicio);
            stmt.setDate(2, fechaFin);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapResultSetToVenta(rs));
                }
            }
        }
        
        return ventas;
    }
    
    /**
     * Crear una nueva venta con sus detalles (Transacción atómica)
     * Si algo falla, se hace rollback automático de TODA la operación
     */
    public Venta createConDetalles(Venta venta, List<DetalleVenta> detalles) throws SQLException {
        log.info("Iniciando creación de venta. Cliente: {}, Items: {}", 
            venta.getClienteId(), detalles.size());
        
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Insertar la venta
            String sqlVenta = "INSERT INTO ventas (fecha, cliente_id, usuario_id, subtotal, descuento, " +
                             "impuestos, total, metodo_pago, estado, observaciones) " +
                             "VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            long ventaId;
            try (PreparedStatement stmt = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setObject(1, venta.getClienteId());
                stmt.setLong(2, venta.getUsuarioId());
                stmt.setBigDecimal(3, venta.getSubtotal());
                stmt.setBigDecimal(4, venta.getDescuento());
                stmt.setBigDecimal(5, venta.getImpuestos());
                stmt.setBigDecimal(6, venta.getTotal());
                stmt.setString(7, venta.getMetodoPago());
                stmt.setString(8, venta.getEstado() != null ? venta.getEstado() : "COMPLETADA");
                stmt.setString(9, venta.getObservaciones());
                
                stmt.executeUpdate();
                
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        ventaId = generatedKeys.getLong(1);
                        venta.setId(ventaId);
                    } else {
                        throw new SQLException("Error al crear venta, no se obtuvo ID");
                    }
                }
            }
            
            // 2. Insertar los detalles (esto activa el trigger de descuento de stock)
            String sqlDetalle = "INSERT INTO detalle_ventas (venta_id, producto_id, nombre_producto, " +
                               "cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(sqlDetalle)) {
                for (DetalleVenta detalle : detalles) {
                    stmt.setLong(1, ventaId);
                    stmt.setLong(2, detalle.getProductoId());
                    stmt.setString(3, detalle.getNombreProducto());
                    stmt.setInt(4, detalle.getCantidad());
                    stmt.setBigDecimal(5, detalle.getPrecioUnitario());
                    stmt.setBigDecimal(6, detalle.getSubtotal());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
            
            conn.commit();
            
            log.info("Venta creada exitosamente. ID: {}, Total: ${}", ventaId, venta.getTotal());
            return venta;
            
        } catch (SQLException e) {
            log.error("Error al crear venta. Cliente: {}, Error: {}", 
                venta.getClienteId(), e.getMessage(), e);
            if (conn != null) {
                try {
                    conn.rollback();
                    log.warn("Rollback exitoso para venta fallida");
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
     * Obtener detalles de una venta
     */
    public List<DetalleVenta> getDetalles(Long ventaId) throws SQLException {
        List<DetalleVenta> detalles = new ArrayList<>();
        String sql = "SELECT dv.*, p.nombre as producto_nombre " +
                     "FROM detalle_ventas dv " +
                     "LEFT JOIN productos p ON dv.producto_id = p.id " +
                     "WHERE dv.venta_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, ventaId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    detalles.add(mapResultSetToDetalleVenta(rs));
                }
            }
        }
        
        return detalles;
    }
    
    /**
     * Cancelar una venta (cambia estado y elimina detalles para revertir stock)
     */
    public boolean cancelar(Long id) throws SQLException {
        Connection conn = null;
        try {
            conn = dbConfig.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Eliminar detalles (esto activa el trigger de reversión de stock)
            String sqlDeleteDetalles = "DELETE FROM detalle_ventas WHERE venta_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlDeleteDetalles)) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }
            
            // 2. Actualizar estado de la venta
            String sqlUpdateVenta = "UPDATE ventas SET estado = 'CANCELADA' WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateVenta)) {
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
     * Obtener total de ventas por período
     */
    public BigDecimal getTotalVentasPorPeriodo(Date fechaInicio, Date fechaFin) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total), 0) as total " +
                     "FROM ventas " +
                     "WHERE DATE(fecha) BETWEEN ? AND ? AND estado = 'COMPLETADA'";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, fechaInicio);
            stmt.setDate(2, fechaFin);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total");
                }
            }
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * Mapear ResultSet a objeto Venta
     */
    private Venta mapResultSetToVenta(ResultSet rs) throws SQLException {
        Venta venta = new Venta(
            rs.getLong("id"),
            rs.getTimestamp("fecha"),
            rs.getObject("cliente_id") != null ? rs.getLong("cliente_id") : null,
            rs.getLong("usuario_id"),
            rs.getBigDecimal("subtotal"),
            rs.getBigDecimal("descuento"),
            rs.getBigDecimal("impuestos"),
            rs.getBigDecimal("total"),
            rs.getString("metodo_pago"),
            rs.getString("estado"),
            rs.getString("observaciones"),
            rs.getTimestamp("created_at"),
            rs.getTimestamp("updated_at")
        );
        
        // Mapear cliente si existe
        try {
            String clienteNombre = rs.getString("cliente_nombre");
            if (clienteNombre != null) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getLong("cliente_id"));
                cliente.setNombre(clienteNombre);
                cliente.setApellido(rs.getString("cliente_apellido"));
                venta.setCliente(cliente);
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
                venta.setUsuario(usuario);
            }
        } catch (SQLException e) {
            // Si no hay JOIN, ignorar
        }
        
        return venta;
    }
    
    /**
     * Mapear ResultSet a objeto DetalleVenta
     */
    private DetalleVenta mapResultSetToDetalleVenta(ResultSet rs) throws SQLException {
        return new DetalleVenta(
            rs.getLong("id"),
            rs.getLong("venta_id"),
            rs.getLong("producto_id"),
            rs.getString("nombre_producto"),
            rs.getInt("cantidad"),
            rs.getBigDecimal("precio_unitario"),
            rs.getBigDecimal("subtotal"),
            rs.getTimestamp("created_at")
        );
    }
}
