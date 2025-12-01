package services;

import config.DatabaseConfig;
import model.RefreshToken;
import java.sql.*;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para gestionar Refresh Tokens
 * Permite renovar tokens JWT sin necesidad de re-autenticarse
 */
public class RefreshTokenService {
    
    // Duración del refresh token: 7 días
    private static final int REFRESH_TOKEN_EXPIRATION_DAYS = 7;
    
    /**
     * Genera un nuevo refresh token para un usuario
     * @param usuarioId ID del usuario
     * @param ipAddress IP del cliente
     * @param userAgent User-Agent del navegador
     * @return RefreshToken generado
     */
    public static RefreshToken generateRefreshToken(Long usuarioId, String ipAddress, String userAgent) {
        String token = generateTokenString();
        
        // Calcular fecha de expiración: ahora + 7 días
        long expirationTime = System.currentTimeMillis() + (REFRESH_TOKEN_EXPIRATION_DAYS * 24L * 60L * 60L * 1000L);
        Timestamp expiresAt = new Timestamp(expirationTime);
        
        String sql = "INSERT INTO refresh_tokens (token, usuario_id, expires_at, ip_address, user_agent) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, token);
            stmt.setLong(2, usuarioId);
            stmt.setTimestamp(3, expiresAt);
            stmt.setString(4, ipAddress);
            stmt.setString(5, userAgent);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Long id = generatedKeys.getLong(1);
                        
                        RefreshToken refreshToken = new RefreshToken();
                        refreshToken.setId(id);
                        refreshToken.setToken(token);
                        refreshToken.setUsuarioId(usuarioId);
                        refreshToken.setExpiresAt(expiresAt);
                        refreshToken.setRevoked(false);
                        refreshToken.setIpAddress(ipAddress);
                        refreshToken.setUserAgent(userAgent);
                        refreshToken.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                        
                        return refreshToken;
                    }
                }
            }
            
            throw new SQLException("Error al generar refresh token, no se obtuvo ID");
            
        } catch (SQLException e) {
            System.err.println("Error al generar refresh token: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Valida un refresh token
     * @param token Token a validar
     * @return RefreshToken si es válido, null si no existe o es inválido
     */
    public static RefreshToken validateRefreshToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        
        String sql = "SELECT id, token, usuario_id, expires_at, revoked, ip_address, user_agent, created_at " +
                     "FROM refresh_tokens WHERE token = ?";
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, token);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    RefreshToken refreshToken = new RefreshToken();
                    refreshToken.setId(rs.getLong("id"));
                    refreshToken.setToken(rs.getString("token"));
                    refreshToken.setUsuarioId(rs.getLong("usuario_id"));
                    refreshToken.setExpiresAt(rs.getTimestamp("expires_at"));
                    refreshToken.setRevoked(rs.getBoolean("revoked"));
                    refreshToken.setIpAddress(rs.getString("ip_address"));
                    refreshToken.setUserAgent(rs.getString("user_agent"));
                    refreshToken.setCreatedAt(rs.getTimestamp("created_at"));
                    
                    // Verificar si el token es válido (no expirado y no revocado)
                    if (refreshToken.isValid()) {
                        return refreshToken;
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al validar refresh token: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Revoca un refresh token específico
     * @param token Token a revocar
     * @return true si se revocó exitosamente
     */
    public static boolean revokeToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        String sql = "UPDATE refresh_tokens SET revoked = true WHERE token = ?";
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, token);
            int rowsAffected = stmt.executeUpdate();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al revocar token: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Revoca todos los refresh tokens de un usuario
     * @param usuarioId ID del usuario
     * @return true si se revocaron exitosamente
     */
    public static boolean revokeAllUserTokens(Long usuarioId) {
        String sql = "UPDATE refresh_tokens SET revoked = true WHERE usuario_id = ? AND revoked = false";
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, usuarioId);
            int rowsAffected = stmt.executeUpdate();
            
            return rowsAffected >= 0; // Retorna true incluso si no había tokens activos
            
        } catch (SQLException e) {
            System.err.println("Error al revocar tokens del usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtiene todos los refresh tokens activos de un usuario
     * @param usuarioId ID del usuario
     * @return Lista de tokens activos
     */
    public static List<RefreshToken> getActiveTokensByUser(Long usuarioId) {
        List<RefreshToken> tokens = new ArrayList<>();
        
        String sql = "SELECT id, token, usuario_id, expires_at, revoked, ip_address, user_agent, created_at " +
                     "FROM refresh_tokens " +
                     "WHERE usuario_id = ? AND revoked = false AND expires_at > NOW() " +
                     "ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, usuarioId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RefreshToken token = new RefreshToken();
                    token.setId(rs.getLong("id"));
                    token.setToken(rs.getString("token"));
                    token.setUsuarioId(rs.getLong("usuario_id"));
                    token.setExpiresAt(rs.getTimestamp("expires_at"));
                    token.setRevoked(rs.getBoolean("revoked"));
                    token.setIpAddress(rs.getString("ip_address"));
                    token.setUserAgent(rs.getString("user_agent"));
                    token.setCreatedAt(rs.getTimestamp("created_at"));
                    
                    tokens.add(token);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener tokens activos: " + e.getMessage());
            e.printStackTrace();
        }
        
        return tokens;
    }
    
    /**
     * Limpia tokens expirados de la base de datos
     * @return Número de tokens eliminados
     */
    public static int cleanExpiredTokens() {
        String sql = "DELETE FROM refresh_tokens WHERE expires_at < NOW()";
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int rowsDeleted = stmt.executeUpdate();
            
            if (rowsDeleted > 0) {
                System.out.println("Tokens expirados limpiados: " + rowsDeleted);
            }
            
            return rowsDeleted;
            
        } catch (SQLException e) {
            System.err.println("Error al limpiar tokens expirados: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Genera una cadena única para el token
     * @return Token UUID
     */
    private static String generateTokenString() {
        // Generar UUID único y seguro
        return UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
    }
    
    /**
     * Rotación de token: Revoca el token anterior y genera uno nuevo
     * @param oldToken Token anterior a revocar
     * @param usuarioId ID del usuario
     * @param ipAddress IP del cliente
     * @param userAgent User-Agent del navegador
     * @return Nuevo RefreshToken o null si falla
     */
    public static RefreshToken rotateToken(String oldToken, Long usuarioId, String ipAddress, String userAgent) {
        // Validar que el token anterior es válido
        RefreshToken oldRefreshToken = validateRefreshToken(oldToken);
        
        if (oldRefreshToken == null || !oldRefreshToken.getUsuarioId().equals(usuarioId)) {
            return null;
        }
        
        // Revocar el token anterior
        revokeToken(oldToken);
        
        // Generar nuevo token
        return generateRefreshToken(usuarioId, ipAddress, userAgent);
    }
}
