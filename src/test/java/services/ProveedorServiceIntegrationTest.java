package services;

import model.Proveedor;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

/**
 * Tests de integración para ProveedorService usando MySQL real
 */
@DisplayName("ProveedorService Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProveedorServiceIntegrationTest {
    
    private static ProveedorService proveedorService;
    
    @BeforeAll
    static void setUp() {
        System.out.println("🧪 Iniciando tests de ProveedorService con MySQL en Docker");
        proveedorService = new ProveedorService();
    }
    
    @Test
    @Order(1)
    @DisplayName("Debe crear proveedor correctamente")
    void debeCrearProveedor() throws Exception {
        // Given
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre("Farmacéutica Global");
        proveedor.setRfc("FAGL" + System.currentTimeMillis()); // RFC único
        proveedor.setTelefono("5551234567");
        proveedor.setEmail("contacto@farmaglobal.com");
        proveedor.setDireccion("Av. Reforma 123");
        proveedor.setCiudad("CDMX");
        proveedor.setActivo(true);
        
        // When
        Proveedor creado = proveedorService.create(proveedor);
        
        // Then
        assertThat(creado).isNotNull();
        assertThat(creado.getId()).isPositive();
        assertThat(creado.getNombre()).isEqualTo("Farmacéutica Global");
        assertThat(creado.getTelefono()).isEqualTo("5551234567");
    }
    
    @Test
    @Order(2)
    @DisplayName("Debe buscar proveedor por ID")
    void debeBuscarProveedorPorId() throws Exception {
        // Given
        Long id = 1L;
        
        // When
        Proveedor proveedor = proveedorService.findById(id);
        
        // Then
        assertThat(proveedor).isNotNull();
        assertThat(proveedor.getId()).isEqualTo(id);
    }
    
    @Test
    @Order(3)
    @DisplayName("Debe obtener todos los proveedores")
    void debeObtenerTodosLosProveedores() throws Exception {
        // When
        List<Proveedor> proveedores = proveedorService.findAll();
        
        // Then
        assertThat(proveedores).isNotEmpty();
    }
    
    @Test
    @Order(4)
    @DisplayName("Debe actualizar proveedor")
    void debeActualizarProveedor() throws Exception {
        // Given
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre("Proveedor Test");
        proveedor.setRfc("FABC" + System.currentTimeMillis()); // RFC único
        proveedor.setTelefono("5559999999");
        proveedor.setEmail("test@proveedor.com");
        proveedor.setDireccion("Calle Test 456");
        proveedor.setCiudad("Monterrey");
        
        Proveedor creado = proveedorService.create(proveedor);
        
        // When
        creado.setTelefono("5558888888");
        creado.setActivo(true); // Preservar activo
        boolean actualizado = proveedorService.update(creado);
        
        // Then
        assertThat(actualizado).isTrue();
        Proveedor verificado = proveedorService.findById(creado.getId());
        assertThat(verificado.getTelefono()).isEqualTo("5558888888");
    }
    
    @Test
    @Order(5)
    @DisplayName("Debe eliminar proveedor")
    void debeEliminarProveedor() throws Exception {
        // Given
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre("Temporal Proveedor");
        proveedor.setRfc("TEMP" + System.currentTimeMillis()); // RFC único
        proveedor.setTelefono("555-0000");
        proveedor.setEmail("temp@test.com");
        proveedor.setDireccion("Temporal 789");
        proveedor.setCiudad("Puebla");
        
        Proveedor creado = proveedorService.create(proveedor);
        Long id = creado.getId();
        
        // When
        boolean eliminado = proveedorService.delete(id);
        
        // Then
        assertThat(eliminado).isTrue();
    }
    
    @Test
    @Order(6)
    @DisplayName("Debe buscar proveedores por nombre")
    void debeBuscarProveedoresPorNombre() throws Exception {
        // When
        List<Proveedor> proveedores = proveedorService.findByNombre("Farm");
        
        // Then
        assertThat(proveedores).isNotNull();
    }
}
