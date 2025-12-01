package filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import security.JwtTokenProvider;
import utils.JsonResponse;

import java.io.IOException;

/**
 * Filtro de autenticación JWT
 * Valida el token en el header Authorization para endpoints protegidos
 */
public class JwtAuthenticationFilter implements Filter {
    
    private JwtTokenProvider tokenProvider;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.tokenProvider = new JwtTokenProvider();
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Log de debug
        String path = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();
        System.out.println("🔍 JwtAuthenticationFilter: " + method + " " + path);
        
        // Permitir OPTIONS (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            System.out.println("✅ Permitiendo OPTIONS (CORS preflight)");
            chain.doFilter(request, response);
            return;
        }
        
        // Permitir rutas públicas
        System.out.println("🔎 Evaluando ruta: '" + path + "'");
        System.out.println("🔎 ¿Contiene '/auth'? " + path.contains("/auth"));
        System.out.println("🔎 ¿Contiene '/google-auth'? " + path.contains("/google-auth"));
        if (isPublicPath(path)) {
            System.out.println("✅ Ruta pública permitida: " + path);
            chain.doFilter(request, response);
            return;
        }
        
        System.out.println("⚠️ Ruta NO es pública, requiere autenticación: " + path);
        
        // Validar token JWT
        String authHeader = httpRequest.getHeader("Authorization");
        System.out.println("🔑 Authorization header: " + (authHeader != null ? authHeader.substring(0, Math.min(30, authHeader.length())) + "..." : "NULL"));
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ Token no encontrado o formato inválido");
            JsonResponse.unauthorized(httpResponse, "Token de autenticación requerido");
            return;
        }
        
        String token = tokenProvider.extractTokenFromHeader(authHeader);
        
        if (token == null || !tokenProvider.validateToken(token)) {
            System.out.println("❌ Token inválido o expirado");
            JsonResponse.unauthorized(httpResponse, "Token inválido o expirado");
            return;
        }
        
        // Agregar información del usuario al request
        Long userId = tokenProvider.getUserIdFromToken(token);
        Integer roleId = tokenProvider.getRoleIdFromToken(token);
        String email = tokenProvider.getEmailFromToken(token);
        
        System.out.println("✅ Token válido - userId: " + userId + ", roleId: " + roleId + ", email: " + email);
        
        httpRequest.setAttribute("userId", userId);
        httpRequest.setAttribute("roleId", roleId);
        httpRequest.setAttribute("userEmail", email);
        
        // Continuar con la cadena de filtros
        chain.doFilter(request, response);
    }
    
    /**
     * Determina si la ruta es pública (no requiere autenticación)
     */
    private boolean isPublicPath(String path) {
        // Rutas públicas
        return path.contains("/auth") ||           // Login (incluye /usuarios/auth y /usuarios/google-auth)
               path.contains("/google-auth") ||     // Google Auth específico
               path.contains("/health") ||          // Health check
               path.contains("/actuator") ||        // Métricas
               path.contains("/swagger") ||         // Documentación
               path.contains("/api-docs") ||        // OpenAPI docs
               path.contains("/test");              // Testing
    }
    
    @Override
    public void destroy() {
        // Limpieza si es necesaria
    }
}
