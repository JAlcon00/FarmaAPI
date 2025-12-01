package security;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Sistema de permisos basado en roles
 * Define qué roles tienen acceso a qué operaciones
 */
public class RolePermissions {
    
    // ==================== ROLES DEL SISTEMA ====================
    // Basado en los 20 roles definidos en la base de datos
    
    public static final int ADMIN = 1;
    public static final int FARMACEUTICO = 2;
    public static final int CAJERO = 3;
    public static final int ALMACEN = 4;
    public static final int GERENTE = 5;
    public static final int ASISTENTE = 6;
    public static final int AUDITOR = 7;
    public static final int SOPORTE = 8;
    public static final int OPERADOR = 9;
    public static final int SUPERVISOR = 10;
    public static final int ENCARGADO_VENTAS = 11;
    public static final int ENCARGADO_COMPRAS = 12;
    public static final int ADMIN_FINANZAS = 13;
    public static final int DIRECTOR = 14;
    public static final int RRHH = 15;
    public static final int MARKETING = 16;
    public static final int TESORERIA = 17;
    public static final int ANALISTA = 18;
    public static final int INTERNO = 19;
    public static final int INVITADO = 20;
    
    // ==================== PERMISOS POR MÓDULO ====================
    
    // ---------- PRODUCTOS ----------
    public static final Set<Integer> PRODUCTOS_READ = new HashSet<>(Arrays.asList(
        ADMIN, FARMACEUTICO, CAJERO, ALMACEN, GERENTE, ASISTENTE, 
        AUDITOR, OPERADOR, SUPERVISOR, ENCARGADO_VENTAS, ENCARGADO_COMPRAS,
        ADMIN_FINANZAS, DIRECTOR, MARKETING, ANALISTA, INVITADO
    ));
    
    public static final Set<Integer> PRODUCTOS_WRITE = new HashSet<>(Arrays.asList(
        ADMIN, FARMACEUTICO, ALMACEN, GERENTE, ENCARGADO_COMPRAS
    ));
    
    public static final Set<Integer> PRODUCTOS_DELETE = new HashSet<>(Arrays.asList(
        ADMIN, GERENTE, DIRECTOR
    ));
    
    // ---------- VENTAS ----------
    public static final Set<Integer> VENTAS_CREATE = new HashSet<>(Arrays.asList(
        ADMIN, CAJERO, FARMACEUTICO, GERENTE, ENCARGADO_VENTAS, OPERADOR
    ));
    
    public static final Set<Integer> VENTAS_READ = new HashSet<>(Arrays.asList(
        ADMIN, CAJERO, FARMACEUTICO, GERENTE, ENCARGADO_VENTAS, 
        AUDITOR, ADMIN_FINANZAS, DIRECTOR, TESORERIA, ANALISTA
    ));
    
    public static final Set<Integer> VENTAS_CANCEL = new HashSet<>(Arrays.asList(
        ADMIN, GERENTE, ENCARGADO_VENTAS, DIRECTOR
    ));
    
    // ---------- COMPRAS ----------
    public static final Set<Integer> COMPRAS_CREATE = new HashSet<>(Arrays.asList(
        ADMIN, ALMACEN, ENCARGADO_COMPRAS, GERENTE, DIRECTOR
    ));
    
    public static final Set<Integer> COMPRAS_READ = new HashSet<>(Arrays.asList(
        ADMIN, ALMACEN, ENCARGADO_COMPRAS, GERENTE, AUDITOR, 
        ADMIN_FINANZAS, DIRECTOR, TESORERIA, ANALISTA
    ));
    
    public static final Set<Integer> COMPRAS_CANCEL = new HashSet<>(Arrays.asList(
        ADMIN, GERENTE, ENCARGADO_COMPRAS, DIRECTOR
    ));
    
    // ---------- CLIENTES ----------
    public static final Set<Integer> CLIENTES_READ = new HashSet<>(Arrays.asList(
        ADMIN, CAJERO, FARMACEUTICO, GERENTE, ENCARGADO_VENTAS, 
        ASISTENTE, OPERADOR, MARKETING, ANALISTA
    ));
    
    public static final Set<Integer> CLIENTES_WRITE = new HashSet<>(Arrays.asList(
        ADMIN, CAJERO, ENCARGADO_VENTAS, GERENTE, ASISTENTE, MARKETING
    ));
    
    public static final Set<Integer> CLIENTES_DELETE = new HashSet<>(Arrays.asList(
        ADMIN, GERENTE, DIRECTOR
    ));
    
    // ---------- PROVEEDORES ----------
    public static final Set<Integer> PROVEEDORES_READ = new HashSet<>(Arrays.asList(
        ADMIN, ALMACEN, ENCARGADO_COMPRAS, GERENTE, AUDITOR, DIRECTOR
    ));
    
