package config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración centralizada de CORS para FarmaControl API
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                    "http://localhost:4200",      // Angular dev
                    "http://localhost:8100",      // Ionic dev
                    "http://localhost:3000",      // React dev
                    "http://localhost:5173",      // Vite dev
                    "https://*.vercel.app",       // Vercel deployments
                    "https://*.netlify.app"       // Netlify deployments
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600); // Cache preflight por 1 hora
    }
}
