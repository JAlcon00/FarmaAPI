package security;

import org.junit.jupiter.api.*;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para RolePermissions
 */
@DisplayName("RolePermissions Tests")
class RolePermissionsTest {
    
    @Nested
    @DisplayName("Constantes de Roles")
    class ConstantesRoles {
        
        @Test
        @DisplayName("Debe tener todos los roles definidos correctamente")
        void debeTenerTodosLosRolesDefinidos() {
            assertThat(RolePermissions.ADMIN).isEqualTo(1);
            assertThat(RolePermissions.FARMACEUTICO).isEqualTo(2);
            assertThat(RolePermissions.CAJERO).isEqualTo(3);
            assertThat(RolePermissions.ALMACEN).isEqualTo(4);
            assertThat(RolePermissions.GERENTE).isEqualTo(5);
            assertThat(RolePermissions.ASISTENTE).isEqualTo(6);
            assertThat(RolePermissions.AUDITOR).isEqualTo(7);
            assertThat(RolePermissions.SOPORTE).isEqualTo(8);
            assertThat(RolePermissions.OPERADOR).isEqualTo(9);
            assertThat(RolePermissions.SUPERVISOR).isEqualTo(10);
            assertThat(RolePermissions.ENCARGADO_VENTAS).isEqualTo(11);
            assertThat(RolePermissions.ENCARGADO_COMPRAS).isEqualTo(12);
            assertThat(RolePermissions.ADMIN_FINANZAS).isEqualTo(13);
            assertThat(RolePermissions.DIRECTOR).isEqualTo(14);
            assertThat(RolePermissions.RRHH).isEqualTo(15);
            assertThat(RolePermissions.MARKETING).isEqualTo(16);
            assertThat(RolePermissions.TESORERIA).isEqualTo(17);
            assertThat(RolePermissions.ANALISTA).isEqualTo(18);
            assertThat(RolePermissions.INTERNO).isEqualTo(19);
            assertThat(RolePermissions.INVITADO).isEqualTo(20);
        }
    }
    
    @Nested
    @DisplayName("Verificación de Permisos - hasPermission")
    class VerificacionPermisos {
        
        @Test
        @DisplayName("Debe retornar true cuando el rol tiene el permiso")
        void debeRetornarTrueCuandoTienePermiso() {
            // Given
            Set<Integer> rolesPermitidos = Set.of(1, 2, 3);
            
            // When/Then
            assertThat(RolePermissions.hasPermission(1, rolesPermitidos)).isTrue();
            assertThat(RolePermissions.hasPermission(2, rolesPermitidos)).isTrue();
            assertThat(RolePermissions.hasPermission(3, rolesPermitidos)).isTrue();
        }
        
        @Test
        @DisplayName("Debe retornar false cuando el rol no tiene el permiso")
        void debeRetornarFalseCuandoNoTienePermiso() {
            // Given
            Set<Integer> rolesPermitidos = Set.of(1, 2, 3);
            
            // When/Then
            assertThat(RolePermissions.hasPermission(4, rolesPermitidos)).isFalse();
            assertThat(RolePermissions.hasPermission(5, rolesPermitidos)).isFalse();
        }
        
        @Test
        @DisplayName("Debe retornar false cuando roleId es null")
        void debeRetornarFalseCuandoRoleIdNull() {
            // Given
            Set<Integer> rolesPermitidos = Set.of(1, 2, 3);
            
            // When/Then
            assertThat(RolePermissions.hasPermission(null, rolesPermitidos)).isFalse();
        }
        
        @Test
        @DisplayName("Debe retornar false cuando allowedRoles es null")
        void debeRetornarFalseCuandoAllowedRolesNull() {
            // When/Then
            assertThat(RolePermissions.hasPermission(1, null)).isFalse();
        }
        
        @Test
        @DisplayName("Debe retornar false cuando ambos son null")
        void debeRetornarFalseCuandoAmbosNull() {
            // When/Then
            assertThat(RolePermissions.hasPermission(null, null)).isFalse();
        }
    }
    
