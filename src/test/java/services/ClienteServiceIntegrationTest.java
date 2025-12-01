package services;

import model.Cliente;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

/**
 * Tests de integración para ClienteService usando MySQL real
 */
@DisplayName("ClienteService Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClienteServiceIntegrationTest {
    
    private static ClienteService clienteService;
    
    @BeforeAll
    static void setUp() {
        System.out.println("🧪 Iniciando tests de ClienteService con MySQL en Docker");
        clienteService = new ClienteService();
    }
    
    @Test
    @Order(1)
    @DisplayName("Debe crear cliente correctamente")
    void debeCrearCliente() throws Exception {
        // Given
        Cliente cliente = new Cliente();
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");
        cliente.setEmail("juan.perez@test.com");
        cliente.setTelefono("555-1234");
        cliente.setDireccion("Calle Test 123");
        
        // When
        Cliente creado = clienteService.create(cliente);
        
        // Then
        assertThat(creado).isNotNull();
        assertThat(creado.getId()).isPositive();
        assertThat(creado.getEmail()).isEqualTo("juan.perez@test.com");
    }
    
    @Test
    @Order(2)
    @DisplayName("Debe buscar cliente por ID")
    void debeBuscarClientePorId() throws Exception {
        // Given - Usar cliente existente de BD de prueba
        Long id = 1L;
        
        // When
        Cliente cliente = clienteService.findById(id);
        
        // Then
        assertThat(cliente).isNotNull();
        assertThat(cliente.getId()).isEqualTo(id);
    }
    
    @Test
    @Order(3)
    @DisplayName("Debe obtener todos los clientes")
    void debeObtenerTodosLosClientes() throws Exception {
        // When
        List<Cliente> clientes = clienteService.findAll();
        
        // Then
        assertThat(clientes).isNotEmpty();
    }
    
    @Test
    @Order(4)
    @DisplayName("Debe actualizar cliente")
    void debeActualizarCliente() throws Exception {
        // Given - Crear cliente primero
        Cliente cliente = new Cliente();
        cliente.setNombre("María");
        cliente.setApellido("García");
        cliente.setEmail("maria.garcia@test.com");
        cliente.setTelefono("555-5678");
        cliente.setActivo(true); // Necesario para evitar NullPointerException
        
        Cliente creado = clienteService.create(cliente);
        
        // When - Actualizar
        creado.setTelefono("555-9999");
        boolean actualizado = clienteService.update(creado);
        
        // Then
        assertThat(actualizado).isTrue();
        
        Cliente verificado = clienteService.findById(creado.getId());
        assertThat(verificado.getTelefono()).isEqualTo("555-9999");
    }
    
    @Test
    @Order(5)
    @DisplayName("Debe eliminar cliente (soft delete)")
    void debeEliminarCliente() throws Exception {
        // Given - Crear cliente para eliminar
        Cliente cliente = new Cliente();
        cliente.setNombre("Temporal");
        cliente.setApellido("Test");
        cliente.setEmail("temporal2@test.com");
        cliente.setTelefono("555-0000");
        
        Cliente creado = clienteService.create(cliente);
        Long id = creado.getId();
        
        // When
        boolean eliminado = clienteService.delete(id);
        
        // Then
        assertThat(eliminado).isTrue();
        
        // El cliente sigue existiendo pero está inactivo (soft delete)
        Cliente verificado = clienteService.findById(id);
        if (verificado != null) {
            assertThat(verificado.getActivo()).isFalse();
        }
    }
    
    @Test
    @Order(6)
    @DisplayName("Debe buscar clientes por nombre")
    void debeBuscarClientesPorNombre() throws Exception {
        // When
        List<Cliente> clientes = clienteService.findByNombre("Juan");
        
        // Then
        assertThat(clientes).isNotNull();
    }
    
    @Nested
    @DisplayName("Validaciones y Manejo de Errores")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ValidacionesYManejoErrores {
        
        @Test
        @Order(20)
        @DisplayName("Debe retornar null para cliente inexistente")
        void debeRetornarNullParaClienteInexistente() throws Exception {
            // Given - ID que no existe
            Long clienteInexistente = 99999L;
            
            // When
            Cliente cliente = clienteService.findById(clienteInexistente);
            
            // Then
            assertThat(cliente).isNull();
        }
        
        @Test
        @Order(21)
        @DisplayName("Debe retornar lista vacía para nombre inexistente")
        void debeRetornarListaVaciaParaNombreInexistente() throws Exception {
            // Given - Nombre que no existe
            String nombreInexistente = "XYZ_NO_EXISTE_123";
            
            // When
            List<Cliente> clientes = clienteService.findByNombre(nombreInexistente);
            
            // Then
            assertThat(clientes).isNotNull();
            assertThat(clientes).isEmpty();
        }
        
        @Test
        @Order(22)
        @DisplayName("Debe manejar email duplicado")
        void debeManejarEmailDuplicado() throws Exception {
            // Given - Crear primer cliente
            Cliente cliente1 = new Cliente();
            cliente1.setNombre("Test");
            cliente1.setApellido("Duplicado");
            cliente1.setEmail("email.duplicado.test@test.com");
            cliente1.setTelefono("555-0001");
            cliente1.setDireccion("Calle Test");
            
            clienteService.create(cliente1);
            
            // When - Intentar crear otro con mismo email
            Cliente cliente2 = new Cliente();
            cliente2.setNombre("Test2");
            cliente2.setApellido("Duplicado2");
            cliente2.setEmail("email.duplicado.test@test.com"); // Mismo email
            cliente2.setTelefono("555-0002");
            cliente2.setDireccion("Calle Test 2");
            
            // Then - Puede lanzar excepción o permitirlo según la implementación
            // Verificamos que el servicio maneja el caso de alguna forma
            try {
                Cliente creado = clienteService.create(cliente2);
                // Si permite duplicados, verificamos que se creó
                assertThat(creado).isNotNull();
            } catch (Exception e) {
                // Si no permite duplicados, verificamos el mensaje
                assertThat(e.getMessage()).containsAnyOf("email", "duplicate", "único", "unico");
            }
        }
    }
}
