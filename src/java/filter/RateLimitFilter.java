package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.JsonResponse;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Filtro de Rate Limiting basado en roles
 * Limita el número de peticiones por minuto según el rol del usuario
 * Usa algoritmo Token Bucket para distribución suave de requests
 */
@WebFilter(filterName = "RateLimitFilter", urlPatterns = {"/api/*"})
public class RateLimitFilter implements Filter {
    
    // Cache de buckets por usuario (userId -> TokenBucket)
    private static final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    
    // Configuración de límites por rol
    private static final ConcurrentHashMap<Integer, RateLimit> ROLE_LIMITS = new ConcurrentHashMap<>();
    
    static {
        // Configurar límites por rol (requests por minuto)
        ROLE_LIMITS.put(1, new RateLimit(Integer.MAX_VALUE, "ADMIN")); // ADMIN: Ilimitado
        ROLE_LIMITS.put(2, new RateLimit(200, "DIRECTOR")); // DIRECTOR: 200/min
        ROLE_LIMITS.put(3, new RateLimit(150, "GERENTE")); // GERENTE: 150/min
        ROLE_LIMITS.put(4, new RateLimit(100, "SUPERVISOR")); // SUPERVISOR: 100/min
        ROLE_LIMITS.put(5, new RateLimit(100, "CAJERO")); // CAJERO: 100/min
        ROLE_LIMITS.put(6, new RateLimit(100, "VENDEDOR")); // VENDEDOR: 100/min
        ROLE_LIMITS.put(7, new RateLimit(80, "INVENTARISTA")); // INVENTARISTA: 80/min
        ROLE_LIMITS.put(8, new RateLimit(80, "CONTADOR")); // CONTADOR: 80/min
        ROLE_LIMITS.put(9, new RateLimit(60, "AUXILIAR_CONTABLE")); // AUXILIAR_CONTABLE: 60/min
        ROLE_LIMITS.put(10, new RateLimit(80, "COMPRADOR")); // COMPRADOR: 80/min
        ROLE_LIMITS.put(11, new RateLimit(60, "RECEPCIONISTA")); // RECEPCIONISTA: 60/min
        ROLE_LIMITS.put(12, new RateLimit(50, "SOPORTE_TECNICO")); // SOPORTE_TECNICO: 50/min
        ROLE_LIMITS.put(13, new RateLimit(100, "ANALISTA_VENTAS")); // ANALISTA_VENTAS: 100/min
        ROLE_LIMITS.put(14, new RateLimit(80, "ENCARGADO_COMPRAS")); // ENCARGADO_COMPRAS: 80/min
        ROLE_LIMITS.put(15, new RateLimit(60, "ASISTENTE_GERENCIA")); // ASISTENTE_GERENCIA: 60/min
        ROLE_LIMITS.put(16, new RateLimit(50, "FARMACEUTICO")); // FARMACEUTICO: 50/min
        ROLE_LIMITS.put(17, new RateLimit(40, "REPARTIDOR")); // REPARTIDOR: 40/min
        ROLE_LIMITS.put(18, new RateLimit(30, "ALMACENISTA")); // ALMACENISTA: 30/min
        ROLE_LIMITS.put(19, new RateLimit(20, "PRACTICANTE")); // PRACTICANTE: 20/min
        ROLE_LIMITS.put(20, new RateLimit(10, "INVITADO")); // INVITADO: 10/min
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("🚦 RateLimitFilter inicializado - Límites por rol configurados");
        
        // Tarea de limpieza periódica cada 5 minutos
        Thread cleanupThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(TimeUnit.MINUTES.toMillis(5));
                    cleanupExpiredBuckets();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        cleanupThread.setDaemon(true);
        cleanupThread.setName("RateLimit-Cleanup");
        cleanupThread.start();
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Saltar rate limiting para OPTIONS (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            chain.doFilter(request, response);
            return;
        }
        
        // Saltar rate limiting para endpoint de autenticación
        String path = httpRequest.getRequestURI();
        if (path.endsWith("/api/usuarios/auth") || path.endsWith("/api/usuarios/refresh")) {
            chain.doFilter(request, response);
            return;
        }
        
        // Obtener información del usuario del request (inyectada por JwtAuthenticationFilter)
        Long userId = (Long) httpRequest.getAttribute("userId");
        Integer roleId = (Integer) httpRequest.getAttribute("roleId");
        
        // Si no hay usuario autenticado, aplicar límite más restrictivo
        if (userId == null || roleId == null) {
            // Límite para requests no autenticados: 20 por minuto por IP
            String key = "anonymous_" + getClientIP(httpRequest);
            if (!checkRateLimit(key, 20, httpResponse)) {
                return;
            }
        } else {
            // Aplicar límite según el rol del usuario
            RateLimit limit = ROLE_LIMITS.getOrDefault(roleId, new RateLimit(50, "UNKNOWN"));
            String key = "user_" + userId;
            
            if (!checkRateLimit(key, limit.requestsPerMinute, httpResponse)) {
                // Log del rate limit excedido
                System.out.println("⚠️ Rate limit excedido - Usuario: " + userId + 
                                   ", Rol: " + limit.roleName + 
                                   ", Límite: " + limit.requestsPerMinute + "/min");
                return;
            }
        }
        
        // Continuar con la cadena de filtros
        chain.doFilter(request, response);
    }
    
