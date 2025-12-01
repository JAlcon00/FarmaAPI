package security;

import model.Usuario;
import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para JwtTokenProvider
 */
@DisplayName("JwtTokenProvider Tests")
class JwtTokenProviderTest {
    
    private JwtTokenProvider jwtTokenProvider;
    private Usuario usuarioTest;
    
    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        
        // Inyectar valores manualmente (como si fueran @Value de Spring)
        ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", 
            "MiFarmaControlSecretKeyParaJWT2025DebeSerLargaYSegura256BitsMinimo");
        ReflectionTestUtils.setField(jwtTokenProvider, "expirationTime", 86400000L);
        
        // Usuario de prueba
        usuarioTest = new Usuario();
        usuarioTest.setId(1L);
        usuarioTest.setEmail("test@farmacontrol.com");
        usuarioTest.setNombre("Test");
        usuarioTest.setApellido("Usuario");
        usuarioTest.setRolId(2); // FARMACEUTICO
    }
    
    @Nested
    @DisplayName("Generación de Tokens")
    class GeneracionTokens {
        
        @Test
        @DisplayName("Debe generar un token válido")
        void debeGenerarTokenValido() {
            // When
            String token = jwtTokenProvider.generateToken(usuarioTest);
            
            // Then
            assertThat(token).isNotNull();
            assertThat(token).isNotEmpty();
            assertThat(token.split("\\.")).hasSize(3); // JWT tiene 3 partes: header.payload.signature
        }
        
        @Test
        @DisplayName("Debe generar tokens únicos para llamadas diferentes")
        void debeGenerarTokensUnicos() throws InterruptedException {
            // Given
            String token1 = jwtTokenProvider.generateToken(usuarioTest);
            Thread.sleep(1100); // Esperar más de 1 segundo para que 'iat' sea diferente
            
            // When
            String token2 = jwtTokenProvider.generateToken(usuarioTest);
            
            // Then
            assertThat(token1).isNotEqualTo(token2);
        }
    }
    
    @Nested
    @DisplayName("Validación de Tokens")
    class ValidacionTokens {
        
        @Test
        @DisplayName("Debe validar un token correcto")
        void debeValidarTokenCorrecto() {
            // Given
            String token = jwtTokenProvider.generateToken(usuarioTest);
            
            // When
            boolean esValido = jwtTokenProvider.validateToken(token);
            
            // Then
            assertThat(esValido).isTrue();
        }
        
        @Test
        @DisplayName("Debe rechazar un token malformado")
        void debeRechazarTokenMalformado() {
            // Given
            String tokenMalformado = "token.invalido.aqui";
            
            // When
            boolean esValido = jwtTokenProvider.validateToken(tokenMalformado);
            
            // Then
            assertThat(esValido).isFalse();
        }
        
        @Test
        @DisplayName("Debe rechazar un token vacío")
        void debeRechazarTokenVacio() {
            // When
            boolean esValido = jwtTokenProvider.validateToken("");
            
            // Then
            assertThat(esValido).isFalse();
        }
    }
    
    @Nested
    @DisplayName("Extracción de Claims")
    class ExtraccionClaims {
        
        @Test
        @DisplayName("Debe extraer el email del token")
        void debeExtraerEmail() {
            // Given
            String token = jwtTokenProvider.generateToken(usuarioTest);
            
            // When
            String email = jwtTokenProvider.getEmailFromToken(token);
            
            // Then
            assertThat(email).isEqualTo("test@farmacontrol.com");
        }
        
        @Test
        @DisplayName("Debe extraer el userId del token")
        void debeExtraerUserId() {
            // Given
            String token = jwtTokenProvider.generateToken(usuarioTest);
            
            // When
            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            
            // Then
            assertThat(userId).isEqualTo(1L);
        }
        
        @Test
        @DisplayName("Debe extraer el roleId del token")
        void debeExtraerRoleId() {
            // Given
            String token = jwtTokenProvider.generateToken(usuarioTest);
            
            // When
            Integer roleId = jwtTokenProvider.getRoleIdFromToken(token);
            
            // Then
            assertThat(roleId).isEqualTo(2);
        }
    }
    
    @Nested
    @DisplayName("Expiración de Tokens")
    class ExpiracionTokens {
        
        @Test
        @DisplayName("Debe detectar que un token nuevo no está expirado")
        void debeDetectarTokenNoExpirado() {
            // Given
            String token = jwtTokenProvider.generateToken(usuarioTest);
            
            // When
            boolean estaExpirado = jwtTokenProvider.isTokenExpired(token);
            
            // Then
            assertThat(estaExpirado).isFalse();
        }
    }
    
    @Nested
    @DisplayName("Casos Edge")
    class CasosEdge {
        
        @Test
        @DisplayName("Debe manejar usuario con campos mínimos")
        void debeManejarUsuarioMinimo() {
            // Given
            Usuario usuarioMinimo = new Usuario();
            usuarioMinimo.setId(1L);
            usuarioMinimo.setEmail("min@test.com");
            usuarioMinimo.setRolId(1);
            
            // When
            String token = jwtTokenProvider.generateToken(usuarioMinimo);
            
            // Then
            assertThat(token).isNotNull();
            assertThat(jwtTokenProvider.validateToken(token)).isTrue();
            assertThat(jwtTokenProvider.getEmailFromToken(token)).isEqualTo("min@test.com");
        }
    }
    
    @Nested
    @DisplayName("Extracción de Token desde Header")
    class ExtraccionDesdeHeader {
        
        @Test
        @DisplayName("Debe extraer token de header Authorization válido")
        void debeExtraerTokenDeHeaderValido() {
            // Given
            String token = jwtTokenProvider.generateToken(usuarioTest);
            String authHeader = "Bearer " + token;
            
            // When
            String extractedToken = jwtTokenProvider.extractTokenFromHeader(authHeader);
            
            // Then
            assertThat(extractedToken).isEqualTo(token);
        }
        
        @Test
        @DisplayName("Debe retornar null si el header no empieza con Bearer")
        void debeRetornarNullSiHeaderInvalido() {
            // Given
            String authHeader = "Basic sometoken123";
            
            // When
            String extractedToken = jwtTokenProvider.extractTokenFromHeader(authHeader);
            
            // Then
            assertThat(extractedToken).isNull();
        }
        
        @Test
        @DisplayName("Debe retornar null si el header es null")
        void debeRetornarNullSiHeaderEsNull() {
            // When
            String extractedToken = jwtTokenProvider.extractTokenFromHeader(null);
            
            // Then
            assertThat(extractedToken).isNull();
        }
        
        @Test
        @DisplayName("Debe retornar null si el header está vacío")
        void debeRetornarNullSiHeaderEstaVacio() {
            // When
            String extractedToken = jwtTokenProvider.extractTokenFromHeader("");
            
            // Then
            assertThat(extractedToken).isNull();
        }
        
        @Test
        @DisplayName("Debe retornar null si el header solo dice Bearer sin token")
        void debeRetornarNullSiBearerSinToken() {
            // When
            String extractedToken = jwtTokenProvider.extractTokenFromHeader("Bearer ");
            
            // Then
            assertThat(extractedToken).isEmpty();
        }
    }
    
    @Nested
    @DisplayName("Validación de Tokens con Firma Incorrecta")
    class TokensConFirmaIncorrecta {
        
        @Test
        @DisplayName("Debe rechazar token con firma modificada")
        void debeRechazarTokenConFirmaModificada() {
            // Given
            String token = jwtTokenProvider.generateToken(usuarioTest);
            String tokenModificado = token.substring(0, token.length() - 10) + "XXXXmodified";
            
            // When
            boolean esValido = jwtTokenProvider.validateToken(tokenModificado);
            
            // Then
            assertThat(esValido).isFalse();
        }
        
        @Test
        @DisplayName("Debe rechazar token null")
        void debeRechazarTokenNull() {
            // When
            boolean esValido = jwtTokenProvider.validateToken(null);
            
            // Then
            assertThat(esValido).isFalse();
        }
    }
    
    @Nested
    @DisplayName("Múltiples Usuarios y Roles")
    class MultipleUsuariosYRoles {
        
        @Test
        @DisplayName("Debe generar tokens diferentes para usuarios diferentes")
        void debeGenerarTokensDiferentesParaUsuariosDiferentes() {
            // Given
            Usuario usuario1 = new Usuario();
            usuario1.setId(1L);
            usuario1.setEmail("user1@test.com");
            usuario1.setRolId(1);
            
            Usuario usuario2 = new Usuario();
            usuario2.setId(2L);
            usuario2.setEmail("user2@test.com");
            usuario2.setRolId(2);
            
            // When
            String token1 = jwtTokenProvider.generateToken(usuario1);
            String token2 = jwtTokenProvider.generateToken(usuario2);
            
            // Then
            assertThat(token1).isNotEqualTo(token2);
            assertThat(jwtTokenProvider.getUserIdFromToken(token1)).isEqualTo(1L);
            assertThat(jwtTokenProvider.getUserIdFromToken(token2)).isEqualTo(2L);
        }
        
        @Test
        @DisplayName("Debe mantener el roleId correcto en el token")
        void debeMantenerRoleIdCorrecto() {
            // Given
            Usuario admin = new Usuario();
            admin.setId(1L);
            admin.setEmail("admin@test.com");
            admin.setRolId(1); // ADMIN
            
            Usuario cajero = new Usuario();
            cajero.setId(2L);
            cajero.setEmail("cajero@test.com");
            cajero.setRolId(3); // CAJERO
            
            // When
            String tokenAdmin = jwtTokenProvider.generateToken(admin);
            String tokenCajero = jwtTokenProvider.generateToken(cajero);
            
            // Then
            assertThat(jwtTokenProvider.getRoleIdFromToken(tokenAdmin)).isEqualTo(1);
            assertThat(jwtTokenProvider.getRoleIdFromToken(tokenCajero)).isEqualTo(3);
        }
        
        @Test
        @DisplayName("Debe manejar correctamente usuarios con emails largos")
        void debeManejarEmailsLargos() {
            // Given
            Usuario usuario = new Usuario();
            usuario.setId(1L);
            usuario.setEmail("usuario.con.un.email.muy.largo.para.probar@farmaceutica-control-sistema.com");
            usuario.setRolId(2);
            
            // When
            String token = jwtTokenProvider.generateToken(usuario);
            String email = jwtTokenProvider.getEmailFromToken(token);
            
            // Then
            assertThat(email).isEqualTo("usuario.con.un.email.muy.largo.para.probar@farmaceutica-control-sistema.com");
        }
    }
    
    @Nested
    @DisplayName("Manejo de Errores en Extracción de Claims")
    class ManejoErroresExtraccionClaims {
        
        @Test
        @DisplayName("Debe lanzar excepción al extraer email de token inválido")
        void debeLanzarExcepcionAlExtraerEmailDeTokenInvalido() {
            // Given
            String tokenInvalido = "invalid.token.here";
            
            // When/Then
            assertThatThrownBy(() -> jwtTokenProvider.getEmailFromToken(tokenInvalido))
                .isInstanceOf(Exception.class);
        }
        
        @Test
        @DisplayName("Debe lanzar excepción al extraer userId de token inválido")
        void debeLanzarExcepcionAlExtraerUserIdDeTokenInvalido() {
            // Given
            String tokenInvalido = "invalid.token.here";
            
            // When/Then
            assertThatThrownBy(() -> jwtTokenProvider.getUserIdFromToken(tokenInvalido))
                .isInstanceOf(Exception.class);
        }
        
        @Test
        @DisplayName("Debe lanzar excepción al extraer roleId de token inválido")
        void debeLanzarExcepcionAlExtraerRoleIdDeTokenInvalido() {
            // Given
            String tokenInvalido = "invalid.token.here";
            
            // When/Then
            assertThatThrownBy(() -> jwtTokenProvider.getRoleIdFromToken(tokenInvalido))
                .isInstanceOf(Exception.class);
        }
        
        @Test
        @DisplayName("Debe retornar true al verificar expiración de token inválido")
        void debeRetornarTrueAlVerificarExpiracionDeTokenInvalido() {
            // Given
            String tokenInvalido = "invalid.token.here";
            
            // When
            boolean estaExpirado = jwtTokenProvider.isTokenExpired(tokenInvalido);
            
            // Then
            assertThat(estaExpirado).isTrue();
        }
    }
}
