package utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import security.RolePermissions;

import java.io.IOException;
import java.util.Set;

/**
 * Helper para validar autorización basada en roles
 */
public class AuthorizationHelper {
    
    /**
     * Verifica si el usuario tiene los permisos necesarios
     * Si no tiene permisos, envía automáticamente un error 403
     * 
     * @param request Request HTTP con atributos userId y roleId
     * @param response Response HTTP para enviar error si no autorizado
     * @param allowedRoles Set de roles permitidos para la operación
     * @return true si el usuario está autorizado, false si no
     */
    public static boolean checkRoles(HttpServletRequest request, 
                                     HttpServletResponse response, 
                                     Set<Integer> allowedRoles) {
        try {
            // Obtener el roleId del request (inyectado por JwtAuthenticationFilter)
            Integer roleId = (Integer) request.getAttribute("roleId");
            Long userId = (Long) request.getAttribute("userId");
            String email = (String) request.getAttribute("userEmail");
            
            // Validar que tenemos información del usuario
            if (roleId == null || userId == null) {
                JsonResponse.unauthorized(response, "Información de autenticación incompleta");
                return false;
            }
            
            // Verificar si el rol tiene permiso
            if (!RolePermissions.hasPermission(roleId, allowedRoles)) {
                String roleName = RolePermissions.getRoleName(roleId);
                JsonResponse.forbidden(response, 
                    "Acceso denegado. Rol '" + roleName + "' no tiene permisos para esta operación");
                return false;
            }
            
            // Usuario autorizado
            return true;
            
        } catch (IOException e) {
            // Error al enviar respuesta
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Verifica si el usuario es ADMIN
     */
    public static boolean isAdmin(HttpServletRequest request, HttpServletResponse response) {
        try {
            Integer roleId = (Integer) request.getAttribute("roleId");
            
            if (roleId == null) {
                JsonResponse.unauthorized(response, "Información de autenticación incompleta");
                return false;
            }
            
            if (!RolePermissions.isAdmin(roleId)) {
                JsonResponse.forbidden(response, "Esta operación requiere rol de ADMIN");
                return false;
            }
            
            return true;
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Verifica si el usuario tiene privilegios administrativos (ADMIN o DIRECTOR)
     */
    public static boolean hasAdminPrivileges(HttpServletRequest request, HttpServletResponse response) {
        try {
            Integer roleId = (Integer) request.getAttribute("roleId");
            
            if (roleId == null) {
                JsonResponse.unauthorized(response, "Información de autenticación incompleta");
                return false;
            }
            
            if (!RolePermissions.hasAdminPrivileges(roleId)) {
                JsonResponse.forbidden(response, 
                    "Esta operación requiere privilegios de ADMIN o DIRECTOR");
                return false;
            }
            
            return true;
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtiene el roleId del request actual
     */
    public static Integer getCurrentRoleId(HttpServletRequest request) {
        return (Integer) request.getAttribute("roleId");
    }
    
    /**
     * Obtiene el userId del request actual
     */
    public static Long getCurrentUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }
    
    /**
     * Obtiene el email del usuario actual
     */
    public static String getCurrentUserEmail(HttpServletRequest request) {
        return (String) request.getAttribute("userEmail");
    }
    
    /**
     * Verifica si el usuario actual es el propietario del recurso
     * Útil para operaciones donde un usuario puede modificar solo sus propios datos
     */
    public static boolean isResourceOwner(HttpServletRequest request, 
                                          HttpServletResponse response, 
                                          Long resourceOwnerId) {
        try {
            Long currentUserId = getCurrentUserId(request);
            Integer roleId = getCurrentRoleId(request);
            
            if (currentUserId == null) {
                JsonResponse.unauthorized(response, "Información de autenticación incompleta");
                return false;
            }
            
            // ADMIN y DIRECTOR pueden acceder a cualquier recurso
            if (RolePermissions.hasAdminPrivileges(roleId)) {
                return true;
            }
            
            // Verificar si es el propietario
            if (!currentUserId.equals(resourceOwnerId)) {
                JsonResponse.forbidden(response, 
                    "No tienes permisos para modificar este recurso");
                return false;
            }
            
            return true;
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
