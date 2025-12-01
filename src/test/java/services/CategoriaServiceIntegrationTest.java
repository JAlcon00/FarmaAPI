package services;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import model.Categoria;

/**
 * Tests de integración para CategoriaService usando MySQL real
 */
@DisplayName("CategoriaService Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoriaServiceIntegrationTest {
    
    private static CategoriaService categoriaService;
    
    @BeforeAll
    static void setUp() {
        System.out.println("🧪 Iniciando tests de CategoriaService con MySQL en Docker");
        categoriaService = new CategoriaService();
    }
    
    @Test
    @Order(1)
    @DisplayName("Debe crear categoría correctamente")
    void debeCrearCategoria() throws Exception {
        // Given
        Categoria categoria = new Categoria();
        categoria.setNombre("Vitaminas Test " + System.currentTimeMillis()); // Nombre único
        categoria.setDescripcion("Suplementos vitamínicos");
        
        // When
        Categoria creada = categoriaService.create(categoria);
        
        // Then
        assertThat(creada).isNotNull();
        assertThat(creada.getId()).isPositive();
        assertThat(creada.getDescripcion()).isEqualTo("Suplementos vitamínicos");
    }
    
    @Test
    @Order(2)
    @DisplayName("Debe buscar categoría por ID")
    void debeBuscarCategoriaPorId() throws Exception {
        // Given - Usar categoría existente de BD
        Long id = 1L;
        
        // When
        Categoria categoria = categoriaService.findById(id);
        
        // Then
        assertThat(categoria).isNotNull();
        assertThat(categoria.getId()).isEqualTo(id);
    }
    
    @Test
    @Order(3)
    @DisplayName("Debe obtener todas las categorías")
    void debeObtenerTodasLasCategorias() throws Exception {
        // When
        List<Categoria> categorias = categoriaService.findAll();
        
        // Then
        assertThat(categorias).isNotEmpty();
        assertThat(categorias.size()).isGreaterThanOrEqualTo(3); // Las 3 de prueba
    }
    
    @Test
    @Order(4)
    @DisplayName("Debe actualizar categoría")
    void debeActualizarCategoria() throws Exception {
        // Given
        Categoria categoria = new Categoria();
        categoria.setNombre("Categoría Test " + System.currentTimeMillis());
        categoria.setDescripcion("Descripción inicial");
        categoria.setActivo(true);
        Categoria creada = categoriaService.create(categoria);
        
        // When
        creada.setDescripcion("Descripción actualizada");
        creada.setActivo(true); // Mantener el valor de activo
        boolean actualizada = categoriaService.update(creada);
        
        // Then
        assertThat(actualizada).isTrue();
        Categoria verificada = categoriaService.findById(creada.getId());
        assertThat(verificada.getDescripcion()).isEqualTo("Descripción actualizada");
    }
    
    @Test
    @Order(5)
    @DisplayName("Debe eliminar categoría")
    void debeEliminarCategoria() throws Exception {
        // Given - Crear categoría para eliminar
        Categoria categoria = new Categoria();
        categoria.setNombre("Temporal Test " + System.currentTimeMillis());
        categoria.setDescripcion("Para eliminar");
        
        Categoria creada = categoriaService.create(categoria);
        Long id = creada.getId();
        
        // When
        boolean eliminada = categoriaService.delete(id);
        
        // Then
        assertThat(eliminada).isTrue();
    }
}
