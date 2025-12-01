#!/bin/bash
# ============================================================================
# STARTUP SCRIPT - FARMACONTROL API
# ============================================================================
# Este script se ejecuta automáticamente cuando la VM inicia por primera vez
# Instala dependencias, clona el repositorio y despliega la aplicación
# ============================================================================

set -e  # Exit on error

# ============================================================================
# VARIABLES (inyectadas por Terraform)
# ============================================================================

DB_HOST="${db_host}"
DB_NAME="${db_name}"
DB_USER="${db_user}"
DB_PASSWORD="${db_password}"
MYSQL_ROOT_PASSWORD="${mysql_root_password}"
JWT_SECRET="${jwt_secret}"
JWT_EXPIRATION="${jwt_expiration}"
JWT_REFRESH_EXPIRATION="${jwt_refresh_expiration}"
SERVER_PORT="${server_port}"
GITHUB_REPO="${github_repo}"
GITHUB_BRANCH="${github_branch}"

# ============================================================================
# LOGGING
# ============================================================================

LOG_FILE="/var/log/farmacontrol-startup.log"
exec > >(tee -a "$LOG_FILE") 2>&1

echo "============================================================================"
echo "FarmaControl API - Startup Script"
echo "Started at: $(date)"
echo "============================================================================"

# ============================================================================
# ACTUALIZAR SISTEMA
# ============================================================================

echo "📦 Updating system packages..."
apt-get update
apt-get upgrade -y

# ============================================================================
# INSTALAR DEPENDENCIAS
# ============================================================================

echo "🔧 Installing dependencies..."

# Docker
echo "🐳 Installing Docker..."
apt-get install -y \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg \
    lsb-release

curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null

apt-get update
apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

# Verificar Docker
docker --version
docker compose version

# Java y Maven
echo "☕ Installing Java and Maven..."
apt-get install -y openjdk-17-jdk maven git

java -version
mvn -version

# ============================================================================
# CREAR USUARIO Y DIRECTORIOS
# ============================================================================

echo "👤 Creating application user..."

# Crear usuario farmacontrol si no existe
if ! id -u farmacontrol > /dev/null 2>&1; then
    useradd -m -s /bin/bash farmacontrol
    usermod -aG docker farmacontrol
fi

APP_DIR="/home/farmacontrol/farmacontrol-api"
mkdir -p "$APP_DIR"

# ============================================================================
# CLONAR REPOSITORIO
# ============================================================================

echo "📥 Cloning repository..."

if [ -d "$APP_DIR/.git" ]; then
    echo "Repository already exists, pulling latest changes..."
    cd "$APP_DIR"
    git pull origin "$GITHUB_BRANCH"
else
    git clone -b "$GITHUB_BRANCH" "$GITHUB_REPO" "$APP_DIR"
    cd "$APP_DIR"
fi

# ============================================================================
# CREAR ARCHIVO .env.production
# ============================================================================

echo "🔐 Creating .env.production file..."

cat > "$APP_DIR/.env.production" <<EOF
# Database Configuration
DB_HOST=$DB_HOST
DB_PORT=3306
DB_NAME=$DB_NAME
DB_USER=$DB_USER
DB_PASSWORD=$DB_PASSWORD

# MySQL Root
MYSQL_ROOT_PASSWORD=$MYSQL_ROOT_PASSWORD

# JWT Configuration
JWT_SECRET=$JWT_SECRET
JWT_EXPIRATION=$JWT_EXPIRATION
JWT_REFRESH_EXPIRATION=$JWT_REFRESH_EXPIRATION

# Server Configuration
SERVER_PORT=$SERVER_PORT
EOF

chmod 600 "$APP_DIR/.env.production"

# ============================================================================
# COMPILAR APLICACIÓN
# ============================================================================

echo "🏗️  Compiling application..."

cd "$APP_DIR"
mvn clean package -DskipTests

