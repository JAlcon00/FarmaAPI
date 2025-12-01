package exception;

/**
 * Excepción para recursos no encontrados (404)
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resourceType, Long id) {
        super(resourceType + " con ID " + id + " no encontrado");
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
