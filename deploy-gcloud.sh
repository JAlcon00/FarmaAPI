#!/bin/bash

# 🚀 Deploy FarmaControl API en Google Cloud
# Ejecutar DENTRO de la VM de Google Cloud

set -e

# Colores
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

log_info() { echo -e "${BLUE}ℹ️  $1${NC}"; }
log_success() { echo -e "${GREEN}✅ $1${NC}"; }
log_warning() { echo -e "${YELLOW}⚠️  $1${NC}"; }
log_error() { echo -e "${RED}❌ $1${NC}"; }

echo "🚀 FarmaControl API - Deployment Google Cloud"
echo "=============================================="
echo ""

# 1. Verificar que estamos en el directorio correcto
if [ ! -f "pom.xml" ]; then
    log_error "No se encuentra pom.xml. Ejecuta este script desde la raíz del proyecto."
    exit 1
fi

log_success "Directorio correcto"

# 2. Verificar Docker
if ! command -v docker &> /dev/null; then
    log_error "Docker no está instalado."
    echo ""
    echo "Instala Docker con:"
    echo "  sudo apt-get update"
    echo "  sudo apt-get install -y docker.io docker-compose-plugin"
    exit 1
fi

if ! docker compose version &> /dev/null && ! command -v docker-compose &> /dev/null; then
    log_error "Docker Compose no está instalado."
    echo ""
    echo "Instala con:"
    echo "  sudo apt-get install -y docker-compose-plugin"
    exit 1
fi

log_success "Docker verificado"

# 3. Verificar archivo de variables de entorno
if [ ! -f ".env.production" ]; then
    log_error "Archivo .env.production no encontrado"
    echo ""
    echo "Crea el archivo .env.production basado en .env.production.example:"
    echo "  cp .env.production.example .env.production"
    echo "  nano .env.production"
    echo ""
    echo "Configura las siguientes variables:"
    echo "  - DB_PASSWORD"
    echo "  - MYSQL_ROOT_PASSWORD"
    echo "  - JWT_SECRET (generar con: openssl rand -base64 64)"
    exit 1
fi

# Verificar que no tenga valores por defecto
if grep -q "CAMBIAR_EN_PRODUCCION" .env.production; then
    log_error "El archivo .env.production contiene valores por defecto"
    echo ""
    echo "Por favor configura las credenciales reales en .env.production"
    echo "Edita con: nano .env.production"
    exit 1
fi

log_success "Variables de entorno verificadas"

# 4. Verificar Maven
if ! command -v mvn &> /dev/null; then
    log_error "Maven no está instalado"
    echo ""
    echo "Instala con:"
    echo "  sudo apt-get install -y maven"
    exit 1
fi

log_success "Maven verificado"

# 5. Detener contenedores anteriores
log_info "Deteniendo contenedores anteriores..."
docker compose -f docker/docker-compose.yml --env-file .env.production down 2>/dev/null || true
log_success "Contenedores detenidos"

# 6. Limpiar imágenes antiguas (opcional)
read -p "¿Limpiar imágenes antiguas? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    log_info "Limpiando imágenes antiguas..."
    docker image prune -f
    log_success "Limpieza completada"
fi

# 7. Compilar aplicación
log_info "Compilando aplicación con Maven..."
if ! mvn clean package -DskipTests -q; then
    log_error "Error en la compilación"
    echo ""
    echo "Intenta compilar manualmente:"
    echo "  mvn clean package"
    exit 1
fi
log_success "Compilación exitosa"

# 8. Verificar que el JAR existe
if [ ! -f "target/farmacontrol-api.jar" ]; then
    log_error "No se encontró el JAR compilado en target/"
    exit 1
fi

JAR_SIZE=$(du -h target/farmacontrol-api.jar | cut -f1)
log_success "JAR generado: $JAR_SIZE"

# 9. Construir imagen Docker
log_info "Construyendo imagen Docker..."
if ! docker build -f docker/Dockerfile -t farmacontrol-api:latest . --no-cache; then
    log_error "Error al construir la imagen Docker"
    exit 1
