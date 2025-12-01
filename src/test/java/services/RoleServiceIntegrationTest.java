package services;

import model.Role;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

/**
 * Tests de integración para RoleService usando MySQL real
 */
@DisplayName("RoleService Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RoleServiceIntegrationTest {
    
    private static RoleService roleService;
    
    @BeforeAll
    static void setUp() {
        System.out.println("🧪 Iniciando tests de RoleService con MySQL en Docker");
        roleService = new RoleService();
    }
    
    @Test
    @Order(1)
    @DisplayName("Debe crear rol correctamente")
    void debeCrearRol() throws Exception {
        // Given
        Role role = new Role();
        role.setNombre("ROL_TEST_" + System.currentTimeMillis());
        role.setDescripcion("Rol de prueba para testing");
        role.setActivo(true);
        
        // When
        Role creado = roleService.create(role);
        
        // Then
        assertThat(creado).isNotNull();
        assertThat(creado.getId()).isPositive();
        assertThat(creado.getNombre()).contains("ROL_TEST_");
        assertThat(creado.getDescripcion()).isEqualTo("Rol de prueba para testing");
        assertThat(creado.getActivo()).isTrue();
    }
    
    @Test
    @Order(2)
    @DisplayName("Debe buscar rol por ID")
    void debeBuscarRolPorId() throws Exception {
        // Given - Rol ADMIN existente en BD de prueba
        Integer id = 1; // ADMIN
        
        // When
        Role role = roleService.findById(id);
        
        // Then
        assertThat(role).isNotNull();
        assertThat(role.getId()).isEqualTo(id);
        assertThat(role.getNombre()).isEqualTo("ADMIN");
        assertThat(role.getActivo()).isTrue();
    }
    
    @Test
    @Order(3)
    @DisplayName("Debe obtener todos los roles activos")
    void debeObtenerTodosLosRoles() throws Exception {
        // When
        List<Role> roles = roleService.findAll();
        
        // Then
        assertThat(roles).isNotEmpty();
        assertThat(roles.size()).isGreaterThanOrEqualTo(3); // ADMIN, FARMACEUTICO, CAJERO
        assertThat(roles).allMatch(r -> r.getActivo() == true);
        assertThat(roles).extracting(Role::getNombre)
            .contains("ADMIN", "FARMACEUTICO", "CAJERO");
    }
    
    @Test
    @Order(4)
    @DisplayName("Debe actualizar rol")
    void debeActualizarRol() throws Exception {
        // Given - Crear rol para actualizar
        Role role = new Role();
        role.setNombre("ROL_UPDATE_" + System.currentTimeMillis());
        role.setDescripcion("Descripción inicial");
        role.setActivo(true);
        
        Role creado = roleService.create(role);
        
        // When
        creado.setDescripcion("Descripción actualizada");
        boolean actualizado = roleService.update(creado);
        
        // Then
        assertThat(actualizado).isTrue();
        Role verificado = roleService.findById(creado.getId());
        assertThat(verificado.getDescripcion()).isEqualTo("Descripción actualizada");
    }
    
    @Test
    @Order(5)
    @DisplayName("Debe eliminar rol (soft delete)")
    void debeEliminarRol() throws Exception {
        // Given - Crear rol para eliminar
        Role role = new Role();
        role.setNombre("ROL_DELETE_" + System.currentTimeMillis());
        role.setDescripcion("Para eliminar");
        role.setActivo(true);
        
        Role creado = roleService.create(role);
        Integer id = creado.getId();
        
        // When
        boolean eliminado = roleService.delete(id);
        
        // Then
        assertThat(eliminado).isTrue();
        
        // Verificar que el rol existe pero está inactivo
        Role verificado = roleService.findById(id);
        assertThat(verificado).isNotNull(); // Soft delete: sigue existiendo
        assertThat(verificado.getActivo()).isFalse(); // Pero está desactivado
        
        // Verificar que no aparece en findAll (solo activos)
        List<Role> roles = roleService.findAll();
        assertThat(roles).noneMatch(r -> r.getId().equals(id));
    }
}
