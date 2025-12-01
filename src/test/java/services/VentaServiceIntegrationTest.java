package services;

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
 * Tests de integración para VentaService usando MySQL real
 */
@DisplayName("VentaService Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VentaServiceIntegrationTest {
    
    private static VentaService ventaService;
    private static Long ventaIdCreada;
    
    @BeforeAll
    static void setUp() {
        System.out.println("🧪 Iniciando tests de VentaService con MySQL en Docker (localhost:3307)");
        ventaService = new VentaService();
    }
    
    @AfterAll
    static void tearDown() {
        System.out.println("✅ Tests de VentaService completados");
    }
    
    @Nested
    @DisplayName("Crear Ventas con Detalles")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class CrearVentasConDetalles {
        
        @Test
        @Order(1)
        @DisplayName("Debe crear venta con múltiples productos")
        void debeCrearVentaConMultiplesProductos() throws Exception {
            // Given - Venta básica
            Venta venta = new Venta();
            venta.setClienteId(1L); // Cliente de prueba en BD
            venta.setUsuarioId(1L); // Usuario de prueba en BD
            venta.setSubtotal(new BigDecimal("85.50"));
            venta.setDescuento(new BigDecimal("5.00"));
            venta.setImpuestos(new BigDecimal("12.88"));
            venta.setTotal(new BigDecimal("93.38"));
            venta.setMetodoPago("EFECTIVO");
            venta.setObservaciones("Venta de prueba");
            
            // Given - Detalles (2 productos)
            List<DetalleVenta> detalles = new ArrayList<>();
            
            DetalleVenta detalle1 = new DetalleVenta();
            detalle1.setProductoId(1L); // Paracetamol
            detalle1.setNombreProducto("Paracetamol 500mg");
            detalle1.setCantidad(3);
            detalle1.setPrecioUnitario(new BigDecimal("15.50"));
            detalle1.setSubtotal(new BigDecimal("46.50"));
            detalles.add(detalle1);
            
            DetalleVenta detalle2 = new DetalleVenta();
            detalle2.setProductoId(2L); // Ibuprofeno
            detalle2.setNombreProducto("Ibuprofeno 400mg");
            detalle2.setCantidad(2);
            detalle2.setPrecioUnitario(new BigDecimal("22.00"));
            detalle2.setSubtotal(new BigDecimal("44.00"));
            detalles.add(detalle2);
            
            // When
            Venta ventaCreada = ventaService.createConDetalles(venta, detalles);
            ventaIdCreada = ventaCreada.getId();
            
            // Then
            assertThat(ventaCreada).isNotNull();
            assertThat(ventaCreada.getId()).isPositive();
            assertThat(ventaCreada.getTotal()).isEqualByComparingTo(new BigDecimal("93.38"));
            assertThat(ventaCreada.getEstado()).isIn("COMPLETADA", null);
            
            // Verificar que los detalles se guardaron
            List<DetalleVenta> detallesGuardados = ventaService.getDetalles(ventaIdCreada);
            assertThat(detallesGuardados).hasSize(2);
        }
        
        @Test
        @Order(2)
        @DisplayName("Debe crear venta sin cliente (venta anónima)")
        void debeCrearVentaSinCliente() throws Exception {
            // Given
            Venta venta = new Venta();
            venta.setClienteId(null); // Sin cliente
            venta.setUsuarioId(1L);
            venta.setSubtotal(new BigDecimal("15.50"));
            venta.setDescuento(new BigDecimal("0.00"));
            venta.setImpuestos(new BigDecimal("2.48"));
            venta.setTotal(new BigDecimal("17.98"));
            venta.setMetodoPago("EFECTIVO");
            
            List<DetalleVenta> detalles = new ArrayList<>();
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProductoId(1L);
            detalle.setNombreProducto("Paracetamol 500mg");
            detalle.setCantidad(1);
            detalle.setPrecioUnitario(new BigDecimal("15.50"));
            detalle.setSubtotal(new BigDecimal("15.50"));
            detalles.add(detalle);
            
            // When
            Venta ventaCreada = ventaService.createConDetalles(venta, detalles);
            
            // Then
            assertThat(ventaCreada).isNotNull();
            assertThat(ventaCreada.getId()).isPositive();
            assertThat(ventaCreada.getClienteId()).isNull();
        }
        
        @Test
        @Order(3)
        @DisplayName("Debe crear venta con tarjeta de crédito")
        void debeCrearVentaConTarjeta() throws Exception {
            // Given
            Venta venta = new Venta();
            venta.setClienteId(2L);
            venta.setUsuarioId(1L);
            venta.setSubtotal(new BigDecimal("45.00"));
            venta.setDescuento(new BigDecimal("2.00"));
            venta.setImpuestos(new BigDecimal("6.88"));
            venta.setTotal(new BigDecimal("49.88"));
            venta.setMetodoPago("TARJETA");
            venta.setObservaciones("Pago con tarjeta Visa");
            
            List<DetalleVenta> detalles = new ArrayList<>();
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProductoId(3L);
            detalle.setNombreProducto("Amoxicilina 500mg");
            detalle.setCantidad(1);
            detalle.setPrecioUnitario(new BigDecimal("45.00"));
            detalle.setSubtotal(new BigDecimal("45.00"));
            detalles.add(detalle);
            
            // When
            Venta ventaCreada = ventaService.createConDetalles(venta, detalles);
            
            // Then
            assertThat(ventaCreada.getMetodoPago()).isEqualTo("TARJETA");
            assertThat(ventaCreada.getObservaciones()).contains("Visa");
        }
    }
    
    @Nested
    @DisplayName("Buscar Ventas")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class BuscarVentas {
        
        @Test
        @Order(10)
        @DisplayName("Debe buscar venta por ID")
        void debeBuscarVentaPorId() throws Exception {
            // Given - Crear venta primero
            Venta venta = new Venta();
            venta.setClienteId(1L);
            venta.setUsuarioId(1L);
            venta.setSubtotal(new BigDecimal("10.00"));
            venta.setDescuento(new BigDecimal("0.00"));
            venta.setImpuestos(new BigDecimal("1.60"));
            venta.setTotal(new BigDecimal("11.60"));
            venta.setMetodoPago("EFECTIVO");
            
            List<DetalleVenta> detalles = new ArrayList<>();
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProductoId(1L);
            detalle.setNombreProducto("Test");
            detalle.setCantidad(1);
            detalle.setPrecioUnitario(new BigDecimal("10.00"));
            detalle.setSubtotal(new BigDecimal("10.00"));
            detalles.add(detalle);
            
            Venta ventaCreada = ventaService.createConDetalles(venta, detalles);
            Long id = ventaCreada.getId();
            
            // When
            Venta ventaEncontrada = ventaService.findById(id);
            
            // Then
            assertThat(ventaEncontrada).isNotNull();
            assertThat(ventaEncontrada.getId()).isEqualTo(id);
            assertThat(ventaEncontrada.getTotal()).isPositive();
        }
        
        @Test
        @Order(11)
        @DisplayName("Debe retornar null para venta inexistente")
        void debeRetornarNullParaVentaInexistente() throws Exception {
            // When
            Venta venta = ventaService.findById(99999L);
            
            // Then
            assertThat(venta).isNull();
        }
        
        @Test
        @Order(12)
        @DisplayName("Debe obtener todas las ventas")
        void debeObtenerTodasLasVentas() throws Exception {
            // When
            List<Venta> ventas = ventaService.findAll();
            
            // Then
            assertThat(ventas).isNotEmpty();
            // Solo verificamos que haya al menos 1
            assertThat(ventas.size()).isGreaterThanOrEqualTo(1);
        }
        
        @Test
        @Order(13)
        @DisplayName("Debe buscar ventas por rango de fechas")
        void debeBuscarVentasPorFecha() throws Exception {
            // Given
            LocalDate hoy = LocalDate.now();
            Date fechaInicio = Date.valueOf(hoy.minusDays(1));
            Date fechaFin = Date.valueOf(hoy.plusDays(1));
            
            // When
            List<Venta> ventas = ventaService.findByFecha(fechaInicio, fechaFin);
            
            // Then
            // Solo verificamos que no falle, puede estar vacío si no hay ventas hoy
            assertThat(ventas).isNotNull();
        }
    }
    
    @Nested
    @DisplayName("Detalles de Ventas")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ObtenerDetalles {
        
        @Test
        @Order(20)
        @DisplayName("Debe obtener detalles de una venta")
        void debeObtenerDetallesDeVenta() throws Exception {
            // Given - Crear venta con detalles
            Venta venta = new Venta();
            venta.setClienteId(1L);
            venta.setUsuarioId(1L);
            venta.setSubtotal(new BigDecimal("46.50"));
            venta.setDescuento(new BigDecimal("0.00"));
            venta.setImpuestos(new BigDecimal("7.44"));
            venta.setTotal(new BigDecimal("53.94"));
            venta.setMetodoPago("EFECTIVO");
            
            List<DetalleVenta> detalles = new ArrayList<>();
            DetalleVenta detalle1 = new DetalleVenta();
            detalle1.setProductoId(1L);
            detalle1.setNombreProducto("Paracetamol 500mg");
            detalle1.setCantidad(3);
            detalle1.setPrecioUnitario(new BigDecimal("15.50"));
            detalle1.setSubtotal(new BigDecimal("46.50"));
            detalles.add(detalle1);
            
            Venta ventaCreada = ventaService.createConDetalles(venta, detalles);
            Long id = ventaCreada.getId();
            
            // When
            List<DetalleVenta> detallesGuardados = ventaService.getDetalles(id);
            
            // Then
            assertThat(detallesGuardados).hasSize(1);
            assertThat(detallesGuardados.get(0).getVentaId()).isEqualTo(id);
            assertThat(detallesGuardados.get(0).getCantidad()).isEqualTo(3);
            assertThat(detallesGuardados.get(0).getPrecioUnitario()).isEqualByComparingTo(new BigDecimal("15.50"));
        }
        
        @Test
        @Order(21)
        @DisplayName("Debe retornar lista vacía para venta sin detalles")
        void debeRetornarListaVaciaParaVentaSinDetalles() throws Exception {
            // When
            List<DetalleVenta> detalles = ventaService.getDetalles(99999L);
            
            // Then
            assertThat(detalles).isEmpty();
        }
    }
    
    @Nested
    @DisplayName("Cancelar Ventas")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class CancelarVentas {
        
        @Test
        @Order(30)
        @DisplayName("Debe cancelar venta exitosamente")
        void debeCancelarVenta() throws Exception {
            // Given - Crear venta para cancelar
            Venta venta = new Venta();
            venta.setClienteId(1L);
            venta.setUsuarioId(1L);
            venta.setSubtotal(new BigDecimal("10.00"));
            venta.setDescuento(new BigDecimal("0.00"));
            venta.setImpuestos(new BigDecimal("1.60"));
            venta.setTotal(new BigDecimal("11.60"));
            venta.setMetodoPago("EFECTIVO");
            
            List<DetalleVenta> detalles = new ArrayList<>();
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProductoId(1L);
            detalle.setNombreProducto("Test Producto");
            detalle.setCantidad(1);
            detalle.setPrecioUnitario(new BigDecimal("10.00"));
            detalle.setSubtotal(new BigDecimal("10.00"));
            detalles.add(detalle);
            
            Venta ventaCreada = ventaService.createConDetalles(venta, detalles);
            Long ventaId = ventaCreada.getId();
            
            // When
            boolean cancelada = ventaService.cancelar(ventaId);
            
            // Then
            assertThat(cancelada).isTrue();
            
            // Verificar que el estado cambió
            Venta ventaCancelada = ventaService.findById(ventaId);
            assertThat(ventaCancelada.getEstado()).isEqualTo("CANCELADA");
        }
        
        @Test
        @Order(31)
        @DisplayName("Cancelar venta inexistente no debe fallar")
        void cancelarVentaInexistenteNoDebeFallar() throws Exception {
            // When - El método no verifica existencia, solo ejecuta UPDATE
            boolean resultado = ventaService.cancelar(99999L);
            
            // Then - El método retorna true aunque no haya filas afectadas
            // Esto es un comportamiento esperado del servicio actual
            assertThat(resultado).isTrue();
        }
    }
    
    @Nested
    @DisplayName("Reportes y Agregaciones")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ReportesYAgregaciones {
        
        @Test
        @Order(40)
        @DisplayName("Debe calcular total de ventas por período")
        void debeCalcularTotalVentasPorPeriodo() throws Exception {
            // Given - Crear una venta primero
            Venta venta = new Venta();
            venta.setClienteId(1L);
            venta.setUsuarioId(1L);
            venta.setSubtotal(new BigDecimal("50.00"));
            venta.setDescuento(new BigDecimal("0.00"));
            venta.setImpuestos(new BigDecimal("8.00"));
            venta.setTotal(new BigDecimal("58.00"));
            venta.setMetodoPago("EFECTIVO");
            
            List<DetalleVenta> detalles = new ArrayList<>();
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProductoId(1L);
            detalle.setNombreProducto("Test");
            detalle.setCantidad(1);
            detalle.setPrecioUnitario(new BigDecimal("50.00"));
            detalle.setSubtotal(new BigDecimal("50.00"));
            detalles.add(detalle);
            
            ventaService.createConDetalles(venta, detalles);
            
            // Given - Fechas del período
            LocalDate hoy = LocalDate.now();
            Date fechaInicio = Date.valueOf(hoy.minusDays(1));
            Date fechaFin = Date.valueOf(hoy.plusDays(1));
            
            // When
            BigDecimal total = ventaService.getTotalVentasPorPeriodo(fechaInicio, fechaFin);
            
            // Then
            assertThat(total).isGreaterThanOrEqualTo(new BigDecimal("58.00"));
        }
    }
    
    @Nested
    @DisplayName("Validaciones y Manejo de Errores")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ValidacionesYManejoErrores {
        
        @Test
        @Order(50)
        @DisplayName("Debe rechazar venta sin cliente válido")
        void debeRechazarVentaSinClienteValido() throws Exception {
            // Given - Venta con cliente inexistente
            Venta venta = new Venta();
            venta.setClienteId(99999L); // ID inexistente
            venta.setUsuarioId(1L);
            venta.setSubtotal(new BigDecimal("100.00"));
            venta.setDescuento(new BigDecimal("0.00"));
            venta.setImpuestos(new BigDecimal("16.00"));
            venta.setTotal(new BigDecimal("116.00"));
            venta.setMetodoPago("EFECTIVO");
            
            List<DetalleVenta> detalles = new ArrayList<>();
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProductoId(1L);
            detalle.setNombreProducto("Test");
            detalle.setCantidad(1);
            detalle.setPrecioUnitario(new BigDecimal("100.00"));
            detalle.setSubtotal(new BigDecimal("100.00"));
            detalles.add(detalle);
            
            // When & Then - Debe lanzar excepción por cliente inválido
            assertThatThrownBy(() -> ventaService.createConDetalles(venta, detalles))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("cliente");
        }
        
        @Test
        @Order(51)
        @DisplayName("Debe rechazar venta sin usuario válido")
        void debeRechazarVentaSinUsuarioValido() throws Exception {
            // Given - Venta con usuario inexistente
            Venta venta = new Venta();
            venta.setClienteId(1L);
            venta.setUsuarioId(99999L); // ID inexistente
            venta.setSubtotal(new BigDecimal("100.00"));
            venta.setDescuento(new BigDecimal("0.00"));
            venta.setImpuestos(new BigDecimal("16.00"));
            venta.setTotal(new BigDecimal("116.00"));
            venta.setMetodoPago("EFECTIVO");
            
            List<DetalleVenta> detalles = new ArrayList<>();
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProductoId(1L);
            detalle.setNombreProducto("Test");
            detalle.setCantidad(1);
            detalle.setPrecioUnitario(new BigDecimal("100.00"));
            detalle.setSubtotal(new BigDecimal("100.00"));
            detalles.add(detalle);
            
            // When & Then - Debe lanzar excepción por usuario inválido
            assertThatThrownBy(() -> ventaService.createConDetalles(venta, detalles))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("usuario");
        }
        
        @Test
        @Order(52)
        @DisplayName("Debe manejar venta con detalles vacíos")
        void debeManejarVentaConDetallesVacios() throws Exception {
            // Given - Venta sin detalles
            Venta venta = new Venta();
            venta.setClienteId(1L);
            venta.setUsuarioId(1L);
            venta.setSubtotal(new BigDecimal("0.00"));
            venta.setDescuento(new BigDecimal("0.00"));
            venta.setImpuestos(new BigDecimal("0.00"));
            venta.setTotal(new BigDecimal("0.00"));
            venta.setMetodoPago("EFECTIVO");
            
            List<DetalleVenta> detalles = new ArrayList<>(); // Lista vacía
            
            // When
            Venta resultado = ventaService.createConDetalles(venta, detalles);
            
            // Then - El servicio permite crear la venta aunque esté vacía
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isNotNull();
            assertThat(resultado.getTotal()).isEqualByComparingTo(new BigDecimal("0.00"));
        }
        
        @Test
        @Order(53)
        @DisplayName("Debe permitir venta con valores en cero")
        void debePermitirVentaConValoresEnCero() throws Exception {
            // Given - Venta con total cero (posible caso de descuento total)
            Venta venta = new Venta();
            venta.setClienteId(1L);
            venta.setUsuarioId(1L);
            venta.setSubtotal(new BigDecimal("0.00"));
            venta.setDescuento(new BigDecimal("0.00"));
            venta.setImpuestos(new BigDecimal("0.00"));
            venta.setTotal(new BigDecimal("0.00"));
            venta.setMetodoPago("EFECTIVO");
            
            List<DetalleVenta> detalles = new ArrayList<>();
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProductoId(1L);
            detalle.setNombreProducto("Test");
            detalle.setCantidad(0); // Cantidad cero
            detalle.setPrecioUnitario(new BigDecimal("0.00"));
            detalle.setSubtotal(new BigDecimal("0.00"));
            detalles.add(detalle);
            
            // When
            Venta resultado = ventaService.createConDetalles(venta, detalles);
            
            // Then - El servicio permite valores en cero
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isNotNull();
        }
        
        @Test
        @Order(54)
        @DisplayName("Debe retornar null para venta inexistente")
        void debeRetornarNullParaVentaInexistente() throws Exception {
            // Given - ID que no existe
            Long ventaInexistente = 99999L;
            
            // When
            Venta venta = ventaService.findById(ventaInexistente);
            
            // Then
            assertThat(venta).isNull();
        }
    }
}