    @Nested
    @DisplayName("Nombres de Roles - getRoleName")
    class NombresRoles {
        
        @Test
        @DisplayName("Debe retornar el nombre correcto para ADMIN")
        void debeRetornarNombreAdmin() {
            assertThat(RolePermissions.getRoleName(1)).isEqualTo("ADMIN");
        }
        
        @Test
        @DisplayName("Debe retornar el nombre correcto para FARMACEUTICO")
        void debeRetornarNombreFarmaceutico() {
            assertThat(RolePermissions.getRoleName(2)).isEqualTo("FARMACEUTICO");
        }
        
        @Test
        @DisplayName("Debe retornar el nombre correcto para CAJERO")
        void debeRetornarNombreCajero() {
            assertThat(RolePermissions.getRoleName(3)).isEqualTo("CAJERO");
        }
        
        @Test
        @DisplayName("Debe retornar el nombre correcto para DIRECTOR")
        void debeRetornarNombreDirector() {
            assertThat(RolePermissions.getRoleName(14)).isEqualTo("DIRECTOR");
        }
        
        @Test
        @DisplayName("Debe retornar DESCONOCIDO para roleId null")
        void debeRetornarDesconocidoParaNull() {
            assertThat(RolePermissions.getRoleName(null)).isEqualTo("DESCONOCIDO");
        }
        
        @Test
        @DisplayName("Debe retornar DESCONOCIDO para roleId inválido")
        void debeRetornarDesconocidoParaInvalido() {
            assertThat(RolePermissions.getRoleName(999)).isEqualTo("DESCONOCIDO");
        }
        
        @Test
        @DisplayName("Debe retornar nombres correctos para todos los roles")
        void debeRetornarNombresCorrectosParaTodos() {
            assertThat(RolePermissions.getRoleName(1)).isEqualTo("ADMIN");
            assertThat(RolePermissions.getRoleName(2)).isEqualTo("FARMACEUTICO");
            assertThat(RolePermissions.getRoleName(3)).isEqualTo("CAJERO");
            assertThat(RolePermissions.getRoleName(4)).isEqualTo("ALMACEN");
            assertThat(RolePermissions.getRoleName(5)).isEqualTo("GERENTE");
            assertThat(RolePermissions.getRoleName(6)).isEqualTo("ASISTENTE");
            assertThat(RolePermissions.getRoleName(7)).isEqualTo("AUDITOR");
            assertThat(RolePermissions.getRoleName(8)).isEqualTo("SOPORTE");
            assertThat(RolePermissions.getRoleName(9)).isEqualTo("OPERADOR");
            assertThat(RolePermissions.getRoleName(10)).isEqualTo("SUPERVISOR");
            assertThat(RolePermissions.getRoleName(11)).isEqualTo("ENCARGADO_VENTAS");
            assertThat(RolePermissions.getRoleName(12)).isEqualTo("ENCARGADO_COMPRAS");
            assertThat(RolePermissions.getRoleName(13)).isEqualTo("ADMIN_FINANZAS");
            assertThat(RolePermissions.getRoleName(14)).isEqualTo("DIRECTOR");
            assertThat(RolePermissions.getRoleName(15)).isEqualTo("RRHH");
            assertThat(RolePermissions.getRoleName(16)).isEqualTo("MARKETING");
            assertThat(RolePermissions.getRoleName(17)).isEqualTo("TESORERIA");
            assertThat(RolePermissions.getRoleName(18)).isEqualTo("ANALISTA");
            assertThat(RolePermissions.getRoleName(19)).isEqualTo("INTERNO");
            assertThat(RolePermissions.getRoleName(20)).isEqualTo("INVITADO");
        }
    }
    
    @Nested
    @DisplayName("Verificación de Roles Específicos")
    class VerificacionRolesEspecificos {
        
