package config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase para cargar variables de entorno desde el archivo .env
 * Similar a dotenv en Node.js
 */
public class EnvConfig {
    private static final Map<String, String> env = new HashMap<>();
    private static boolean loaded = false;
    
    /**
     * Carga las variables del archivo .env
     */
    public static void load() {
        if (loaded) return;
        
        String envPath = System.getProperty("user.dir") + "/.env";
        
        try (BufferedReader reader = new BufferedReader(new FileReader(envPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Ignorar líneas vacías y comentarios
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                // Separar clave=valor
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    env.put(key, value);
                }
            }
            loaded = true;
            System.out.println("✅ Variables de entorno cargadas correctamente");
        } catch (IOException e) {
            System.err.println("⚠️ Error al cargar .env: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene el valor de una variable de entorno
     */
    public static String get(String key) {
        if (!loaded) {
            load();
        }
        return env.get(key);
    }
    
    /**
     * Obtiene el valor de una variable con un valor por defecto
     */
    public static String get(String key, String defaultValue) {
        String value = get(key);
        return value != null ? value : defaultValue;
    }
}
