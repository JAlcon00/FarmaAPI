# 🚀 Deployment en Google Cloud - FarmaControl API

Guía completa para desplegar FarmaControl API en una máquina virtual de Google Cloud.

---

## 📋 Tabla de Contenidos

1. [Requisitos Previos](#requisitos-previos)
2. [Paso 1: Crear VM en Google Cloud](#paso-1-crear-vm-en-google-cloud)
3. [Paso 2: Preparar el Servidor](#paso-2-preparar-el-servidor)
4. [Paso 3: Clonar el Proyecto](#paso-3-clonar-el-proyecto)
5. [Paso 4: Configurar Variables](#paso-4-configurar-variables)
6. [Paso 5: Ejecutar Deployment](#paso-5-ejecutar-deployment)
7. [Paso 6: Verificar](#paso-6-verificar)
8. [Paso 7: Configurar Firewall](#paso-7-configurar-firewall)
9. [Post-Deployment](#post-deployment)
10. [Troubleshooting](#troubleshooting)

---

## 📋 Requisitos Previos

### En tu computadora local:
- Cuenta de Google Cloud Platform
- `gcloud` CLI instalado (opcional)
- Git

---

## 🖥️ Paso 1: Crear VM en Google Cloud

### Opción A: Desde Google Cloud Console (Web)

1. Ve a **Compute Engine** → **VM Instances**
2. Click en **CREATE INSTANCE**
3. Configuración recomendada:

```
Nombre:           farmacontrol-api
Región:           us-central1 (o la más cercana)
Zona:             us-central1-a
Machine type:     e2-medium (2 vCPU, 4 GB RAM)
Boot disk:        
  - SO: Ubuntu 22.04 LTS
  - Tipo: SSD persistente
  - Tamaño: 20 GB
Firewall:
  ✅ Allow HTTP traffic
  ✅ Allow HTTPS traffic
```

4. Click en **CREATE**
5. Esperar a que la VM esté corriendo (luz verde)

### Opción B: Desde gcloud CLI

```bash
gcloud compute instances create farmacontrol-api \
    --zone=us-central1-a \
    --machine-type=e2-medium \
    --image-family=ubuntu-2204-lts \
    --image-project=ubuntu-os-cloud \
    --boot-disk-size=20GB \
    --boot-disk-type=pd-ssd \
    --tags=http-server,https-server
```

### Configurar IP Estática (Recomendado)

```bash
# Reservar IP estática
gcloud compute addresses create farmacontrol-api-ip --region=us-central1

# Asignar a la VM
gcloud compute instances add-access-config farmacontrol-api \
    --zone=us-central1-a \
    --access-config-name="External NAT" \
    --address=$(gcloud compute addresses describe farmacontrol-api-ip --region=us-central1 --format="value(address)")
```

---

## 🔧 Paso 2: Preparar el Servidor

### Conectar a la VM

**Opción A: Desde Console (más fácil)**
- En VM Instances, click en **SSH** junto a tu VM

**Opción B: Desde terminal**
```bash
gcloud compute ssh farmacontrol-api --zone=us-central1-a
```

### Instalar Dependencias

Una vez dentro de la VM:

```bash
# Actualizar sistema
sudo apt-get update && sudo apt-get upgrade -y

# Instalar Docker
sudo apt-get install -y docker.io docker-compose-plugin

# Agregar usuario al grupo docker
sudo usermod -aG docker $USER

# ⚠️ IMPORTANTE: Aplicar cambios
exit
# Volver a conectar por SSH
```

Verificar Docker:
```bash
docker --version
docker compose version
```

Instalar Java y Maven:
```bash
sudo apt-get install -y openjdk-17-jdk maven git curl

# Verificar
java -version
mvn -version
```

---

## 📦 Paso 3: Clonar el Proyecto

```bash
# Clonar desde GitHub
git clone https://github.com/TU_USUARIO/farmacontrol-api.git
cd farmacontrol-api

# Verificar
ls -la
```

---

## 🔐 Paso 4: Configurar Variables

### Crear archivo de producción

```bash
cp .env.production.example .env.production
nano .env.production
```

### Configurar con valores reales

```bash
# Base de datos
DB_HOST=mysql-db
DB_PORT=3306
DB_NAME=farmacontrol
DB_USER=farmacontrol_user
DB_PASSWORD=TuPasswordSegura123!  # ← Cambiar

# MySQL Root
MYSQL_ROOT_PASSWORD=RootPasswordSegura456!  # ← Cambiar

# JWT (generar con: openssl rand -base64 64)
JWT_SECRET=$(openssl rand -base64 64)  # ← Ejecutar este comando
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# Servidor
SERVER_PORT=8080
```

**Guardar**: `Ctrl+X` → `Y` → `Enter`

### Verificar configuración

```bash
# Ver que no tenga valores por defecto
grep "CAMBIAR_EN_PRODUCCION" .env.production
# No debe retornar nada
```

---

## 🚀 Paso 5: Ejecutar Deployment

```bash
# Dar permisos de ejecución
chmod +x deploy-gcloud.sh

# Ejecutar deployment
./deploy-gcloud.sh
```

El script hará:
1. ✅ Verificar Docker y Maven
2. ✅ Compilar con Maven
3. ✅ Construir imagen Docker
4. ✅ Iniciar MySQL
5. ✅ Iniciar API
6. ✅ Verificar health checks
7. ✅ Mostrar información de acceso

**Tiempo estimado**: 3-5 minutos

---

## 🔍 Paso 6: Verificar

### Ver contenedores

```bash
docker ps
```

Deberías ver:
```
CONTAINER ID   IMAGE                      STATUS        PORTS
xxxxx          farmacontrol-api:latest    Up 1 minute   0.0.0.0:8080->8080/tcp
xxxxx          mysql:8.0                  Up 1 minute   0.0.0.0:3306->3306/tcp
```

### Verificar health check

```bash
curl http://localhost:8080/actuator/health
```

Respuesta esperada:
```json
{"status":"UP"}
```

### Ver logs

```bash
# Logs de la API
docker compose -f docker/docker-compose.yml logs -f farmaapi

# Logs de MySQL
docker compose -f docker/docker-compose.yml logs -f mysql-db

# Salir de logs: Ctrl+C
```

---

## 🔥 Paso 7: Configurar Firewall

### Opción A: Desde Console

1. **VPC Network** → **Firewall** → **CREATE FIREWALL RULE**
2. Configuración:
```
Name:               allow-farmacontrol-api
Targets:            All instances in the network
Source IP ranges:   0.0.0.0/0
Protocols/ports:    tcp:8080
```
3. **CREATE**

### Opción B: Desde gcloud CLI

```bash
gcloud compute firewall-rules create allow-farmacontrol-api \
    --allow tcp:8080 \
    --source-ranges 0.0.0.0/0 \
    --description="Allow access to FarmaControl API"
```

---

## 🌐 Acceder desde Internet

### Obtener IP externa

```bash
# Dentro de la VM
curl -s http://checkip.amazonaws.com
```

O desde Console:
- **Compute Engine** → **VM instances** → Ver **External IP**

### Acceder a la API

Tu API estará en:
```
http://TU_IP_EXTERNA:8080/api
```

Endpoints principales:
- Health: `http://TU_IP_EXTERNA:8080/actuator/health`
- Swagger: `http://TU_IP_EXTERNA:8080/swagger-ui.html`
- API: `http://TU_IP_EXTERNA:8080/api/productos`

---

## 🎯 Post-Deployment

### 1. Crear usuario de prueba

```bash
./crear-usuario.sh admin@farmacontrol.com admin123 Admin User 1
```

### 2. Probar login

```bash
curl -X POST http://localhost:8080/api/usuarios/auth \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@farmacontrol.com","password":"admin123"}'
```

### 3. Configurar backups (Recomendado)

```bash
# Crear script de backup
nano backup-mysql.sh
```

Contenido:
```bash
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
docker exec farmacontrol-mysql mysqladmin -uroot -p$MYSQL_ROOT_PASSWORD \
  mysqldump farmacontrol > backup_$DATE.sql
```

```bash
chmod +x backup-mysql.sh

# Ejecutar backup
./backup-mysql.sh

# Programar backups diarios con cron
crontab -e
# Agregar: 0 2 * * * /home/usuario/farmacontrol-api/backup-mysql.sh
```

### 4. Configurar SSL (Opcional - Producción real)

Si tienes un dominio:

```bash
# Instalar Nginx y Certbot
sudo apt-get install -y nginx certbot python3-certbot-nginx

# Configurar reverse proxy
sudo nano /etc/nginx/sites-available/farmacontrol
```

Configuración Nginx:
```nginx
server {
    listen 80;
    server_name tu-dominio.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

```bash
# Activar configuración
sudo ln -s /etc/nginx/sites-available/farmacontrol /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx

# Obtener certificado SSL
sudo certbot --nginx -d tu-dominio.com
```

---

## 🔄 Actualizar la Aplicación

```bash
# 1. Conectar a la VM
gcloud compute ssh farmacontrol-api --zone=us-central1-a

# 2. Ir al proyecto
cd farmacontrol-api

# 3. Actualizar código
git pull origin main

# 4. Redesplegar
./deploy-gcloud.sh
```

---

## 🐛 Troubleshooting

### La API no inicia

```bash
# Ver logs detallados
docker compose -f docker/docker-compose.yml logs farmaapi

# Verificar si el puerto está ocupado
sudo lsof -i :8080

# Reiniciar contenedor
docker compose -f docker/docker-compose.yml restart farmaapi
```

### MySQL no conecta

```bash
# Verificar estado de MySQL
docker ps | grep mysql

# Ver health check
docker inspect farmacontrol-mysql | grep Health -A 10

# Entrar a MySQL manualmente
docker exec -it farmacontrol-mysql mysql -uroot -p
```

### Puerto 8080 no accesible desde internet

```bash
# Verificar firewall de Google Cloud
gcloud compute firewall-rules list | grep 8080

# Verificar que el contenedor esté escuchando en 0.0.0.0
docker ps | grep 8080
```

### Error de permisos con Docker

```bash
# Agregar usuario al grupo docker
sudo usermod -aG docker $USER

# Aplicar cambios (cerrar sesión y volver)
exit
```

### Compilación falla

```bash
# Limpiar y recompilar
mvn clean
mvn package -DskipTests

# Si hay error de memoria
export MAVEN_OPTS="-Xmx1024m"
mvn clean package -DskipTests
```

---

## 💰 Costos Estimados

### VM e2-medium (2 vCPU, 4 GB RAM)
- **Costo mensual**: ~$24 USD
- **Storage (20 GB SSD)**: ~$3 USD/mes
- **Network egress**: Variable según tráfico

**Total estimado**: $27-35 USD/mes

### Optimizar costos:
- Usar `e2-small` si el tráfico es bajo (~$13 USD/mes)
- Detener VM cuando no se use (desarrollo)
- Usar `preemptible instances` para pruebas

---

## 📚 Comandos Útiles

```bash
# Ver estado de todos los servicios
docker compose -f docker/docker-compose.yml ps

# Ver logs en tiempo real
docker compose -f docker/docker-compose.yml logs -f

# Reiniciar un servicio específico
docker compose -f docker/docker-compose.yml restart farmaapi

# Detener todos los servicios
docker compose -f docker/docker-compose.yml down

# Detener y eliminar volúmenes
docker compose -f docker/docker-compose.yml down -v

# Ver uso de recursos
docker stats

# Limpiar espacio
docker system prune -a
```

---

## ✅ Checklist de Deployment

- [ ] VM creada en Google Cloud
- [ ] Docker instalado
- [ ] Proyecto clonado
- [ ] `.env.production` configurado con credenciales reales
- [ ] Firewall configurado (puerto 8080)
- [ ] Deployment ejecutado exitosamente
- [ ] Health check responde
- [ ] API accesible desde internet
- [ ] Usuario de prueba creado
- [ ] Login funciona correctamente
- [ ] Backups configurados (recomendado)
- [ ] SSL configurado (si aplica)

---

## 🆘 Soporte

Si tienes problemas:

1. **Ver logs**: `docker compose -f docker/docker-compose.yml logs`
2. **Verificar firewall**: Google Cloud Console → VPC Network → Firewall
3. **Revisar variables**: `cat .env.production` (sin mostrar contraseñas)
4. **Consultar documentación**: [docs/](../docs/)

---

## 🎉 ¡Listo!

Tu API de FarmaControl está desplegada en Google Cloud y lista para usar.

**Siguiente paso**: Conectar tu aplicación móvil (Ionic/Angular) a la API usando la IP externa.
