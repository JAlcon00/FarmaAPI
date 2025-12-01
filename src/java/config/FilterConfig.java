package config;

import filter.JwtAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de filtros para Spring Boot
 */
@Configuration
public class FilterConfig {
    
    /**
     * Registra el filtro JWT de autenticación
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilter() {
        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        
        registrationBean.setFilter(new JwtAuthenticationFilter());
        registrationBean.addUrlPatterns("/api/productos/*");
        registrationBean.addUrlPatterns("/api/ventas/*");
        registrationBean.addUrlPatterns("/api/compras/*");
        registrationBean.addUrlPatterns("/api/clientes/*");
        registrationBean.addUrlPatterns("/api/proveedores/*");
        registrationBean.addUrlPatterns("/api/categorias/*");
        registrationBean.addUrlPatterns("/api/roles/*");
        registrationBean.addUrlPatterns("/api/usuarios/*");
        registrationBean.addUrlPatterns("/api/reportes/*");
        registrationBean.setName("jwtAuthenticationFilter");
        registrationBean.setOrder(2); // Después de CORS (order 1)
        
        System.out.println("✅ JwtAuthenticationFilter registrado en Spring Boot");
        
        return registrationBean;
    }
}