        @Test
        @DisplayName("Debe identificar correctamente el rol ADMIN")
        void debeIdentificarAdmin() {
            assertThat(RolePermissions.isAdmin(1)).isTrue();
            assertThat(RolePermissions.isAdmin(2)).isFalse();
            assertThat(RolePermissions.isAdmin(null)).isFalse();
        }
        
        @Test
        @DisplayName("Debe identificar correctamente el rol DIRECTOR")
        void debeIdentificarDirector() {
            assertThat(RolePermissions.isDirector(14)).isTrue();
            assertThat(RolePermissions.isDirector(1)).isFalse();
            assertThat(RolePermissions.isDirector(null)).isFalse();
        }
        
        @Test
        @DisplayName("Debe identificar correctamente los privilegios administrativos")
        void debeIdentificarPrivilegiosAdministrativos() {
            assertThat(RolePermissions.hasAdminPrivileges(1)).isTrue();  // ADMIN
            assertThat(RolePermissions.hasAdminPrivileges(14)).isTrue(); // DIRECTOR
            assertThat(RolePermissions.hasAdminPrivileges(2)).isFalse(); // FARMACEUTICO
            assertThat(RolePermissions.hasAdminPrivileges(3)).isFalse(); // CAJERO
            assertThat(RolePermissions.hasAdminPrivileges(null)).isFalse();
        }
    }
    
    @Nested
    @DisplayName("Permisos de Productos")
    class PermisosProductos {
        
        @Test
        @DisplayName("ADMIN debe tener permisos completos de productos")
        void adminDebeTenerPermisosTotales() {
            assertThat(RolePermissions.PRODUCTOS_READ).contains(RolePermissions.ADMIN);
            assertThat(RolePermissions.PRODUCTOS_WRITE).contains(RolePermissions.ADMIN);
            assertThat(RolePermissions.PRODUCTOS_DELETE).contains(RolePermissions.ADMIN);
        }
        
        @Test
        @DisplayName("FARMACEUTICO debe poder leer y escribir productos")
        void farmaceuticoDebeLeerYEscribir() {
            assertThat(RolePermissions.PRODUCTOS_READ).contains(RolePermissions.FARMACEUTICO);
            assertThat(RolePermissions.PRODUCTOS_WRITE).contains(RolePermissions.FARMACEUTICO);
        }
        
        @Test
        @DisplayName("CAJERO debe poder solo leer productos")
        void cajeroDebeSoloLeer() {
            assertThat(RolePermissions.PRODUCTOS_READ).contains(RolePermissions.CAJERO);
            assertThat(RolePermissions.PRODUCTOS_WRITE).doesNotContain(RolePermissions.CAJERO);
            assertThat(RolePermissions.PRODUCTOS_DELETE).doesNotContain(RolePermissions.CAJERO);
        }
        
        @Test
        @DisplayName("INVITADO debe poder solo leer productos")
        void invitadoDebeSoloLeer() {
            assertThat(RolePermissions.PRODUCTOS_READ).contains(RolePermissions.INVITADO);
            assertThat(RolePermissions.PRODUCTOS_WRITE).doesNotContain(RolePermissions.INVITADO);
            assertThat(RolePermissions.PRODUCTOS_DELETE).doesNotContain(RolePermissions.INVITADO);
        }
    }
    
    @Nested
    @DisplayName("Permisos de Ventas")
    class PermisosVentas {
        
        @Test
        @DisplayName("CAJERO debe poder crear ventas")
        void cajeroDebeCrearVentas() {
            assertThat(RolePermissions.VENTAS_CREATE).contains(RolePermissions.CAJERO);
        }
        
        @Test
        @DisplayName("Solo roles administrativos deben cancelar ventas")
        void soloAdminDebenCancelar() {
            assertThat(RolePermissions.VENTAS_CANCEL).contains(RolePermissions.ADMIN);
            assertThat(RolePermissions.VENTAS_CANCEL).contains(RolePermissions.GERENTE);
            assertThat(RolePermissions.VENTAS_CANCEL).contains(RolePermissions.DIRECTOR);
            assertThat(RolePermissions.VENTAS_CANCEL).doesNotContain(RolePermissions.CAJERO);
        }
        
