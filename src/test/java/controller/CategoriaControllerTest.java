package controller;

import model.Categoria;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

/**
 * Tests de integración para CategoriaController
 * Verifica validaciones y lógica de negocio
 */
@DisplayName("CategoriaController Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoriaControllerTest {
    
    private static CategoriaController controller;
    
    @BeforeAll
    static void setUp() {
        System.out.println("🧪 Iniciando tests de CategoriaController con MySQL en Docker");
        controller = new CategoriaController();
    }
    
    @AfterAll
    static void tearDown() {
        System.out.println("✅ Tests de CategoriaController completados");
    }
    
    @Nested
    @DisplayName("Validaciones de Entrada")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ValidacionesEntrada {
        
        @Test
        @Order(1)
        @DisplayName("Debe rechazar ID nulo en getCategoriaById")
        void debeRechazarIdNuloEnGet() {
            // When & Then
            assertThatThrownBy(() -> controller.getCategoriaById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
        }
        
        @Test
        @Order(2)
        @DisplayName("Debe rechazar ID negativo en getCategoriaById")
        void debeRechazarIdNegativoEnGet() {
            // When & Then
            assertThatThrownBy(() -> controller.getCategoriaById(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
        }
        
        @Test
        @Order(3)
        @DisplayName("Debe rechazar ID cero en getCategoriaById")
        void debeRechazarIdCeroEnGet() {
            // When & Then
            assertThatThrownBy(() -> controller.getCategoriaById(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
        }
    }
    
    @Nested
    @DisplayName("Validaciones de Creación")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ValidacionesCreacion {
        
        @Test
        @Order(10)
        @DisplayName("Debe rechazar nombre vacío")
        void debeRechazarNombreVacio() {
            // Given
            Categoria categoria = new Categoria();
            categoria.setNombre("");
            
            // When & Then
            assertThatThrownBy(() -> controller.createCategoria(categoria))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nombre");
        }
        
        @Test
        @Order(11)
        @DisplayName("Debe rechazar nombre nulo")
        void debeRechazarNombreNulo() {
            // Given
            Categoria categoria = new Categoria();
            categoria.setNombre(null);
            
            // When & Then
            assertThatThrownBy(() -> controller.createCategoria(categoria))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nombre");
        }
        
        @Test
        @Order(12)
        @DisplayName("Debe rechazar nombre muy largo")
        void debeRechazarNombreMuyLargo() {
            // Given - Nombre de 101 caracteres
            String nombreLargo = "A".repeat(101);
            Categoria categoria = new Categoria();
            categoria.setNombre(nombreLargo);
            
            // When & Then
            assertThatThrownBy(() -> controller.createCategoria(categoria))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("100 caracteres");
        }
        
        @Test
        @Order(13)
        @DisplayName("Debe crear categoría con nombre válido")
        void debeCrearCategoriaConNombreValido() throws Exception {
            // Given
            Categoria categoria = new Categoria();
            categoria.setNombre("Nueva Categoría Test " + System.currentTimeMillis());
            categoria.setDescripcion("Descripción de test");
            
            // When
            Categoria resultado = controller.createCategoria(categoria);
            
            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isNotNull();
        }
    }
    
    @Nested
    @DisplayName("Validaciones de Actualización")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ValidacionesActualizacion {
        
        @Test
        @Order(20)
        @DisplayName("Debe rechazar ID nulo en update")
        void debeRechazarIdNuloEnUpdate() {
            // Given
            Categoria categoria = new Categoria();
            categoria.setId(null);
            categoria.setNombre("Test");
            
            // When & Then
            assertThatThrownBy(() -> controller.updateCategoria(categoria))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID");
        }
        
        @Test
        @Order(21)
        @DisplayName("Debe rechazar nombre vacío en update")
        void debeRechazarNombreVacioEnUpdate() {
            // Given
            Categoria categoria = new Categoria();
            categoria.setId(1L);
            categoria.setNombre("");
            
            // When & Then
            assertThatThrownBy(() -> controller.updateCategoria(categoria))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nombre");
        }
        
        @Test
        @Order(22)
        @DisplayName("Debe rechazar nombre nulo en update")
        void debeRechazarNombreNuloEnUpdate() {
            // Given
            Categoria categoria = new Categoria();
            categoria.setId(1L);
            categoria.setNombre(null);
            
            // When & Then
            assertThatThrownBy(() -> controller.updateCategoria(categoria))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nombre");
        }
        
        @Test
        @Order(23)
        @DisplayName("Debe rechazar categoría inexistente en update")
        void debeRechazarCategoriaInexistenteEnUpdate() {
            // Given
            Categoria categoria = new Categoria();
            categoria.setId(99999L);
            categoria.setNombre("Test");
            
            // When & Then
            assertThatThrownBy(() -> controller.updateCategoria(categoria))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("no encontrada");
        }
    }
    
    @Nested
    @DisplayName("Validaciones de Eliminación")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ValidacionesEliminacion {
        
        @Test
        @Order(30)
        @DisplayName("Debe rechazar ID nulo en delete")
        void debeRechazarIdNuloEnDelete() {
            // When & Then
            assertThatThrownBy(() -> controller.deleteCategoria(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
        }
        
        @Test
        @Order(31)
        @DisplayName("Debe rechazar categoría inexistente en delete")
        void debeRechazarCategoriaInexistenteEnDelete() {
            // When & Then
            assertThatThrownBy(() -> controller.deleteCategoria(99999L))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("no encontrada");
        }
    }
    
    @Nested
    @DisplayName("Operaciones Exitosas")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class OperacionesExitosas {
        
        @Test
        @Order(40)
        @DisplayName("Debe obtener todas las categorías")
        void debeObtenerTodasLasCategorias() throws Exception {
            // When
            List<Categoria> categorias = controller.getAllCategorias();
            
            // Then
            assertThat(categorias).isNotNull();
        }
        
        @Test
        @Order(41)
        @DisplayName("Debe lanzar excepción para categoría inexistente")
        void debeLanzarExcepcionParaCategoriaInexistente() {
            // When & Then
            assertThatThrownBy(() -> controller.getCategoriaById(99999L))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("no encontrada");
        }
    }
}
