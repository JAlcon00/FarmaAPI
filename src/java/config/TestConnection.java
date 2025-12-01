package config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Clase de prueba para validar la conexión a la base de datos
 */
public class TestConnection {
    
    public static void main(String[] args) {
        System.out.println("🔧 Iniciando prueba de conexión a la base de datos...\n");
        
        try {
            // Obtener instancia de DatabaseConfig
            DatabaseConfig dbConfig = DatabaseConfig.getInstance();
            
            System.out.println("\n📊 Información de configuración:");
            System.out.println("   URL: " + dbConfig.getUrl());
            System.out.println("   Host: " + dbConfig.getHost());
            System.out.println("   Port: " + dbConfig.getPort());
            System.out.println("   Database: " + dbConfig.getDatabase());
            
            // Probar conexión
            System.out.println("\n🔌 Probando conexión...");
            boolean connected = dbConfig.testConnection();
            
            if (connected) {
                System.out.println("\n✨ ¡Conexión exitosa!");
                
                // Realizar una consulta de prueba
                System.out.println("\n📋 Ejecutando consulta de prueba...");
                Connection conn = dbConfig.getConnection();
                Statement stmt = conn.createStatement();
                
                // Contar roles
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM roles");
                if (rs.next()) {
                    int totalRoles = rs.getInt("total");
                    System.out.println("   Total de roles en la BD: " + totalRoles);
                }
                rs.close();
                
                // Contar usuarios
                rs = stmt.executeQuery("SELECT COUNT(*) as total FROM usuarios");
                if (rs.next()) {
                    int totalUsuarios = rs.getInt("total");
                    System.out.println("   Total de usuarios en la BD: " + totalUsuarios);
                }
                rs.close();
                
                // Contar productos
                rs = stmt.executeQuery("SELECT COUNT(*) as total FROM productos");
                if (rs.next()) {
                    int totalProductos = rs.getInt("total");
                    System.out.println("   Total de productos en la BD: " + totalProductos);
                }
                rs.close();
                
                stmt.close();
                
                System.out.println("\n✅ Todas las pruebas pasaron correctamente");
            } else {
                System.out.println("\n❌ No se pudo establecer conexión");
            }
            
            // Cerrar conexión
            dbConfig.closeConnection();
            
        } catch (Exception e) {
            System.err.println("\n❌ Error durante la prueba:");
            System.err.println("   " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n🏁 Prueba finalizada");
    }
}
