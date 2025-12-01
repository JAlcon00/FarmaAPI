package routes;

import controller.ProveedorController;
import model.Proveedor;
import model.AuditLog;
import services.AuditService;
import utils.JsonResponse;
import utils.JsonParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ProveedorServlet", urlPatterns = {"/api/proveedores", "/api/proveedores/*"})
public class ProveedorServlet extends HttpServlet {
    
    private ProveedorController proveedorController;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.proveedorController = new ProveedorController();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        enableCORS(response);
        
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                String searchTerm = request.getParameter("search");
                
                if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                    List<Proveedor> proveedores = proveedorController.searchProveedores(searchTerm);
                    JsonResponse.success(response, proveedores);
                } else {
                    List<Proveedor> proveedores = proveedorController.getAllProveedores();
                    JsonResponse.success(response, proveedores);
                }
                
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    try {
                        Long id = Long.parseLong(pathParts[1]);
                        Proveedor proveedor = proveedorController.getProveedorById(id);
                        JsonResponse.success(response, proveedor);
                    } catch (NumberFormatException e) {
                        JsonResponse.badRequest(response, "ID de proveedor inválido");
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
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        enableCORS(response);
        
        // Verificar autorización para crear proveedores
        if (!utils.AuthorizationHelper.checkRoles(request, response, 
                security.RolePermissions.PROVEEDORES_WRITE)) {
            return;
        }
        
        try {
            Map<String, Object> json = JsonParser.readJsonFromRequest(request);
            
            String nombre = json.containsKey("nombre") && json.get("nombre") != null 
                ? json.get("nombre").toString() : null;
            String rfc = json.containsKey("rfc") && json.get("rfc") != null 
                ? json.get("rfc").toString() : null;
            String telefono = json.containsKey("telefono") && json.get("telefono") != null 
                ? json.get("telefono").toString() : null;
            String email = json.containsKey("email") && json.get("email") != null 
                ? json.get("email").toString() : null;
            String direccion = json.containsKey("direccion") && json.get("direccion") != null 
                ? json.get("direccion").toString() : null;
            String ciudad = json.containsKey("ciudad") && json.get("ciudad") != null 
                ? json.get("ciudad").toString() : null;
            
            if (nombre == null || nombre.trim().isEmpty()) {
                JsonResponse.badRequest(response, "El nombre del proveedor es requerido");
                return;
            }
            
            Proveedor proveedor = new Proveedor(nombre.trim(), rfc, telefono, email, direccion, ciudad);
            Proveedor nuevoProveedor = proveedorController.createProveedor(proveedor);
            
            // Auditoría: Registrar creación de proveedor
            AuditService.logCreate(request, AuditLog.ENTIDAD_PROVEEDOR, nuevoProveedor.getId(),
                String.format("Proveedor '%s' creado - RFC: %s, Email: %s", 
                    nuevoProveedor.getNombre(), 
                    rfc != null ? rfc : "N/A", 
                    email != null ? email : "N/A"));
            
            JsonResponse.created(response, nuevoProveedor);
            
        } catch (IllegalArgumentException e) {
            JsonResponse.badRequest(response, e.getMessage());
        } catch (SQLException e) {
            JsonResponse.internalError(response, "Error en la base de datos: " + e.getMessage());
        } catch (Exception e) {
            JsonResponse.internalError(response, "Error interno del servidor: " + e.getMessage());
        }
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        enableCORS(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    private void enableCORS(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}
