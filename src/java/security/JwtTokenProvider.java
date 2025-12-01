package security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import model.Usuario;

/**
 * Proveedor de tokens JWT para autenticación
 */
@Component
public class JwtTokenProvider {
    
    // Clave secreta para firmar tokens (256 bits)
    private final String secretKey;
    
    // Duración del token: 24 horas
    private final long expirationTime;
    
    public JwtTokenProvider() {
        this.secretKey = System.getenv().getOrDefault("JWT_SECRET", 
            "MiFarmaControlSecretKeyParaJWT2025DebeSerLargaYSegura256BitsMinimo");
        this.expirationTime = Long.parseLong(
            System.getenv().getOrDefault("JWT_EXPIRATION", "86400000"));
    }
    
    /**
     * Genera un token JWT para un usuario
     */
    public String generateToken(Usuario usuario) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", usuario.getId());
        claims.put("email", usuario.getEmail());
        claims.put("roleId", usuario.getRolId());
        claims.put("nombre", usuario.getNombre());
        
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .claims(claims)
                .subject(usuario.getEmail())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }
    
    /**
     * Extrae el email del token
     */
    public String getEmailFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }
    
    /**
     * Extrae el ID de usuario del token
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("userId", Long.class);
    }
    
    /**
     * Extrae el ID de rol del token
     */
    public Integer getRoleIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("roleId", Integer.class);
    }
    
    /**
     * Valida si el token es válido
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Token inválido o expirado
            return false;
        }
    }
    
    /**
     * Verifica si el token ha expirado
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
    
    /**
     * Obtiene los claims del token
     */
    private Claims getClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * Extrae el token del header Authorization
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
