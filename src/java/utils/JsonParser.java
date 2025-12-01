package utils;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * Utilidad para parsear JSON manualmente sin dependencias externas.
 * Diseñado para manejar JSON simple con objetos y arrays anidados.
 */
public class JsonParser {
    
    /**
     * Lee el cuerpo de la petición HTTP y lo parsea como JSON
     */
    public static Map<String, Object> readJsonFromRequest(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        
        String jsonString = sb.toString();
        System.out.println("📥 JSON recibido: " + jsonString);
        return parseSimpleJson(jsonString);
    }
    
    /**
     * Parsea una cadena JSON simple a un Map
     */
    public static Map<String, Object> parseSimpleJson(String json) {
        Map<String, Object> result = new HashMap<>();
        
        if (json == null || json.trim().isEmpty()) {
            return result;
        }
        
        json = json.trim();
        if (!json.startsWith("{") || !json.endsWith("}")) {
            return result;
        }
        
        // Remover llaves externas
        json = json.substring(1, json.length() - 1).trim();
        
        if (json.isEmpty()) {
            return result;
        }
        
        // Parsear pares clave-valor
        int depth = 0;
        boolean inString = false;
        boolean escaped = false;
        StringBuilder keyBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();
        boolean parsingKey = true;
        
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            
            if (escaped) {
                if (parsingKey) {
                    keyBuilder.append(c);
                } else {
                    valueBuilder.append(c);
                }
                escaped = false;
                continue;
            }
            
            if (c == '\\') {
                escaped = true;
                if (!parsingKey) {
                    valueBuilder.append(c);
                }
                continue;
            }
            
            if (c == '"') {
                inString = !inString;
                if (!parsingKey && depth == 0) {
                    valueBuilder.append(c);
                }
                continue;
            }
            
            if (!inString) {
                if (c == '{' || c == '[') {
                    depth++;
                    if (!parsingKey) {
                        valueBuilder.append(c);
                    }
                    continue;
                }
                
                if (c == '}' || c == ']') {
                    depth--;
                    if (!parsingKey) {
                        valueBuilder.append(c);
                    }
                    continue;
                }
                
                if (depth == 0) {
                    if (c == ':' && parsingKey) {
                        parsingKey = false;
                        continue;
                    }
                    
                    if (c == ',') {
                        String key = keyBuilder.toString().trim().replace("\"", "");
                        String value = valueBuilder.toString().trim();
                        result.put(key, parseValue(value));
                        
                        keyBuilder = new StringBuilder();
                        valueBuilder = new StringBuilder();
                        parsingKey = true;
                        continue;
                    }
                }
            }
            
            if (parsingKey) {
                keyBuilder.append(c);
            } else {
                valueBuilder.append(c);
            }
        }
        
        // Último par clave-valor
        if (keyBuilder.length() > 0 || valueBuilder.length() > 0) {
            String key = keyBuilder.toString().trim().replace("\"", "");
            String value = valueBuilder.toString().trim();
            if (!key.isEmpty()) {
                result.put(key, parseValue(value));
            }
        }
        
        return result;
    }
    
    /**
     * Parsea un valor JSON (string, number, boolean, null, array, object)
     */
    private static Object parseValue(String value) {
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
        
        // string (con comillas)
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1)
                    .replace("\\\"", "\"")
                    .replace("\\n", "\n")
                    .replace("\\t", "\t")
                    .replace("\\\\", "\\");
        }
        
        // array
        if (value.startsWith("[") && value.endsWith("]")) {
            return parseJsonArray(value);
        }
        
        // object anidado
        if (value.startsWith("{") && value.endsWith("}")) {
            return parseSimpleJson(value);
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
     * Parsea un array JSON
     */
    private static List<Object> parseJsonArray(String arrayStr) {
        List<Object> result = new ArrayList<>();
        
        if (arrayStr == null || arrayStr.trim().isEmpty()) {
            return result;
        }
        
        arrayStr = arrayStr.trim();
        if (!arrayStr.startsWith("[") || !arrayStr.endsWith("]")) {
            return result;
        }
        
        // Remover corchetes
        arrayStr = arrayStr.substring(1, arrayStr.length() - 1).trim();
        
        if (arrayStr.isEmpty()) {
            return result;
        }
        
        // Parsear elementos
        int depth = 0;
        boolean inString = false;
        boolean escaped = false;
        StringBuilder elementBuilder = new StringBuilder();
        
        for (int i = 0; i < arrayStr.length(); i++) {
            char c = arrayStr.charAt(i);
            
            if (escaped) {
                elementBuilder.append(c);
                escaped = false;
                continue;
            }
            
            if (c == '\\') {
                escaped = true;
                elementBuilder.append(c);
                continue;
            }
            
            if (c == '"') {
                inString = !inString;
                elementBuilder.append(c);
                continue;
            }
            
            if (!inString) {
                if (c == '{' || c == '[') {
                    depth++;
                } else if (c == '}' || c == ']') {
                    depth--;
                } else if (c == ',' && depth == 0) {
                    String element = elementBuilder.toString().trim();
                    if (!element.isEmpty()) {
                        // Detectar si es objeto o valor primitivo
                        if (element.startsWith("{")) {
                            result.add(parseSimpleJson(element));
                        } else {
                            result.add(parseValue(element));
                        }
                    }
                    elementBuilder = new StringBuilder();
                    continue;
                }
            }
            
            elementBuilder.append(c);
        }
        
        // Último elemento
        String element = elementBuilder.toString().trim();
        if (!element.isEmpty()) {
            if (element.startsWith("{")) {
                result.add(parseSimpleJson(element));
            } else {
                result.add(parseValue(element));
            }
        }
        
        return result;
    }
    
    /**
     * Convierte un valor a String de forma segura
     */
    public static String getString(Map<String, Object> json, String key) {
        Object value = json.get(key);
        return value != null ? value.toString() : null;
    }
    
    /**
     * Convierte un valor a Long de forma segura
     */
    public static Long getLong(Map<String, Object> json, String key) {
        Object value = json.get(key);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Convierte un valor a Integer de forma segura
     */
    public static Integer getInteger(Map<String, Object> json, String key) {
        Object value = json.get(key);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Convierte un valor a Double de forma segura
     */
    public static Double getDouble(Map<String, Object> json, String key) {
        Object value = json.get(key);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Convierte un valor a Boolean de forma segura
     */
    public static Boolean getBoolean(Map<String, Object> json, String key) {
        Object value = json.get(key);
        if (value == null) return null;
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.parseBoolean(value.toString());
    }
}
