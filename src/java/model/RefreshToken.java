package model;

import java.sql.Timestamp;

/**
 * Modelo para Refresh Tokens
 * Permite renovar JWT sin requerir login
 */
public class RefreshToken {
    private Long id;
    private String token;
    private Long usuarioId;
    private Timestamp expiresAt;
    private boolean revoked;
    private Timestamp revokedAt;
    private Timestamp createdAt;
    private String ipAddress;
    private String userAgent;

    // Constructores
    public RefreshToken() {}

    public RefreshToken(String token, Long usuarioId, Timestamp expiresAt, String ipAddress, String userAgent) {
        this.token = token;
        this.usuarioId = usuarioId;
        this.expiresAt = expiresAt;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.revoked = false;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Timestamp getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Timestamp expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public Timestamp getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(Timestamp revokedAt) {
        this.revokedAt = revokedAt;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * Verifica si el token ha expirado
     */
    public boolean isExpired() {
        return expiresAt != null && expiresAt.before(new Timestamp(System.currentTimeMillis()));
    }

    /**
     * Verifica si el token es válido (no revocado y no expirado)
     */
    public boolean isValid() {
        return !revoked && !isExpired();
    }

    @Override
    public String toString() {
        return "RefreshToken{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", expiresAt=" + expiresAt +
                ", revoked=" + revoked +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }
}
