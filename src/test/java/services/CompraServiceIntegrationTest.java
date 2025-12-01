package services;

import model.Compra;
import model.DetalleCompra;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests de integración para CompraService usando MySQL real
 */
@DisplayName("CompraService Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CompraServiceIntegrationTest {
    
    private static CompraService compraService;
    
    @BeforeAll
    static void setUp() {
        System.out.println("🧪 Iniciando tests de CompraService con MySQL en Docker");
        compraService = new CompraService();
    }
    
    @AfterAll
    static void tearDown() {
        System.out.println("✅ Tests de CompraService completados");
    }
    
    @Nested
    @DisplayName("Crear Compras con Detalles")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class CrearComprasConDetalles {
        
        @Test
        @Order(1)
        @DisplayName("Debe crear compra con múltiples productos")
        void debeCrearCompraConMultiplesProductos() throws Exception {
            // Given - Compra básica
            Compra compra = new Compra();
            compra.setProveedorId(1L); // Proveedor de prueba en BD
            compra.setUsuarioId(1L); // Usuario de prueba en BD
            compra.setSubtotal(new BigDecimal("200.00"));
            compra.setImpuestos(new BigDecimal("32.00"));
            compra.setTotal(new BigDecimal("232.00"));
            compra.setObservaciones("Compra de prueba");
            
            // Given - Detalles (2 productos)
            List<DetalleCompra> detalles = new ArrayList<>();
            
            DetalleCompra detalle1 = new DetalleCompra();
            detalle1.setProductoId(1L);
            detalle1.setCantidad(50);
            detalle1.setPrecioUnitario(new BigDecimal("10.00"));
            detalle1.setSubtotal(new BigDecimal("500.00"));
            detalles.add(detalle1);
            
            DetalleCompra detalle2 = new DetalleCompra();
            detalle2.setProductoId(2L);
            detalle2.setCantidad(30);
            detalle2.setPrecioUnitario(new BigDecimal("15.00"));
            detalle2.setSubtotal(new BigDecimal("450.00"));
            detalles.add(detalle2);
            
            // When
            Compra compraCreada = compraService.createConDetalles(compra, detalles);
            
            // Then
            assertThat(compraCreada).isNotNull();
            assertThat(compraCreada.getId()).isPositive();
            assertThat(compraCreada.getTotal()).isEqualByComparingTo(new BigDecimal("232.00"));
            
            // Verificar que los detalles se guardaron
            List<DetalleCompra> detallesGuardados = compraService.getDetalles(compraCreada.getId());
            assertThat(detallesGuardados).hasSize(2);
        }
        
        @Test
        @Order(2)
        @DisplayName("Debe crear compra simple")
        void debeCrearCompraSimple() throws Exception {
            // Given
            Compra compra = new Compra();
            compra.setProveedorId(1L);
            compra.setUsuarioId(1L);
            compra.setSubtotal(new BigDecimal("100.00"));
            compra.setImpuestos(new BigDecimal("16.00"));
            compra.setTotal(new BigDecimal("116.00"));
            
            List<DetalleCompra> detalles = new ArrayList<>();
            DetalleCompra detalle = new DetalleCompra();
            detalle.setProductoId(3L);
            detalle.setCantidad(20);
            detalle.setPrecioUnitario(new BigDecimal("5.00"));
            detalle.setSubtotal(new BigDecimal("100.00"));
            detalles.add(detalle);
            
            // When
            Compra compraCreada = compraService.createConDetalles(compra, detalles);
            
            // Then
            assertThat(compraCreada.getId()).isPositive();
            assertThat(compraCreada.getProveedorId()).isEqualTo(1L);
        }
    }
    
    @Nested
    @DisplayName("Buscar Compras")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class BuscarCompras {
        
        @Test
        @Order(10)
        @DisplayName("Debe buscar compra por ID")
        void debeBuscarCompraPorId() throws Exception {
            // Given - Crear compra primero
            Compra compra = new Compra();
            compra.setProveedorId(1L);
            compra.setUsuarioId(1L);
            compra.setSubtotal(new BigDecimal("50.00"));
            compra.setImpuestos(new BigDecimal("8.00"));
            compra.setTotal(new BigDecimal("58.00"));
            
            List<DetalleCompra> detalles = new ArrayList<>();
            DetalleCompra detalle = new DetalleCompra();
            detalle.setProductoId(1L);
            detalle.setCantidad(10);
            detalle.setPrecioUnitario(new BigDecimal("5.00"));
            detalle.setSubtotal(new BigDecimal("50.00"));
            detalles.add(detalle);
            
            Compra compraCreada = compraService.createConDetalles(compra, detalles);
            Long id = compraCreada.getId();
            
            // When
            Compra compraEncontrada = compraService.findById(id);
            
            // Then
            assertThat(compraEncontrada).isNotNull();
            assertThat(compraEncontrada.getId()).isEqualTo(id);
            assertThat(compraEncontrada.getTotal()).isPositive();
        }
        
        @Test
        @Order(11)
        @DisplayName("Debe retornar null para compra inexistente")
        void debeRetornarNullParaCompraInexistente() throws Exception {
            // When
            Compra compra = compraService.findById(99999L);
            
            // Then
            assertThat(compra).isNull();
        }
        
        @Test
        @Order(12)
        @DisplayName("Debe obtener todas las compras")
        void debeObtenerTodasLasCompras() throws Exception {
            // When
            List<Compra> compras = compraService.findAll();
            
            // Then
            assertThat(compras).isNotEmpty();
        }
        
        @Test
        @Order(13)
        @DisplayName("Debe buscar compras por proveedor")
        void debeBuscarComprasPorProveedor() throws Exception {
            // When
            List<Compra> compras = compraService.findByProveedor(1L);
            
            // Then
            assertThat(compras).isNotEmpty();
            compras.forEach(c -> assertThat(c.getProveedorId()).isEqualTo(1L));
        }
        
        @Test
        @Order(14)
        @DisplayName("Debe buscar compras por proveedor correctamente")
        void debeBuscarComprasPorProveedorCorrectamente() throws Exception {
            // When
            List<Compra> compras = compraService.findByProveedor(1L);
            
            // Then
            assertThat(compras).isNotNull();
            // Verificar que todas las compras son del proveedor 1
            if (!compras.isEmpty()) {
                compras.forEach(c -> assertThat(c.getProveedorId()).isEqualTo(1L));
            }
        }
    }
    
    @Nested
    @DisplayName("Detalles de Compras")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ObtenerDetalles {
        
        @Test
        @Order(20)
        @DisplayName("Debe obtener detalles de una compra")
        void debeObtenerDetallesDeCompra() throws Exception {
            // Given - Crear compra con detalles
            Compra compra = new Compra();
            compra.setProveedorId(1L);
            compra.setUsuarioId(1L);
            compra.setSubtotal(new BigDecimal("75.00"));
            compra.setImpuestos(new BigDecimal("12.00"));
            compra.setTotal(new BigDecimal("87.00"));
            
            List<DetalleCompra> detalles = new ArrayList<>();
            DetalleCompra detalle1 = new DetalleCompra();
            detalle1.setProductoId(1L);
            detalle1.setCantidad(15);
            detalle1.setPrecioUnitario(new BigDecimal("5.00"));
            detalle1.setSubtotal(new BigDecimal("75.00"));
            detalles.add(detalle1);
            
            Compra compraCreada = compraService.createConDetalles(compra, detalles);
            Long id = compraCreada.getId();
            
            // When
            List<DetalleCompra> detallesGuardados = compraService.getDetalles(id);
            
            // Then
            assertThat(detallesGuardados).hasSize(1);
            assertThat(detallesGuardados.get(0).getCompraId()).isEqualTo(id);
            assertThat(detallesGuardados.get(0).getCantidad()).isEqualTo(15);
        }
        
        @Test
        @Order(21)
        @DisplayName("Debe retornar lista vacía para compra sin detalles")
        void debeRetornarListaVaciaParaCompraSinDetalles() throws Exception {
            // When
            List<DetalleCompra> detalles = compraService.getDetalles(99999L);
            
            // Then
            assertThat(detalles).isEmpty();
        }
    }
    
    @Nested
    @DisplayName("Cancelar Compras")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class CancelarCompras {
        
        @Test
        @Order(30)
        @DisplayName("Debe cancelar compra exitosamente")
        void debeCancelarCompra() throws Exception {
            // Given - Crear compra para cancelar
            Compra compra = new Compra();
            compra.setProveedorId(1L);
            compra.setUsuarioId(1L);
            compra.setSubtotal(new BigDecimal("30.00"));
            compra.setImpuestos(new BigDecimal("4.80"));
            compra.setTotal(new BigDecimal("34.80"));
            
            List<DetalleCompra> detalles = new ArrayList<>();
            DetalleCompra detalle = new DetalleCompra();
            detalle.setProductoId(1L);
            detalle.setCantidad(5);
            detalle.setPrecioUnitario(new BigDecimal("6.00"));
            detalle.setSubtotal(new BigDecimal("30.00"));
            detalles.add(detalle);
            
            Compra compraCreada = compraService.createConDetalles(compra, detalles);
            Long compraId = compraCreada.getId();
            
            // When
            boolean cancelada = compraService.cancelar(compraId);
            
            // Then
            assertThat(cancelada).isTrue();
            
            // Verificar que el estado cambió
            Compra compraCancelada = compraService.findById(compraId);
            assertThat(compraCancelada.getEstado()).isEqualTo("CANCELADA");
        }
        
        @Test
        @Order(31)
        @DisplayName("Cancelar compra inexistente no debe fallar")
        void cancelarCompraInexistenteNoDebeFallar() throws Exception {
            // When
            boolean resultado = compraService.cancelar(99999L);
            
            // Then
            assertThat(resultado).isTrue();
        }
    }
    
    @Nested
    @DisplayName("Validaciones y Manejo de Errores")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ValidacionesYManejoErrores {
        
        @Test
        @Order(40)
        @DisplayName("Debe rechazar compra con proveedor inválido")
        void debeRechazarCompraConProveedorInvalido() throws Exception {
            // Given - Compra con proveedor inexistente
            Compra compra = new Compra();
            compra.setProveedorId(99999L); // ID inexistente
            compra.setUsuarioId(1L);
            compra.setSubtotal(new BigDecimal("100.00"));
            compra.setImpuestos(new BigDecimal("16.00"));
            compra.setTotal(new BigDecimal("116.00"));
            
            List<DetalleCompra> detalles = new ArrayList<>();
            DetalleCompra detalle = new DetalleCompra();
            detalle.setProductoId(1L);
            detalle.setCantidad(10);
            detalle.setPrecioUnitario(new BigDecimal("10.00"));
            detalle.setSubtotal(new BigDecimal("100.00"));
            detalles.add(detalle);
            
            // When & Then - Debe lanzar excepción por proveedor inválido
            assertThatThrownBy(() -> compraService.createConDetalles(compra, detalles))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("proveedor");
        }
        
        @Test
        @Order(41)
        @DisplayName("Debe rechazar compra con usuario inválido")
        void debeRechazarCompraConUsuarioInvalido() throws Exception {
            // Given - Compra con usuario inexistente
            Compra compra = new Compra();
            compra.setProveedorId(1L);
            compra.setUsuarioId(99999L); // ID inexistente
            compra.setSubtotal(new BigDecimal("100.00"));
            compra.setImpuestos(new BigDecimal("16.00"));
            compra.setTotal(new BigDecimal("116.00"));
            
            List<DetalleCompra> detalles = new ArrayList<>();
            DetalleCompra detalle = new DetalleCompra();
            detalle.setProductoId(1L);
            detalle.setCantidad(10);
            detalle.setPrecioUnitario(new BigDecimal("10.00"));
            detalle.setSubtotal(new BigDecimal("100.00"));
            detalles.add(detalle);
            
            // When & Then - Debe lanzar excepción por usuario inválido
            assertThatThrownBy(() -> compraService.createConDetalles(compra, detalles))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("usuario");
        }
        
        @Test
        @Order(42)
        @DisplayName("Debe retornar null para compra inexistente")
        void debeRetornarNullParaCompraInexistente() throws Exception {
            // Given - ID que no existe
            Long compraInexistente = 99999L;
            
            // When
            Compra compra = compraService.findById(compraInexistente);
            
            // Then
            assertThat(compra).isNull();
        }
        
        @Test
        @Order(43)
        @DisplayName("Debe manejar compra con detalles vacíos")
        void debeManejarCompraConDetallesVacios() throws Exception {
            // Given - Compra sin detalles
            Compra compra = new Compra();
            compra.setProveedorId(1L);
            compra.setUsuarioId(1L);
            compra.setSubtotal(new BigDecimal("0.00"));
            compra.setImpuestos(new BigDecimal("0.00"));
            compra.setTotal(new BigDecimal("0.00"));
            
            List<DetalleCompra> detalles = new ArrayList<>(); // Lista vacía
            
            // When
            Compra resultado = compraService.createConDetalles(compra, detalles);
            
            // Then - El servicio permite crear la compra aunque esté vacía
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isNotNull();
        }
    }
}
