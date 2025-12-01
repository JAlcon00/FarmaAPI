package exception;

/**
 * Excepción para acceso prohibido por falta de permisos (403)
 */
public class ForbiddenException extends RuntimeException {
    
    public ForbiddenException(String message) {
        super(message);
    }
    
    public ForbiddenException(String action, String resource) {
        super("No tiene permisos para " + action + " en " + resource);
    }
    
    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
