package controller;

import model.DetalleVenta;
import model.Venta;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests de integración para VentaController
 * Verifica validaciones y lógica de negocio
 */
@DisplayName("VentaController Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VentaControllerTest {
    
    private static VentaController controller;
    
    @BeforeAll
    static void setUp() {
        System.out.println("🧪 Iniciando tests de VentaController con MySQL en Docker");
        controller = new VentaController();
    }
    
    @AfterAll
    static void tearDown() {
        System.out.println("✅ Tests de VentaController completados");
    }
    
    @Nested
    @DisplayName("Validaciones de Entrada")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ValidacionesEntrada {
        
        @Test
        @Order(1)
        @DisplayName("Debe rechazar ID nulo en getVentaById")
        void debeRechazarIdNuloEnGet() {
            // When & Then
            assertThatThrownBy(() -> controller.getVentaById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
        }
        
        @Test
        @Order(2)
        @DisplayName("Debe rechazar ID negativo en getVentaById")
        void debeRechazarIdNegativoEnGet() {
            // When & Then
            assertThatThrownBy(() -> controller.getVentaById(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
        }
        
        @Test
        @Order(3)
        @DisplayName("Debe rechazar fechas nulas en getVentasByFecha")
        void debeRechazarFechasNulas() {
            // When & Then
            assertThatThrownBy(() -> controller.getVentasByFecha(null, Date.valueOf(LocalDate.now())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("fechas");
        }
        
        @Test
        @Order(4)
        @DisplayName("Debe rechazar fecha inicio posterior a fecha fin")
        void debeRechazarFechaInicioMayorQueFin() {
            // Given
            Date fechaInicio = Date.valueOf(LocalDate.now());
            Date fechaFin = Date.valueOf(LocalDate.now().minusDays(5));
            
            // When & Then
            assertThatThrownBy(() -> controller.getVentasByFecha(fechaInicio, fechaFin))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("anterior");
        }
    }
    
    @Nested
    @DisplayName("Validaciones de Creación")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ValidacionesCreacion {
        
        @Test
        @Order(10)
        @DisplayName("Debe rechazar usuario nulo")
        void debeRechazarUsuarioNulo() {
            // Given
            Venta venta = new Venta();
            venta.setUsuarioId(null);
            venta.setMetodoPago("EFECTIVO");
            
            List<DetalleVenta> detalles = new ArrayList<>();
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProductoId(1L);
            detalle.setCantidad(1);
            detalle.setPrecioUnitario(new BigDecimal("100.00"));
            detalles.add(detalle);
            
            // When & Then
            assertThatThrownBy(() -> controller.createVenta(venta, detalles))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("usuario");
        }
        
        @Test
        @Order(11)
        @DisplayName("Debe rechazar detalles nulos")
        void debeRechazarDetallesNulos() {
            // Given
            Venta venta = new Venta();
            venta.setUsuarioId(1L);
            venta.setMetodoPago("EFECTIVO");
            
            // When & Then
            assertThatThrownBy(() -> controller.createVenta(venta, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("producto");
        }
        
        @Test
        @Order(12)
        @DisplayName("Debe rechazar detalles vacíos")
        void debeRechazarDetallesVacios() {
            // Given
            Venta venta = new Venta();
            venta.setUsuarioId(1L);
            venta.setMetodoPago("EFECTIVO");
            
            List<DetalleVenta> detalles = new ArrayList<>();
            
            // When & Then
            assertThatThrownBy(() -> controller.createVenta(venta, detalles))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("producto");
        }
        
        @Test
        @Order(13)
        @DisplayName("Debe rechazar cliente inexistente")
        void debeRechazarClienteInexistente() {
            // Given
            Venta venta = new Venta();
            venta.setUsuarioId(1L);
            venta.setClienteId(99999L); // Cliente inexistente
            venta.setMetodoPago("EFECTIVO");
            
            List<DetalleVenta> detalles = new ArrayList<>();
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProductoId(1L);
            detalle.setCantidad(1);
            detalle.setPrecioUnitario(new BigDecimal("100.00"));
            detalles.add(detalle);
            
            // When & Then
            assertThatThrownBy(() -> controller.createVenta(venta, detalles))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("Cliente no encontrado");
        }
        
        @Test
        @Order(14)
        @DisplayName("Debe rechazar método de pago inválido")
        void debeRechazarMetodoPagoInvalido() {
            // Given
            Venta venta = new Venta();
            venta.setUsuarioId(1L);
            venta.setMetodoPago("BITCOIN"); // Método inválido
            
            List<DetalleVenta> detalles = new ArrayList<>();
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProductoId(1L);
            detalle.setCantidad(1);
            detalle.setPrecioUnitario(new BigDecimal("100.00"));
            detalles.add(detalle);
            
            // When & Then
            assertThatThrownBy(() -> controller.createVenta(venta, detalles))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Método de pago inválido");
        }
        
        @Test
        @Order(15)
        @DisplayName("Debe rechazar producto inexistente en detalles")
        void debeRechazarProductoInexistente() {
            // Given
            Venta venta = new Venta();
            venta.setUsuarioId(1L);
            venta.setMetodoPago("EFECTIVO");
            
            List<DetalleVenta> detalles = new ArrayList<>();
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProductoId(99999L); // Producto inexistente
            detalle.setCantidad(1);
            detalle.setPrecioUnitario(new BigDecimal("100.00"));
            detalles.add(detalle);
            
            // When & Then
            assertThatThrownBy(() -> controller.createVenta(venta, detalles))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("Producto no encontrado");
        }
        
        @Test
        @Order(16)
        @DisplayName("Debe rechazar cantidad cero o negativa")
        void debeRechazarCantidadInvalida() {
            // Given
            Venta venta = new Venta();
            venta.setUsuarioId(1L);
            venta.setMetodoPago("EFECTIVO");
            
            List<DetalleVenta> detalles = new ArrayList<>();
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProductoId(1L);
            detalle.setCantidad(0); // Cantidad inválida
            detalle.setPrecioUnitario(new BigDecimal("100.00"));
            detalles.add(detalle);
            
            // When & Then
            assertThatThrownBy(() -> controller.createVenta(venta, detalles))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cantidad");
        }
        
        @Test
        @Order(17)
        @DisplayName("Debe rechazar precio unitario inválido")
        void debeRechazarPrecioInvalido() {
            // Given
            Venta venta = new Venta();
            venta.setUsuarioId(1L);
            venta.setMetodoPago("EFECTIVO");
            
            List<DetalleVenta> detalles = new ArrayList<>();
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProductoId(1L);
            detalle.setCantidad(1);
            detalle.setPrecioUnitario(BigDecimal.ZERO); // Precio inválido
            detalles.add(detalle);
            
            // When & Then
            assertThatThrownBy(() -> controller.createVenta(venta, detalles))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("precio");
        }
        
        @Test
        @Order(18)
        @DisplayName("Debe rechazar descuento negativo")
        void debeRechazarDescuentoNegativo() {
            // Given
            Venta venta = new Venta();
            venta.setUsuarioId(1L);
            venta.setMetodoPago("EFECTIVO");
            venta.setDescuento(new BigDecimal("-10.00")); // Descuento negativo
            
            List<DetalleVenta> detalles = new ArrayList<>();
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProductoId(1L);
            detalle.setCantidad(1);
            detalle.setPrecioUnitario(new BigDecimal("100.00"));
            detalles.add(detalle);
            
            // When & Then
            assertThatThrownBy(() -> controller.createVenta(venta, detalles))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("descuento");
        }
    }
    
    @Nested
    @DisplayName("Validaciones de Cancelación")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ValidacionesCancelacion {
        
        @Test
        @Order(20)
        @DisplayName("Debe rechazar ID nulo en cancelación")
        void debeRechazarIdNuloEnCancelacion() {
            // When & Then
            assertThatThrownBy(() -> controller.cancelarVenta(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
        }
        
        @Test
        @Order(21)
        @DisplayName("Debe rechazar venta inexistente en cancelación")
        void debeRechazarVentaInexistenteEnCancelacion() {
            // When & Then
            assertThatThrownBy(() -> controller.cancelarVenta(99999L))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("no encontrada");
        }
    }
    
    @Nested
    @DisplayName("Operaciones Exitosas")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class OperacionesExitosas {
        
        @Test
        @Order(30)
        @DisplayName("Debe obtener todas las ventas")
        void debeObtenerTodasLasVentas() throws Exception {
            // When
            List<Venta> ventas = controller.getAllVentas();
            
            // Then
            assertThat(ventas).isNotNull();
        }
        
        @Test
        @Order(31)
        @DisplayName("Debe obtener ventas por rango de fechas válido")
        void debeObtenerVentasPorFechasValidas() throws Exception {
            // Given
            Date fechaInicio = Date.valueOf(LocalDate.now().minusDays(30));
            Date fechaFin = Date.valueOf(LocalDate.now());
            
            // When
            List<Venta> ventas = controller.getVentasByFecha(fechaInicio, fechaFin);
            
            // Then
            assertThat(ventas).isNotNull();
        }
        
        @Test
        @Order(32)
        @DisplayName("Debe lanzar excepción para venta inexistente")
        void debeLanzarExcepcionParaVentaInexistente() {
            // When & Then
            assertThatThrownBy(() -> controller.getVentaById(99999L))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("no encontrada");
        }
        
        @Test
        @Order(33)
        @DisplayName("Debe obtener total de ventas por período")
        void debeObtenerTotalVentasPorPeriodo() throws Exception {
            // Given
            Date fechaInicio = Date.valueOf(LocalDate.now().minusDays(30));
            Date fechaFin = Date.valueOf(LocalDate.now());
            
            // When
            BigDecimal total = controller.getTotalVentasPorPeriodo(fechaInicio, fechaFin);
            
            // Then
            assertThat(total).isNotNull();
            assertThat(total).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        }
    }
}