if [ ! -f "target/farmacontrol-api.jar" ]; then
    echo "❌ ERROR: JAR file not found after compilation"
    exit 1
fi

echo "✅ Compilation successful"

# ============================================================================
# CONSTRUIR IMAGEN DOCKER
# ============================================================================

echo "🐳 Building Docker image..."

docker build -f docker/Dockerfile -t farmacontrol-api:latest .

echo "✅ Docker image built successfully"

# ============================================================================
# INICIAR SERVICIOS
# ============================================================================

echo "🚀 Starting services..."

cd "$APP_DIR"

# Detener contenedores existentes
docker compose -f docker/docker-compose.yml --env-file .env.production down || true

# Iniciar servicios
docker compose -f docker/docker-compose.yml --env-file .env.production up -d

echo "✅ Services started"

# ============================================================================
# ESPERAR A QUE LA API ESTÉ LISTA
# ============================================================================

echo "⏳ Waiting for API to be ready..."

MAX_WAIT=180
COUNTER=0

until curl -sf http://localhost:$SERVER_PORT/actuator/health > /dev/null 2>&1; do
    if [ $COUNTER -ge $MAX_WAIT ]; then
        echo "❌ ERROR: API did not start within $MAX_WAIT seconds"
        docker compose -f docker/docker-compose.yml logs
        exit 1
    fi
    
    echo "Waiting... ($COUNTER/$MAX_WAIT)"
    sleep 5
    COUNTER=$((COUNTER + 5))
done

echo "✅ API is ready!"

# ============================================================================
# CONFIGURAR SYSTEMD SERVICE (reinicio automático)
# ============================================================================

echo "⚙️  Configuring systemd service..."

cat > /etc/systemd/system/farmacontrol.service <<EOF
[Unit]
Description=FarmaControl API
After=docker.service
Requires=docker.service

[Service]
Type=oneshot
RemainAfterExit=yes
WorkingDirectory=$APP_DIR
ExecStart=/usr/bin/docker compose -f docker/docker-compose.yml --env-file .env.production up -d
ExecStop=/usr/bin/docker compose -f docker/docker-compose.yml down
User=root

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable farmacontrol.service

echo "✅ Systemd service configured"

# ============================================================================
# AJUSTAR PERMISOS
# ============================================================================

chown -R farmacontrol:farmacontrol "$APP_DIR"

# ============================================================================
# MOSTRAR INFORMACIÓN
# ============================================================================

EXTERNAL_IP=$(curl -s http://checkip.amazonaws.com || echo "Unable to fetch")
INTERNAL_IP=$(hostname -I | awk '{print $1}')

echo ""
echo "============================================================================"
echo "✅ DEPLOYMENT COMPLETED SUCCESSFULLY!"
echo "============================================================================"
echo ""
echo "📊 Deployment Information:"
echo "  - External IP:  $EXTERNAL_IP"
echo "  - Internal IP:  $INTERNAL_IP"
echo "  - API URL:      http://$EXTERNAL_IP:$SERVER_PORT/api"
echo "  - Health Check: http://$EXTERNAL_IP:$SERVER_PORT/actuator/health"
echo "  - Swagger UI:   http://$EXTERNAL_IP:$SERVER_PORT/swagger-ui.html"
echo ""
echo "🐳 Docker Containers:"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
echo ""
echo "📝 Logs:"
echo "  - Startup log:  $LOG_FILE"
echo "  - API logs:     docker compose -f docker/docker-compose.yml logs -f farmaapi"
echo "  - MySQL logs:   docker compose -f docker/docker-compose.yml logs -f mysql-db"
echo ""
echo "🔧 Useful Commands:"
echo "  - Restart:      systemctl restart farmacontrol"
echo "  - Stop:         systemctl stop farmacontrol"
echo "  - Status:       systemctl status farmacontrol"
echo "  - Logs:         journalctl -u farmacontrol -f"
echo ""
echo "Completed at: $(date)"
echo "============================================================================"
