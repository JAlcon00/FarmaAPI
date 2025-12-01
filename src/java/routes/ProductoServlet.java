package routes;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controller.ProductoController;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.AuditLog;
import model.Producto;
import services.AuditService;
import utils.JsonResponse;

/**
 * Servlet REST para productos
 * Rutas: /api/productos
 */
@WebServlet(name = "ProductoServlet", urlPatterns = {"/api/productos", "/api/productos/*"})
public class ProductoServlet extends HttpServlet {
    
    private ProductoController productoController;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.productoController = new ProductoController();
    }
    
    /**
     * GET /api/productos - Obtener todos los productos
     * GET /api/productos/{id} - Obtener producto por ID
     * GET /api/productos?categoria={id} - Obtener productos por categoría
     * GET /api/productos?stock=bajo - Obtener productos con stock bajo
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        enableCORS(response);
        
        try {
            String pathInfo = request.getPathInfo();
            String categoriaParam = request.getParameter("categoria");
            String stockParam = request.getParameter("stock");
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // Verificar parámetros de consulta
                if (categoriaParam != null) {
                    // GET /api/productos?categoria={id}
                    try {
                        Long categoriaId = Long.parseLong(categoriaParam);
                        List<Producto> productos = productoController.getProductosByCategoria(categoriaId);
                        JsonResponse.success(response, productos);
                    } catch (NumberFormatException e) {
                        JsonResponse.badRequest(response, "ID de categoría inválido");
                    }
                } else if ("bajo".equals(stockParam)) {
                    // GET /api/productos?stock=bajo
                    List<Producto> productos = productoController.getProductosConStockBajo();
                    JsonResponse.success(response, productos);
                } else {
                    // GET /api/productos - Obtener todos
                    List<Producto> productos = productoController.getAllProductos();
                    JsonResponse.success(response, productos);
                }
                
            } else {
                // Manejar rutas especiales
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    String segment = pathParts[1];
                    
                    // GET /api/productos/buscar?q=query
                    if ("buscar".equals(segment)) {
                        String query = request.getParameter("q");
                        if (query != null && !query.trim().isEmpty()) {
                            List<Producto> productos = productoController.searchProductos(query.trim());
                            JsonResponse.success(response, productos);
                        } else {
                            JsonResponse.badRequest(response, "Parámetro de búsqueda 'q' es requerido");
                        }
                        return;
                    }
                    
                    // GET /api/productos/stock-bajo
                    if ("stock-bajo".equals(segment)) {
                        List<Producto> productos = productoController.getProductosConStockBajo();
                        JsonResponse.success(response, productos);
                        return;
                    }
                    
                    // GET /api/productos/{id} - Obtener por ID
                    try {
                        Long id = Long.parseLong(segment);
                        Producto producto = productoController.getProductoById(id);
                        JsonResponse.success(response, producto);
                    } catch (NumberFormatException e) {
                        JsonResponse.badRequest(response, "ID de producto inválido");
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
     * POST /api/productos - Crear nuevo producto
     * Requiere rol: ADMIN, FARMACEUTICO, ALMACEN, GERENTE
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        enableCORS(response);
        
        // Verificar autorización - Solo roles con permiso PRODUCTOS_WRITE
        if (!utils.AuthorizationHelper.checkRoles(request, response, 
                security.RolePermissions.PRODUCTOS_WRITE)) {
            return; // Ya se envió la respuesta de error
        }
        
        try {
            // Leer JSON del request
            Map<String, Object> json = readJsonFromRequest(request);
            System.out.println("📝 ProductoServlet POST - JSON recibido: " + json);
            
            // Debug: Ver tipos de cada campo
            json.forEach((key, value) -> {
                System.out.println("  " + key + " = " + value + " (tipo: " + (value != null ? value.getClass().getName() : "null") + ")");
            });
            
            // Extraer datos con toString() para evitar ClassCastException
            String nombre = json.containsKey("nombre") && json.get("nombre") != null 
                ? json.get("nombre").toString() : null;
            String descripcion = json.containsKey("descripcion") && json.get("descripcion") != null 
                ? json.get("descripcion").toString() : null;
            String codigoBarras = json.containsKey("codigoBarras") && json.get("codigoBarras") != null 
                ? json.get("codigoBarras").toString() : null;
            
            // Validaciones básicas
            if (nombre == null || nombre.trim().isEmpty()) {
                JsonResponse.badRequest(response, "El nombre es requerido");
                return;
            }
            
            if (!json.containsKey("categoriaId")) {
                JsonResponse.badRequest(response, "La categoría es requerida");
                return;
            }
            
            if (!json.containsKey("precio")) {
                JsonResponse.badRequest(response, "El precio es requerido");
                return;
            }
            
            try {
                Long categoriaId = ((Number) json.get("categoriaId")).longValue();
                BigDecimal precio = new BigDecimal(json.get("precio").toString());
                Integer stock = json.containsKey("stock") ? ((Number) json.get("stock")).intValue() : 0;
                Integer stockMinimo = json.containsKey("stockMinimo") ? ((Number) json.get("stockMinimo")).intValue() : 5;
                
                Producto producto = new Producto(nombre.trim(), descripcion, categoriaId, 
                                               precio, stock, stockMinimo);
                producto.setCodigoBarras(codigoBarras);
                
                Producto nuevoProducto = productoController.createProducto(producto);
                
                // 📝 Registrar en auditoría
                AuditService.logCreate(request, AuditLog.ENTIDAD_PRODUCTO, nuevoProducto.getId(),
                    String.format("Producto '%s' creado - Precio: $%.2f, Stock: %d", 
                        nuevoProducto.getNombre(), nuevoProducto.getPrecio(), nuevoProducto.getStock()));
                
                JsonResponse.created(response, nuevoProducto);
                
            } catch (NumberFormatException e) {
                JsonResponse.badRequest(response, "Formato de número inválido");
            }
            
        } catch (IllegalArgumentException e) {
            JsonResponse.badRequest(response, e.getMessage());
        } catch (SQLException e) {
            JsonResponse.internalError(response, "Error en la base de datos: " + e.getMessage());
        } catch (Exception e) {
            JsonResponse.internalError(response, "Error interno del servidor");
        }
    }
    
    /**
     * PUT /api/productos/{id} - Actualizar producto
     * PUT /api/productos/{id}/stock - Actualizar solo el stock
     * Requiere rol: ADMIN, FARMACEUTICO, ALMACEN, GERENTE
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        enableCORS(response);
        
        // Verificar autorización - Solo roles con permiso PRODUCTOS_WRITE
        if (!utils.AuthorizationHelper.checkRoles(request, response, 
                security.RolePermissions.PRODUCTOS_WRITE)) {
            return;
        }
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                JsonResponse.badRequest(response, "ID de producto requerido");
                return;
            }
            
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length < 2) {
                JsonResponse.badRequest(response, "Ruta inválida");
                return;
            }
            
            Long id;
            try {
                id = Long.parseLong(pathParts[1]);
            } catch (NumberFormatException e) {
                JsonResponse.badRequest(response, "ID de producto inválido");
                return;
            }
            
            // Verificar si es actualización de stock únicamente
            if (pathParts.length == 3 && "stock".equals(pathParts[2])) {
                // PUT /api/productos/{id}/stock
                String nuevoStockStr = request.getParameter("stock");
                if (nuevoStockStr == null) {
                    JsonResponse.badRequest(response, "El nuevo stock es requerido");
                    return;
                }
                
                try {
                    Integer nuevoStock = Integer.parseInt(nuevoStockStr);
                    boolean actualizado = productoController.updateStock(id, nuevoStock);
                    
                    if (actualizado) {
                        // 📝 Registrar en auditoría
                        AuditService.logUpdate(request, AuditLog.ENTIDAD_PRODUCTO, id,
                            String.format("Stock actualizado a %d", nuevoStock));
                        
                        JsonResponse.success(response, "Stock actualizado exitosamente", null);
                    } else {
                        JsonResponse.internalError(response, "No se pudo actualizar el stock");
                    }
                } catch (NumberFormatException e) {
                    JsonResponse.badRequest(response, "Formato de stock inválido");
                }
                
            } else {
                // PUT /api/productos/{id} - Actualizar producto completo
                try {
                    Map<String, Object> json = readJsonFromRequest(request);
                    System.out.println("📝 ProductoServlet PUT - JSON recibido: " + json);
                    
                    // Debug: Ver tipos de cada campo
                    json.forEach((key, value) -> {
                        System.out.println("  " + key + " = " + value + " (tipo: " + (value != null ? value.getClass().getName() : "null") + ")");
                    });
                    
                    String nombre = json.containsKey("nombre") && json.get("nombre") != null 
                        ? json.get("nombre").toString() : null;
                    String descripcion = json.containsKey("descripcion") && json.get("descripcion") != null 
                        ? json.get("descripcion").toString() : null;
                    String codigoBarras = json.containsKey("codigoBarras") && json.get("codigoBarras") != null 
                        ? json.get("codigoBarras").toString() : null;
                    
                    if (nombre == null || nombre.trim().isEmpty()) {
                        JsonResponse.badRequest(response, "El nombre es requerido");
                        return;
                    }
                    
                    try {
                        Long categoriaId = json.containsKey("categoriaId") && json.get("categoriaId") != null
                            ? ((Number) json.get("categoriaId")).longValue() : null;
                        BigDecimal precio = json.containsKey("precio") && json.get("precio") != null
                            ? new BigDecimal(json.get("precio").toString()) : null;
                        Integer stock = json.containsKey("stock") && json.get("stock") != null
                            ? ((Number) json.get("stock")).intValue() : null;
                        Integer stockMinimo = json.containsKey("stockMinimo") && json.get("stockMinimo") != null
                            ? ((Number) json.get("stockMinimo")).intValue() : null;
                        Boolean activo = json.containsKey("activo") && json.get("activo") != null
                            ? (Boolean) json.get("activo") : true;
                        
                        Producto producto = new Producto();
                        producto.setId(id);
                        producto.setNombre(nombre.trim());
                        producto.setDescripcion(descripcion);
                        producto.setCategoriaId(categoriaId);
                        producto.setPrecio(precio);
                        producto.setStock(stock);
                        producto.setStockMinimo(stockMinimo);
                        producto.setCodigoBarras(codigoBarras);
                        producto.setActivo(activo);
                        
                        boolean actualizado = productoController.updateProducto(producto);
                        
                        if (actualizado) {
                            // 📝 Registrar en auditoría
                            AuditService.logUpdate(request, AuditLog.ENTIDAD_PRODUCTO, id,
                                String.format("Producto '%s' actualizado - Precio: $%.2f, Stock: %d", 
                                    producto.getNombre(), 
                                    producto.getPrecio() != null ? producto.getPrecio() : BigDecimal.ZERO, 
                                    producto.getStock() != null ? producto.getStock() : 0));
                            
                            JsonResponse.success(response, "Producto actualizado exitosamente", producto);
                        } else {
                            JsonResponse.internalError(response, "No se pudo actualizar el producto");
                        }
                        
                    } catch (NumberFormatException e) {
                        System.err.println("❌ Error parseando números: " + e.getMessage());
                        e.printStackTrace();
                        JsonResponse.badRequest(response, "Formato de número inválido: " + e.getMessage());
                    } catch (ClassCastException e) {
                        System.err.println("❌ Error de casting: " + e.getMessage());
                        e.printStackTrace();
                        JsonResponse.badRequest(response, "Tipo de dato inválido: " + e.getMessage());
                    }
                } catch (Exception e) {
                    System.err.println("❌ Error general en PUT: " + e.getMessage());
                    e.printStackTrace();
                    JsonResponse.internalError(response, "Error al procesar la solicitud: " + e.getMessage());
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
     * DELETE /api/productos/{id} - Eliminar producto
     * Requiere rol: ADMIN únicamente
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        enableCORS(response);
        
        // Verificar autorización - Solo ADMIN, GERENTE, DIRECTOR pueden eliminar
        if (!utils.AuthorizationHelper.checkRoles(request, response, 
                security.RolePermissions.PRODUCTOS_DELETE)) {
            return;
        }
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                JsonResponse.badRequest(response, "ID de producto requerido");
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
                JsonResponse.badRequest(response, "ID de producto inválido");
                return;
            }
            
            boolean eliminado = productoController.deleteProducto(id);
            
            if (eliminado) {
                // 📝 Registrar en auditoría
                AuditService.logDelete(request, AuditLog.ENTIDAD_PRODUCTO, id,
                    String.format("Producto ID %d eliminado", id));
                
                JsonResponse.success(response, "Producto eliminado exitosamente", null);
            } else {
                JsonResponse.internalError(response, "No se pudo eliminar el producto");
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