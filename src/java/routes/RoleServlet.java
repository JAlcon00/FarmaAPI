package routes;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import controller.RoleController;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Role;
import model.AuditLog;
import services.AuditService;
import utils.JsonResponse;

/**
 * Servlet REST para roles
 * Rutas: /api/roles
 */
@WebServlet(name = "RoleServlet", urlPatterns = {"/api/roles", "/api/roles/*"})
public class RoleServlet extends HttpServlet {
    
    private RoleController roleController;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.roleController = new RoleController();
    }
    
    /**
     * GET /api/roles - Obtener todos los roles
     * GET /api/roles/{id} - Obtener role por ID
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Habilitar CORS
        enableCORS(response);
        
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/roles - Obtener todos
                List<Role> roles = roleController.getAllRoles();
                JsonResponse.success(response, roles);
                
            } else {
                // GET /api/roles/{id} - Obtener por ID
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    try {
                        Integer id = Integer.parseInt(pathParts[1]);
                        Role role = roleController.getRoleById(id);
                        JsonResponse.success(response, role);
                    } catch (NumberFormatException e) {
                        JsonResponse.badRequest(response, "ID de role inválido");
                    }
                } else {
                    JsonResponse.notFound(response, "Ruta no encontrada");
                }
            }
            
        } catch (IllegalArgumentException e) {
            JsonResponse.badRequest(response, e.getMessage());
        } catch (SQLException e) {
            if (e.getMessage().contains("no encontrado")) {
                JsonResponse.notFound(response, e.getMessage());
            } else {
                JsonResponse.internalError(response, "Error en la base de datos: " + e.getMessage());
            }
        } catch (Exception e) {
            JsonResponse.internalError(response, "Error interno del servidor");
        }
    }
    
    /**
     * POST /api/roles - Crear nuevo role
     * Requiere rol: ADMIN únicamente
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        enableCORS(response);
        
        // Verificar autorización - Solo ADMIN y DIRECTOR pueden crear roles
        if (!utils.AuthorizationHelper.checkRoles(request, response, 
                security.RolePermissions.ROLES_MANAGE)) {
            return;
        }
        
        try {
            // Leer parámetros del formulario
            String nombre = request.getParameter("nombre");
            String descripcion = request.getParameter("descripcion");
            
            if (nombre == null || nombre.trim().isEmpty()) {
                JsonResponse.badRequest(response, "El nombre es requerido");
                return;
            }
            
            Role role = new Role(nombre.trim(), descripcion);
            Role nuevoRole = roleController.createRole(role);
            
            // Auditoría: Registrar creación de rol (alta seguridad)
            AuditService.logCreate(request, AuditLog.ENTIDAD_ROLE, nuevoRole.getId().longValue(),
                String.format("Rol '%s' creado - Descripción: %s", 
                    nuevoRole.getNombre(), 
                    descripcion != null ? descripcion : "N/A"));
            
            JsonResponse.created(response, nuevoRole);
            
        } catch (IllegalArgumentException e) {
            JsonResponse.badRequest(response, e.getMessage());
        } catch (SQLException e) {
            JsonResponse.internalError(response, "Error en la base de datos: " + e.getMessage());
        } catch (Exception e) {
            JsonResponse.internalError(response, "Error interno del servidor");
        }
    }
    
    /**
     * PUT /api/roles/{id} - Actualizar role
     * Requiere rol: ADMIN únicamente
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        enableCORS(response);
        
        // Verificar autorización - Solo ADMIN y DIRECTOR pueden actualizar roles
        if (!utils.AuthorizationHelper.checkRoles(request, response, 
                security.RolePermissions.ROLES_MANAGE)) {
            return;
        }
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                JsonResponse.badRequest(response, "ID de role requerido");
                return;
            }
            
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length != 2) {
                JsonResponse.badRequest(response, "Ruta inválida");
                return;
            }
            
            Integer id;
            try {
                id = Integer.parseInt(pathParts[1]);
            } catch (NumberFormatException e) {
                JsonResponse.badRequest(response, "ID de role inválido");
                return;
            }
            
            // Leer parámetros
            String nombre = request.getParameter("nombre");
            String descripcion = request.getParameter("descripcion");
            String activoStr = request.getParameter("activo");
            
            if (nombre == null || nombre.trim().isEmpty()) {
                JsonResponse.badRequest(response, "El nombre es requerido");
                return;
            }
            
            Role role = new Role();
            role.setId(id);
            role.setNombre(nombre.trim());
            role.setDescripcion(descripcion);
            role.setActivo(activoStr != null ? Boolean.parseBoolean(activoStr) : true);
            
            boolean actualizado = roleController.updateRole(role);
            
            if (actualizado) {
                // Auditoría: Registrar actualización de rol (alta seguridad)
                AuditService.logUpdate(request, AuditLog.ENTIDAD_ROLE, id.longValue(),
                    String.format("Rol '%s' actualizado - Activo: %s", 
                        nombre.trim(), 
                        role.getActivo() ? "Sí" : "No"));
                
                JsonResponse.success(response, "Role actualizado exitosamente", role);
            } else {
                JsonResponse.internalError(response, "No se pudo actualizar el role");
            }
            
        } catch (IllegalArgumentException e) {
            JsonResponse.badRequest(response, e.getMessage());
        } catch (SQLException e) {
            if (e.getMessage().contains("no encontrado")) {
                JsonResponse.notFound(response, e.getMessage());
            } else {
                JsonResponse.internalError(response, "Error en la base de datos: " + e.getMessage());
            }
        } catch (Exception e) {
            JsonResponse.internalError(response, "Error interno del servidor");
        }
    }
    
    /**
     * DELETE /api/roles/{id} - Eliminar role
     * Requiere rol: ADMIN únicamente
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        enableCORS(response);
        
        // Verificar autorización - Solo ADMIN y DIRECTOR pueden eliminar roles
        if (!utils.AuthorizationHelper.checkRoles(request, response, 
                security.RolePermissions.ROLES_MANAGE)) {
            return;
        }
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                JsonResponse.badRequest(response, "ID de role requerido");
                return;
            }
            
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length != 2) {
                JsonResponse.badRequest(response, "Ruta inválida");
                return;
            }
            
            Integer id;
            try {
                id = Integer.parseInt(pathParts[1]);
            } catch (NumberFormatException e) {
                JsonResponse.badRequest(response, "ID de role inválido");
                return;
            }
            
            boolean eliminado = roleController.deleteRole(id);
            
            if (eliminado) {
                // Auditoría: Registrar eliminación de rol (alta seguridad)
                AuditService.logDelete(request, AuditLog.ENTIDAD_ROLE, id.longValue(),
                    String.format("Rol ID %d eliminado", id));
                
                JsonResponse.success(response, "Role eliminado exitosamente", null);
            } else {
                JsonResponse.internalError(response, "No se pudo eliminar el role");
            }
            
        } catch (IllegalArgumentException e) {
            JsonResponse.badRequest(response, e.getMessage());
        } catch (SQLException e) {
            if (e.getMessage().contains("no encontrado")) {
                JsonResponse.notFound(response, e.getMessage());
            } else {
                JsonResponse.internalError(response, "Error en la base de datos: " + e.getMessage());
            }
        } catch (Exception e) {
            JsonResponse.internalError(response, "Error interno del servidor");
        }
    }
    
    /**
     * OPTIONS - Para CORS
     */
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        enableCORS(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    /**
     * Habilitar CORS para permitir peticiones desde frontend
     */
    private void enableCORS(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}