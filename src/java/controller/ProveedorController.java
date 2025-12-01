package controller;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;
import model.Proveedor;
import services.ProveedorService;

/**
 * Controlador para proveedores
 */
public class ProveedorController {
    private final ProveedorService proveedorService;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    public ProveedorController() {
        this.proveedorService = new ProveedorService();
    }
    
    /**
     * Obtener todos los proveedores
     */
    public List<Proveedor> getAllProveedores() throws SQLException {
        return proveedorService.findAll();
    }
    
    /**
     * Obtener proveedor por ID
     */
    public Proveedor getProveedorById(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de proveedor inválido");
        }
        
        Proveedor proveedor = proveedorService.findById(id);
        if (proveedor == null) {
            throw new SQLException("Proveedor no encontrado con ID: " + id);
        }
        
        return proveedor;
    }
    
    /**
     * Buscar proveedores por nombre
     */
    public List<Proveedor> searchProveedores(String nombre) throws SQLException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El término de búsqueda es requerido");
        }
        
        return proveedorService.findByNombre(nombre);
    }
    
    /**
     * Crear un nuevo proveedor
     */
    public Proveedor createProveedor(Proveedor proveedor) throws SQLException {
        // Validaciones
        if (proveedor.getNombre() == null || proveedor.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del proveedor es requerido");
        }
        
        if (proveedor.getNombre().length() > 200) {
            throw new IllegalArgumentException("El nombre no puede exceder 200 caracteres");
        }
        
        // Validar RFC si se proporciona
        if (proveedor.getRfc() != null && !proveedor.getRfc().trim().isEmpty()) {
            if (proveedor.getRfc().length() > 20) {
                throw new IllegalArgumentException("El RFC no puede exceder 20 caracteres");
            }
        }
        
        // Validar email si se proporciona
        if (proveedor.getEmail() != null && !proveedor.getEmail().trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(proveedor.getEmail()).matches()) {
                throw new IllegalArgumentException("El formato del email es inválido");
            }
        }
        
        // Validar teléfono si se proporciona
        if (proveedor.getTelefono() != null && !proveedor.getTelefono().trim().isEmpty()) {
            if (proveedor.getTelefono().length() > 20) {
                throw new IllegalArgumentException("El teléfono no puede exceder 20 caracteres");
            }
        }
        
        return proveedorService.create(proveedor);
    }
    
    /**
     * Actualizar un proveedor
     */
    public boolean updateProveedor(Proveedor proveedor) throws SQLException {
        // Validaciones
        if (proveedor.getId() == null || proveedor.getId() <= 0) {
            throw new IllegalArgumentException("ID de proveedor inválido");
        }
        
        if (proveedor.getNombre() == null || proveedor.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del proveedor es requerido");
        }
        
        // Validar email si se proporciona
        if (proveedor.getEmail() != null && !proveedor.getEmail().trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(proveedor.getEmail()).matches()) {
                throw new IllegalArgumentException("El formato del email es inválido");
            }
        }
        
        // Verificar que existe
        Proveedor existing = proveedorService.findById(proveedor.getId());
        if (existing == null) {
            throw new SQLException("Proveedor no encontrado con ID: " + proveedor.getId());
        }
        
        return proveedorService.update(proveedor);
    }
    
    /**
     * Eliminar (desactivar) un proveedor
     */
    public boolean deleteProveedor(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de proveedor inválido");
        }
        
        // Verificar que existe
        Proveedor existing = proveedorService.findById(id);
        if (existing == null) {
            throw new SQLException("Proveedor no encontrado con ID: " + id);
        }
        
        return proveedorService.delete(id);
    }
}
