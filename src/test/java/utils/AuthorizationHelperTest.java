package utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import security.RolePermissions;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para AuthorizationHelper
 */
@DisplayName("AuthorizationHelper Tests")
class AuthorizationHelperTest {
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    private StringWriter responseWriter;
    private AutoCloseable closeable;
    
    @BeforeEach
    void setUp() throws IOException {
        closeable = MockitoAnnotations.openMocks(this);
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }
    
    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
    
    @Nested
    @DisplayName("Verificación de Roles - checkRoles")
    class VerificacionRoles {
        
        @Test
        @DisplayName("Debe autorizar cuando el rol está en la lista de permitidos")
        void debeAutorizarRolPermitido() throws IOException {
            // Given
            when(request.getAttribute("roleId")).thenReturn(1); // ADMIN
            when(request.getAttribute("userId")).thenReturn(1L);
            when(request.getAttribute("userEmail")).thenReturn("admin@test.com");
            
            Set<Integer> rolesPermitidos = Set.of(1, 2, 3); // ADMIN, FARMACEUTICO, CAJERO
            
            // When
            boolean autorizado = AuthorizationHelper.checkRoles(request, response, rolesPermitidos);
            
            // Then
            assertThat(autorizado).isTrue();
            verify(response, never()).setStatus(anyInt());
        }
        
