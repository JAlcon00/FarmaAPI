package filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.Mockito.*;

/**
 * Tests unitarios para CORSFilter
 */
@DisplayName("CORSFilter Tests")
class CORSFilterTest {
    
    private CORSFilter corsFilter;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private FilterChain filterChain;
    
    private AutoCloseable closeable;
    
    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        corsFilter = new CORSFilter();
    }
    
    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
    
    @Nested
    @DisplayName("Configuración de Headers CORS")
    class ConfiguracionHeadersCORS {
        
        @Test
        @DisplayName("Debe configurar Access-Control-Allow-Origin")
        void debeConfigurarAccessControlAllowOrigin() throws IOException, ServletException {
            // Given
            when(request.getMethod()).thenReturn("GET");
            
            // When
            corsFilter.doFilter(request, response, filterChain);
            
            // Then
            verify(response).setHeader("Access-Control-Allow-Origin", "*");
        }
        
        @Test
        @DisplayName("Debe configurar Access-Control-Allow-Methods")
        void debeConfigurarAccessControlAllowMethods() throws IOException, ServletException {
            // Given
            when(request.getMethod()).thenReturn("GET");
            
            // When
            corsFilter.doFilter(request, response, filterChain);
            
            // Then
            verify(response).setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        }
        
        @Test
        @DisplayName("Debe configurar Access-Control-Allow-Headers")
        void debeConfigurarAccessControlAllowHeaders() throws IOException, ServletException {
            // Given
            when(request.getMethod()).thenReturn("GET");
            
            // When
            corsFilter.doFilter(request, response, filterChain);
            
            // Then
            verify(response).setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Accept");
        }
        
        @Test
        @DisplayName("Debe configurar Access-Control-Allow-Credentials")
        void debeConfigurarAccessControlAllowCredentials() throws IOException, ServletException {
            // Given
            when(request.getMethod()).thenReturn("GET");
            
            // When
            corsFilter.doFilter(request, response, filterChain);
            
            // Then
            verify(response).setHeader("Access-Control-Allow-Credentials", "true");
        }
        
        @Test
        @DisplayName("Debe configurar Access-Control-Max-Age")
        void debeConfigurarAccessControlMaxAge() throws IOException, ServletException {
            // Given
            when(request.getMethod()).thenReturn("GET");
            
            // When
            corsFilter.doFilter(request, response, filterChain);
            
            // Then
            verify(response).setHeader("Access-Control-Max-Age", "3600");
        }
    }
    
    @Nested
    @DisplayName("Manejo de Método OPTIONS")
    class ManejoMetodoOPTIONS {
        
        @Test
        @DisplayName("Debe responder 200 OK para peticiones OPTIONS")
        void debeResponder200ParaOPTIONS() throws IOException, ServletException {
            // Given
            when(request.getMethod()).thenReturn("OPTIONS");
            
            // When
            corsFilter.doFilter(request, response, filterChain);
            
            // Then
            verify(response).setStatus(HttpServletResponse.SC_OK);
            verify(filterChain, never()).doFilter(request, response);
        }
        
        @Test
        @DisplayName("No debe continuar la cadena de filtros para OPTIONS")
        void noDebeContinuarCadenaParaOPTIONS() throws IOException, ServletException {
            // Given
            when(request.getMethod()).thenReturn("OPTIONS");
            
            // When
            corsFilter.doFilter(request, response, filterChain);
            
            // Then
            verify(filterChain, never()).doFilter(request, response);
        }
        
        @Test
        @DisplayName("Debe configurar headers CORS antes de responder OPTIONS")
        void debeConfigurarHeadersAntesDeResponderOPTIONS() throws IOException, ServletException {
            // Given
            when(request.getMethod()).thenReturn("OPTIONS");
            
            // When
            corsFilter.doFilter(request, response, filterChain);
            
            // Then
            verify(response).setHeader("Access-Control-Allow-Origin", "*");
            verify(response).setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            verify(response).setStatus(HttpServletResponse.SC_OK);
        }
    }
    
    @Nested
    @DisplayName("Continuación de Cadena de Filtros")
    class ContinuacionCadenaFiltros {
        
        @Test
        @DisplayName("Debe continuar la cadena para peticiones GET")
        void debeContinuarCadenaParaGET() throws IOException, ServletException {
            // Given
            when(request.getMethod()).thenReturn("GET");
            
            // When
            corsFilter.doFilter(request, response, filterChain);
            
            // Then
            verify(filterChain).doFilter(request, response);
        }
        
        @Test
        @DisplayName("Debe continuar la cadena para peticiones POST")
        void debeContinuarCadenaParaPOST() throws IOException, ServletException {
            // Given
            when(request.getMethod()).thenReturn("POST");
            
            // When
            corsFilter.doFilter(request, response, filterChain);
            
            // Then
            verify(filterChain).doFilter(request, response);
        }
        
        @Test
        @DisplayName("Debe continuar la cadena para peticiones PUT")
        void debeContinuarCadenaParaPUT() throws IOException, ServletException {
            // Given
            when(request.getMethod()).thenReturn("PUT");
            
            // When
            corsFilter.doFilter(request, response, filterChain);
            
            // Then
            verify(filterChain).doFilter(request, response);
        }
        
        @Test
        @DisplayName("Debe continuar la cadena para peticiones DELETE")
        void debeContinuarCadenaParaDELETE() throws IOException, ServletException {
            // Given
            when(request.getMethod()).thenReturn("DELETE");
            
            // When
            corsFilter.doFilter(request, response, filterChain);
            
            // Then
            verify(filterChain).doFilter(request, response);
        }
    }
    
    @Nested
    @DisplayName("Manejo de Case-Insensitive")
    class ManejoCaseInsensitive {
        
        @Test
        @DisplayName("Debe manejar OPTIONS en minúsculas")
        void debeManejarOPTIONSEnMinusculas() throws IOException, ServletException {
            // Given
            when(request.getMethod()).thenReturn("options");
            
            // When
            corsFilter.doFilter(request, response, filterChain);
            
            // Then
            verify(response).setStatus(HttpServletResponse.SC_OK);
            verify(filterChain, never()).doFilter(request, response);
        }
        
        @Test
        @DisplayName("Debe manejar OPTIONS en mayúsculas")
        void debeManejarOPTIONSEnMayusculas() throws IOException, ServletException {
            // Given
            when(request.getMethod()).thenReturn("OPTIONS");
            
            // When
            corsFilter.doFilter(request, response, filterChain);
            
            // Then
            verify(response).setStatus(HttpServletResponse.SC_OK);
            verify(filterChain, never()).doFilter(request, response);
        }
        
        @Test
        @DisplayName("Debe manejar OPTIONS en MixedCase")
        void debeManejarOPTIONSEnMixedCase() throws IOException, ServletException {
            // Given
            when(request.getMethod()).thenReturn("Options");
            
            // When
            corsFilter.doFilter(request, response, filterChain);
            
            // Then
            verify(response).setStatus(HttpServletResponse.SC_OK);
            verify(filterChain, never()).doFilter(request, response);
        }
    }
    
    @Nested
    @DisplayName("Headers CORS en Todas las Peticiones")
    class HeadersCORSEnTodasPeticiones {
        
        @Test
        @DisplayName("Debe configurar headers CORS para GET")
        void debeConfigurarHeadersCORSParaGET() throws IOException, ServletException {
            // Given
            when(request.getMethod()).thenReturn("GET");
            
            // When
            corsFilter.doFilter(request, response, filterChain);
            
            // Then
            verify(response).setHeader("Access-Control-Allow-Origin", "*");
            verify(response).setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            verify(response).setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Accept");
        }
        
        @Test
        @DisplayName("Debe configurar headers CORS para POST")
        void debeConfigurarHeadersCORSParaPOST() throws IOException, ServletException {
            // Given
            when(request.getMethod()).thenReturn("POST");
            
            // When
            corsFilter.doFilter(request, response, filterChain);
            
            // Then
            verify(response).setHeader("Access-Control-Allow-Origin", "*");
            verify(response).setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            verify(response).setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Accept");
        }
        
        @Test
        @DisplayName("Debe configurar headers CORS para PUT")
        void debeConfigurarHeadersCORSParaPUT() throws IOException, ServletException {
            // Given
            when(request.getMethod()).thenReturn("PUT");
            
            // When
            corsFilter.doFilter(request, response, filterChain);
            
            // Then
            verify(response).setHeader("Access-Control-Allow-Origin", "*");
            verify(response).setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            verify(response).setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Accept");
        }
        
        @Test
        @DisplayName("Debe configurar headers CORS para DELETE")
        void debeConfigurarHeadersCORSParaDELETE() throws IOException, ServletException {
            // Given
            when(request.getMethod()).thenReturn("DELETE");
            
            // When
            corsFilter.doFilter(request, response, filterChain);
            
            // Then
            verify(response).setHeader("Access-Control-Allow-Origin", "*");
            verify(response).setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            verify(response).setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Accept");
        }
    }
}
