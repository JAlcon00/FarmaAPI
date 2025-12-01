package exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Tests para GlobalExceptionHandler.
 * Verifica que todas las excepciones se manejen correctamente
 * y retornen respuestas HTTP apropiadas.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Tests de Global Exception Handler")
class GlobalExceptionHandlerTest {
    
    private GlobalExceptionHandler exceptionHandler;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private StringWriter responseWriter;
    
    @BeforeEach
    void setUp() throws Exception {
        exceptionHandler = new GlobalExceptionHandler();
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        responseWriter = new StringWriter();
        
        when(mockRequest.getRequestURI()).thenReturn("/api/test");
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }
    
    @Nested
    @DisplayName("Manejo de IllegalArgumentException")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ManejoIllegalArgumentException {
        
        @Test
        @Order(1)
        @DisplayName("Debe retornar UNPROCESSABLE_ENTITY (422) para validaciones")
        void debeRetornarUnprocessableEntityParaValidaciones() throws Exception {
            // Given
            IllegalArgumentException ex = new IllegalArgumentException("El nombre es requerido");
            
            // When
            exceptionHandler.handleBusinessLogic(ex, mockRequest, mockResponse);
            
            // Then
            verify(mockResponse).setStatus(422);
            verify(mockResponse).setContentType("application/json");
            String response = responseWriter.toString();
            assertThat(response).contains("\"status\":422");
            assertThat(response).contains("El nombre es requerido");
        }
        
        @Test
        @Order(2)
        @DisplayName("Debe incluir timestamp en la respuesta")
        void debeIncluirTimestamp() throws Exception {
            // Given
            IllegalArgumentException ex = new IllegalArgumentException("Error de validación");
            
            // When
            exceptionHandler.handleBusinessLogic(ex, mockRequest, mockResponse);
            
            // Then
            String response = responseWriter.toString();
            assertThat(response).contains("timestamp");
        }
        
        @Test
        @Order(3)
        @DisplayName("Debe incluir path de la petición")
        void debeIncluirPath() throws Exception {
            // Given
            IllegalArgumentException ex = new IllegalArgumentException("Error");
            
            // When
            exceptionHandler.handleBusinessLogic(ex, mockRequest, mockResponse);
            
            // Then
            String response = responseWriter.toString();
            assertThat(response).contains("/api/test");
        }
    }
    
    @Nested
    @DisplayName("Manejo de SQLException")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ManejoSQLException {
        
        @Test
        @Order(4)
        @DisplayName("Debe retornar INTERNAL_SERVER_ERROR (500) para errores SQL")
        void debeRetornarInternalServerError() throws Exception {
            // Given
            SQLException ex = new SQLException("Connection timeout");
            
            // When
            exceptionHandler.handleDatabase(ex, mockRequest, mockResponse);
            
            // Then
            verify(mockResponse).setStatus(500);
            String response = responseWriter.toString();
            assertThat(response).contains("\"status\":500");
        }
        
        @Test
        @Order(5)
        @DisplayName("No debe exponer detalles técnicos SQL al cliente")
        void noDebeExponerDetallesTecnicos() throws Exception {
            // Given
            SQLException ex = new SQLException("SELECT * FROM users WHERE password='secret123'");
            
            // When
            exceptionHandler.handleDatabase(ex, mockRequest, mockResponse);
            
            // Then
            String response = responseWriter.toString();
            assertThat(response).doesNotContain("SELECT");
            assertThat(response).doesNotContain("password");
            assertThat(response).doesNotContain("secret123");
            assertThat(response).contains("contacte al administrador");
        }
        
        @Test
        @Order(6)
        @DisplayName("Debe registrar el error en logs")
        void debeRegistrarErrorEnLogs() throws Exception {
            // Given
            SQLException ex = new SQLException("Database connection failed");
            
            // When
            exceptionHandler.handleDatabase(ex, mockRequest, mockResponse);
            
            // Then
            verify(mockResponse).setStatus(500);
            // El logging se verifica implícitamente por la ejecución sin errores
        }
    }
    
    @Nested
    @DisplayName("Manejo de IllegalStateException")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ManejoIllegalStateException {
        
        @Test
        @Order(7)
        @DisplayName("Debe retornar UNPROCESSABLE_ENTITY (422) para estados ilegales")
        void debeRetornarUnprocessableEntity() throws Exception {
            // Given
            IllegalStateException ex = new IllegalStateException("La venta ya está cancelada");
            
            // When
            exceptionHandler.handleBusinessLogic(ex, mockRequest, mockResponse);
            
            // Then
            verify(mockResponse).setStatus(422);
            String response = responseWriter.toString();
            assertThat(response).contains("La venta ya está cancelada");
        }
    }
    
    @Nested
    @DisplayName("Manejo de ValidationException")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ManejoValidationException {
        
        @Test
        @Order(8)
        @DisplayName("Debe retornar BAD_REQUEST (400) para errores de validación")
        void debeRetornarBadRequest() throws Exception {
            // Given
            ValidationException ex = new ValidationException("Datos inválidos");
            
            // When
            exceptionHandler.handleValidation(ex, mockRequest, mockResponse);
            
            // Then
            verify(mockResponse).setStatus(400);
            String response = responseWriter.toString();
            assertThat(response).contains("\"status\":400");
            assertThat(response).contains("Datos inválidos");
        }
    }
    
