package routes;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import controller.VentaController;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.AuditLog;
import model.DetalleVenta;
import model.Venta;
import services.AuditService;
import utils.JsonResponse;

/**
 * Servlet REST para ventas
 * Rutas: /api/ventas
 */
@WebServlet(name = "VentaServlet", urlPatterns = {"/api/ventas", "/api/ventas/*"})
public class VentaServlet extends HttpServlet {
    
    private VentaController ventaController;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.ventaController = new VentaController();
    }
    
    /**
     * GET /api/ventas - Obtener todas las ventas
     * GET /api/ventas/{id} - Obtener venta por ID
     * GET /api/ventas/{id}/detalles - Obtener detalles de una venta
     * GET /api/ventas?fechaInicio={yyyy-MM-dd}&fechaFin={yyyy-MM-dd} - Obtener ventas por rango de fechas
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        enableCORS(response);
        
        try {
            String pathInfo = request.getPathInfo();
            String fechaInicioParam = request.getParameter("fechaInicio");
            String fechaFinParam = request.getParameter("fechaFin");
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // Verificar parámetros de consulta
                if (fechaInicioParam != null && fechaFinParam != null) {
                    // GET /api/ventas?fechaInicio={yyyy-MM-dd}&fechaFin={yyyy-MM-dd}
                    try {
                        java.sql.Date fechaInicio = new java.sql.Date(dateFormat.parse(fechaInicioParam).getTime());
                        java.sql.Date fechaFin = new java.sql.Date(dateFormat.parse(fechaFinParam).getTime());
                        List<Venta> ventas = ventaController.getVentasByFecha(fechaInicio, fechaFin);
                        JsonResponse.success(response, ventas);
                    } catch (ParseException e) {
                        JsonResponse.badRequest(response, "Formato de fecha inválido. Use yyyy-MM-dd");
                    }
                } else {
                    // GET /api/ventas - Obtener todas
                    List<Venta> ventas = ventaController.getAllVentas();
                    JsonResponse.success(response, ventas);
                }
                
            } else {
                // GET /api/ventas/{id} o /api/ventas/{id}/detalles
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    try {
                        Long id = Long.parseLong(pathParts[1]);
                        Venta venta = ventaController.getVentaById(id);
                        JsonResponse.success(response, venta);
                    } catch (NumberFormatException e) {
                        JsonResponse.badRequest(response, "ID de venta inválido");
                    }
                } else if (pathParts.length == 3 && "detalles".equals(pathParts[2])) {
                    try {
                        Long id = Long.parseLong(pathParts[1]);
                        List<DetalleVenta> detalles = ventaController.getDetallesVenta(id);
                        JsonResponse.success(response, detalles);
                    } catch (NumberFormatException e) {
                        JsonResponse.badRequest(response, "ID de venta inválido");
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
     * POST /api/ventas - Crear nueva venta
     * Acepta JSON: {"clienteId": 1, "usuarioId": 1, "detalles": [{"productoId": 1, "cantidad": 2, "precioUnitario": 10.5}]}
     * clienteId es opcional (si no se envía, se crea un cliente genérico "Público General")
     * Requiere rol: ADMIN, CAJERO, FARMACEUTICO, VENDEDOR
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        enableCORS(response);
        
        // Verificar autorización - CAJERO, FARMACEUTICO, ADMIN, etc.
        if (!utils.AuthorizationHelper.checkRoles(request, response, 
                security.RolePermissions.VENTAS_CREATE)) {
            return;
        }
        
        try {
            // Leer JSON del request
            Map<String, Object> json = readJsonFromRequest(request);
            
            // 🐛 DEBUG: Log del JSON recibido
            System.out.println("🔍 [DEBUG] JSON recibido en POST /api/ventas:");
            System.out.println(json);
            
            // Extraer datos
            Long clienteId = json.containsKey("clienteId") && json.get("clienteId") != null
                ? ((Number) json.get("clienteId")).longValue()
                : null; // Cliente opcional
            
            Long usuarioId = json.containsKey("usuarioId") && json.get("usuarioId") != null
                ? ((Number) json.get("usuarioId")).longValue()
                : null;
            
            String metodoPago = json.containsKey("metodoPago")
                ? (String) json.get("metodoPago")
                : "EFECTIVO";
            
            String observaciones = json.containsKey("observaciones")
                ? (String) json.get("observaciones")
                : null;
            
            // Validar detalles
            if (!json.containsKey("detalles")) {
                JsonResponse.badRequest(response, "Debe incluir al menos un producto");
                return;
            }
            
            // Procesar detalles
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> detallesArray = (List<Map<String, Object>>) json.get("detalles");
            
            if (detallesArray == null || detallesArray.isEmpty()) {
                JsonResponse.badRequest(response, "Debe incluir al menos un producto");
                return;
            }
            
            List<DetalleVenta> detalles = new ArrayList<>();
            
            for (Map<String, Object> detalleJson : detallesArray) {
                // 🐛 DEBUG: Log de cada detalle
                System.out.println("🔍 [DEBUG] Detalle recibido: " + detalleJson);
                
                if (!detalleJson.containsKey("productoId") || !detalleJson.containsKey("cantidad")) {
                    JsonResponse.badRequest(response, "Cada detalle debe incluir productoId y cantidad");
                    return;
                }
                
                Long productoId = ((Number) detalleJson.get("productoId")).longValue();
                Integer cantidad = ((Number) detalleJson.get("cantidad")).intValue();
                BigDecimal precioUnitario = detalleJson.containsKey("precioUnitario")
                    ? new BigDecimal(detalleJson.get("precioUnitario").toString())
                    : null; // Se obtendrá del producto
                
                // 🐛 DEBUG: Log del precio parseado
                System.out.println("🔍 [DEBUG] Precio parseado: " + precioUnitario);
                
                DetalleVenta detalle = new DetalleVenta();
                detalle.setProductoId(productoId);
                detalle.setCantidad(cantidad);
                if (precioUnitario != null) {
                    detalle.setPrecioUnitario(precioUnitario);
                }
                detalles.add(detalle);
            }
            
            // Crear venta
            Venta venta = new Venta();
            venta.setClienteId(clienteId); // Puede ser null
            
            // Si no hay usuarioId, obtenerlo del JWT
            if (usuarioId == null) {
                Object userIdAttr = request.getAttribute("userId");
                if (userIdAttr != null) {
                    usuarioId = Long.parseLong(userIdAttr.toString());
                } else {
                    usuarioId = 1L; // Usuario por defecto
                }
            }
            venta.setUsuarioId(usuarioId);
            venta.setMetodoPago(metodoPago);
            venta.setObservaciones(observaciones);
            
            Venta nuevaVenta = ventaController.createVenta(venta, detalles);
            
            // 📝 Registrar en auditoría
            AuditService.logCreate(request, AuditLog.ENTIDAD_VENTA, nuevaVenta.getId(),
                String.format("Venta creada - Total: $%.2f, Cliente ID: %s, Items: %d",
                    nuevaVenta.getTotal(),
                    clienteId != null ? clienteId.toString() : "Público",
                    detalles.size()));
            
            JsonResponse.created(response, nuevaVenta);
            
        } catch (IllegalArgumentException e) {
            JsonResponse.badRequest(response, e.getMessage());
        } catch (SQLException e) {
            if (e.getMessage().contains("stock insuficiente")) {
                JsonResponse.badRequest(response, e.getMessage());
            } else {
                JsonResponse.internalError(response, "Error en la base de datos: " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsonResponse.internalError(response, "Error interno del servidor: " + e.getMessage());
        }
    }
    
    /**
     * PUT /api/ventas/{id}/cancelar - Cancelar venta
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        enableCORS(response);
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                JsonResponse.badRequest(response, "ID de venta requerido");
                return;
            }
            
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length != 3 || !"cancelar".equals(pathParts[2])) {
                JsonResponse.badRequest(response, "Ruta inválida. Use: /api/ventas/{id}/cancelar");
                return;
            }
            
            Long id;
            try {
                id = Long.parseLong(pathParts[1]);
            } catch (NumberFormatException e) {
                JsonResponse.badRequest(response, "ID de venta inválido");
                return;
            }
            
            boolean cancelada = ventaController.cancelarVenta(id);
            
            if (cancelada) {
                JsonResponse.success(response, "Venta cancelada exitosamente", null);
            } else {
                JsonResponse.internalError(response, "No se pudo cancelar la venta");
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
     * DELETE /api/ventas/{id} - Eliminar venta (solo si está cancelada)
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        enableCORS(response);
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                JsonResponse.badRequest(response, "ID de venta requerido");
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
                JsonResponse.badRequest(response, "ID de venta inválido");
                return;
            }
            
            // Para este ejemplo, no permitimos eliminar ventas, solo cancelar
            JsonResponse.badRequest(response, "Las ventas no se pueden eliminar. Use PUT /api/ventas/{id}/cancelar para cancelar.");
            
        } catch (IllegalArgumentException e) {
            JsonResponse.badRequest(response, e.getMessage());
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
    
    /**
     * Método helper para leer y parsear JSON desde el request body
     */
    private Map<String, Object> readJsonFromRequest(HttpServletRequest request) throws IOException {
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        }
        
        String jsonString = jsonBuilder.toString().trim();
        return parseSimpleJson(jsonString);
    }
    
    /**
     * Parser JSON manual simple
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseSimpleJson(String json) {
        Map<String, Object> result = new HashMap<>();
        if (json == null || json.isEmpty()) {
            return result;
        }
        
        // Remover llaves externas
        json = json.trim();
        if (json.startsWith("{")) {
            json = json.substring(1);
        }
        if (json.endsWith("}")) {
            json = json.substring(0, json.length() - 1);
        }
        
        // Parsear pares key:value
        int depth = 0;
        int arrayDepth = 0;
        StringBuilder currentKey = new StringBuilder();
        StringBuilder currentValue = new StringBuilder();
        boolean inKey = true;
        boolean inString = false;
        boolean escape = false;
        
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            
            if (escape) {
                if (inKey) {
                    currentKey.append(c);
                } else {
                    currentValue.append(c);
                }
                escape = false;
                continue;
            }
            
            if (c == '\\') {
                escape = true;
                continue;
            }
            
            if (c == '"' && depth == 0 && arrayDepth == 0) {
                inString = !inString;
                continue;
            }
            
            if (!inString) {
                if (c == '{') {
                    depth++;
                } else if (c == '}') {
                    depth--;
                } else if (c == '[') {
                    arrayDepth++;
                } else if (c == ']') {
                    arrayDepth--;
                }
            }
            
            if (depth == 0 && arrayDepth == 0 && !inString) {
                if (c == ':' && inKey) {
                    inKey = false;
                    continue;
                } else if (c == ',') {
                    // Guardar el par key-value
                    String key = currentKey.toString().trim().replaceAll("\"", "");
                    String value = currentValue.toString().trim();
                    
                    if (!key.isEmpty()) {
                        result.put(key, parseValue(value));
                    }
                    
                    currentKey = new StringBuilder();
                    currentValue = new StringBuilder();
                    inKey = true;
                    continue;
                }
            }
            
            if (inKey) {
                currentKey.append(c);
            } else {
                currentValue.append(c);
            }
        }
        
        // Guardar el último par
        if (currentKey.length() > 0) {
            String key = currentKey.toString().trim().replaceAll("\"", "");
            String value = currentValue.toString().trim();
            if (!key.isEmpty()) {
                result.put(key, parseValue(value));
            }
        }
        
        return result;
    }
    
    /**
     * Parsear valor JSON (null, boolean, number, string, array)
     */
    private Object parseValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        
        value = value.trim();
        
        // null
        if ("null".equals(value)) {
            return null;
        }
        
        // boolean
        if ("true".equals(value)) {
            return true;
        }
        if ("false".equals(value)) {
            return false;
        }
        
        // string
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        
        // array
        if (value.startsWith("[") && value.endsWith("]")) {
            return parseJsonArray(value);
        }
        
        // number
        try {
            if (value.contains(".")) {
                return Double.parseDouble(value);
            } else {
                return Long.parseLong(value);
            }
        } catch (NumberFormatException e) {
            // Si no es número, devolver como string
            return value;
        }
    }
    
    /**
     * Parsear array JSON
     */
    private List<Map<String, Object>> parseJsonArray(String arrayStr) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (arrayStr == null || arrayStr.length() <= 2) {
            return list;
        }
        
        // Remover corchetes
        arrayStr = arrayStr.substring(1, arrayStr.length() - 1).trim();
        if (arrayStr.isEmpty()) {
            return list;
        }
        
        // Separar elementos por comas (respetando objetos anidados)
        List<String> elements = new ArrayList<>();
        int depth = 0;
        StringBuilder current = new StringBuilder();
        boolean inString = false;
        
        for (int i = 0; i < arrayStr.length(); i++) {
            char c = arrayStr.charAt(i);
            
            if (c == '"' && (i == 0 || arrayStr.charAt(i - 1) != '\\')) {
                inString = !inString;
            }
            
            if (!inString) {
                if (c == '{' || c == '[') {
                    depth++;
                } else if (c == '}' || c == ']') {
                    depth--;
                }
            }
            
            if (c == ',' && depth == 0 && !inString) {
                elements.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            elements.add(current.toString().trim());
        }
        
        // Parsear cada elemento
        for (String element : elements) {
            element = element.trim();
            if (element.startsWith("{") && element.endsWith("}")) {
                list.add(parseSimpleJson(element));
            }
        }
        
        return list;
    }
    
    private void enableCORS(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}