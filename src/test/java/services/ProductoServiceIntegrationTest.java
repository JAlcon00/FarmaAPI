package services;

import model.Producto;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Tests de integración para ProductoService usando MySQL real
 */
@DisplayName("ProductoService Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductoServiceIntegrationTest {
    
    private static ProductoService productoService;
    
    @BeforeAll
    static void setUp() throws Exception {
        // Forzar uso de credenciales de test modificando el .env temporalmente
        // O mejor, crear un script que copie .env.test a .env antes de correr tests
        
        // Por ahora, usar variables de sistema (no funcionará con EnvConfig)
        // Solución: Verificar que MySQL Docker esté corriendo
        System.out.println("🧪 Iniciando tests de integración con MySQL en Docker (localhost:3307)");
        System.out.println("⚠️  Asegúrate de que el contenedor esté corriendo: ./start-test-db.sh");
        
        productoService = new ProductoService();
    }
    
    @AfterAll
    static void tearDown() {
        // Limpiar datos de test si es necesario
    }
    
    @Nested
    @DisplayName("Operaciones de Lectura")
    class OperacionesLectura {
        
        @Test
        @Order(1)
        @DisplayName("Debe obtener todos los productos de la base de datos")
        void debeObtenerTodosLosProductos() throws SQLException {
            // When
            List<Producto> productos = productoService.findAll();
            
            // Then
            assertThat(productos).isNotNull();
            assertThat(productos).isNotEmpty();
            assertThat(productos.size()).isGreaterThanOrEqualTo(3); // Hay 3 productos de prueba
            
            // Verificar primer producto
            Producto primerProducto = productos.get(0);
            assertThat(primerProducto.getId()).isNotNull();
            assertThat(primerProducto.getNombre()).isNotNull();
            assertThat(primerProducto.getPrecio()).isPositive();
        }
        
        @Test
        @Order(2)
        @DisplayName("Debe encontrar un producto por ID")
        void debeEncontrarProductoPorId() throws SQLException {
            // Given - Sabemos que existe producto con ID 1 (Paracetamol)
            Long productoId = 1L;
            
            // When
            Producto producto = productoService.findById(productoId);
            
            // Then
            assertThat(producto).isNotNull();
            assertThat(producto.getId()).isEqualTo(productoId);
            assertThat(producto.getNombre()).isEqualTo("Paracetamol 500mg");
            assertThat(producto.getPrecio()).isEqualByComparingTo(new BigDecimal("15.50"));
            assertThat(producto.getStock()).isEqualTo(100);
        }
        
        @Test
        @Order(3)
        @DisplayName("Debe retornar null cuando producto no existe")
        void debeRetornarNullCuandoProductoNoExiste() throws SQLException {
            // Given
            Long idInexistente = 99999L;
            
            // When
            Producto producto = productoService.findById(idInexistente);
            
            // Then
            assertThat(producto).isNull();
        }
    }
    
    @Nested
    @DisplayName("Operaciones de Escritura")
    class OperacionesEscritura {
        
        @Test
        @Order(10)
        @DisplayName("Debe crear un nuevo producto")
        void debeCrearNuevoProducto() throws SQLException {
            // Given
            Producto nuevoProducto = new Producto();
            nuevoProducto.setNombre("Test Vitamina C 1000mg");
            nuevoProducto.setDescripcion("Producto de prueba");
            nuevoProducto.setCategoriaId(1L); // Analgésicos
            nuevoProducto.setPrecio(new BigDecimal("85.50"));
            nuevoProducto.setStock(50);
            nuevoProducto.setStockMinimo(10);
            nuevoProducto.setCodigoBarras("TEST" + System.currentTimeMillis());
            nuevoProducto.setActivo(true);
            
            // When
            Producto productoCreado = productoService.create(nuevoProducto);
            
            // Then
            assertThat(productoCreado).isNotNull();
            assertThat(productoCreado.getId()).isNotNull();
            assertThat(productoCreado.getNombre()).isEqualTo("Test Vitamina C 1000mg");
            assertThat(productoCreado.getPrecio()).isEqualByComparingTo(new BigDecimal("85.50"));
            
            // Verificar que se puede recuperar
            Producto productoRecuperado = productoService.findById(productoCreado.getId());
            assertThat(productoRecuperado).isNotNull();
            assertThat(productoRecuperado.getNombre()).isEqualTo("Test Vitamina C 1000mg");
        }
        
        @Test
        @Order(11)
        @DisplayName("Debe actualizar un producto existente")
        void debeActualizarProductoExistente() throws SQLException {
            // Given - Crear producto para actualizar
            Producto producto = new Producto();
            producto.setNombre("Test Producto Temporal");
            producto.setCategoriaId(1L);
            producto.setPrecio(new BigDecimal("10.00"));
            producto.setStock(10);
            producto.setStockMinimo(5);
            producto.setCodigoBarras("TEMP" + System.currentTimeMillis());
            producto.setActivo(true);
            
            Producto productoCreado = productoService.create(producto);
            Long productoId = productoCreado.getId();
            
            // When - Actualizar precio y stock
            productoCreado.setPrecio(new BigDecimal("15.99"));
            productoCreado.setStock(25);
            boolean actualizado = productoService.update(productoCreado);
            
            // Then
            assertThat(actualizado).isTrue();
            
            // Verificar cambios
            Producto productoActualizado = productoService.findById(productoId);
            assertThat(productoActualizado.getPrecio()).isEqualByComparingTo(new BigDecimal("15.99"));
            assertThat(productoActualizado.getStock()).isEqualTo(25);
        }
        
        @Test
        @Order(12)
        @DisplayName("Debe actualizar solo el stock de un producto")
        void debeActualizarStock() throws SQLException {
            // Given
            Long productoId = 1L; // Paracetamol
            Producto productoOriginal = productoService.findById(productoId);
            int stockOriginal = productoOriginal.getStock();
            int nuevoStock = stockOriginal + 50;
            
            // When
            boolean actualizado = productoService.updateStock(productoId, nuevoStock);
            
            // Then
            assertThat(actualizado).isTrue();
            
            Producto productoActualizado = productoService.findById(productoId);
            assertThat(productoActualizado.getStock()).isEqualTo(nuevoStock);
            
            // Restaurar stock original
            productoService.updateStock(productoId, stockOriginal);
        }
    }
    
    @Nested
    @DisplayName("Validaciones y Casos Edge")
    class ValidacionesYCasosEdge {
        
        @Test
        @Order(20)
        @DisplayName("No debe crear producto con datos inválidos")
        void noDebeCrearProductoConDatosInvalidos() {
            // Given - Producto sin nombre (campo requerido)
            Producto productoInvalido = new Producto();
            productoInvalido.setPrecio(new BigDecimal("10.00"));
            productoInvalido.setStock(10);
            
            // When & Then
            assertThatThrownBy(() -> productoService.create(productoInvalido))
                .isInstanceOf(Exception.class);
        }
        
        @Test
        @Order(21)
        @DisplayName("No debe actualizar producto inexistente")
        void noDebeActualizarProductoInexistente() throws SQLException {
            // Given
            Producto productoInexistente = new Producto();
            productoInexistente.setId(99999L);
            productoInexistente.setNombre("No existe");
            productoInexistente.setDescripcion("Producto de prueba");
            productoInexistente.setCategoriaId(1L);
            productoInexistente.setPrecio(new BigDecimal("10.00"));
            productoInexistente.setStock(100);
            productoInexistente.setStockMinimo(10);
            productoInexistente.setCodigoBarras("999999999");
            productoInexistente.setActivo(true);
            
            // When
            boolean actualizado = productoService.update(productoInexistente);
            
            // Then
            assertThat(actualizado).isFalse();
        }
        
        @Test
        @Order(22)
        @DisplayName("Debe manejar productos con stock cero")
        void debeManejarProductosConStockCero() throws SQLException {
            // Given
            Producto producto = new Producto();
            producto.setNombre("Test Producto Sin Stock");
            producto.setCategoriaId(1L);
            producto.setPrecio(new BigDecimal("20.00"));
            producto.setStock(0); // Stock en cero
            producto.setStockMinimo(5);
            producto.setCodigoBarras("ZERO" + System.currentTimeMillis());
            producto.setActivo(true);
            
            // When
            Producto productoCreado = productoService.create(producto);
            
            // Then
            assertThat(productoCreado).isNotNull();
            assertThat(productoCreado.getStock()).isEqualTo(0);
            
            // Verificar que se puede recuperar
            Producto productoRecuperado = productoService.findById(productoCreado.getId());
            assertThat(productoRecuperado.getStock()).isEqualTo(0);
        }
    }
    
    @Nested
    @DisplayName("Transacciones y Concurrencia")
    class TransaccionesYConcurrencia {
        
        @Test
        @Order(30)
        @DisplayName("Debe manejar múltiples actualizaciones de stock")
        void debeManejarMultiplesActualizacionesStock() throws SQLException {
            // Given
            Long productoId = 2L; // Ibuprofeno
            Producto productoOriginal = productoService.findById(productoId);
            int stockOriginal = productoOriginal.getStock();
            
            // When - Hacer varias actualizaciones
            productoService.updateStock(productoId, stockOriginal - 10);
            productoService.updateStock(productoId, stockOriginal - 20);
            productoService.updateStock(productoId, stockOriginal - 5);
            
            // Then
            Producto productoFinal = productoService.findById(productoId);
            assertThat(productoFinal.getStock()).isEqualTo(stockOriginal - 5);
            
            // Restaurar
            productoService.updateStock(productoId, stockOriginal);
        }
    }
    
    @Nested
    @DisplayName("Validaciones y Manejo de Errores")
    class ValidacionesYManejoErrores {
        
        @Test
        @Order(31)
        @DisplayName("Debe retornar null al buscar producto inexistente")
        void debeRetornarNullProductoInexistente() throws SQLException {
            // Given
            Long idInexistente = 99999L;
            
            // When
            Producto producto = productoService.findById(idInexistente);
            
            // Then
            assertThat(producto).isNull();
        }
        
        @Test
        @Order(32)
        @DisplayName("Debe manejar búsqueda por categoría inexistente")
        void debeManejarCategoriaInexistente() throws SQLException {
            // Given
            Long categoriaInexistente = 99999L;
            
            // When
            List<Producto> productos = productoService.findByCategoria(categoriaInexistente);
            
            // Then
            assertThat(productos).isEmpty();
        }
        
        @Test
        @Order(33)
        @DisplayName("Debe retornar lista vacía si no hay productos activos")
        void debeManejarSinProductosActivos() throws SQLException {
            // When - Aunque haya productos inactivos
            List<Producto> productos = productoService.findAll();
            
            // Then - Solo retorna activos
            assertThat(productos).allMatch(p -> p.getActivo() == true);
        }
    }
}