        @Test
        @DisplayName("AUDITOR debe poder leer ventas pero no crearlas")
        void auditorDebeSoloLeer() {
            assertThat(RolePermissions.VENTAS_READ).contains(RolePermissions.AUDITOR);
            assertThat(RolePermissions.VENTAS_CREATE).doesNotContain(RolePermissions.AUDITOR);
        }
    }
    
    @Nested
    @DisplayName("Permisos de Compras")
    class PermisosCompras {
        
        @Test
        @DisplayName("ALMACEN debe poder crear compras")
        void almacenDebeCrearCompras() {
            assertThat(RolePermissions.COMPRAS_CREATE).contains(RolePermissions.ALMACEN);
        }
        
        @Test
        @DisplayName("ENCARGADO_COMPRAS debe tener permisos completos de compras")
        void encargadoComprasDebeTenerPermisosTotales() {
            assertThat(RolePermissions.COMPRAS_CREATE).contains(RolePermissions.ENCARGADO_COMPRAS);
            assertThat(RolePermissions.COMPRAS_READ).contains(RolePermissions.ENCARGADO_COMPRAS);
            assertThat(RolePermissions.COMPRAS_CANCEL).contains(RolePermissions.ENCARGADO_COMPRAS);
        }
        
        @Test
        @DisplayName("CAJERO no debe tener acceso a compras")
        void cajeroNoDebeAccederCompras() {
            assertThat(RolePermissions.COMPRAS_CREATE).doesNotContain(RolePermissions.CAJERO);
            assertThat(RolePermissions.COMPRAS_READ).doesNotContain(RolePermissions.CAJERO);
        }
    }
    
    @Nested
    @DisplayName("Permisos de Clientes")
    class PermisosClientes {
        
        @Test
        @DisplayName("CAJERO debe poder leer y escribir clientes")
        void cajeroDebeLeerYEscribirClientes() {
            assertThat(RolePermissions.CLIENTES_READ).contains(RolePermissions.CAJERO);
            assertThat(RolePermissions.CLIENTES_WRITE).contains(RolePermissions.CAJERO);
        }
        
        @Test
        @DisplayName("MARKETING debe poder gestionar clientes")
        void marketingDebeGestionarClientes() {
            assertThat(RolePermissions.CLIENTES_READ).contains(RolePermissions.MARKETING);
            assertThat(RolePermissions.CLIENTES_WRITE).contains(RolePermissions.MARKETING);
        }
        
        @Test
        @DisplayName("Solo roles administrativos deben eliminar clientes")
        void soloAdminDebenEliminarClientes() {
            assertThat(RolePermissions.CLIENTES_DELETE).contains(RolePermissions.ADMIN);
            assertThat(RolePermissions.CLIENTES_DELETE).contains(RolePermissions.GERENTE);
            assertThat(RolePermissions.CLIENTES_DELETE).doesNotContain(RolePermissions.CAJERO);
        }
    }
    
    @Nested
    @DisplayName("Permisos de Reportes")
    class PermisosReportes {
        
        @Test
        @DisplayName("AUDITOR debe tener acceso a reportes")
        void auditorDebeAccederReportes() {
            assertThat(RolePermissions.REPORTES_READ).contains(RolePermissions.AUDITOR);
        }
        
        @Test
        @DisplayName("ADMIN_FINANZAS debe acceder a reportes financieros")
        void adminFinanzasDebeAccederReportesFinancieros() {
            assertThat(RolePermissions.REPORTES_FINANCIEROS).contains(RolePermissions.ADMIN_FINANZAS);
        }
        
        @Test
        @DisplayName("CAJERO no debe tener acceso a reportes")
        void cajeroNoDebeAccederReportes() {
            assertThat(RolePermissions.REPORTES_READ).doesNotContain(RolePermissions.CAJERO);
        }
    }
    
