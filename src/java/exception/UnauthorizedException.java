package exception;

/**
 * Excepción para acceso no autorizado (401)
 */
public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException(String message) {
        super(message);
    }
    
    public UnauthorizedException() {
        super("Token inválido o expirado. Por favor, inicie sesión nuevamente.");
    }
    
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