    public static final Set<Integer> PROVEEDORES_WRITE = new HashSet<>(Arrays.asList(
        ADMIN, ENCARGADO_COMPRAS, GERENTE, DIRECTOR
    ));
    
    public static final Set<Integer> PROVEEDORES_DELETE = new HashSet<>(Arrays.asList(
        ADMIN, GERENTE, DIRECTOR
    ));
    
    // ---------- CATEGORÍAS ----------
    public static final Set<Integer> CATEGORIAS_READ = new HashSet<>(Arrays.asList(
        ADMIN, FARMACEUTICO, CAJERO, ALMACEN, GERENTE, ASISTENTE,
        OPERADOR, ENCARGADO_VENTAS, ENCARGADO_COMPRAS, ANALISTA, INVITADO
    ));
    
    public static final Set<Integer> CATEGORIAS_WRITE = new HashSet<>(Arrays.asList(
        ADMIN, FARMACEUTICO, GERENTE, DIRECTOR
    ));
    
    public static final Set<Integer> CATEGORIAS_DELETE = new HashSet<>(Arrays.asList(
        ADMIN, GERENTE, DIRECTOR
    ));
    
    // ---------- REPORTES ----------
    public static final Set<Integer> REPORTES_READ = new HashSet<>(Arrays.asList(
        ADMIN, GERENTE, AUDITOR, ADMIN_FINANZAS, DIRECTOR, 
        TESORERIA, ANALISTA, SUPERVISOR
    ));
    
    public static final Set<Integer> REPORTES_FINANCIEROS = new HashSet<>(Arrays.asList(
        ADMIN, GERENTE, ADMIN_FINANZAS, DIRECTOR, TESORERIA
    ));
    
    // ---------- USUARIOS ----------
    public static final Set<Integer> USUARIOS_READ = new HashSet<>(Arrays.asList(
        ADMIN, GERENTE, RRHH, DIRECTOR, SUPERVISOR
    ));
    
    public static final Set<Integer> USUARIOS_WRITE = new HashSet<>(Arrays.asList(
        ADMIN, RRHH, DIRECTOR
    ));
    
    public static final Set<Integer> USUARIOS_DELETE = new HashSet<>(Arrays.asList(
        ADMIN, DIRECTOR
    ));
    
    // ---------- ROLES ----------
    public static final Set<Integer> ROLES_MANAGE = new HashSet<>(Arrays.asList(
        ADMIN, DIRECTOR
    ));
    
    // ==================== MÉTODOS DE UTILIDAD ====================
    
    /**
     * Verifica si un rol tiene un permiso específico
     */
    public static boolean hasPermission(Integer roleId, Set<Integer> allowedRoles) {
        if (roleId == null || allowedRoles == null) {
            return false;
        }
        return allowedRoles.contains(roleId);
    }
    
    /**
     * Obtiene el nombre del rol por su ID
     */
    public static String getRoleName(Integer roleId) {
        if (roleId == null) return "DESCONOCIDO";
        
        switch (roleId) {
            case ADMIN: return "ADMIN";
            case FARMACEUTICO: return "FARMACEUTICO";
            case CAJERO: return "CAJERO";
            case ALMACEN: return "ALMACEN";
            case GERENTE: return "GERENTE";
            case ASISTENTE: return "ASISTENTE";
            case AUDITOR: return "AUDITOR";
            case SOPORTE: return "SOPORTE";
            case OPERADOR: return "OPERADOR";
            case SUPERVISOR: return "SUPERVISOR";
            case ENCARGADO_VENTAS: return "ENCARGADO_VENTAS";
            case ENCARGADO_COMPRAS: return "ENCARGADO_COMPRAS";
            case ADMIN_FINANZAS: return "ADMIN_FINANZAS";
            case DIRECTOR: return "DIRECTOR";
            case RRHH: return "RRHH";
            case MARKETING: return "MARKETING";
            case TESORERIA: return "TESORERIA";
            case ANALISTA: return "ANALISTA";
            case INTERNO: return "INTERNO";
            case INVITADO: return "INVITADO";
            default: return "DESCONOCIDO";
        }
    }
    
    /**
     * Verifica si el rol es administrador
     */
    public static boolean isAdmin(Integer roleId) {
        return roleId != null && roleId.equals(ADMIN);
    }
    
    /**
     * Verifica si el rol es director
     */
    public static boolean isDirector(Integer roleId) {
        return roleId != null && roleId.equals(DIRECTOR);
    }
    
    /**
     * Verifica si el rol tiene privilegios administrativos
     */
    public static boolean hasAdminPrivileges(Integer roleId) {
        return roleId != null && (roleId.equals(ADMIN) || roleId.equals(DIRECTOR));
    }
}
