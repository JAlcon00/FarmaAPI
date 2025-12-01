package controller;

import java.sql.SQLException;
import java.util.List;

import model.Usuario;
import services.UsuarioService;

/**
 * Controlador para la gestión de usuarios
 */
public class UsuarioController {
    private final UsuarioService usuarioService;
    
    public UsuarioController() {
        this.usuarioService = new UsuarioService();
    }
    
    /**
     * Obtener todos los usuarios
     */
    public List<Usuario> getAllUsuarios() throws SQLException {
        return usuarioService.getAllUsuarios();
    }
    
    /**
     * Obtener usuario por ID
     */
    public Usuario getUsuarioById(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de usuario no válido");
        }
        return usuarioService.getUsuarioById(id);
    }
    
    /**
     * Obtener usuario por email
     */
    public Usuario getUsuarioByEmail(String email) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email no puede estar vacío");
        }
        return usuarioService.getUsuarioByEmail(email.trim());
    }
    
    /**
     * Crear nuevo usuario
     */
    public Usuario createUsuario(Usuario usuario) throws SQLException {
        // Validaciones
        if (usuario == null) {
            throw new IllegalArgumentException("Los datos del usuario son requeridos");
        }
        
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email es requerido");
        }
        
        if (usuario.getPasswordHash() == null || usuario.getPasswordHash().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es requerida");
        }
        
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es requerido");
        }
        
        if (usuario.getApellido() == null || usuario.getApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido es requerido");
        }
        
        if (usuario.getRolId() == null || usuario.getRolId() <= 0) {
            throw new IllegalArgumentException("El rol es requerido");
        }
        
        // Verificar que el email no esté en uso
        Usuario existingUser = usuarioService.getUsuarioByEmail(usuario.getEmail().trim());
        if (existingUser != null) {
            throw new IllegalArgumentException("El email ya está en uso");
        }
        
        // Limpiar datos
        usuario.setEmail(usuario.getEmail().trim().toLowerCase());
        usuario.setNombre(usuario.getNombre().trim());
        usuario.setApellido(usuario.getApellido().trim());
        
        return usuarioService.createUsuario(usuario);
    }
    
    /**
     * Actualizar usuario existente
     */
    public Usuario updateUsuario(Long id, Usuario usuario) throws SQLException {
        // Validaciones
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de usuario no válido");
        }
        
        if (usuario == null) {
            throw new IllegalArgumentException("Los datos del usuario son requeridos");
        }
        
        // Verificar que el usuario existe
        Usuario existingUser = usuarioService.getUsuarioById(id);
        if (existingUser == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        
        // Validaciones de campos
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email es requerido");
        }
        
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es requerido");
        }
        
        if (usuario.getApellido() == null || usuario.getApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido es requerido");
        }
        
        if (usuario.getRolId() == null || usuario.getRolId() <= 0) {
            throw new IllegalArgumentException("El rol es requerido");
        }
        
        // Verificar que el email no esté en uso por otro usuario
        Usuario userWithEmail = usuarioService.getUsuarioByEmail(usuario.getEmail().trim());
        if (userWithEmail != null && !userWithEmail.getId().equals(id)) {
            throw new IllegalArgumentException("El email ya está en uso por otro usuario");
        }
        
        // Limpiar datos
        usuario.setEmail(usuario.getEmail().trim().toLowerCase());
        usuario.setNombre(usuario.getNombre().trim());
        usuario.setApellido(usuario.getApellido().trim());
        
        return usuarioService.updateUsuario(id, usuario);
    }
    
    /**
     * Actualizar contraseña del usuario
     */
    public boolean updatePassword(Long id, String newPassword) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de usuario no válido");
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("La nueva contraseña es requerida");
        }
        
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
        
        // Verificar que el usuario existe
        Usuario existingUser = usuarioService.getUsuarioById(id);
        if (existingUser == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        
        return usuarioService.updatePassword(id, newPassword);
    }
    
    /**
     * Eliminar usuario (desactivar)
     */
    public boolean deleteUsuario(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de usuario no válido");
        }
        
        // Verificar que el usuario existe
        Usuario existingUser = usuarioService.getUsuarioById(id);
        if (existingUser == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        
        return usuarioService.deleteUsuario(id);
    }
    
    /**
     * Autenticar usuario (login)
     */
    public Usuario authenticateUser(String email, String password) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email es requerido");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es requerida");
        }
        
        return usuarioService.authenticateUser(email.trim().toLowerCase(), password);
    }
    
    /**
     * Obtener usuarios por rol
     */
    public List<Usuario> getUsuariosByRole(Integer rolId) throws SQLException {
        if (rolId == null || rolId <= 0) {
            throw new IllegalArgumentException("ID de rol no válido");
        }
        
        return usuarioService.getUsuariosByRole(rolId);
    }
    
    /**
     * Activar/desactivar usuario
     */
    public Usuario toggleUsuarioStatus(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de usuario no válido");
        }
        
        Usuario usuario = usuarioService.getUsuarioById(id);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        
        // Cambiar el estado
        usuario.setActivo(!usuario.getActivo());
        
        return usuarioService.updateUsuario(id, usuario);
    }
}