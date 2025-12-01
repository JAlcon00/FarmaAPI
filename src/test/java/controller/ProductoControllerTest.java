package controller;

import model.Producto;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Tests de integración para ProductoController
 * Verifica validaciones y lógica de negocio
 */
@DisplayName("ProductoController Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductoControllerTest {
    
    private static ProductoController controller;
    
    @BeforeAll
    static void setUp() {
        System.out.println("🧪 Iniciando tests de ProductoController con MySQL en Docker");
        controller = new ProductoController();
    }
    
    @AfterAll
    static void tearDown() {
        System.out.println("✅ Tests de ProductoController completados");
    }
    
    @Nested
    @DisplayName("Validaciones de Entrada")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ValidacionesEntrada {
        
        @Test
        @Order(1)
        @DisplayName("Debe rechazar ID nulo en getProductoById")
        void debeRechazarIdNuloEnGet() {
            // When & Then
            assertThatThrownBy(() -> controller.getProductoById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
        }
        
        @Test
        @Order(2)
        @DisplayName("Debe rechazar ID negativo en getProductoById")
        void debeRechazarIdNegativoEnGet() {
            // When & Then
            assertThatThrownBy(() -> controller.getProductoById(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
        }
        
        @Test
        @Order(3)
        @DisplayName("Debe rechazar ID cero en getProductoById")
        void debeRechazarIdCeroEnGet() {
            // When & Then
            assertThatThrownBy(() -> controller.getProductoById(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
        }
        
        @Test
        @Order(4)
        @DisplayName("Debe rechazar categoría nula en getProductosByCategoria")
        void debeRechazarCategoriaNula() {
            // When & Then
            assertThatThrownBy(() -> controller.getProductosByCategoria(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
        }
        
        @Test
        @Order(5)
        @DisplayName("Debe rechazar categoría inexistente en getProductosByCategoria")
        void debeRechazarCategoriaInexistente() {
            // When & Then
            assertThatThrownBy(() -> controller.getProductosByCategoria(99999L))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("no encontrada");
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
            Producto producto = new Producto();
            producto.setNombre("");
            producto.setPrecio(new BigDecimal("100.00"));
            producto.setStock(10);
            producto.setCategoriaId(1L);
            
            // When & Then
            assertThatThrownBy(() -> controller.createProducto(producto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nombre");
        }
        
        @Test
        @Order(11)
        @DisplayName("Debe rechazar nombre nulo")
        void debeRechazarNombreNulo() {
            // Given
            Producto producto = new Producto();
            producto.setNombre(null);
            producto.setPrecio(new BigDecimal("100.00"));
            producto.setStock(10);
            producto.setCategoriaId(1L);
            
            // When & Then
            assertThatThrownBy(() -> controller.createProducto(producto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nombre");
        }
        
        @Test
        @Order(12)
        @DisplayName("Debe rechazar nombre muy largo")
        void debeRechazarNombreMuyLargo() {
            // Given - Nombre de 201 caracteres (excede límite de Bean Validation: 100)
            String nombreLargo = "A".repeat(201);
            Producto producto = new Producto();
            producto.setNombre(nombreLargo);
            producto.setPrecio(new BigDecimal("100.00"));
            producto.setStock(10);
            producto.setCategoriaId(1L);
            
            // When & Then - Bean Validation valida max 100 caracteres
            assertThatThrownBy(() -> controller.createProducto(producto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("100 caracteres");
        }
        
        @Test
        @Order(13)
        @DisplayName("Debe rechazar categoría nula")
        void debeRechazarCategoriaNulaEnCreacion() {
            // Given
            Producto producto = new Producto();
            producto.setNombre("Test Producto");
            producto.setPrecio(new BigDecimal("100.00"));
            producto.setStock(10);
            producto.setCategoriaId(null);
            
            // When & Then
            assertThatThrownBy(() -> controller.createProducto(producto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("categoría");
        }
        
        @Test
        @Order(14)
        @DisplayName("Debe rechazar categoría inexistente en creación")
        void debeRechazarCategoriaInexistenteEnCreacion() {
            // Given
            Producto producto = new Producto();
            producto.setNombre("Test Producto");
            producto.setPrecio(new BigDecimal("100.00"));
            producto.setStock(10);
            producto.setCategoriaId(99999L);
            
            // When & Then
            assertThatThrownBy(() -> controller.createProducto(producto))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("no encontrada");
        }
        
        @Test
        @Order(15)
        @DisplayName("Debe rechazar precio nulo")
        void debeRechazarPrecioNulo() {
            // Given
            Producto producto = new Producto();
            producto.setNombre("Test Producto");
            producto.setPrecio(null);
            producto.setStock(10);
            producto.setCategoriaId(1L);
            
            // When & Then
            assertThatThrownBy(() -> controller.createProducto(producto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("precio");
        }
        
        @Test
        @Order(16)
        @DisplayName("Debe rechazar precio cero")
        void debeRechazarPrecioCero() {
            // Given
            Producto producto = new Producto();
            producto.setNombre("Test Producto");
            producto.setPrecio(BigDecimal.ZERO);
            producto.setStock(10);
            producto.setCategoriaId(1L);
            
            // When & Then
            assertThatThrownBy(() -> controller.createProducto(producto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("precio");
        }
        
        @Test
        @Order(17)
        @DisplayName("Debe rechazar precio negativo")
        void debeRechazarPrecioNegativo() {
            // Given
            Producto producto = new Producto();
            producto.setNombre("Test Producto");
            producto.setPrecio(new BigDecimal("-10.00"));
            producto.setStock(10);
            producto.setCategoriaId(1L);
            
            // When & Then
            assertThatThrownBy(() -> controller.createProducto(producto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("precio");
        }
        
        @Test
        @Order(18)
        @DisplayName("Debe rechazar stock negativo")
        void debeRechazarStockNegativo() {
            // Given
            Producto producto = new Producto();
            producto.setNombre("Test Producto");
            producto.setPrecio(new BigDecimal("100.00"));
            producto.setStock(-5);
            producto.setCategoriaId(1L);
            
            // When & Then
            assertThatThrownBy(() -> controller.createProducto(producto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("stock");
        }
        
        @Test
        @Order(19)
        @DisplayName("Debe rechazar stock mínimo negativo")
        void debeRechazarStockMinimoNegativo() {
            // Given
            Producto producto = new Producto();
            producto.setNombre("Test Producto");
            producto.setPrecio(new BigDecimal("100.00"));
            producto.setStock(10);
            producto.setStockMinimo(-5);
            producto.setCategoriaId(1L);
            
            // When & Then
            assertThatThrownBy(() -> controller.createProducto(producto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("stock mínimo");
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
            Producto producto = new Producto();
            producto.setId(null);
            producto.setNombre("Test");
            producto.setPrecio(new BigDecimal("100.00"));
            
            // When & Then
            assertThatThrownBy(() -> controller.updateProducto(producto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID");
        }
        
        @Test
        @Order(21)
        @DisplayName("Debe rechazar producto inexistente en update")
        void debeRechazarProductoInexistenteEnUpdate() {
            // Given - Producto con todos los campos requeridos por Bean Validation
            Producto producto = new Producto();
            producto.setId(99999L);
            producto.setNombre("Test");
            producto.setPrecio(new BigDecimal("100.00"));
            producto.setStock(10);  // Agregar stock requerido por Bean Validation
            producto.setCategoriaId(1L);
            
            // When & Then - Ahora debería lanzar SQLException porque valida primero y luego busca
            assertThatThrownBy(() -> controller.updateProducto(producto))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("no encontrado");
        }
    }
    
    @Nested
    @DisplayName("Validaciones de Stock")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ValidacionesStock {
        
        @Test
        @Order(30)
        @DisplayName("Debe rechazar stock negativo en updateStock")
        void debeRechazarStockNegativoEnUpdate() {
            // When & Then
            assertThatThrownBy(() -> controller.updateStock(1L, -10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("stock");
        }
        
        @Test
        @Order(31)
        @DisplayName("Debe rechazar stock nulo en updateStock")
        void debeRechazarStockNuloEnUpdate() {
            // When & Then
            assertThatThrownBy(() -> controller.updateStock(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("stock");
        }
        
        @Test
        @Order(32)
        @DisplayName("Debe rechazar ID inexistente en updateStock")
        void debeRechazarIdInexistenteEnUpdateStock() {
            // When & Then
            assertThatThrownBy(() -> controller.updateStock(99999L, 10))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("no encontrado");
        }
    }
    
    @Nested
    @DisplayName("Operaciones Exitosas")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class OperacionesExitosas {
        
        @Test
        @Order(40)
        @DisplayName("Debe obtener todos los productos")
        void debeObtenerTodosLosProductos() throws Exception {
            // When
            List<Producto> productos = controller.getAllProductos();
            
            // Then
            assertThat(productos).isNotNull();
            assertThat(productos).isNotEmpty();
        }
        
        @Test
        @Order(41)
        @DisplayName("Debe obtener producto por ID válido")
        void debeObtenerProductoPorIdValido() throws Exception {
            // When
            Producto producto = controller.getProductoById(1L);
            
            // Then
            assertThat(producto).isNotNull();
            assertThat(producto.getId()).isEqualTo(1L);
        }
        
        @Test
        @Order(42)
        @DisplayName("Debe lanzar excepción para producto inexistente")
        void debeLanzarExcepcionParaProductoInexistente() {
            // When & Then
            assertThatThrownBy(() -> controller.getProductoById(99999L))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("no encontrado");
        }
        
        @Test
        @Order(43)
        @DisplayName("Debe obtener productos por categoría válida")
        void debeObtenerProductosPorCategoriaValida() throws Exception {
            // When
            List<Producto> productos = controller.getProductosByCategoria(1L);
            
            // Then
            assertThat(productos).isNotNull();
        }
        
        @Test
        @Order(44)
        @DisplayName("Debe obtener productos con stock bajo")
        void debeObtenerProductosConStockBajo() throws Exception {
            // When
            List<Producto> productos = controller.getProductosConStockBajo();
            
            // Then
            assertThat(productos).isNotNull();
        }
    }
}
