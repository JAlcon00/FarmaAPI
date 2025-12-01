package controller;

import model.Cliente;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

/**
 * Tests de integración para ClienteController
 * Verifica validaciones y lógica de negocio
 */
@DisplayName("ClienteController Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClienteControllerTest {
    
    private static ClienteController controller;
    
    @BeforeAll
    static void setUp() {
        System.out.println("🧪 Iniciando tests de ClienteController con MySQL en Docker");
        controller = new ClienteController();
    }
    
    @AfterAll
    static void tearDown() {
        System.out.println("✅ Tests de ClienteController completados");
    }
    
    @Nested
    @DisplayName("Validaciones de Entrada")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ValidacionesEntrada {
        
        @Test
        @Order(1)
        @DisplayName("Debe rechazar ID nulo en getClienteById")
        void debeRechazarIdNuloEnGet() {
            // When & Then
            assertThatThrownBy(() -> controller.getClienteById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
        }
        
        @Test
        @Order(2)
        @DisplayName("Debe rechazar ID negativo en getClienteById")
        void debeRechazarIdNegativoEnGet() {
            // When & Then
            assertThatThrownBy(() -> controller.getClienteById(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
        }
        
        @Test
        @Order(3)
        @DisplayName("Debe rechazar ID cero en getClienteById")
        void debeRechazarIdCeroEnGet() {
            // When & Then
            assertThatThrownBy(() -> controller.getClienteById(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
        }
        
        @Test
        @Order(4)
        @DisplayName("Debe rechazar búsqueda con término vacío")
        void debeRechazarBusquedaVacia() {
            // When & Then
            assertThatThrownBy(() -> controller.searchClientes(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("búsqueda");
        }
        
        @Test
        @Order(5)
        @DisplayName("Debe rechazar búsqueda con término nulo")
        void debeRechazarBusquedaNula() {
            // When & Then
            assertThatThrownBy(() -> controller.searchClientes(null))
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
            Cliente cliente = new Cliente();
            cliente.setNombre("");
            cliente.setEmail("test@test.com");
            
            // When & Then
            assertThatThrownBy(() -> controller.createCliente(cliente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nombre");
        }
        
        @Test
        @Order(11)
        @DisplayName("Debe rechazar nombre nulo")
        void debeRechazarNombreNulo() {
            // Given
            Cliente cliente = new Cliente();
            cliente.setNombre(null);
            cliente.setEmail("test@test.com");
            
            // When & Then
            assertThatThrownBy(() -> controller.createCliente(cliente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nombre");
        }
        
        @Test
        @Order(12)
        @DisplayName("Debe rechazar nombre muy largo")
        void debeRechazarNombreMuyLargo() {
            // Given - Nombre de 101 caracteres
            String nombreLargo = "A".repeat(101);
            Cliente cliente = new Cliente();
            cliente.setNombre(nombreLargo);
            cliente.setEmail("test@test.com");
            
            // When & Then
            assertThatThrownBy(() -> controller.createCliente(cliente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("100 caracteres");
        }
        
        @Test
        @Order(13)
        @DisplayName("Debe rechazar email inválido")
        void debeRechazarEmailInvalido() {
            // Given - Agregar apellido requerido por Bean Validation
            Cliente cliente = new Cliente();
            cliente.setNombre("Test");
            cliente.setApellido("Cliente");  // Requerido
            cliente.setEmail("email-invalido"); // Sin @
            
            // When & Then
            assertThatThrownBy(() -> controller.createCliente(cliente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email");
        }
        
        @Test
        @Order(14)
        @DisplayName("Debe rechazar email sin dominio")
        void debeRechazarEmailSinDominio() {
            // Given - Agregar apellido requerido por Bean Validation
            Cliente cliente = new Cliente();
            cliente.setNombre("Test");
            cliente.setApellido("Cliente");  // Requerido
            cliente.setEmail("test@"); // Sin dominio
            
            // When & Then
            assertThatThrownBy(() -> controller.createCliente(cliente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email");
        }
        
        @Test
        @Order(15)
        @DisplayName("Debe rechazar teléfono muy largo")
        void debeRechazarTelefonoMuyLargo() {
            // Given - Bean Validation requiere exactamente 10 dígitos
            String telefonoLargo = "12345678901"; // 11 dígitos (excede máximo)
            Cliente cliente = new Cliente();
            cliente.setNombre("Test");
            cliente.setApellido("Cliente");  // Requerido
            cliente.setTelefono(telefonoLargo);
            
            // When & Then
            assertThatThrownBy(() -> controller.createCliente(cliente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("teléfono");
        }
        
        @Test
        @Order(16)
        @DisplayName("Debe aceptar email válido con subdominios")
        void debeAceptarEmailValidoConSubdominios() throws Exception {
            // Given - Teléfono debe tener exactamente 10 dígitos
            Cliente cliente = new Cliente();
            cliente.setNombre("Test Cliente Email");
            cliente.setApellido("Valido");
            cliente.setEmail("usuario@subdominio.empresa.com");
            cliente.setTelefono("5551234567");  // 10 dígitos
            
            // When
            Cliente resultado = controller.createCliente(cliente);
            
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
            Cliente cliente = new Cliente();
            cliente.setId(null);
            cliente.setNombre("Test");
            
            // When & Then
            assertThatThrownBy(() -> controller.updateCliente(cliente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID");
        }
        
        @Test
        @Order(21)
        @DisplayName("Debe rechazar nombre vacío en update")
        void debeRechazarNombreVacioEnUpdate() {
            // Given
            Cliente cliente = new Cliente();
            cliente.setId(1L);
            cliente.setNombre("");
            
            // When & Then
            assertThatThrownBy(() -> controller.updateCliente(cliente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nombre");
        }
        
        @Test
        @Order(22)
        @DisplayName("Debe rechazar email inválido en update")
        void debeRechazarEmailInvalidoEnUpdate() {
            // Given - Agregar apellido requerido por Bean Validation
            Cliente cliente = new Cliente();
            cliente.setId(1L);
            cliente.setNombre("Test");
            cliente.setApellido("Cliente");  // Requerido
            cliente.setEmail("email-sin-arroba");
            
            // When & Then
            assertThatThrownBy(() -> controller.updateCliente(cliente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email");
        }
        
        @Test
        @Order(23)
        @DisplayName("Debe rechazar cliente inexistente en update")
        void debeRechazarClienteInexistenteEnUpdate() {
            // Given - Agregar todos los campos requeridos por Bean Validation
            Cliente cliente = new Cliente();
            cliente.setId(99999L);
            cliente.setNombre("Test");
            cliente.setApellido("Cliente");  // Requerido
            cliente.setEmail("test@test.com");
            
            // When & Then
            assertThatThrownBy(() -> controller.updateCliente(cliente))
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
            assertThatThrownBy(() -> controller.deleteCliente(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
        }
        
        @Test
        @Order(31)
        @DisplayName("Debe rechazar cliente inexistente en delete")
        void debeRechazarClienteInexistenteEnDelete() {
            // When & Then
            assertThatThrownBy(() -> controller.deleteCliente(99999L))
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
        @DisplayName("Debe obtener todos los clientes")
        void debeObtenerTodosLosClientes() throws Exception {
            // When
            List<Cliente> clientes = controller.getAllClientes();
            
            // Then
            assertThat(clientes).isNotNull();
        }
        
        @Test
        @Order(41)
        @DisplayName("Debe buscar clientes por nombre")
        void debeBuscarClientesPorNombre() throws Exception {
            // When
            List<Cliente> clientes = controller.searchClientes("Juan");
            
            // Then
            assertThat(clientes).isNotNull();
        }
        
        @Test
        @Order(42)
        @DisplayName("Debe lanzar excepción para cliente inexistente")
        void debeLanzarExcepcionParaClienteInexistente() {
            // When & Then
            assertThatThrownBy(() -> controller.getClienteById(99999L))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("no encontrado");
        }
    }
}
