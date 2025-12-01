package controller;

import model.Compra;
import model.DetalleCompra;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests de integración para CompraController
 * Verifica validaciones y lógica de negocio
 */
@DisplayName("CompraController Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CompraControllerTest {
    
    private static CompraController controller;
    
    @BeforeAll
    static void setUp() {
        System.out.println("🧪 Iniciando tests de CompraController con MySQL en Docker");
        controller = new CompraController();
    }
    
    @AfterAll
    static void tearDown() {
        System.out.println("✅ Tests de CompraController completados");
    }
    
    @Nested
    @DisplayName("Validaciones de Entrada")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ValidacionesEntrada {
        
        @Test
        @Order(1)
        @DisplayName("Debe rechazar ID nulo en getCompraById")
        void debeRechazarIdNuloEnGet() {
            // When & Then
            assertThatThrownBy(() -> controller.getCompraById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
        }
        
        @Test
        @Order(2)
        @DisplayName("Debe rechazar ID negativo en getCompraById")
        void debeRechazarIdNegativoEnGet() {
            // When & Then
            assertThatThrownBy(() -> controller.getCompraById(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
        }
        
        @Test
        @Order(3)
        @DisplayName("Debe rechazar ID cero en getCompraById")
        void debeRechazarIdCeroEnGet() {
            // When & Then
            assertThatThrownBy(() -> controller.getCompraById(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
        }
        
        @Test
        @Order(4)
        @DisplayName("Debe rechazar proveedor ID nulo en getComprasByProveedor")
        void debeRechazarProveedorIdNulo() {
            // When & Then
            assertThatThrownBy(() -> controller.getComprasByProveedor(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("proveedor");
        }
        
        @Test
        @Order(5)
        @DisplayName("Debe rechazar proveedor inexistente")
        void debeRechazarProveedorInexistente() {
            // When & Then
            assertThatThrownBy(() -> controller.getComprasByProveedor(99999L))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("Proveedor no encontrado");
        }
    }
    
    @Nested
    @DisplayName("Validaciones de Creación")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ValidacionesCreacion {
        
        @Test
        @Order(10)
        @DisplayName("Debe rechazar proveedor nulo")
        void debeRechazarProveedorNulo() {
            // Given
            Compra compra = new Compra();
            compra.setProveedorId(null);
            compra.setUsuarioId(1L);
            List<DetalleCompra> detalles = new ArrayList<>();
            detalles.add(new DetalleCompra());
            
            // When & Then
            assertThatThrownBy(() -> controller.createCompra(compra, detalles))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("proveedor");
        }
        
        @Test
        @Order(11)
        @DisplayName("Debe rechazar proveedor con ID cero")
        void debeRechazarProveedorIdCero() {
            // Given
            Compra compra = new Compra();
            compra.setProveedorId(0L);
            compra.setUsuarioId(1L);
            List<DetalleCompra> detalles = new ArrayList<>();
            detalles.add(new DetalleCompra());
            
            // When & Then
            assertThatThrownBy(() -> controller.createCompra(compra, detalles))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("proveedor");
        }
        
        @Test
        @Order(12)
        @DisplayName("Debe rechazar usuario nulo")
        void debeRechazarUsuarioNulo() {
            // Given
            Compra compra = new Compra();
            compra.setProveedorId(1L);
            compra.setUsuarioId(null);
            List<DetalleCompra> detalles = new ArrayList<>();
            detalles.add(new DetalleCompra());
            
            // When & Then
            assertThatThrownBy(() -> controller.createCompra(compra, detalles))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("usuario");
        }
        
        @Test
        @Order(13)
        @DisplayName("Debe rechazar usuario con ID cero")
        void debeRechazarUsuarioIdCero() {
            // Given
            Compra compra = new Compra();
            compra.setProveedorId(1L);
            compra.setUsuarioId(0L);
            List<DetalleCompra> detalles = new ArrayList<>();
            detalles.add(new DetalleCompra());
            
            // When & Then
            assertThatThrownBy(() -> controller.createCompra(compra, detalles))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("usuario");
        }
        
        @Test
        @Order(14)
        @DisplayName("Debe rechazar detalles nulos")
        void debeRechazarDetallesNulos() {
            // Given
            Compra compra = new Compra();
            compra.setProveedorId(1L);
            compra.setUsuarioId(1L);
            
            // When & Then
            assertThatThrownBy(() -> controller.createCompra(compra, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("producto");
        }
        
        @Test
        @Order(15)
        @DisplayName("Debe rechazar detalles vacíos")
        void debeRechazarDetallesVacios() {
            // Given
            Compra compra = new Compra();
            compra.setProveedorId(1L);
            compra.setUsuarioId(1L);
            List<DetalleCompra> detalles = new ArrayList<>();
            
            // When & Then
            assertThatThrownBy(() -> controller.createCompra(compra, detalles))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("producto");
        }
        
        @Test
        @Order(16)
        @DisplayName("Debe rechazar proveedor inexistente en creación")
        void debeRechazarProveedorInexistenteEnCreacion() {
            // Given
            Compra compra = new Compra();
            compra.setProveedorId(99999L);
            compra.setUsuarioId(1L);
            
            List<DetalleCompra> detalles = new ArrayList<>();
            DetalleCompra detalle = new DetalleCompra();
            detalle.setProductoId(1L);
            detalle.setCantidad(5);
            detalle.setPrecioUnitario(new BigDecimal("10.00"));
            detalles.add(detalle);
            
            // When & Then
            assertThatThrownBy(() -> controller.createCompra(compra, detalles))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("Proveedor no encontrado");
        }
        
        @Test
        @Order(17)
        @DisplayName("Debe rechazar estado inválido")
        void debeRechazarEstadoInvalido() {
            // Given
            Compra compra = new Compra();
            compra.setProveedorId(1L);
            compra.setUsuarioId(1L);
            compra.setEstado("INVALIDO");
            
            List<DetalleCompra> detalles = new ArrayList<>();
            DetalleCompra detalle = new DetalleCompra();
            detalle.setProductoId(1L);
            detalle.setCantidad(5);
            detalle.setPrecioUnitario(new BigDecimal("10.00"));
            detalles.add(detalle);
            
            // When & Then
            assertThatThrownBy(() -> controller.createCompra(compra, detalles))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Estado inválido");
        }
        
        @Test
        @Order(18)
        @DisplayName("Debe rechazar producto inexistente en detalle")
        void debeRechazarProductoInexistente() {
            // Given
            Compra compra = new Compra();
            compra.setProveedorId(1L);
            compra.setUsuarioId(1L);
            
            List<DetalleCompra> detalles = new ArrayList<>();
            DetalleCompra detalle = new DetalleCompra();
            detalle.setProductoId(99999L);
            detalle.setCantidad(5);
            detalle.setPrecioUnitario(new BigDecimal("10.00"));
            detalles.add(detalle);
            
            // When & Then
            assertThatThrownBy(() -> controller.createCompra(compra, detalles))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("Producto no encontrado");
        }
        
        @Test
        @Order(19)
        @DisplayName("Debe rechazar cantidad cero en detalle")
        void debeRechazarCantidadCero() {
            // Given
            Compra compra = new Compra();
            compra.setProveedorId(1L);
            compra.setUsuarioId(1L);
            
            List<DetalleCompra> detalles = new ArrayList<>();
            DetalleCompra detalle = new DetalleCompra();
            detalle.setProductoId(1L);
            detalle.setCantidad(0);
            detalle.setPrecioUnitario(new BigDecimal("10.00"));
            detalles.add(detalle);
            
            // When & Then
            assertThatThrownBy(() -> controller.createCompra(compra, detalles))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cantidad");
        }
        
        @Test
        @Order(20)
        @DisplayName("Debe rechazar cantidad negativa en detalle")
        void debeRechazarCantidadNegativa() {
            // Given
            Compra compra = new Compra();
            compra.setProveedorId(1L);
            compra.setUsuarioId(1L);
            
            List<DetalleCompra> detalles = new ArrayList<>();
            DetalleCompra detalle = new DetalleCompra();
            detalle.setProductoId(1L);
            detalle.setCantidad(-5);
            detalle.setPrecioUnitario(new BigDecimal("10.00"));
            detalles.add(detalle);
            
            // When & Then
            assertThatThrownBy(() -> controller.createCompra(compra, detalles))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cantidad");
        }
        
        @Test
        @Order(21)
        @DisplayName("Debe rechazar precio cero en detalle")
        void debeRechazarPrecioCero() {
            // Given
            Compra compra = new Compra();
            compra.setProveedorId(1L);
            compra.setUsuarioId(1L);
            
            List<DetalleCompra> detalles = new ArrayList<>();
            DetalleCompra detalle = new DetalleCompra();
            detalle.setProductoId(1L);
            detalle.setCantidad(5);
            detalle.setPrecioUnitario(BigDecimal.ZERO);
            detalles.add(detalle);
            
            // When & Then
            assertThatThrownBy(() -> controller.createCompra(compra, detalles))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("precio");
        }
        
        @Test
        @Order(22)
        @DisplayName("Debe rechazar precio negativo en detalle")
        void debeRechazarPrecioNegativo() {
            // Given
            Compra compra = new Compra();
            compra.setProveedorId(1L);
            compra.setUsuarioId(1L);
            
            List<DetalleCompra> detalles = new ArrayList<>();
            DetalleCompra detalle = new DetalleCompra();
            detalle.setProductoId(1L);
            detalle.setCantidad(5);
            detalle.setPrecioUnitario(new BigDecimal("-10.00"));
            detalles.add(detalle);
            
            // When & Then
            assertThatThrownBy(() -> controller.createCompra(compra, detalles))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("precio");
        }
    }
    
    @Nested
    @DisplayName("Validaciones de Actualización de Estado")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ValidacionesActualizacionEstado {
        
        @Test
        @Order(30)
        @DisplayName("Debe rechazar ID nulo en updateEstado")
        void debeRechazarIdNuloEnUpdate() {
            // When & Then
            assertThatThrownBy(() -> controller.updateEstadoCompra(null, "RECIBIDA"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID");
        }
        
        @Test
        @Order(31)
        @DisplayName("Debe rechazar estado nulo")
        void debeRechazarEstadoNulo() {
            // When & Then
            assertThatThrownBy(() -> controller.updateEstadoCompra(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("estado");
        }
        
        @Test
        @Order(32)
        @DisplayName("Debe rechazar estado vacío")
        void debeRechazarEstadoVacio() {
            // When & Then
            assertThatThrownBy(() -> controller.updateEstadoCompra(1L, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("estado");
        }
        
        @Test
        @Order(33)
        @DisplayName("Debe rechazar estado inválido en update")
        void debeRechazarEstadoInvalidoEnUpdate() {
            // When & Then
            assertThatThrownBy(() -> controller.updateEstadoCompra(1L, "ESTADO_MALO"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Estado inválido");
        }
        
        @Test
        @Order(34)
        @DisplayName("Debe rechazar compra inexistente en updateEstado")
        void debeRechazarCompraInexistenteEnUpdate() {
            // When & Then
            assertThatThrownBy(() -> controller.updateEstadoCompra(99999L, "RECIBIDA"))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("no encontrada");
        }
    }
    
    @Nested
    @DisplayName("Validaciones de Cancelación")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ValidacionesCancelacion {
        
        @Test
        @Order(40)
        @DisplayName("Debe rechazar ID nulo en cancelar")
        void debeRechazarIdNuloEnCancelar() {
            // When & Then
            assertThatThrownBy(() -> controller.cancelarCompra(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
        }
        
        @Test
        @Order(41)
        @DisplayName("Debe rechazar compra inexistente en cancelar")
        void debeRechazarCompraInexistenteEnCancelar() {
            // When & Then
            assertThatThrownBy(() -> controller.cancelarCompra(99999L))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("no encontrada");
        }
    }
    
    @Nested
    @DisplayName("Operaciones Exitosas")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class OperacionesExitosas {
        
        @Test
        @Order(50)
        @DisplayName("Debe obtener todas las compras")
        void debeObtenerTodasLasCompras() throws Exception {
            // When
            List<Compra> compras = controller.getAllCompras();
            
            // Then
            assertThat(compras).isNotNull();
        }
        
        @Test
        @Order(51)
        @DisplayName("Debe obtener detalles de compra")
        void debeObtenerDetallesCompra() throws Exception {
            // When
            List<DetalleCompra> detalles = controller.getDetallesCompra(1L);
            
            // Then
            assertThat(detalles).isNotNull();
        }
        
        @Test
        @Order(52)
        @DisplayName("Debe lanzar excepción para compra inexistente")
        void debeLanzarExcepcionParaCompraInexistente() {
            // When & Then
            assertThatThrownBy(() -> controller.getCompraById(99999L))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("no encontrada");
        }
        
        @Test
        @Order(53)
        @DisplayName("Debe obtener compras por proveedor")
        void debeObtenerComprasPorProveedor() throws Exception {
            // When
            List<Compra> compras = controller.getComprasByProveedor(1L);
            
            // Then
            assertThat(compras).isNotNull();
        }
    }
}
