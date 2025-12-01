package controller;

import java.sql.SQLException;
import java.util.List;
import model.Role;
import services.RoleService;

/**
 * Controlador para roles
 */
public class RoleController {
    private final RoleService roleService;
    
    public RoleController() {
        this.roleService = new RoleService();
    }
    
    /**
     * Obtener todos los roles
     */
    public List<Role> getAllRoles() throws SQLException {
        return roleService.findAll();
    }
    
    /**
     * Obtener role por ID
     */
    public Role getRoleById(Integer id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de role inválido");
        }
        
        Role role = roleService.findById(id);
        if (role == null) {
            throw new SQLException("Role no encontrado con ID: " + id);
        }
        
        return role;
    }
    
    /**
     * Crear un nuevo role
     */
    public Role createRole(Role role) throws SQLException {
        // Validaciones
        if (role.getNombre() == null || role.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del role es requerido");
        }
        
        if (role.getNombre().length() > 50) {
            throw new IllegalArgumentException("El nombre del role no puede exceder 50 caracteres");
        }
        
        return roleService.create(role);
    }
    
    /**
     * Actualizar un role
     */
    public boolean updateRole(Role role) throws SQLException {
        // Validaciones
        if (role.getId() == null || role.getId() <= 0) {
            throw new IllegalArgumentException("ID de role inválido");
        }
        
        if (role.getNombre() == null || role.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del role es requerido");
        }
        
        // Verificar que existe
        Role existing = roleService.findById(role.getId());
        if (existing == null) {
            throw new SQLException("Role no encontrado con ID: " + role.getId());
        }
        
        return roleService.update(role);
    }
    
    /**
     * Eliminar (desactivar) un role
     */
    public boolean deleteRole(Integer id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de role inválido");
        }
        
        // Verificar que existe
        Role existing = roleService.findById(id);
        if (existing == null) {
            throw new SQLException("Role no encontrado con ID: " + id);
        }
        
        return roleService.delete(id);
    }
}
