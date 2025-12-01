package services;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio para validar objetos usando Jakarta Bean Validation.
 * Permite usar validaciones declarativas (@NotNull, @Size, etc.) programáticamente.
 * 
 * @author FarmaControl Team
 * @version 1.0
 */
public class ValidationService {
    
    private static final Validator validator;
    
    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    /**
     * Valida un objeto y lanza IllegalArgumentException si hay errores.
     * 
     * @param object Objeto a validar
     * @param <T> Tipo del objeto
     * @throws IllegalArgumentException si hay violaciones de validación
     */
    public static <T> void validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
            
            throw new IllegalArgumentException(errorMessage);
        }
    }
    
    /**
     * Valida un objeto y retorna los mensajes de error.
     * 
     * @param object Objeto a validar
     * @param <T> Tipo del objeto
     * @return Set de mensajes de error, vacío si no hay errores
     */
    public static <T> Set<String> getValidationErrors(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        
        return violations.stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.toSet());
    }
    
    /**
     * Verifica si un objeto es válido.
     * 
     * @param object Objeto a validar
     * @param <T> Tipo del objeto
     * @return true si es válido, false si tiene errores
     */
    public static <T> boolean isValid(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        return violations.isEmpty();
    }
    
    /**
     * Valida un objeto y retorna un mensaje formateado con todos los errores.
     * 
     * @param object Objeto a validar
     * @param <T> Tipo del objeto
     * @return Mensaje con todos los errores, o null si no hay errores
     */
    public static <T> String getFormattedErrors(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        
        if (violations.isEmpty()) {
            return null;
        }
        
        return "Errores de validación: " + violations.stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
            .collect(Collectors.joining("; "));
    }
}
