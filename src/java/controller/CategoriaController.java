package controller;

import java.sql.SQLException;
import java.util.List;
import model.Categoria;
import services.CategoriaService;

/**
 * Controlador para categorías
 */
public class CategoriaController {
    private final CategoriaService categoriaService;
    
    public CategoriaController() {
        this.categoriaService = new CategoriaService();
    }
    
    /**
     * Obtener todas las categorías
     */
    public List<Categoria> getAllCategorias() throws SQLException {
        return categoriaService.findAll();
    }
    
    /**
     * Obtener categoría por ID
     */
    public Categoria getCategoriaById(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de categoría inválido");
        }
        
        Categoria categoria = categoriaService.findById(id);
        if (categoria == null) {
            throw new SQLException("Categoría no encontrada con ID: " + id);
        }
        
        return categoria;
    }
    
    /**
     * Crear una nueva categoría
     */
    public Categoria createCategoria(Categoria categoria) throws SQLException {
        // Validaciones
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría es requerido");
        }
        
        if (categoria.getNombre().length() > 100) {
            throw new IllegalArgumentException("El nombre no puede exceder 100 caracteres");
        }
        
        return categoriaService.create(categoria);
    }
    
    /**
     * Actualizar una categoría
     */
    public boolean updateCategoria(Categoria categoria) throws SQLException {
        // Validaciones
        if (categoria.getId() == null || categoria.getId() <= 0) {
            throw new IllegalArgumentException("ID de categoría inválido");
        }
        
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría es requerido");
        }
        
        // Verificar que existe
        Categoria existing = categoriaService.findById(categoria.getId());
        if (existing == null) {
            throw new SQLException("Categoría no encontrada con ID: " + categoria.getId());
        }
        
        return categoriaService.update(categoria);
    }
    
    /**
     * Eliminar (desactivar) una categoría
     */
    public boolean deleteCategoria(Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de categoría inválido");
        }
        
        // Verificar que existe
        Categoria existing = categoriaService.findById(id);
        if (existing == null) {
            throw new SQLException("Categoría no encontrada con ID: " + id);
        }
        
        return categoriaService.delete(id);
    }
}