        @Test
        @DisplayName("Debe denegar cuando el rol no está en la lista de permitidos")
        void debeDenegarRolNoPermitido() throws IOException {
            // Given
            when(request.getAttribute("roleId")).thenReturn(6); // ASISTENTE
            when(request.getAttribute("userId")).thenReturn(1L);
            when(request.getAttribute("userEmail")).thenReturn("asistente@test.com");
            
            Set<Integer> rolesPermitidos = Set.of(1, 2, 3); // Solo ADMIN, FARMACEUTICO, CAJERO
            
            // When
            boolean autorizado = AuthorizationHelper.checkRoles(request, response, rolesPermitidos);
            
            // Then
            assertThat(autorizado).isFalse();
            verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
        
        @Test
        @DisplayName("Debe denegar cuando roleId es null")
        void debeDenegarRoleIdNull() throws IOException {
            // Given
            when(request.getAttribute("roleId")).thenReturn(null);
            when(request.getAttribute("userId")).thenReturn(1L);
            
            Set<Integer> rolesPermitidos = Set.of(1, 2, 3);
            
            // When
            boolean autorizado = AuthorizationHelper.checkRoles(request, response, rolesPermitidos);
            
            // Then
            assertThat(autorizado).isFalse();
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        
        @Test
        @DisplayName("Debe denegar cuando userId es null")
        void debeDenegarUserIdNull() throws IOException {
            // Given
            when(request.getAttribute("roleId")).thenReturn(1);
            when(request.getAttribute("userId")).thenReturn(null);
            
            Set<Integer> rolesPermitidos = Set.of(1, 2, 3);
            
            // When
            boolean autorizado = AuthorizationHelper.checkRoles(request, response, rolesPermitidos);
            
            // Then
            assertThat(autorizado).isFalse();
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
    
    @Nested
    @DisplayName("Verificación de ADMIN - isAdmin")
    class VerificacionAdmin {
        
        @Test
        @DisplayName("Debe retornar true para rol ADMIN")
        void debeRetornarTrueParaAdmin() throws IOException {
            // Given
            when(request.getAttribute("roleId")).thenReturn(RolePermissions.ADMIN);
            
            // When
            boolean esAdmin = AuthorizationHelper.isAdmin(request, response);
            
            // Then
            assertThat(esAdmin).isTrue();
            verify(response, never()).setStatus(anyInt());
        }
        
        @Test
        @DisplayName("Debe retornar false para rol no ADMIN")
        void debeRetornarFalseParaNoAdmin() throws IOException {
            // Given
            when(request.getAttribute("roleId")).thenReturn(RolePermissions.CAJERO);
            
            // When
            boolean esAdmin = AuthorizationHelper.isAdmin(request, response);
            
            // Then
            assertThat(esAdmin).isFalse();
            verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
        
        @Test
        @DisplayName("Debe retornar false cuando roleId es null")
        void debeRetornarFalseSiRoleIdNull() throws IOException {
            // Given
            when(request.getAttribute("roleId")).thenReturn(null);
            
            // When
            boolean esAdmin = AuthorizationHelper.isAdmin(request, response);
            
            // Then
            assertThat(esAdmin).isFalse();
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
    
    @Nested
    @DisplayName("Verificación de Privilegios Administrativos - hasAdminPrivileges")
    class VerificacionPrivilegiosAdministrativos {
        
        @Test
        @DisplayName("Debe retornar true para ADMIN")
        void debeRetornarTrueParaAdmin() throws IOException {
            // Given
            when(request.getAttribute("roleId")).thenReturn(RolePermissions.ADMIN);
            
            // When
            boolean tienePrivilegios = AuthorizationHelper.hasAdminPrivileges(request, response);
            
            // Then
            assertThat(tienePrivilegios).isTrue();
            verify(response, never()).setStatus(anyInt());
        }
        
        @Test
        @DisplayName("Debe retornar true para DIRECTOR")
        void debeRetornarTrueParaDirector() throws IOException {
            // Given
            when(request.getAttribute("roleId")).thenReturn(RolePermissions.DIRECTOR);
            
            // When
            boolean tienePrivilegios = AuthorizationHelper.hasAdminPrivileges(request, response);
            
            // Then
            assertThat(tienePrivilegios).isTrue();
            verify(response, never()).setStatus(anyInt());
        }
        
        @Test
        @DisplayName("Debe retornar false para roles sin privilegios administrativos")
        void debeRetornarFalseParaRolesSinPrivilegios() throws IOException {
            // Given
            when(request.getAttribute("roleId")).thenReturn(RolePermissions.CAJERO);
            
            // When
            boolean tienePrivilegios = AuthorizationHelper.hasAdminPrivileges(request, response);
            
            // Then
            assertThat(tienePrivilegios).isFalse();
            verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
        
        @Test
        @DisplayName("Debe retornar false cuando roleId es null")
        void debeRetornarFalseSiRoleIdNull() throws IOException {
            // Given
            when(request.getAttribute("roleId")).thenReturn(null);
            
            // When
            boolean tienePrivilegios = AuthorizationHelper.hasAdminPrivileges(request, response);
            
            // Then
            assertThat(tienePrivilegios).isFalse();
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
    
    @Nested
    @DisplayName("Obtención de Datos del Usuario Actual")
    class ObtenerDatosUsuario {
        
        @Test
        @DisplayName("Debe obtener el roleId actual")
        void debeObtenerRoleIdActual() {
            // Given
            when(request.getAttribute("roleId")).thenReturn(2);
            
            // When
            Integer roleId = AuthorizationHelper.getCurrentRoleId(request);
            
            // Then
            assertThat(roleId).isEqualTo(2);
        }
        
        @Test
        @DisplayName("Debe obtener el userId actual")
        void debeObtenerUserIdActual() {
            // Given
            when(request.getAttribute("userId")).thenReturn(123L);
            
            // When
            Long userId = AuthorizationHelper.getCurrentUserId(request);
            
            // Then
            assertThat(userId).isEqualTo(123L);
        }
        
        @Test
        @DisplayName("Debe obtener el email del usuario actual")
        void debeObtenerEmailActual() {
            // Given
            when(request.getAttribute("userEmail")).thenReturn("usuario@test.com");
            
            // When
            String email = AuthorizationHelper.getCurrentUserEmail(request);
            
            // Then
            assertThat(email).isEqualTo("usuario@test.com");
        }
        
        @Test
        @DisplayName("Debe retornar null cuando roleId no existe")
        void debeRetornarNullSiRoleIdNoExiste() {
            // Given
            when(request.getAttribute("roleId")).thenReturn(null);
            
            // When
            Integer roleId = AuthorizationHelper.getCurrentRoleId(request);
            
            // Then
            assertThat(roleId).isNull();
        }
        
        @Test
        @DisplayName("Debe retornar null cuando userId no existe")
        void debeRetornarNullSiUserIdNoExiste() {
            // Given
            when(request.getAttribute("userId")).thenReturn(null);
            
            // When
            Long userId = AuthorizationHelper.getCurrentUserId(request);
            
            // Then
            assertThat(userId).isNull();
        }
        
        @Test
        @DisplayName("Debe retornar null cuando email no existe")
        void debeRetornarNullSiEmailNoExiste() {
            // Given
            when(request.getAttribute("userEmail")).thenReturn(null);
            
            // When
            String email = AuthorizationHelper.getCurrentUserEmail(request);
            
            // Then
            assertThat(email).isNull();
        }
    }
    
    @Nested
    @DisplayName("Verificación de Propietario de Recurso - isResourceOwner")
    class VerificacionPropietarioRecurso {
        
        @Test
        @DisplayName("Debe autorizar cuando el usuario es propietario del recurso")
        void debeAutorizarPropietario() throws IOException {
            // Given
            when(request.getAttribute("userId")).thenReturn(1L);
            when(request.getAttribute("roleId")).thenReturn(RolePermissions.CAJERO);
            Long resourceOwnerId = 1L;
            
            // When
            boolean esPropietario = AuthorizationHelper.isResourceOwner(request, response, resourceOwnerId);
            
            // Then
            assertThat(esPropietario).isTrue();
            verify(response, never()).setStatus(anyInt());
        }
        
        @Test
        @DisplayName("Debe denegar cuando el usuario no es propietario")
        void debeDenegarNoPropietario() throws IOException {
            // Given
            when(request.getAttribute("userId")).thenReturn(1L);
            when(request.getAttribute("roleId")).thenReturn(RolePermissions.CAJERO);
            Long resourceOwnerId = 2L; // Recurso de otro usuario
            
            // When
            boolean esPropietario = AuthorizationHelper.isResourceOwner(request, response, resourceOwnerId);
            
            // Then
            assertThat(esPropietario).isFalse();
            verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
        
        @Test
        @DisplayName("Debe autorizar cuando el usuario es ADMIN aunque no sea propietario")
        void debeAutorizarAdminSiempreWithRolAdministrativo() throws IOException {
            // Given
            when(request.getAttribute("userId")).thenReturn(1L);
            when(request.getAttribute("roleId")).thenReturn(RolePermissions.ADMIN);
            Long resourceOwnerId = 2L; // Recurso de otro usuario
            
            // When
            boolean esPropietario = AuthorizationHelper.isResourceOwner(request, response, resourceOwnerId);
            
            // Then
            assertThat(esPropietario).isTrue();
            verify(response, never()).setStatus(anyInt());
        }
        
        @Test
        @DisplayName("Debe autorizar cuando el usuario es DIRECTOR aunque no sea propietario")
        void debeAutorizarDirectorSiempre() throws IOException {
            // Given
            when(request.getAttribute("userId")).thenReturn(1L);
            when(request.getAttribute("roleId")).thenReturn(RolePermissions.DIRECTOR);
            Long resourceOwnerId = 2L; // Recurso de otro usuario
            
            // When
            boolean esPropietario = AuthorizationHelper.isResourceOwner(request, response, resourceOwnerId);
            
            // Then
            assertThat(esPropietario).isTrue();
            verify(response, never()).setStatus(anyInt());
        }
        
        @Test
        @DisplayName("Debe denegar cuando userId es null")
        void debeDenegarUserIdNull() throws IOException {
            // Given
            when(request.getAttribute("userId")).thenReturn(null);
            when(request.getAttribute("roleId")).thenReturn(RolePermissions.CAJERO);
            Long resourceOwnerId = 1L;
            
            // When
            boolean esPropietario = AuthorizationHelper.isResourceOwner(request, response, resourceOwnerId);
            
            // Then
            assertThat(esPropietario).isFalse();
            verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
    
    @Nested
    @DisplayName("Casos Edge y Manejo de Errores")
    class CasosEdge {
        
        @Test
        @DisplayName("Debe manejar roles con valores extremos")
        void debeManejarRolesExtremos() throws IOException {
            // Given
            when(request.getAttribute("roleId")).thenReturn(999);
            when(request.getAttribute("userId")).thenReturn(1L);
            
            Set<Integer> rolesPermitidos = Set.of(1, 2, 3);
            
            // When
            boolean autorizado = AuthorizationHelper.checkRoles(request, response, rolesPermitidos);
            
            // Then
            assertThat(autorizado).isFalse();
        }
        
        @Test
        @DisplayName("Debe manejar lista de roles vacía")
        void debeManejarListaRolesVacia() throws IOException {
            // Given
            when(request.getAttribute("roleId")).thenReturn(1);
            when(request.getAttribute("userId")).thenReturn(1L);
            
            Set<Integer> rolesPermitidos = Set.of();
            
            // When
            boolean autorizado = AuthorizationHelper.checkRoles(request, response, rolesPermitidos);
            
            // Then
            assertThat(autorizado).isFalse();
        }
        
        @Test
        @DisplayName("Debe manejar múltiples roles permitidos correctamente")
        void debeManejarMultiplesRolesPermitidos() throws IOException {
            // Given
            when(request.getAttribute("roleId")).thenReturn(5); // GERENTE
            when(request.getAttribute("userId")).thenReturn(1L);
            when(request.getAttribute("userEmail")).thenReturn("gerente@test.com");
            
            Set<Integer> rolesPermitidos = Set.of(1, 2, 3, 4, 5, 6); // Varios roles
            
            // When
            boolean autorizado = AuthorizationHelper.checkRoles(request, response, rolesPermitidos);
            
            // Then
            assertThat(autorizado).isTrue();
        }
    }
}
