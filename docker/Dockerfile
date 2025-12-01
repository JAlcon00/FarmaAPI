# Dockerfile optimizado para múltiples plataformas
FROM amazoncorretto:17-alpine

# Metadata
LABEL maintainer="farmacontrol@example.com"
LABEL version="1.0.0"
LABEL description="FarmaControl API - Sistema de gestión farmacéutica"

# Crear directorio de trabajo
WORKDIR /app

# Copiar JAR ya compilado (desde el contexto de build que es la raíz del proyecto)
COPY docker/farmacontrol-api.jar app.jar

# Variables de entorno para JVM (optimizadas para producción)
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseStringDeduplication -Djava.security.egd=file:/dev/./urandom"
ENV SERVER_PORT=8080

# Exponer puerto
EXPOSE 8080

# Ejecutar aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]