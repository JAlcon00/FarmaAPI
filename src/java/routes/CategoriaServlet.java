package routes;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controller.CategoriaController;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.AuditLog;
import model.Categoria;
import services.AuditService;
import utils.JsonResponse;

/**
 * Servlet REST para categorías
 * Rutas: /api/categorias
 */
@WebServlet(name = "CategoriaServlet", urlPatterns = {"/api/categorias", "/api/categorias/*"})
public class CategoriaServlet extends HttpServlet {
    
    private CategoriaController categoriaController;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.categoriaController = new CategoriaController();
    }
    
    /**
     * GET /api/categorias - Obtener todas las categorías
     * GET /api/categorias/{id} - Obtener categoría por ID
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        enableCORS(response);
        
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/categorias - Obtener todas
                List<Categoria> categorias = categoriaController.getAllCategorias();
                JsonResponse.success(response, categorias);
                
            } else {
                // GET /api/categorias/{id} - Obtener por ID
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    try {
                        Long id = Long.parseLong(pathParts[1]);
                        Categoria categoria = categoriaController.getCategoriaById(id);
                        JsonResponse.success(response, categoria);
                    } catch (NumberFormatException e) {
                        JsonResponse.badRequest(response, "ID de categoría inválido");
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
     * POST /api/categorias - Crear nueva categoría
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        enableCORS(response);
        
        // Verificar autorización para crear categorías
        if (!utils.AuthorizationHelper.checkRoles(request, response, 
                security.RolePermissions.CATEGORIAS_WRITE)) {
            return;
        }
        
        try {
            // Leer JSON del request
            Map<String, Object> json = readJsonFromRequest(request);
            
            String nombre = json.containsKey("nombre") ? (String) json.get("nombre") : null;
            String descripcion = json.containsKey("descripcion") ? (String) json.get("descripcion") : null;
            
            if (nombre == null || nombre.trim().isEmpty()) {
                JsonResponse.badRequest(response, "El nombre es requerido");
                return;
            }
            
            Categoria categoria = new Categoria(nombre.trim(), descripcion);
            Categoria nuevaCategoria = categoriaController.createCategoria(categoria);
            
            // Auditoría: Registrar creación de categoría
            AuditService.logCreate(request, AuditLog.ENTIDAD_CATEGORIA, nuevaCategoria.getId(),
                String.format("Categoría '%s' creada - Descripción: %s", 
                    nuevaCategoria.getNombre(), 
                    descripcion != null ? descripcion : "N/A"));
            
            JsonResponse.created(response, nuevaCategoria);
            
        } catch (IllegalArgumentException e) {
            JsonResponse.badRequest(response, e.getMessage());
        } catch (SQLException e) {
            JsonResponse.internalError(response, "Error en la base de datos: " + e.getMessage());
        } catch (Exception e) {
            JsonResponse.internalError(response, "Error interno del servidor");
        }
    }
    
    /**
     * PUT /api/categorias/{id} - Actualizar categoría
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        enableCORS(response);
        
        // Verificar autorización para actualizar categorías
        if (!utils.AuthorizationHelper.checkRoles(request, response, 
                security.RolePermissions.CATEGORIAS_WRITE)) {
            return;
        }
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                JsonResponse.badRequest(response, "ID de categoría requerido");
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
                JsonResponse.badRequest(response, "ID de categoría inválido");
                return;
            }
            
            // Leer JSON del request
            Map<String, Object> json = readJsonFromRequest(request);
            
            String nombre = json.containsKey("nombre") ? (String) json.get("nombre") : null;
            String descripcion = json.containsKey("descripcion") ? (String) json.get("descripcion") : null;
            
            if (nombre == null || nombre.trim().isEmpty()) {
                JsonResponse.badRequest(response, "El nombre es requerido");
                return;
            }
            
            Categoria categoria = new Categoria();
            categoria.setId(id);
            categoria.setNombre(nombre.trim());
            categoria.setDescripcion(descripcion);
            categoria.setActivo(json.containsKey("activo") && json.get("activo") != null
                ? (Boolean) json.get("activo") : true);
            
            boolean actualizado = categoriaController.updateCategoria(categoria);
            
            if (actualizado) {
                // Auditoría: Registrar actualización de categoría
                AuditService.logUpdate(request, AuditLog.ENTIDAD_CATEGORIA, id,
                    String.format("Categoría '%s' actualizada - Activo: %s", 
                        nombre.trim(), 
                        categoria.getActivo() ? "Sí" : "No"));
                
                JsonResponse.success(response, "Categoría actualizada exitosamente", categoria);
            } else {
                JsonResponse.internalError(response, "No se pudo actualizar la categoría");
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
     * DELETE /api/categorias/{id} - Eliminar categoría
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        enableCORS(response);
        
        // Verificar autorización para eliminar categorías
        if (!utils.AuthorizationHelper.checkRoles(request, response, 
                security.RolePermissions.CATEGORIAS_DELETE)) {
            return;
        }
        
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                JsonResponse.badRequest(response, "ID de categoría requerido");
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
                JsonResponse.badRequest(response, "ID de categoría inválido");
                return;
            }
            
            boolean eliminado = categoriaController.deleteCategoria(id);
            
            if (eliminado) {
                // Auditoría: Registrar eliminación de categoría
                AuditService.logDelete(request, AuditLog.ENTIDAD_CATEGORIA, id,
                    String.format("Categoría ID %d eliminada", id));
                
                JsonResponse.success(response, "Categoría eliminada exitosamente", null);
            } else {
                JsonResponse.internalError(response, "No se pudo eliminar la categoría");
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
