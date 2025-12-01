package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Configuración de conexión a la base de datos MySQL
 * Usa el patrón Singleton para mantener una única instancia
 */
public class DatabaseConfig {
    private static DatabaseConfig instance;
    private Connection connection;
    
    // Credenciales de la base de datos
    private final String host;
    private final String port;
    private final String database;
    private final String username;
    private final String password;
    private final String url;
    
    /**
     * Constructor privado (Singleton)
     */
    private DatabaseConfig() {
        // Cargar variables de entorno desde archivo .env (si existe)
        EnvConfig.load();
        
        // Prioridad: System.getenv() (Docker/K8s) > EnvConfig (.env file) > defaults
        this.host = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : 
                    EnvConfig.get("HOST_DB", "localhost");
        this.port = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : 
                    EnvConfig.get("PORT_DB", "3306");
        this.database = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : 
                        EnvConfig.get("NAME_DB", "farmacontrol");
        this.username = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : 
                        EnvConfig.get("USER_DB", "root");
        this.password = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : 
                        EnvConfig.get("PASSWORD_DB", "");
        
        // Construir URL de conexión
        this.url = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
            host, port, database
        );
        
        System.out.println("📦 DatabaseConfig inicializado");
        System.out.println("   Host: " + host);
        System.out.println("   Port: " + port);
        System.out.println("   Database: " + database);
    }
    
    /**
     * Obtiene la instancia única de DatabaseConfig
     */
    public static DatabaseConfig getInstance() {
        if (instance == null) {
            synchronized (DatabaseConfig.class) {
                if (instance == null) {
                    instance = new DatabaseConfig();
                }
            }
        }
        return instance;
    }
    
    /**
     * Obtiene una conexión a la base de datos
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Cargar el driver de MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Establecer conexión
                connection = DriverManager.getConnection(url, username, password);
                System.out.println("✅ Conexión a la base de datos establecida");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver MySQL no encontrado: " + e.getMessage());
            } catch (SQLException e) {
                System.err.println("❌ Error al conectar a la base de datos: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }
    
    /**
     * Cierra la conexión a la base de datos
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("🔌 Conexión a la base de datos cerrada");
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
    
    /**
     * Prueba la conexión a la base de datos
     */
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            boolean isValid = conn != null && !conn.isClosed();
            System.out.println(isValid ? "✅ Conexión exitosa" : "❌ Conexión fallida");
            return isValid;
        } catch (SQLException e) {
            System.err.println("❌ Error en test de conexión: " + e.getMessage());
            return false;
        }
    }
    
    // Getters
    public String getHost() { return host; }
    public String getPort() { return port; }
    public String getDatabase() { return database; }
    public String getUrl() { return url; }
}
