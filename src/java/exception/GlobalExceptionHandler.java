package exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manejador global de excepciones para FarmaControl API
 * Proporciona respuestas consistentes y estructuradas para todos los errores
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionHandler.class.getName());
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Maneja recursos no encontrados (404)
     */
    @ExceptionHandler({
        ResourceNotFoundException.class,
        NoHandlerFoundException.class
    })
    public void handleNotFound(Exception ex, HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        LOGGER.log(Level.WARNING, "Recurso no encontrado: {0}", ex.getMessage());
        
        sendErrorResponse(
            response,
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage() != null ? ex.getMessage() : "Recurso no encontrado",
            request.getRequestURI()
        );
    }
    
    /**
     * Maneja errores de validación (400)
     */
    @ExceptionHandler(ValidationException.class)
    public void handleValidation(ValidationException ex, HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        LOGGER.log(Level.WARNING, "Error de validación: {0}", ex.getMessage());
        
        sendErrorResponse(
            response,
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ex.getMessage(),
            request.getRequestURI()
        );
    }
    
    /**
     * Maneja errores de autenticación (401)
     */
    @ExceptionHandler(UnauthorizedException.class)
    public void handleUnauthorized(UnauthorizedException ex, HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        LOGGER.log(Level.WARNING, "Acceso no autorizado: {0}", ex.getMessage());
        
        sendErrorResponse(
            response,
            HttpStatus.UNAUTHORIZED.value(),
            "Unauthorized",
            ex.getMessage(),
            request.getRequestURI()
        );
    }
    
    /**
     * Maneja errores de permisos (403)
     */
    @ExceptionHandler(ForbiddenException.class)
    public void handleForbidden(ForbiddenException ex, HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        LOGGER.log(Level.WARNING, "Acceso prohibido: {0}", ex.getMessage());
        
        sendErrorResponse(
            response,
            HttpStatus.FORBIDDEN.value(),
            "Forbidden",
            ex.getMessage(),
            request.getRequestURI()
        );
    }
    
    /**
     * Maneja errores de lógica de negocio (422)
     */
    @ExceptionHandler({
        BusinessLogicException.class,
        IllegalArgumentException.class,
        IllegalStateException.class
    })
    public void handleBusinessLogic(Exception ex, HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        LOGGER.log(Level.WARNING, "Error de lógica de negocio: {0}", ex.getMessage());
        
        sendErrorResponse(
            response,
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            "Business Logic Error",
            ex.getMessage(),
            request.getRequestURI()
        );
    }
    
    /**
     * Maneja errores de base de datos (500) - NO expone detalles técnicos
     */
    @ExceptionHandler(SQLException.class)
    public void handleDatabase(SQLException ex, HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        LOGGER.log(Level.SEVERE, "Error de base de datos", ex);
        
        sendErrorResponse(
            response,
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Database Error",
            "Error al procesar la solicitud. Por favor contacte al administrador.",
            request.getRequestURI()
        );
    }
    
    /**
     * Catch-all para errores inesperados (500)
     */
    @ExceptionHandler(Exception.class)
    public void handleGeneral(Exception ex, HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        LOGGER.log(Level.SEVERE, "Error inesperado", ex);
        
        sendErrorResponse(
            response,
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "Error interno del servidor. Por favor contacte al administrador.",
            request.getRequestURI()
        );
    }
    
    /**
     * Método auxiliar para enviar respuestas de error consistentes
     */
    private void sendErrorResponse(
            HttpServletResponse response,
            int status,
            String error,
            String message,
            String path
    ) throws IOException {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", status);
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("path", path);
        
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }
}