    /**
     * Verifica si se permite una petición según el rate limit
     */
    private boolean checkRateLimit(String key, int maxRequestsPerMinute, HttpServletResponse response) 
            throws IOException {
        
        // Obtener o crear bucket para este usuario/IP
        TokenBucket bucket = buckets.computeIfAbsent(key, 
            k -> new TokenBucket(maxRequestsPerMinute));
        
        // Intentar consumir un token
        if (!bucket.tryConsume()) {
            // Rate limit excedido
            long waitSeconds = bucket.getSecondsUntilRefill();
            
            // Agregar headers informativos
            response.setHeader("X-RateLimit-Limit", String.valueOf(maxRequestsPerMinute));
            response.setHeader("X-RateLimit-Remaining", "0");
            response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() / 1000 + waitSeconds));
            response.setHeader("Retry-After", String.valueOf(waitSeconds));
            
            JsonResponse.tooManyRequests(response, 
                "Límite de peticiones excedido. Intenta nuevamente en " + waitSeconds + " segundos.");
            
            return false;
        }
        
        // Agregar headers informativos
        response.setHeader("X-RateLimit-Limit", String.valueOf(maxRequestsPerMinute));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(bucket.getAvailableTokens()));
        
        return true;
    }
    
    /**
     * Limpia buckets que no se han usado recientemente
     */
    private void cleanupExpiredBuckets() {
        long now = System.currentTimeMillis();
        int removed = 0;
        
        for (String key : buckets.keySet()) {
            TokenBucket bucket = buckets.get(key);
            if (bucket != null && bucket.isExpired(now)) {
                buckets.remove(key);
                removed++;
            }
        }
        
        if (removed > 0) {
            System.out.println("🧹 RateLimit cleanup: " + removed + " buckets expirados eliminados");
        }
    }
    
    /**
     * Obtiene la IP del cliente (considerando proxies)
     */
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // Si viene de X-Forwarded-For, puede tener múltiples IPs (tomar la primera)
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
    
    @Override
    public void destroy() {
        buckets.clear();
        System.out.println("🚦 RateLimitFilter destruido");
    }
    
    /**
     * Clase para almacenar configuración de límite por rol
     */
    private static class RateLimit {
        final int requestsPerMinute;
        final String roleName;
        
        RateLimit(int requestsPerMinute, String roleName) {
            this.requestsPerMinute = requestsPerMinute;
            this.roleName = roleName;
        }
    }
    
    /**
     * Implementación del algoritmo Token Bucket
     * Permite ráfagas controladas de requests
     */
    private static class TokenBucket {
        private final int capacity;
        private final double refillRate; // tokens por milisegundo
        private double tokens;
        private long lastRefillTime;
        private long lastAccessTime;
        
        TokenBucket(int requestsPerMinute) {
            this.capacity = requestsPerMinute;
            this.refillRate = requestsPerMinute / 60000.0; // requests por ms
            this.tokens = requestsPerMinute; // Empezar con bucket lleno
            this.lastRefillTime = System.currentTimeMillis();
            this.lastAccessTime = System.currentTimeMillis();
        }
        
        /**
         * Intenta consumir un token
         */
        synchronized boolean tryConsume() {
            refill();
            lastAccessTime = System.currentTimeMillis();
            
            if (tokens >= 1) {
                tokens -= 1;
                return true;
            }
            return false;
        }
        
        /**
         * Rellena el bucket según el tiempo transcurrido
         */
        private void refill() {
            long now = System.currentTimeMillis();
            long timePassed = now - lastRefillTime;
            
            double tokensToAdd = timePassed * refillRate;
            tokens = Math.min(capacity, tokens + tokensToAdd);
            
            lastRefillTime = now;
        }
        
        /**
         * Obtiene tokens disponibles actuales
         */
        synchronized int getAvailableTokens() {
            refill();
            return (int) tokens;
        }
        
        /**
         * Calcula segundos hasta el próximo token disponible
         */
        synchronized long getSecondsUntilRefill() {
            if (tokens >= 1) {
                return 0;
            }
            
            double tokensNeeded = 1 - tokens;
            long msNeeded = (long) (tokensNeeded / refillRate);
            return (msNeeded / 1000) + 1; // Redondear arriba
        }
        
        /**
         * Verifica si el bucket ha expirado (no usado en 10 minutos)
         */
        boolean isExpired(long now) {
            return (now - lastAccessTime) > TimeUnit.MINUTES.toMillis(10);
        }
    }
    
    /**
     * Obtiene estadísticas del rate limiter (útil para monitoreo)
     */
    public static String getStats() {
        return String.format("RateLimiter{activeBuckets=%d, roles=%d}", 
            buckets.size(), ROLE_LIMITS.size());
    }
}