    @Nested
    @DisplayName("Permisos de Usuarios")
    class PermisosUsuarios {
        
        @Test
        @DisplayName("RRHH debe poder gestionar usuarios")
        void rrhhDebeGestionarUsuarios() {
            assertThat(RolePermissions.USUARIOS_READ).contains(RolePermissions.RRHH);
            assertThat(RolePermissions.USUARIOS_WRITE).contains(RolePermissions.RRHH);
        }
        
        @Test
        @DisplayName("Solo ADMIN y DIRECTOR deben eliminar usuarios")
        void soloAdminYDirectorDebenEliminarUsuarios() {
            assertThat(RolePermissions.USUARIOS_DELETE).contains(RolePermissions.ADMIN);
            assertThat(RolePermissions.USUARIOS_DELETE).contains(RolePermissions.DIRECTOR);
            assertThat(RolePermissions.USUARIOS_DELETE).doesNotContain(RolePermissions.RRHH);
        }
        
        @Test
        @DisplayName("CAJERO no debe tener acceso a gestión de usuarios")
        void cajeroNoDebeAccederUsuarios() {
            assertThat(RolePermissions.USUARIOS_READ).doesNotContain(RolePermissions.CAJERO);
        }
    }
    
    @Nested
    @DisplayName("Permisos de Roles")
    class PermisosRoles {
        
        @Test
        @DisplayName("Solo ADMIN y DIRECTOR deben gestionar roles")
        void soloAdminYDirectorDebenGestionarRoles() {
            assertThat(RolePermissions.ROLES_MANAGE).contains(RolePermissions.ADMIN);
            assertThat(RolePermissions.ROLES_MANAGE).contains(RolePermissions.DIRECTOR);
            assertThat(RolePermissions.ROLES_MANAGE).doesNotContain(RolePermissions.GERENTE);
        }
    }
    
    @Nested
    @DisplayName("Casos Edge")
    class CasosEdge {
        
        @Test
        @DisplayName("Debe manejar roleId con valor 0")
        void debeManejarRoleIdCero() {
            assertThat(RolePermissions.getRoleName(0)).isEqualTo("DESCONOCIDO");
            assertThat(RolePermissions.isAdmin(0)).isFalse();
        }
        
        @Test
        @DisplayName("Debe manejar roleId con valor negativo")
        void debeManejarRoleIdNegativo() {
            assertThat(RolePermissions.getRoleName(-1)).isEqualTo("DESCONOCIDO");
            assertThat(RolePermissions.isAdmin(-1)).isFalse();
        }
        
        @Test
        @DisplayName("Debe manejar roleId con valor muy grande")
        void debeManejarRoleIdMuyGrande() {
            assertThat(RolePermissions.getRoleName(9999)).isEqualTo("DESCONOCIDO");
            assertThat(RolePermissions.hasAdminPrivileges(9999)).isFalse();
        }
        
        @Test
        @DisplayName("Debe verificar que los sets de permisos no sean null")
        void debeTenerSetsNoNulos() {
            assertThat(RolePermissions.PRODUCTOS_READ).isNotNull();
            assertThat(RolePermissions.VENTAS_CREATE).isNotNull();
            assertThat(RolePermissions.COMPRAS_READ).isNotNull();
            assertThat(RolePermissions.CLIENTES_WRITE).isNotNull();
            assertThat(RolePermissions.REPORTES_READ).isNotNull();
        }
        
        @Test
        @DisplayName("Debe verificar que los sets de permisos no estén vacíos")
        void debeTenerSetsNoVacios() {
            assertThat(RolePermissions.PRODUCTOS_READ).isNotEmpty();
            assertThat(RolePermissions.VENTAS_CREATE).isNotEmpty();
            assertThat(RolePermissions.COMPRAS_READ).isNotEmpty();
            assertThat(RolePermissions.ROLES_MANAGE).isNotEmpty();
        }
    }
}
