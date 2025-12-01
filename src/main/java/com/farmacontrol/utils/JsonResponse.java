package com.farmacontrol.utils;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Utilidad para manejar respuestas JSON
 */
public class JsonResponse {
    
    /**
     * Enviar respuesta JSON exitosa
     */
    public static void success(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        
        PrintWriter out = response.getWriter();
        out.print(toJson(data));
        out.flush();
    }
    
    /**
     * Enviar respuesta JSON exitosa con mensaje
     */
    public static void success(HttpServletResponse response, String message, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        
        PrintWriter out = response.getWriter();
        out.print("{\"success\":true,\"message\":\"" + escapeJson(message) + "\",\"data\":" + toJson(data) + "}");
        out.flush();
    }
    
    /**
     * Enviar respuesta JSON de creación exitosa
     */
    public static void created(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_CREATED);
        
        PrintWriter out = response.getWriter();
        out.print("{\"success\":true,\"message\":\"Recurso creado exitosamente\",\"data\":" + toJson(data) + "}");
        out.flush();
    }
    
    /**
     * Enviar respuesta JSON de error
     */
    public static void error(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(statusCode);
        
        PrintWriter out = response.getWriter();
        out.print("{\"success\":false,\"error\":\"" + escapeJson(message) + "\"}");
        out.flush();
    }
    
    /**
     * Enviar respuesta JSON de error de validación
     */
    public static void badRequest(HttpServletResponse response, String message) throws IOException {
        error(response, HttpServletResponse.SC_BAD_REQUEST, message);
    }
    
    /**
     * Enviar respuesta JSON de no encontrado
     */
    public static void notFound(HttpServletResponse response, String message) throws IOException {
        error(response, HttpServletResponse.SC_NOT_FOUND, message);
    }
    
    /**
     * Enviar respuesta JSON de error interno del servidor
     */
    public static void internalError(HttpServletResponse response, String message) throws IOException {
        error(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
    }
    
    /**
     * Convertir objeto a JSON simple (sin librerías externas)
     */
    private static String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        
        if (obj instanceof String) {
            return "\"" + escapeJson((String) obj) + "\"";
        }
        
        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }
        
        if (obj instanceof java.util.List) {
            StringBuilder sb = new StringBuilder("[");
            java.util.List<?> list = (java.util.List<?>) obj;
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(toJson(list.get(i)));
            }
            sb.append("]");
            return sb.toString();
        }
        
        // Para objetos complejos, usar reflection básico
        return objectToJson(obj);
    }
    
    /**
     * Convertir objeto a JSON usando reflection
     */
    private static String objectToJson(Object obj) {
        if (obj == null) return "null";
        
        StringBuilder json = new StringBuilder("{");
        Class<?> clazz = obj.getClass();
        java.lang.reflect.Method[] methods = clazz.getMethods();
        boolean first = true;
        
        for (java.lang.reflect.Method method : methods) {
            String methodName = method.getName();
            
            // Solo procesar getters
            if (methodName.startsWith("get") && !methodName.equals("getClass") && 
                method.getParameterCount() == 0) {
                
                try {
                    Object value = method.invoke(obj);
                    String fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
                    
                    if (!first) json.append(",");
                    first = false;
                    
                    json.append("\"").append(fieldName).append("\":");
                    
                    if (value == null) {
                        json.append("null");
                    } else if (value instanceof String) {
                        json.append("\"").append(escapeJson((String) value)).append("\"");
                    } else if (value instanceof Number || value instanceof Boolean) {
                        json.append(value);
                    } else if (value instanceof java.sql.Date || value instanceof java.sql.Timestamp) {
                        json.append("\"").append(value.toString()).append("\"");
                    } else if (value instanceof java.util.List) {
                        json.append(toJson(value));
                    } else {
                        // Para objetos anidados, llamar recursivamente
                        json.append(objectToJson(value));
                    }
                } catch (Exception e) {
                    // Ignorar errores de reflexión
                }
            }
        }
        
        json.append("}");
        return json.toString();
    }
    
    /**
     * Escapar caracteres especiales para JSON
     */
    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
