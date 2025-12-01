package routes;

import controller.ClienteController;
import model.Cliente;
import model.AuditLog;
import services.AuditService;
import utils.JsonResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Servlet REST para clientes
 * Rutas: /api/clientes
 */
@WebServlet(name = "ClienteServlet", urlPatterns = {"/api/clientes", "/api/clientes/*"})
public class ClienteServlet extends HttpServlet {
    
    private ClienteController clienteController;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.clienteController = new ClienteController();
    }
    
    /**
     * GET /api/clientes - Obtener todos los clientes
     * GET /api/clientes/{id} - Obtener cliente por ID
     * GET /api/clientes?search={texto} - Buscar clientes por nombre o email
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        enableCORS(response);
        
        try {
            String pathInfo = request.getPathInfo();
            String searchParam = request.getParameter("search");
            String qParam = request.getParameter("q");
            
            if (pathInfo == null || pathInfo.equals("/")) {
                if (searchParam != null && !searchParam.trim().isEmpty()) {
                    // GET /api/clientes?search={texto}
                    List<Cliente> clientes = clienteController.searchClientes(searchParam.trim());
                    JsonResponse.success(response, clientes);
                } else {
                    // GET /api/clientes - Obtener todos
                    List<Cliente> clientes = clienteController.getAllClientes();
                    JsonResponse.success(response, clientes);
                }
                
            } else {
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    String segment = pathParts[1];
                    
                    // Manejar rutas especiales antes de intentar parsear como ID
                    if ("buscar".equals(segment)) {
                        // GET /api/clientes/buscar?q={texto}
                        if (qParam != null && !qParam.trim().isEmpty()) {
                            List<Cliente> clientes = clienteController.searchClientes(qParam.trim());
                            JsonResponse.success(response, clientes);
                        } else {
                            JsonResponse.badRequest(response, "Parámetro 'q' requerido para búsqueda");
                        }
                    } else {
                        // GET /api/clientes/{id} - Obtener por ID
                        try {
                            Long id = Long.parseLong(segment);
                            Cliente cliente = clienteController.getClienteById(id);
                            JsonResponse.success(response, cliente);
                        } catch (NumberFormatException e) {
                            JsonResponse.badRequest(response, "ID de cliente inválido");
                        }
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
     * POST /api/clientes - Crear nuevo cliente
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        enableCORS(response);
        
        // Verificar autorización para crear clientes
        if (!utils.AuthorizationHelper.checkRoles(request, response, 
                security.RolePermissions.CLIENTES_WRITE)) {
            return;
        }
        
        try {
            // Leer JSON del request
            Map<String, Object> json = utils.JsonParser.readJsonFromRequest(request);
            
            String nombre = utils.JsonParser.getString(json, "nombre");
            String apellido = utils.JsonParser.getString(json, "apellido");
            String email = utils.JsonParser.getString(json, "email");
            String telefono = utils.JsonParser.getString(json, "telefono");
            String direccion = utils.JsonParser.getString(json, "direccion");
            String rfc = utils.JsonParser.getString(json, "rfc");
            
            // Validaciones básicas
            if (nombre == null || nombre.trim().isEmpty()) {
                JsonResponse.badRequest(response, "El nombre es requerido");
                return;
            }
            
            if (apellido == null || apellido.trim().isEmpty()) {
                JsonResponse.badRequest(response, "El apellido es requerido");
                return;
            }
            
            if (email == null || email.trim().isEmpty()) {
                JsonResponse.badRequest(response, "El email es requerido");
                return;
            }
            
            if (telefono == null || telefono.trim().isEmpty()) {
                JsonResponse.badRequest(response, "El teléfono es requerido");
                return;
            }
            
            // Constructor correcto: Cliente(nombre, apellido, telefono, email)
            Cliente cliente = new Cliente(nombre.trim(), apellido.trim(), telefono.trim(), email.trim());
            if (direccion != null && !direccion.trim().isEmpty()) {
                cliente.setDireccion(direccion.trim());
            }
            
            Cliente nuevoCliente = clienteController.createCliente(cliente);
            
            // Auditoría: Registrar creación de cliente
            AuditService.logCreate(request, AuditLog.ENTIDAD_CLIENTE, nuevoCliente.getId(),
                String.format("Cliente '%s' creado - Email: %s, Teléfono: %s", 
                    nuevoCliente.getNombre(), nuevoCliente.getEmail(), 
                    telefono != null ? telefono : "N/A"));
            
            JsonResponse.created(response, nuevoCliente);
            
        } catch (IllegalArgumentException e) {
            JsonResponse.badRequest(response, e.getMessage());
        } catch (SQLException e) {
            if (e.getMessage().contains("ya existe")) {
                JsonResponse.badRequest(response, e.getMessage());
            } else {
                JsonResponse.internalError(response, "Error en la base de datos: " + e.getMessage());
            }
        } catch (Exception e) {
            JsonResponse.internalError(response, "Error interno del servidor");
        }
    }
    
    /**
     * PUT /api/clientes/{id} - Actualizar cliente
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        enableCORS(response);
        
        // Verificar autorización para actualizar clientes
        if (!utils.AuthorizationHelper.checkRoles(request, response, 
                security.RolePermissions.CLIENTES_WRITE)) {
            return;
        }
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                JsonResponse.badRequest(response, "ID de cliente requerido");
                return;
            }
            
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length != 2) {
                JsonResponse.badRequest(response, "Ruta inválida");
                return;
            }
            
            Long id;
            try {
                id = Long.parseLong(pathParts[1]);
            } catch (NumberFormatException e) {
                JsonResponse.badRequest(response, "ID de cliente inválido");
                return;
            }
            
            // Leer JSON del request
            Map<String, Object> json = utils.JsonParser.readJsonFromRequest(request);
            
            String nombre = utils.JsonParser.getString(json, "nombre");
            String email = utils.JsonParser.getString(json, "email");
            String telefono = utils.JsonParser.getString(json, "telefono");
            String direccion = utils.JsonParser.getString(json, "direccion");
            String rfc = utils.JsonParser.getString(json, "rfc");
            
            if (nombre == null || nombre.trim().isEmpty()) {
                JsonResponse.badRequest(response, "El nombre es requerido");
                return;
            }
            
            if (email == null || email.trim().isEmpty()) {
                JsonResponse.badRequest(response, "El email es requerido");
                return;
            }
            
            Cliente cliente = new Cliente();
            cliente.setId(id);
            cliente.setNombre(nombre.trim());
            cliente.setEmail(email.trim());
            cliente.setTelefono(telefono);
            cliente.setDireccion(direccion);
            Boolean activo = utils.JsonParser.getBoolean(json, "activo");
            cliente.setActivo(activo != null ? activo : true);
            
            boolean actualizado = clienteController.updateCliente(cliente);
            
            if (actualizado) {
                // Auditoría: Registrar actualización de cliente
                AuditService.logUpdate(request, AuditLog.ENTIDAD_CLIENTE, id,
                    String.format("Cliente '%s' actualizado - Email: %s, Activo: %s", 
                        nombre.trim(), email.trim(), 
                        cliente.getActivo() ? "Sí" : "No"));
                
                JsonResponse.success(response, "Cliente actualizado exitosamente", cliente);
            } else {
                JsonResponse.internalError(response, "No se pudo actualizar el cliente");
            }
            
        } catch (IllegalArgumentException e) {
            JsonResponse.badRequest(response, e.getMessage());
        } catch (SQLException e) {
            if (e.getMessage().contains("no encontrado")) {
                JsonResponse.notFound(response, e.getMessage());
            } else if (e.getMessage().contains("ya existe")) {
                JsonResponse.badRequest(response, e.getMessage());
            } else {
                JsonResponse.internalError(response, "Error en la base de datos: " + e.getMessage());
            }
        } catch (Exception e) {
            JsonResponse.internalError(response, "Error interno del servidor");
        }
    }
    
    /**
     * DELETE /api/clientes/{id} - Eliminar cliente
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        enableCORS(response);
        
        // Verificar autorización para eliminar clientes
        if (!utils.AuthorizationHelper.checkRoles(request, response, 
                security.RolePermissions.CLIENTES_DELETE)) {
            return;
        }
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                JsonResponse.badRequest(response, "ID de cliente requerido");
                return;
            }
            
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length != 2) {
                JsonResponse.badRequest(response, "Ruta inválida");
                return;
            }
            
            Long id;
            try {
                id = Long.parseLong(pathParts[1]);
            } catch (NumberFormatException e) {
                JsonResponse.badRequest(response, "ID de cliente inválido");
                return;
            }
            
            boolean eliminado = clienteController.deleteCliente(id);
            
            if (eliminado) {
                // Auditoría: Registrar eliminación de cliente
                AuditService.logDelete(request, AuditLog.ENTIDAD_CLIENTE, id,
                    String.format("Cliente ID %d eliminado", id));
                
                JsonResponse.success(response, "Cliente eliminado exitosamente", null);
            } else {
                JsonResponse.internalError(response, "No se pudo eliminar el cliente");
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