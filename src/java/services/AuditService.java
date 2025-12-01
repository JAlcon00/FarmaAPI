package services;

import config.DatabaseConfig;
import model.AuditLog;
import jakarta.servlet.http.HttpServletRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para registro de auditoría
 * Registra todas las operaciones críticas del sistema
 */
public class AuditService {
    
    /**
     * Registra una operación en el log de auditoría
     */
    public static void log(Long usuarioId, String usuarioEmail, String accion, String entidad, 
                          Long entidadId, String detalles, String ipAddress, String userAgent) {
        String sql = "INSERT INTO audit_log (usuario_id, usuario_email, accion, entidad, " +
                    "entidad_id, detalles, ip_address, user_agent) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setObject(1, usuarioId);
            stmt.setString(2, usuarioEmail);
            stmt.setString(3, accion);
            stmt.setString(4, entidad);
            stmt.setObject(5, entidadId);
            stmt.setString(6, detalles);
            stmt.setString(7, ipAddress);
            stmt.setString(8, userAgent);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            // No lanzar excepción para no interrumpir la operación principal
            System.err.println("Error al registrar auditoría: " + e.getMessage());
        }
    }
    
    /**
     * Registra una operación usando datos del request
     */
    public static void log(HttpServletRequest request, String accion, String entidad, 
                          Long entidadId, String detalles) {
        Long usuarioId = (Long) request.getAttribute("userId");
        String usuarioEmail = (String) request.getAttribute("email");
        String ipAddress = getClientIP(request);
        String userAgent = request.getHeader("User-Agent");
        
        log(usuarioId, usuarioEmail, accion, entidad, entidadId, detalles, ipAddress, userAgent);
    }
    
    /**
     * Registra creación de una entidad
     */
    public static void logCreate(HttpServletRequest request, String entidad, Long entidadId, String detalles) {
        log(request, AuditLog.ACCION_CREATE, entidad, entidadId, detalles);
    }
    
    /**
     * Registra actualización de una entidad
     */
    public static void logUpdate(HttpServletRequest request, String entidad, Long entidadId, String detalles) {
        log(request, AuditLog.ACCION_UPDATE, entidad, entidadId, detalles);
    }
    
    /**
     * Registra eliminación de una entidad
     */
    public static void logDelete(HttpServletRequest request, String entidad, Long entidadId, String detalles) {
        log(request, AuditLog.ACCION_DELETE, entidad, entidadId, detalles);
    }
    
    /**
     * Registra login exitoso
     */
    public static void logLogin(Long usuarioId, String usuarioEmail, String ipAddress, String userAgent) {
        log(usuarioId, usuarioEmail, AuditLog.ACCION_LOGIN, "USUARIO", usuarioId, 
            "Login exitoso", ipAddress, userAgent);
    }
    
    /**
     * Registra intento de login fallido
     */
    public static void logLoginFailed(String usuarioEmail, String reason, String ipAddress, String userAgent) {
        log(null, usuarioEmail, AuditLog.ACCION_LOGIN_FAILED, "USUARIO", null, 
            reason, ipAddress, userAgent);
    }
    
    /**
     * Obtiene registros de auditoría por usuario
     */
    public static List<AuditLog> getByUsuario(Long usuarioId, int limit) throws SQLException {
        String sql = "SELECT * FROM audit_log WHERE usuario_id = ? ORDER BY created_at DESC LIMIT ?";
        List<AuditLog> logs = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, usuarioId);
            stmt.setInt(2, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToAuditLog(rs));
                }
            }
        }
        
        return logs;
    }
    
    /**
     * Obtiene registros de auditoría por entidad
     */
    public static List<AuditLog> getByEntidad(String entidad, Long entidadId, int limit) throws SQLException {
        String sql = "SELECT * FROM audit_log WHERE entidad = ? AND entidad_id = ? " +
                    "ORDER BY created_at DESC LIMIT ?";
        List<AuditLog> logs = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, entidad);
            stmt.setLong(2, entidadId);
            stmt.setInt(3, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToAuditLog(rs));
                }
            }
        }
        
        return logs;
    }
    
    /**
     * Obtiene los últimos N registros de auditoría
     */
    public static List<AuditLog> getRecent(int limit) throws SQLException {
        String sql = "SELECT * FROM audit_log ORDER BY created_at DESC LIMIT ?";
        List<AuditLog> logs = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToAuditLog(rs));
                }
            }
        }
        
        return logs;
    }
    
    /**
     * Obtiene la IP del cliente desde el request
     */
    public static String getClientIP(HttpServletRequest request) {
        String[] headers = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };
        
        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // Si hay múltiples IPs, tomar la primera
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * Mapea ResultSet a AuditLog
     */
    private static AuditLog mapResultSetToAuditLog(ResultSet rs) throws SQLException {
        AuditLog log = new AuditLog();
        log.setId(rs.getLong("id"));
        log.setUsuarioId(rs.getObject("usuario_id", Long.class));
        log.setUsuarioEmail(rs.getString("usuario_email"));
        log.setAccion(rs.getString("accion"));
        log.setEntidad(rs.getString("entidad"));
        log.setEntidadId(rs.getObject("entidad_id", Long.class));
        log.setDetalles(rs.getString("detalles"));
        log.setIpAddress(rs.getString("ip_address"));
        log.setUserAgent(rs.getString("user_agent"));
        log.setCreatedAt(rs.getTimestamp("created_at"));
        return log;
    }
}
