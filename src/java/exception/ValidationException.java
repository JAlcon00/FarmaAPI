package exception;

/**
 * Excepción para errores de validación de datos (400)
 */
public class ValidationException extends RuntimeException {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String field, String error) {
        super("Error en campo '" + field + "': " + error);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
