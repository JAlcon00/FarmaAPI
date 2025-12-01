#!/bin/bash

# 🎓 FarmaControl API - Despliegue Escolar
# Configuración simple para proyectos académicos

set -e

echo "🎓 FarmaControl API - Despliegue Escolar"
echo "======================================"

# Colores
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

log_info() { echo -e "${BLUE}ℹ️  $1${NC}"; }
log_success() { echo -e "${GREEN}✅ $1${NC}"; }
log_warning() { echo -e "${YELLOW}⚠️  $1${NC}"; }

# Verificar Docker
if ! command -v docker &> /dev/null; then
    log_warning "Docker no está instalado. Por favor instala Docker Desktop."
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    log_warning "Docker Compose no está disponible. Por favor instala Docker Desktop."
    exit 1
fi

log_success "Docker verificado correctamente"

# Limpiar contenedores anteriores
log_info "Limpiando contenedores anteriores..."
docker-compose -f docker/docker-compose.yml down -v 2>/dev/null || true
log_success "Limpieza completada"

# Construir aplicación con Maven
log_info "Construyendo aplicación..."
mvn clean package -DskipTests -q
log_success "Aplicación construida"

# Construir imagen Docker
log_info "Construyendo imagen Docker..."
docker build -f docker/Dockerfile -t farmacontrol-api:latest . --quiet
log_success "Imagen Docker construida"

# Iniciar servicios
log_info "Iniciando servicios..."
docker-compose -f docker/docker-compose.yml up -d

# Esperar a que los servicios estén listos
log_info "Esperando que los servicios estén listos..."
sleep 30

# Verificar que la API esté funcionando
log_info "Verificando servicios..."
if curl -f http://localhost:8080/api/productos &>/dev/null; then
    log_success "¡API funcionando correctamente!"
else
    log_warning "La API puede tardar un poco más en estar lista..."
fi

echo ""
echo "🎉 ¡FarmaControl API desplegada exitosamente!"
echo ""
echo "📡 Accede a tu aplicación en:"
echo "   🌐 API Base:           http://localhost:8080/api"
echo "   📊 Dashboard:          http://localhost:8080/api/reportes"
echo "   📝 Documentación:      http://localhost:8080/swagger-ui.html"
echo "   🔧 Tester de API:      http://localhost:8080/api-tester.html"
echo ""
echo "🔑 Credenciales de prueba:"
echo "   Email: admin@farmacontrol.com"
echo "   Password: admin123"
echo ""
echo "🛠️ Comandos útiles:"
echo "   Ver logs:              docker-compose -f docker-compose.escolar.yml logs -f"
echo "   Parar servicios:       docker-compose -f docker-compose.escolar.yml down"
echo "   Reiniciar:             docker-compose -f docker-compose.escolar.yml restart"
echo ""
log_success "¡Proyecto listo para demostración!"