fi
log_success "Imagen Docker construida"

# 10. Iniciar servicios
log_info "Iniciando servicios..."
docker compose -f docker/docker-compose.yml --env-file .env.production up -d

# 11. Esperar a que MySQL esté listo
log_info "Esperando que MySQL esté listo..."
MYSQL_READY=false
for i in {1..60}; do
    if docker exec farmacontrol-mysql mysqladmin ping -h localhost --silent 2>/dev/null; then
        MYSQL_READY=true
        break
    fi
    echo -n "."
    sleep 2
done
echo ""

if [ "$MYSQL_READY" = false ]; then
    log_error "MySQL no está respondiendo después de 120 segundos"
    echo ""
    echo "Ver logs con:"
    echo "  docker compose -f docker/docker-compose.yml logs mysql-db"
    exit 1
fi

log_success "MySQL está listo"

# 12. Esperar a que la API esté lista
log_info "Esperando que la API esté lista..."
API_READY=false
for i in {1..60}; do
    if curl -f http://localhost:8080/actuator/health &>/dev/null; then
        API_READY=true
        break
    fi
    echo -n "."
    sleep 2
done
echo ""

if [ "$API_READY" = false ]; then
    log_warning "La API no responde en /actuator/health después de 120 segundos"
    log_info "Verificando endpoint alternativo..."
    
    # Intentar con endpoint de productos
    if curl -f http://localhost:8080/api/productos &>/dev/null; then
        log_success "API responde en /api/productos"
        API_READY=true
    else
        log_error "La API no está respondiendo"
        echo ""
        echo "Ver logs con:"
        echo "  docker compose -f docker/docker-compose.yml logs farmaapi"
        exit 1
    fi
fi

if [ "$API_READY" = true ]; then
    log_success "API está lista"
fi

# 13. Verificar endpoints
log_info "Verificando endpoints..."
if curl -f http://localhost:8080/actuator/health &>/dev/null; then
    log_success "Health check funcionando"
else
    log_warning "Health check no disponible (puede ser normal)"
fi

# 14. Obtener IP del servidor
SERVER_IP=$(hostname -I | awk '{print $1}')
if [ -z "$SERVER_IP" ]; then
    SERVER_IP="localhost"
fi

# 15. Mostrar información final
echo ""
echo "═══════════════════════════════════════════════════════"
echo "🎉 ¡Deployment completado exitosamente!"
echo "═══════════════════════════════════════════════════════"
echo ""
echo "📡 Tu API está disponible en:"
echo "   🌐 API Base:         http://${SERVER_IP}:8080/api"
echo "   💚 Health Check:     http://${SERVER_IP}:8080/actuator/health"
echo "   📝 Swagger UI:       http://${SERVER_IP}:8080/swagger-ui.html"
echo "   🧪 API Tester:       http://${SERVER_IP}:8080/api-tester.html"
echo ""
echo "🗄️ Base de datos MySQL:"
echo "   📍 Host:            ${SERVER_IP}:3306"
echo "   🗂️  Database:        farmacontrol"
echo ""
echo "📋 Comandos útiles:"
echo "   Ver logs API:       docker compose -f docker/docker-compose.yml logs -f farmaapi"
echo "   Ver logs MySQL:     docker compose -f docker/docker-compose.yml logs -f mysql-db"
echo "   Ver estado:         docker compose -f docker/docker-compose.yml ps"
echo "   Reiniciar:          docker compose -f docker/docker-compose.yml restart"
echo "   Detener:            docker compose -f docker/docker-compose.yml down"
echo ""
echo "🔧 Para crear un usuario de prueba:"
echo "   ./crear-usuario.sh admin@test.com admin123 Admin User 1"
echo ""
echo "🔒 Recomendaciones de seguridad:"
echo "   ⚠️  Configura firewall para puerto 8080"
echo "   ⚠️  Considera usar nginx como reverse proxy con SSL"
echo "   ⚠️  Habilita backups automáticos de la base de datos"
echo ""
log_success "¡Proyecto listo en Google Cloud!"
