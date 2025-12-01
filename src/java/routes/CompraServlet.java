package routes;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import controller.CompraController;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.AuditLog;
import model.Compra;
import model.DetalleCompra;
import services.AuditService;
import utils.JsonResponse;
import utils.JsonParser;

@WebServlet(name = "CompraServlet", urlPatterns = {"/api/compras", "/api/compras/*"})
public class CompraServlet extends HttpServlet {
    
    private CompraController compraController;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.compraController = new CompraController();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        enableCORS(response);
        
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/compras o GET /api/compras?proveedor=id
                String proveedorParam = request.getParameter("proveedor");
                
                if (proveedorParam != null && !proveedorParam.trim().isEmpty()) {
                    try {
                        Long proveedorId = Long.parseLong(proveedorParam);
                        List<Compra> compras = compraController.getComprasByProveedor(proveedorId);
                        JsonResponse.success(response, compras);
                    } catch (NumberFormatException e) {
                        JsonResponse.badRequest(response, "ID de proveedor inválido");
                    }
                } else {
                    List<Compra> compras = compraController.getAllCompras();
                    JsonResponse.success(response, compras);
                }
                
            } else {
                // GET /api/compras/{id} o GET /api/compras/{id}/detalles
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length >= 2) {
                    try {
                        Long id = Long.parseLong(pathParts[1]);
                        
                        if (pathParts.length == 2) {
                            // GET /api/compras/{id}
                            Compra compra = compraController.getCompraById(id);
                            JsonResponse.success(response, compra);
                        } else if (pathParts.length == 3 && "detalles".equals(pathParts[2])) {
                            // GET /api/compras/{id}/detalles
                            List<DetalleCompra> detalles = compraController.getDetallesCompra(id);
                            JsonResponse.success(response, detalles);
                        } else {
                            JsonResponse.notFound(response, "Ruta no encontrada");
                        }
                    } catch (NumberFormatException e) {
                        JsonResponse.badRequest(response, "ID de compra inválido");
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
        
        // Verificar autorización - Solo ciertos roles pueden crear compras
        if (!utils.AuthorizationHelper.checkRoles(request, response, 
                security.RolePermissions.COMPRAS_CREATE)) {
            return;
        }
        
        try {
            Map<String, Object> json = JsonParser.readJsonFromRequest(request);
            
            // Extraer parámetros principales
            Long proveedorId = json.containsKey("proveedorId") && json.get("proveedorId") != null
                ? ((Number) json.get("proveedorId")).longValue() : null;
            Long usuarioId = json.containsKey("usuarioId") && json.get("usuarioId") != null
                ? ((Number) json.get("usuarioId")).longValue() : null;
            String estado = json.containsKey("estado") && json.get("estado") != null
                ? json.get("estado").toString() : "PENDIENTE";
            String observaciones = json.containsKey("observaciones") && json.get("observaciones") != null
                ? json.get("observaciones").toString() : null;
            
            // Validar parámetros requeridos
            if (proveedorId == null) {
                JsonResponse.badRequest(response, "El ID del proveedor es requerido");
                return;
            }
            
            if (usuarioId == null) {
                JsonResponse.badRequest(response, "El ID del usuario es requerido");
                return;
            }
            
            // Crear compra
            Compra compra = new Compra();
            compra.setProveedorId(proveedorId);
            compra.setUsuarioId(usuarioId);
            compra.setEstado(estado != null ? estado : "PENDIENTE");
            compra.setObservaciones(observaciones);
            
            // Crear lista de detalles (simplificado - en una implementación real vendría del JSON)
            List<DetalleCompra> detalles = new ArrayList<>();
            
            // Por ahora crear una compra sin detalles para prueba
            if (detalles.isEmpty()) {
                // Agregar un detalle de prueba si no hay detalles
                DetalleCompra detalle = new DetalleCompra();
                detalle.setProductoId(1L); // Producto de ejemplo
                detalle.setCantidad(1);
                detalle.setPrecioUnitario(new BigDecimal("100.00"));
                detalles.add(detalle);
            }
            
            Compra nuevaCompra = compraController.createCompra(compra, detalles);
            
            // Auditoría: Registrar creación de compra
            AuditService.logCreate(request, AuditLog.ENTIDAD_COMPRA, nuevaCompra.getId(),
                String.format("Compra creada - Total: $%.2f, Proveedor ID: %d, Estado: %s", 
                    nuevaCompra.getTotal(), proveedorId, nuevaCompra.getEstado()));
            
            JsonResponse.created(response, nuevaCompra);
            
        } catch (NumberFormatException e) {
            JsonResponse.badRequest(response, "IDs deben ser números válidos");
        } catch (IllegalArgumentException e) {
            JsonResponse.badRequest(response, e.getMessage());
        } catch (SQLException e) {
            JsonResponse.internalError(response, "Error en la base de datos: " + e.getMessage());
        } catch (Exception e) {
            JsonResponse.internalError(response, "Error interno del servidor");
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        enableCORS(response);
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                JsonResponse.badRequest(response, "ID de compra requerido");
                return;
            }
            
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length < 2) {
                JsonResponse.badRequest(response, "ID de compra requerido");
                return;
            }
            
            Long id = Long.parseLong(pathParts[1]);
            
            if (pathParts.length == 3 && "estado".equals(pathParts[2])) {
                // PUT /api/compras/{id}/estado - Cambiar estado
                
                // Verificar autorización para cambiar estado
                if (!utils.AuthorizationHelper.checkRoles(request, response, 
                        security.RolePermissions.COMPRAS_CREATE)) {
                    return;
                }
                
                Map<String, Object> json = JsonParser.readJsonFromRequest(request);
                String nuevoEstado = json.containsKey("estado") && json.get("estado") != null
                    ? json.get("estado").toString() : null;
                
                if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
                    JsonResponse.badRequest(response, "El nuevo estado es requerido");
                    return;
                }
                
                boolean actualizado = compraController.updateEstadoCompra(id, nuevoEstado);
                
                if (actualizado) {
                    Compra compraActualizada = compraController.getCompraById(id);
                    
                    // Auditoría: Registrar cambio de estado
                    AuditService.logUpdate(request, AuditLog.ENTIDAD_COMPRA, id,
                        String.format("Estado de compra actualizado a: %s", nuevoEstado));
                    
                    JsonResponse.success(response, "Estado actualizado exitosamente", compraActualizada);
                } else {
                    JsonResponse.internalError(response, "No se pudo actualizar el estado");
                }
            } else if (pathParts.length == 3 && "cancelar".equals(pathParts[2])) {
                // PUT /api/compras/{id}/cancelar - Cancelar compra
                
                // Verificar autorización para cancelar compra
                if (!utils.AuthorizationHelper.checkRoles(request, response, 
                        security.RolePermissions.COMPRAS_CANCEL)) {
                    return;
                }
                
                boolean cancelada = compraController.cancelarCompra(id);
                
                if (cancelada) {
                    Compra compraCancelada = compraController.getCompraById(id);
                    
                    // Auditoría: Registrar cancelación de compra
                    AuditService.logUpdate(request, AuditLog.ENTIDAD_COMPRA, id,
                        String.format("Compra cancelada - Total: $%.2f", 
                            compraCancelada.getTotal()));
                    
                    JsonResponse.success(response, "Compra cancelada exitosamente", compraCancelada);
                } else {
                    JsonResponse.internalError(response, "No se pudo cancelar la compra");
                }
            } else {
                JsonResponse.notFound(response, "Operación no soportada");
            }
            
        } catch (NumberFormatException e) {
            JsonResponse.badRequest(response, "ID de compra inválido");
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