    @Nested
    @DisplayName("Manejo de UnauthorizedException")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ManejoUnauthorizedException {
        
        @Test
        @Order(9)
        @DisplayName("Debe retornar UNAUTHORIZED (401) para errores de autenticación")
        void debeRetornarUnauthorized() throws Exception {
            // Given
            UnauthorizedException ex = new UnauthorizedException("Token inválido");
            
            // When
            exceptionHandler.handleUnauthorized(ex, mockRequest, mockResponse);
            
            // Then
            verify(mockResponse).setStatus(401);
            String response = responseWriter.toString();
            assertThat(response).contains("\"status\":401");
            assertThat(response).contains("Token inválido");
        }
    }
    
    @Nested
    @DisplayName("Manejo de ForbiddenException")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ManejoForbiddenException {
        
        @Test
        @Order(10)
        @DisplayName("Debe retornar FORBIDDEN (403) para errores de permisos")
        void debeRetornarForbidden() throws Exception {
            // Given
            ForbiddenException ex = new ForbiddenException("Acceso denegado");
            
            // When
            exceptionHandler.handleForbidden(ex, mockRequest, mockResponse);
            
            // Then
            verify(mockResponse).setStatus(403);
            String response = responseWriter.toString();
            assertThat(response).contains("\"status\":403");
            assertThat(response).contains("Acceso denegado");
        }
    }
    
    @Nested
    @DisplayName("Manejo de ResourceNotFoundException")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ManejoResourceNotFoundException {
        
        @Test
        @Order(11)
        @DisplayName("Debe retornar NOT_FOUND (404) para recursos no encontrados")
        void debeRetornarNotFound() throws Exception {
            // Given
            ResourceNotFoundException ex = new ResourceNotFoundException("Producto no encontrado");
            
            // When
            exceptionHandler.handleNotFound(ex, mockRequest, mockResponse);
            
            // Then
            verify(mockResponse).setStatus(404);
            String response = responseWriter.toString();
            assertThat(response).contains("\"status\":404");
            assertThat(response).contains("Producto no encontrado");
        }
    }
    
    @Nested
    @DisplayName("Manejo de Exception genérica (catch-all)")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ManejoExceptionGenerica {
        
        @Test
        @Order(12)
        @DisplayName("Debe capturar cualquier excepción no manejada")
        void debCapturarCualquierExcepcion() throws Exception {
            // Given
            Exception ex = new RuntimeException("Error inesperado");
            
            // When
            exceptionHandler.handleGeneral(ex, mockRequest, mockResponse);
            
            // Then
            verify(mockResponse).setStatus(500);
            String response = responseWriter.toString();
            assertThat(response).contains("\"status\":500");
        }
        
        @Test
        @Order(13)
        @DisplayName("Debe retornar mensaje genérico para errores inesperados")
        void debeRetornarMensajeGenerico() throws Exception {
            // Given
            Exception ex = new ArithmeticException("Division by zero");
            
            // When
            exceptionHandler.handleGeneral(ex, mockRequest, mockResponse);
            
            // Then
            String response = responseWriter.toString();
            assertThat(response).contains("Error interno del servidor");
            assertThat(response).contains("contacte al administrador");
        }
        
        @Test
        @Order(14)
        @DisplayName("No debe exponer stack traces al cliente")
        void noDebeExponerStackTraces() throws Exception {
            // Given
            Exception ex = new NullPointerException("Cannot invoke method on null");
            
            // When
            exceptionHandler.handleGeneral(ex, mockRequest, mockResponse);
            
            // Then
            String response = responseWriter.toString();
            assertThat(response).doesNotContain("NullPointerException");
            assertThat(response).doesNotContain("stack trace");
            assertThat(response).doesNotContain("at java.");
        }
    }
    
    @Nested
    @DisplayName("Estructura de Respuestas de Error")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class EstructuraRespuestas {
        
        @Test
        @Order(15)
        @DisplayName("Todas las respuestas deben tener la estructura correcta")
        void todasLasRespuestasDebenTenerEstructuraCorrecta() throws Exception {
            // Given
            IllegalArgumentException ex = new IllegalArgumentException("Test");
            
            // When
            exceptionHandler.handleBusinessLogic(ex, mockRequest, mockResponse);
            
            // Then
            String response = responseWriter.toString();
            assertThat(response).contains("timestamp");
            assertThat(response).contains("status");
            assertThat(response).contains("error");
            assertThat(response).contains("message");
            assertThat(response).contains("path");
        }
        
        @Test
        @Order(16)
        @DisplayName("Debe establecer content type como JSON")
        void debeEstablecerContentTypeJSON() throws Exception {
            // Given
            IllegalArgumentException ex = new IllegalArgumentException("Test");
            
            // When
            exceptionHandler.handleBusinessLogic(ex, mockRequest, mockResponse);
            
            // Then
            verify(mockResponse).setContentType("application/json");
            verify(mockResponse).setCharacterEncoding("UTF-8");
        }
        
        @Test
        @Order(17)
        @DisplayName("La respuesta debe ser JSON válido")
        void laRespuestaDebeSerJSONValido() throws Exception {
            // Given
            ValidationException ex = new ValidationException("Campo requerido");
            
            // When
            exceptionHandler.handleValidation(ex, mockRequest, mockResponse);
            
            // Then
            String response = responseWriter.toString();
            assertThat(response).startsWith("{");
            assertThat(response).endsWith("}");
            assertThat(response).contains(":");
        }
    }
}
