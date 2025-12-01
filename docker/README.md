# 🐳 Configuración Docker - FarmaControl API

## Archivos

### `Dockerfile`
Dockerfile optimizado para despliegue académico/escolar.

**Características:**
- Base: `openjdk:17-jdk-slim`
- Usuario no privilegiado
- Health checks configurados
- Copia `.env` para configuración

**Uso:**
```bash
docker build -f docker/Dockerfile -t farmacontrol-api:latest .
```

---

### `docker-compose.yml`
Configuración completa: API + MySQL.

**Servicios:**
- `farmaapi`: API Spring Boot en puerto 8080
- `mysql-db`: MySQL 8.0 en puerto 3306

**Uso:**
```bash
# Iniciar
docker-compose -f docker/docker-compose.yml up -d

# Detener
docker-compose -f docker/docker-compose.yml down

# Ver logs
docker-compose -f docker/docker-compose.yml logs -f
```

---

### `docker-compose.test.yml`
MySQL para tests automatizados.

**Configuración:**
- Puerto: 3307 (no conflictua con MySQL local)
- Usuario: root / root123
- Database: farmacontrol
- Schema automático: `src/java/database_schema_test.sql`

**Uso:**
```bash
# Iniciar
docker compose -f docker/docker-compose.test.yml up -d

# Detener
docker compose -f docker/docker-compose.test.yml down
```

---

### `.dockerignore`
Archivos excluidos de la imagen Docker.

**Excluye:**
- Documentación (`.md`, `docs/`)
- Git (`.git`)
- Logs y temporales
- Configuración de IDEs
- Build intermedios de Maven

---

## 🚀 Despliegue Rápido

### Opción 1: Script automatizado (Recomendado)
```bash
./desplegar-escolar.sh
```

### Opción 2: Manual
```bash
# 1. Compilar
mvn clean package -DskipTests

# 2. Construir imagen
docker build -f docker/Dockerfile -t farmacontrol-api:latest .

# 3. Iniciar servicios
docker-compose -f docker/docker-compose.yml up -d
```

---

## 🧪 Tests con Docker

```bash
# Iniciar MySQL de test
./start-test-db.sh

# Ejecutar tests
./run-tests.sh
```

---

## 📊 Comandos Útiles

```bash
# Ver contenedores corriendo
docker ps

# Ver logs de API
docker logs farmacontrol-api -f

# Ver logs de MySQL
docker logs farmacontrol-mysql -f

# Entrar al contenedor de API
docker exec -it farmacontrol-api sh

# Entrar a MySQL
docker exec -it farmacontrol-mysql mysql -uroot -p

# Limpiar todo
docker-compose -f docker/docker-compose.yml down -v
docker system prune -f
```

---

## 🔧 Variables de Entorno

Las variables se toman del archivo `.env` en la raíz del proyecto.

Ver [`.env.example`](../.env.example) para configuración completa.

**Principales:**
```bash
DB_HOST=localhost
DB_PORT=3306
DB_NAME=farmacontrol
DB_USER=root
DB_PASSWORD=root123

JWT_SECRET=tu_secreto_aqui
SERVER_PORT=8080
```

---

## 📡 Acceso a la Aplicación

Una vez desplegado:

| Servicio | URL |
|----------|-----|
| API Base | http://localhost:8080/api |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| MySQL | localhost:3306 |

**Credenciales de prueba:**
- Email: `admin@farmacontrol.com`
- Password: `admin123`

---

## ⚠️ Notas

- El Dockerfile espera el JAR compilado en `target/farmacontrol-api.jar`
- Compilar antes de construir la imagen: `mvn clean package`
- El archivo `.env` debe existir en la raíz del proyecto
- MySQL crea automáticamente volumen persistente: `mysql_data`
