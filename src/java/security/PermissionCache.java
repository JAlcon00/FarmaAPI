package security;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.sql.*;
import config.DatabaseConfig;

/**
 * Cache de permisos por rol con TTL (Time To Live)
 * Mejora el rendimiento al evitar consultas repetitivas a la base de datos
 * Thread-safe usando ConcurrentHashMap
 */
public class PermissionCache {
    
    // Cache principal: roleId -> Set de permisos
    private static final ConcurrentHashMap<Integer, CacheEntry> cache = new ConcurrentHashMap<>();
    
    // TTL del cache: 5 minutos (en milisegundos)
    private static final long CACHE_TTL_MS = TimeUnit.MINUTES.toMillis(5);
    
    // Estadísticas del cache (para monitoreo)
    private static long cacheHits = 0;
    private static long cacheMisses = 0;
    
    /**
     * Clase interna para almacenar permisos con timestamp
     */
    private static class CacheEntry {
        final Set<String> permissions;
        final long timestamp;
        
        CacheEntry(Set<String> permissions) {
            this.permissions = permissions;
            this.timestamp = System.currentTimeMillis();
        }
        
        boolean isExpired() {
            return (System.currentTimeMillis() - timestamp) > CACHE_TTL_MS;
        }
    }
    
    /**
     * Obtiene los permisos de un rol desde el cache o la base de datos
     * @param roleId ID del rol
     * @return Set de códigos de permisos
     */
    public static Set<String> getPermissions(Integer roleId) {
        if (roleId == null) {
            cacheMisses++;
            return Set.of(); // Set vacío
        }
        
        // Intentar obtener del cache
        CacheEntry entry = cache.get(roleId);
        
        // Verificar si existe y no ha expirado
        if (entry != null && !entry.isExpired()) {
            cacheHits++;
            return entry.permissions;
        }
        
        // Cache miss o expirado: consultar base de datos
        cacheMisses++;
        Set<String> permissions = loadPermissionsFromDatabase(roleId);
        
        // Almacenar en cache
        cache.put(roleId, new CacheEntry(permissions));
        
        return permissions;
    }
    
    /**
     * Carga los permisos de un rol desde la base de datos
     * @param roleId ID del rol
     * @return Set de códigos de permisos
     */
    private static Set<String> loadPermissionsFromDatabase(Integer roleId) {
        Set<String> permissions = ConcurrentHashMap.newKeySet();
        
        String sql = "SELECT p.codigo " +
                     "FROM permisos p " +
                     "INNER JOIN rol_permisos rp ON p.id = rp.permiso_id " +
                     "WHERE rp.rol_id = ? AND p.activo = true";
        
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, roleId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    permissions.add(rs.getString("codigo"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error al cargar permisos del rol " + roleId + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return permissions;
    }
    
    /**
     * Verifica si un rol tiene un permiso específico
     * @param roleId ID del rol
     * @param permissionCode Código del permiso a verificar
     * @return true si el rol tiene el permiso
     */
    public static boolean hasPermission(Integer roleId, String permissionCode) {
        if (roleId == null || permissionCode == null) {
            return false;
        }
        
        Set<String> permissions = getPermissions(roleId);
        return permissions.contains(permissionCode);
    }
    
    /**
     * Verifica si un rol tiene ALGUNO de los permisos especificados
     * @param roleId ID del rol
     * @param permissionCodes Códigos de permisos a verificar
     * @return true si el rol tiene al menos uno de los permisos
     */
    public static boolean hasAnyPermission(Integer roleId, String... permissionCodes) {
        if (roleId == null || permissionCodes == null || permissionCodes.length == 0) {
            return false;
        }
        
        Set<String> permissions = getPermissions(roleId);
        
        for (String code : permissionCodes) {
            if (permissions.contains(code)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Verifica si un rol tiene TODOS los permisos especificados
     * @param roleId ID del rol
     * @param permissionCodes Códigos de permisos a verificar
     * @return true si el rol tiene todos los permisos
     */
    public static boolean hasAllPermissions(Integer roleId, String... permissionCodes) {
        if (roleId == null || permissionCodes == null || permissionCodes.length == 0) {
            return false;
        }
        
        Set<String> permissions = getPermissions(roleId);
        
        for (String code : permissionCodes) {
            if (!permissions.contains(code)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Invalida el cache de un rol específico
     * Útil cuando se modifican los permisos de un rol
     * @param roleId ID del rol a invalidar
     */
    public static void invalidate(Integer roleId) {
        if (roleId != null) {
            cache.remove(roleId);
            System.out.println("Cache invalidado para rol ID: " + roleId);
        }
    }
    
    /**
     * Invalida todo el cache
     * Útil cuando se hacen cambios masivos en permisos
     */
    public static void invalidateAll() {
        cache.clear();
        System.out.println("Cache de permisos completamente invalidado");
    }
    
    /**
     * Limpia entradas expiradas del cache
     * @return Número de entradas eliminadas
     */
    public static int cleanExpired() {
        int removed = 0;
        
        for (Integer roleId : cache.keySet()) {
            CacheEntry entry = cache.get(roleId);
            if (entry != null && entry.isExpired()) {
                if (cache.remove(roleId) != null) {
                    removed++;
                }
            }
        }
        
        if (removed > 0) {
            System.out.println("Entradas de cache expiradas eliminadas: " + removed);
        }
        
        return removed;
    }
    
    /**
     * Precarga los permisos de un rol en el cache
     * Útil para optimizar el primer acceso
     * @param roleId ID del rol a precargar
     */
    public static void preload(Integer roleId) {
        if (roleId != null) {
            getPermissions(roleId); // Esto cargará y cacheará los permisos
        }
    }
    
    /**
     * Precarga los permisos de múltiples roles
     * @param roleIds IDs de los roles a precargar
     */
    public static void preloadMultiple(Integer... roleIds) {
        if (roleIds != null) {
            for (Integer roleId : roleIds) {
                preload(roleId);
            }
        }
    }
    
    /**
     * Obtiene estadísticas del cache
     * @return Mapa con estadísticas
     */
    public static CacheStats getStats() {
        return new CacheStats(
            cache.size(),
            cacheHits,
            cacheMisses,
            cacheHits + cacheMisses > 0 ? 
                (double) cacheHits / (cacheHits + cacheMisses) * 100 : 0
        );
    }
    
    /**
     * Resetea las estadísticas del cache
     */
    public static void resetStats() {
        cacheHits = 0;
        cacheMisses = 0;
    }
    
    /**
     * Clase para estadísticas del cache
     */
    public static class CacheStats {
        public final int size;
        public final long hits;
        public final long misses;
        public final double hitRate;
        
        CacheStats(int size, long hits, long misses, double hitRate) {
            this.size = size;
            this.hits = hits;
            this.misses = misses;
            this.hitRate = hitRate;
        }
        
        @Override
        public String toString() {
            return String.format(
                "CacheStats{size=%d, hits=%d, misses=%d, hitRate=%.2f%%}",
                size, hits, misses, hitRate
            );
        }
    }
}
