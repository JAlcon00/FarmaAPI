package services;

import dto.ProductoDTO;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests para ValidationService.
 * Verifica que las validaciones de Jakarta Bean Validation funcionen correctamente.
 * Se enfoca en ProductoDTO que tiene validaciones completas.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Tests de Validation Service")
class ValidationServiceTest {
    
    @Nested
    @DisplayName("Validación de ProductoDTO")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class ValidacionProductoDTO {
        
        @Test
        @Order(1)
        @DisplayName("Debe validar producto válido sin errores")
        void debeValidarProductoValido() {
            // Given
            ProductoDTO producto = new ProductoDTO();
            producto.setNombre("Paracetamol");
            producto.setDescripcion("Analgésico");
            producto.setPrecio(new BigDecimal("25.50"));
            producto.setStock(100);
            producto.setCategoriaId(1L);
            producto.setProveedorId(1L);
            producto.setEstado("activo");
            
            // When/Then
            assertThat(ValidationService.isValid(producto)).isTrue();
            assertThat(ValidationService.getValidationErrors(producto)).isEmpty();
        }
        
        @Test
        @Order(2)
        @DisplayName("Debe rechazar producto con nombre vacío")
        void debeRechazarProductoConNombreVacio() {
            // Given
            ProductoDTO producto = new ProductoDTO();
            producto.setNombre("");
            producto.setPrecio(new BigDecimal("25.50"));
            producto.setStock(100);
            producto.setCategoriaId(1L);
            producto.setProveedorId(1L);
            
            // When/Then
            assertThat(ValidationService.isValid(producto)).isFalse();
            assertThatThrownBy(() -> ValidationService.validate(producto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nombre");
        }
        
        @Test
        @Order(3)
        @DisplayName("Debe rechazar producto con nombre muy corto")
        void debeRechazarProductoConNombreMuyCorto() {
            // Given
            ProductoDTO producto = new ProductoDTO();
            producto.setNombre("AB"); // Menor a 3 caracteres
            producto.setPrecio(new BigDecimal("25.50"));
            producto.setStock(100);
            producto.setCategoriaId(1L);
            producto.setProveedorId(1L);
            
            // When
            Set<String> errors = ValidationService.getValidationErrors(producto);
            
            // Then
            assertThat(errors).isNotEmpty();
            assertThat(errors.toString()).containsIgnoringCase("caracteres");
        }
        
        @Test
        @Order(4)
        @DisplayName("Debe rechazar producto con precio negativo")
        void debeRechazarProductoConPrecioNegativo() {
            // Given
            ProductoDTO producto = new ProductoDTO();
            producto.setNombre("Paracetamol");
            producto.setPrecio(new BigDecimal("-10.00"));
            producto.setStock(100);
            producto.setCategoriaId(1L);
            producto.setProveedorId(1L);
            
            // When/Then
            assertThatThrownBy(() -> ValidationService.validate(producto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("precio");
        }
        
        @Test
        @Order(5)
        @DisplayName("Debe rechazar producto con stock negativo")
        void debeRechazarProductoConStockNegativo() {
            // Given
            ProductoDTO producto = new ProductoDTO();
            producto.setNombre("Paracetamol");
            producto.setPrecio(new BigDecimal("25.50"));
            producto.setStock(-5);
            producto.setCategoriaId(1L);
            producto.setProveedorId(1L);
            
            // When/Then
            assertThatThrownBy(() -> ValidationService.validate(producto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("stock");
        }
        
        @Test
        @Order(6)
        @DisplayName("Debe rechazar producto sin categoría")
        void debeRechazarProductoSinCategoria() {
            // Given
            ProductoDTO producto = new ProductoDTO();
            producto.setNombre("Paracetamol");
            producto.setPrecio(new BigDecimal("25.50"));
            producto.setStock(100);
            producto.setProveedorId(1L);
            // categoriaId null
            
            // When/Then
            assertThatThrownBy(() -> ValidationService.validate(producto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("categoría");
        }
        
        @Test
        @Order(7)
        @DisplayName("Debe rechazar producto con estado inválido")
        void debeRechazarProductoConEstadoInvalido() {
            // Given
            ProductoDTO producto = new ProductoDTO();
            producto.setNombre("Paracetamol");
            producto.setPrecio(new BigDecimal("25.50"));
            producto.setStock(100);
            producto.setCategoriaId(1L);
            producto.setProveedorId(1L);
            producto.setEstado("invalido");
            
            // When
            Set<String> errors = ValidationService.getValidationErrors(producto);
            
            // Then
            assertThat(errors).isNotEmpty();
            assertThat(errors.toString()).containsIgnoringCase("estado");
        }
    }
    
    @Nested
    @DisplayName("Métodos Auxiliares de ValidationService")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class MetodosAuxiliares {
        
        @Test
        @Order(8)
        @DisplayName("getFormattedErrors debe retornar null para objeto válido")
        void getFormattedErrorsDebeRetornarNullParaObjetoValido() {
            // Given
            ProductoDTO producto = new ProductoDTO();
            producto.setNombre("Paracetamol");
            producto.setPrecio(new BigDecimal("25.50"));
            producto.setStock(100);
            producto.setCategoriaId(1L);
            producto.setProveedorId(1L);
            
            // When
            String errors = ValidationService.getFormattedErrors(producto);
            
            // Then
            assertThat(errors).isNull();
        }
        
        @Test
        @Order(9)
        @DisplayName("getFormattedErrors debe retornar mensaje formateado para objeto inválido")
        void getFormattedErrorsDebeRetornarMensajeFormateado() {
            // Given
            ProductoDTO producto = new ProductoDTO();
            // Nombre vacío, sin precio, sin stock, sin categoría
            
            // When
            String errors = ValidationService.getFormattedErrors(producto);
            
            // Then
            assertThat(errors).isNotNull();
            assertThat(errors).contains("Errores de validación:");
        }
        
        @Test
        @Order(10)
        @DisplayName("isValid debe retornar false para múltiples errores")
        void isValidDebeRetornarFalseParaMultiplesErrores() {
            // Given
            ProductoDTO producto = new ProductoDTO();
            producto.setNombre("AB"); // Muy corto
            producto.setPrecio(new BigDecimal("-10")); // Negativo
            producto.setStock(-5); // Negativo
            
            // When/Then
            assertThat(ValidationService.isValid(producto)).isFalse();
            Set<String> errors = ValidationService.getValidationErrors(producto);
            assertThat(errors).hasSizeGreaterThan(1);
        }
        
        @Test
        @Order(11)
        @DisplayName("validate debe incluir todos los errores en el mensaje")
        void validateDebeIncluirTodosLosErrores() {
            // Given
            ProductoDTO producto = new ProductoDTO();
            // Objeto con múltiples errores
            
            // When/Then
            assertThatThrownBy(() -> ValidationService.validate(producto))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
