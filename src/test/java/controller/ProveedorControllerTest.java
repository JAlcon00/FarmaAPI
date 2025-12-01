package controller;

import model.Proveedor;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

/**
 * Tests de integración para ProveedorController
 * Verifica validaciones y lógica de negocio
 */
@DisplayName("ProveedorController Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProveedorControllerTest {
    
    private static ProveedorController controller;
    
    @BeforeAll
    static void setUp() {
        System.out.println("🧪 Iniciando tests de ProveedorController con MySQL en Docker");
        controller = new ProveedorController();
    }
    
    @AfterAll
    static void tearDown() {
        System.out.println("✅ Tests de ProveedorController completados");
    }
    
    @Nested
    @DisplayName("Validaciones de Entrada")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ValidacionesEntrada {
        
        @Test
        @Order(1)
        @DisplayName("Debe rechazar ID nulo en getProveedorById")
        void debeRechazarIdNuloEnGet() {
            // When & Then
            assertThatThrownBy(() -> controller.getProveedorById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
        }
        
        @Test
        @Order(2)
        @DisplayName("Debe rechazar ID negativo en getProveedorById")
        void debeRechazarIdNegativoEnGet() {
            // When & Then
            assertThatThrownBy(() -> controller.getProveedorById(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
        }
        
        @Test
        @Order(3)
        @DisplayName("Debe rechazar ID cero en getProveedorById")
        void debeRechazarIdCeroEnGet() {
            // When & Then
            assertThatThrownBy(() -> controller.getProveedorById(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
        }
        
        @Test
        @Order(4)
        @DisplayName("Debe rechazar búsqueda con término vacío")
        void debeRechazarBusquedaVacia() {
            // When & Then
            assertThatThrownBy(() -> controller.searchProveedores(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("búsqueda");
        }
        
        @Test
        @Order(5)
        @DisplayName("Debe rechazar búsqueda con término nulo")
        void debeRechazarBusquedaNula() {
            // When & Then
            assertThatThrownBy(() -> controller.searchProveedores(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("búsqueda");
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
            Proveedor proveedor = new Proveedor();
            proveedor.setNombre("");
            
            // When & Then
            assertThatThrownBy(() -> controller.createProveedor(proveedor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nombre");
        }
        
        @Test
        @Order(11)
        @DisplayName("Debe rechazar nombre nulo")
        void debeRechazarNombreNulo() {
            // Given
            Proveedor proveedor = new Proveedor();
            proveedor.setNombre(null);
            
            // When & Then
            assertThatThrownBy(() -> controller.createProveedor(proveedor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nombre");
        }
        
        @Test
        @Order(12)
        @DisplayName("Debe rechazar nombre muy largo")
        void debeRechazarNombreMuyLargo() {
            // Given - Nombre de 201 caracteres
            String nombreLargo = "A".repeat(201);
            Proveedor proveedor = new Proveedor();
            proveedor.setNombre(nombreLargo);
            
            // When & Then
            assertThatThrownBy(() -> controller.createProveedor(proveedor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("200 caracteres");
        }
        
        @Test
        @Order(13)
        @DisplayName("Debe rechazar RFC muy largo")
        void debeRechazarRfcMuyLargo() {
            // Given - RFC de 21 caracteres
            String rfcLargo = "A".repeat(21);
            Proveedor proveedor = new Proveedor();
            proveedor.setNombre("Test Proveedor");
            proveedor.setRfc(rfcLargo);
            
            // When & Then
            assertThatThrownBy(() -> controller.createProveedor(proveedor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("RFC");
        }
        
        @Test
        @Order(14)
        @DisplayName("Debe rechazar email inválido")
        void debeRechazarEmailInvalido() {
            // Given
            Proveedor proveedor = new Proveedor();
            proveedor.setNombre("Test Proveedor");
            proveedor.setEmail("email-invalido"); // Sin @
            
            // When & Then
            assertThatThrownBy(() -> controller.createProveedor(proveedor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");
        }
        
        @Test
        @Order(15)
        @DisplayName("Debe rechazar email sin dominio")
        void debeRechazarEmailSinDominio() {
            // Given
            Proveedor proveedor = new Proveedor();
            proveedor.setNombre("Test Proveedor");
            proveedor.setEmail("test@"); // Sin dominio
            
            // When & Then
            assertThatThrownBy(() -> controller.createProveedor(proveedor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");
        }
        
        @Test
        @Order(16)
        @DisplayName("Debe rechazar teléfono muy largo")
        void debeRechazarTelefonoMuyLargo() {
            // Given - Teléfono de 21 caracteres
            String telefonoLargo = "1".repeat(21);
            Proveedor proveedor = new Proveedor();
            proveedor.setNombre("Test Proveedor");
            proveedor.setTelefono(telefonoLargo);
            
            // When & Then
            assertThatThrownBy(() -> controller.createProveedor(proveedor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("teléfono");
        }
        
        @Test
        @Order(17)
        @DisplayName("Debe aceptar email válido con subdominios")
        void debeAceptarEmailValidoConSubdominios() throws Exception {
            // Given
            long timestamp = System.currentTimeMillis();
            Proveedor proveedor = new Proveedor();
            proveedor.setNombre("Proveedor Test Email " + timestamp);
            proveedor.setEmail("usuario" + timestamp + "@subdominio.empresa.com");
            proveedor.setTelefono("555-1234");
            proveedor.setRfc("RFC" + String.valueOf(timestamp).substring(6));
            
            // When
            Proveedor resultado = controller.createProveedor(proveedor);
            
            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isNotNull();
        }
        
        @Test
        @Order(18)
        @DisplayName("Debe aceptar proveedor con campos opcionales vacíos")
        void debeAceptarProveedorConCamposOpcionalesVacios() throws Exception {
            // Given
            Proveedor proveedor = new Proveedor();
            proveedor.setNombre("Proveedor Mínimo");
            // Sin email, teléfono, RFC, dirección
            
            // When
            Proveedor resultado = controller.createProveedor(proveedor);
            
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
            Proveedor proveedor = new Proveedor();
            proveedor.setId(null);
            proveedor.setNombre("Test");
            
            // When & Then
            assertThatThrownBy(() -> controller.updateProveedor(proveedor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID");
        }
        
        @Test
        @Order(21)
        @DisplayName("Debe rechazar nombre vacío en update")
        void debeRechazarNombreVacioEnUpdate() {
            // Given
            Proveedor proveedor = new Proveedor();
            proveedor.setId(1L);
            proveedor.setNombre("");
            
            // When & Then
            assertThatThrownBy(() -> controller.updateProveedor(proveedor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nombre");
        }
        
        @Test
        @Order(22)
        @DisplayName("Debe rechazar email inválido en update")
        void debeRechazarEmailInvalidoEnUpdate() {
            // Given
            Proveedor proveedor = new Proveedor();
            proveedor.setId(1L);
            proveedor.setNombre("Test");
            proveedor.setEmail("email-sin-arroba");
            
            // When & Then
            assertThatThrownBy(() -> controller.updateProveedor(proveedor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");
        }
        
        @Test
        @Order(23)
        @DisplayName("Debe rechazar proveedor inexistente en update")
        void debeRechazarProveedorInexistenteEnUpdate() {
            // Given
            Proveedor proveedor = new Proveedor();
            proveedor.setId(99999L);
            proveedor.setNombre("Test");
            
            // When & Then
            assertThatThrownBy(() -> controller.updateProveedor(proveedor))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("no encontrado");
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
            assertThatThrownBy(() -> controller.deleteProveedor(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
        }
        
        @Test
        @Order(31)
        @DisplayName("Debe rechazar proveedor inexistente en delete")
        void debeRechazarProveedorInexistenteEnDelete() {
            // When & Then
            assertThatThrownBy(() -> controller.deleteProveedor(99999L))
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
        @DisplayName("Debe obtener todos los proveedores")
        void debeObtenerTodosLosProveedores() throws Exception {
            // When
            List<Proveedor> proveedores = controller.getAllProveedores();
            
            // Then
            assertThat(proveedores).isNotNull();
        }
        
        @Test
        @Order(41)
        @DisplayName("Debe buscar proveedores por nombre")
        void debeBuscarProveedoresPorNombre() throws Exception {
            // When
            List<Proveedor> proveedores = controller.searchProveedores("Farmaceutica");
            
            // Then
            assertThat(proveedores).isNotNull();
        }
        
        @Test
        @Order(42)
        @DisplayName("Debe lanzar excepción para proveedor inexistente")
        void debeLanzarExcepcionParaProveedorInexistente() {
            // When & Then
            assertThatThrownBy(() -> controller.getProveedorById(99999L))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("no encontrado");
        }
    }
}
