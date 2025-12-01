# 🏥 FarmaControl API

> Sistema de gestión farmacéutica profesional con Spring Boot, JWT y MySQL

[![Tests](https://img.shields.io/badge/tests-222%20passing-brightgreen)](./run-tests.sh)
[![Coverage](https://img.shields.io/badge/coverage-66%25%20services-blue)](./ver-cobertura.sh)
[![Build](https://img.shields.io/badge/build-passing-success)](./pom.xml)
[![Version](https://img.shields.io/badge/version-1.0.0-blue)](./pom.xml)
[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-green)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-ready-2496ED)](./docker/)
[![Terraform](https://img.shields.io/badge/Terraform-IaC-7B42BC)](./terraform/)
[![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-2088FF)](./.github/workflows/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

## 🚀 Características

### 🔐 Seguridad y Autenticación
- ✅ **Autenticación JWT** con refresh tokens y rotación automática
- ✅ **Sistema de roles** con 20 roles predefinidos (ADMIN, CAJERO, VENDEDOR, etc.)
- ✅ **Autorización granular** basada en permisos por endpoint
- ✅ **Rate limiting** por rol para prevenir abuso
- ✅ **Auditoría completa** de operaciones críticas
- ✅ **CORS configurado** para integración con frontend Ionic
- ✅ **BCrypt hashing** para contraseñas

### 📊 Gestión Farmacéutica
- ✅ **CRUD completo** de productos, ventas, compras, clientes y proveedores
- ✅ **Control de inventario** con alertas de stock mínimo
- ✅ **Sistema de ventas** con cálculo automático de totales e impuestos
- ✅ **Gestión de compras** a proveedores con actualización de inventario
- ✅ **Reportes y estadísticas** en tiempo real
- ✅ **Movimientos de inventario** con trazabilidad completa

### 🧪 Calidad y Testing
- ✅ **222 tests automatizados** (100% pasando)
- ✅ **66% cobertura** en Services
- ✅ **61% cobertura** en Controllers
- ✅ **Build en 6.8s** con Maven
- ✅ **JaCoCo** para análisis de cobertura
- ✅ **Tests unitarios e integración** con JUnit 5 y Mockito

### 🚀 DevOps & Cloud
- ✅ **Infrastructure as Code** con Terraform para Google Cloud
- ✅ **CI/CD Automático** con GitHub Actions (3 workflows)
- ✅ **Deploy en Google Cloud** (Compute Engine + Cloud SQL)
- ✅ **Docker Compose** para desarrollo local y producción
- ✅ **Tests automáticos** en cada PR y push
- ✅ **Undertow** como servidor web (optimizado para producción)
- ✅ **Health checks** configurados para alta disponibilidad
- ✅ **Variables de entorno** seguras con .env

## 📋 Tabla de Contenidos

- [Características](#características)
- [Quick Start](#quick-start)
  - [Desarrollo Local](#desarrollo-local)
  - [Deploy en Google Cloud](#deploy-en-google-cloud)
- [Requisitos](#requisitos)
- [Instalación](#instalación)
- [Configuración](#configuración)
- [Tests](#tests)
- [API Endpoints](#api-endpoints)
- [CI/CD](#cicd)
- [Documentación](#documentación)
- [Arquitectura](#arquitectura)

---

## ⚡ Quick Start

### Desarrollo Local

```bash
# 1. Clonar repositorio
git clone https://github.com/tu-usuario/farmacontrol-api.git
cd farmacontrol-api

# 2. Iniciar base de datos
./start-test-db.sh

# 3. Ejecutar aplicación
mvn spring-boot:run

# 4. Acceder a Swagger
open http://localhost:8080/swagger-ui.html
```

### Deploy en Google Cloud

**Opción 1: Automático con GitHub Actions (Recomendado)**

```bash
# 1. Configurar GitHub Secrets (una sola vez)
# Ver: docs/GITHUB-ACTIONS-SETUP.md

# 2. Push a main
git push origin main

# ✅ ¡GitHub Actions despliega automáticamente!
```

**Opción 2: Manual con Terraform**

```bash
# 1. Setup inicial (una sola vez)
./scripts/setup-terraform.sh

# 2. Desplegar
cd terraform
terraform apply

# ✅ API disponible en ~10-15 minutos
```

Ver guía completa: [docs/TERRAFORM-SETUP.md](./docs/TERRAFORM-SETUP.md)

---

## 🛠️ Requisitos

### Para Desarrollo
- **Java 17** (OpenJDK o Oracle JDK)
- **Maven 3.9+** para build y gestión de dependencias
- **MySQL 8.0+** (local o Docker)
- **Git** para control de versiones

### Para Producción (Adicional)
- **Docker 20+** y Docker Compose 2.0+
- **Cuenta Google Cloud** (para deploy con Terraform)
- **Terraform 1.0+** (para IaC)

## 📦 Instalación

### Opción 1: Quick Start con Docker (Recomendado)

```bash
# 1. Clonar el repositorio
git clone https://github.com/JAlcon00/gestpharmaapp.git
cd gestpharmaapp/FarmaApi

# 2. Iniciar todo con Docker Compose
cd docker
docker-compose up -d

# 3. Verificar que todo esté corriendo
docker ps

# ✅ API disponible en http://localhost:8080
# ✅ Swagger UI en http://localhost:8080/swagger-ui.html
```

### Opción 2: Instalación Manual

#### 1. Clonar y configurar

```bash
git clone https://github.com/JAlcon00/gestpharmaapp.git
cd gestpharmaapp/FarmaApi

# Copiar y configurar variables de entorno
cp .env.example .env
nano .env  # Editar con tus credenciales
```

#### 2. Configurar Base de Datos

```bash
# Opción A: Con script automático (recomendado)
./start-test-db.sh

# Opción B: Manual
mysql -u root -p
mysql> CREATE DATABASE farmacontrol;
mysql> source src/java/database_schema.sql;
mysql> exit;
```

#### 3. Compilar y ejecutar

```bash
# Compilar
mvn clean install -DskipTests

# Ejecutar
mvn spring-boot:run

# O ejecutar el JAR generado
java -jar target/farmacontrol-api-1.0.0.jar
```

## ⚙️ Configuración

### Variables de Entorno (.env)

```bash
# Base de datos
DB_HOST=localhost
DB_PORT=3306
DB_NAME=farmacontrol
DB_USER=tu_usuario
DB_PASSWORD=tu_password

# JWT
JWT_SECRET=tu_clave_secreta_larga_y_compleja
JWT_EXPIRATION=86400000

# Servidor
SERVER_PORT=8080
```

Ver [.env.example](.env.example) para configuración completa.

## 🚀 Uso

### Desarrollo Local

```bash
mvn spring-boot:run
```

La API estará disponible en: `http://localhost:8080/api`

### Producción con Docker

```bash
./deploy-modern.sh
```

O manualmente:

```bash
docker-compose up -d
```

## 🧪 Tests

### Ejecutar todos los tests

```bash
# Tests completos
./run-tests.sh

# O con Maven
mvn clean test
```

### Ver reporte de cobertura

```bash
# Generar reporte HTML
./ver-cobertura.sh

# O manualmente
mvn clean test jacoco:report
open target/site/jacoco/index.html
```

### Resultados Actuales ✅

```bash
[INFO] Tests run: 222, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
[INFO] Total time: 6.822 s
```

**Cobertura por módulo:**
- **Services**: 66% (lógica de negocio)
- **Controllers**: 61% (endpoints REST)
- **Security**: 65% (JWT y autenticación)

Ver [docs/REPORTE-CALIDAD-FINAL.md](./docs/REPORTE-CALIDAD-FINAL.md) para análisis detallado.

## 📡 API Endpoints

### Autenticación

```bash
POST /api/usuarios/auth          # Login
POST /api/usuarios/refresh       # Renovar token
```

### Gestión

```bash
# Productos
GET    /api/productos
POST   /api/productos
PUT    /api/productos/:id
DELETE /api/productos/:id

# Ventas
GET    /api/ventas
POST   /api/ventas
PUT    /api/ventas/:id/cancelar

# Compras
GET    /api/compras
POST   /api/compras
PUT    /api/compras/:id

# Clientes
GET    /api/clientes
POST   /api/clientes
PUT    /api/clientes/:id
DELETE /api/clientes/:id

# Reportes
GET    /api/reportes/dashboard
GET    /api/reportes/ventas
GET    /api/reportes/productos-mas-vendidos
```

Ver [docs/API_ENDPOINTS_COMPLETA.md](./docs/API_ENDPOINTS_COMPLETA.md) para documentación completa.

---

## 🤖 CI/CD

### GitHub Actions Workflows

Este proyecto incluye 3 workflows automáticos:

| Workflow | Trigger | Duración | Descripción |
|----------|---------|----------|-------------|
| **CI** | Push/PR | ~5-8 min | Tests, coverage, lint, security scan |
| **CD** | Push a `main` | ~10-15 min | Deploy automático a Google Cloud |
| **Terraform Plan** | PR con cambios en `terraform/` | ~3-5 min | Preview de cambios de infraestructura |

### Flujo Automático

```
Developer → Push to main → GitHub Actions → Terraform → Google Cloud → API Live
```

**Ver guías:**
- � [GITHUB-ACTIONS-SETUP.md](./docs/GITHUB-ACTIONS-SETUP.md) - Configuración de CI/CD
- 📖 [DIAGRAMA-FLUJO-CICD.md](./docs/DIAGRAMA-FLUJO-CICD.md) - Flujo visual completo

---

## �📚 Documentación

### Desarrollo y API
| Documento | Descripción |
|-----------|-------------|
| [docs/API_ENDPOINTS_COMPLETA.md](./docs/API_ENDPOINTS_COMPLETA.md) | API Reference completa |
| [docs/FRONTEND_GUIDE.md](./docs/FRONTEND_GUIDE.md) | Integración con frontend |
| [JWT-AUTHENTICATION-GUIDE.md](./JWT-AUTHENTICATION-GUIDE.md) | Guía de autenticación JWT |

### Deployment y DevOps
| Documento | Descripción |
|-----------|-------------|
| 🌟 [docs/TERRAFORM-SETUP.md](./docs/TERRAFORM-SETUP.md) | **Guía completa de Terraform** |
| 🌟 [docs/GITHUB-ACTIONS-SETUP.md](./docs/GITHUB-ACTIONS-SETUP.md) | **Guía completa de CI/CD** |
| [docs/GOOGLE-CLOUD-DEPLOYMENT.md](./docs/GOOGLE-CLOUD-DEPLOYMENT.md) | Deploy manual en GCP |
| [docs/DIAGRAMA-FLUJO-CICD.md](./docs/DIAGRAMA-FLUJO-CICD.md) | Diagramas visuales |
| [docs/RESUMEN-TERRAFORM-GITHUB-ACTIONS.md](./docs/RESUMEN-TERRAFORM-GITHUB-ACTIONS.md) | Resumen ejecutivo |
| [terraform/README.md](./terraform/README.md) | Quick reference de Terraform |

### Roles y Seguridad
| Documento | Descripción |
|-----------|-------------|
| [FASE-2-ROLES-COMPLETA.md](./FASE-2-ROLES-COMPLETA.md) | Sistema de roles y permisos |
| [MATRIZ-PERMISOS-ROLES.md](./MATRIZ-PERMISOS-ROLES.md) | Matriz completa de permisos |
| [FASE-3-AUDITORIA-SEGURIDAD.md](./FASE-3-AUDITORIA-SEGURIDAD.md) | Auditoría y seguridad |

### Tests y Calidad
| Documento | Descripción |
|-----------|-------------|
| [REPORTE-CALIDAD-FINAL.md](./REPORTE-CALIDAD-FINAL.md) | Reporte de calidad del código |
| [RESUMEN-TESTS-FINAL.md](./RESUMEN-TESTS-FINAL.md) | Resumen de tests |
| [TESTS-README.md](./TESTS-README.md) | Guía de tests |

---

## 🏗️ Arquitectura

### Stack Tecnológico

**Backend:**
- **Java 17** (OpenJDK)
- **Spring Boot 3.1.5** con Undertow
- **Spring Security 6.1.5** + JWT (0.12.3)
- **MySQL 8.0.33** con MySQL Connector J
- **Maven 3.9+** para gestión de dependencias
- **SpringDoc OpenAPI 2.2.0** (Swagger UI)
- **Lombok** para reducir boilerplate
- **BCrypt** para hashing de contraseñas

**DevOps:**
- **Docker 20+** + Docker Compose 3.8
- **Terraform** (Infrastructure as Code para GCP)
- **GitHub Actions** (3 workflows: CI, CD, Terraform Plan)
- **Google Cloud Platform:**
  - Compute Engine (VMs)
  - Cloud SQL (MySQL)
  - VPC Networks

**Tests y Calidad:**
- **JUnit 5** (Jupiter)
- **Mockito** para mocking
- **Spring Boot Test** para integración
- **JaCoCo 0.8.11** para cobertura de código
- **Maven Surefire** para ejecución de tests

### Estructura del Proyecto

```
farmacontrol-api/
├── src/
│   ├── main/java/
│   │   ├── controller/      # REST Controllers
│   │   ├── model/           # Entidades JPA
│   │   ├── services/        # Lógica de negocio
│   │   ├── security/        # JWT, Roles, Permisos
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── exception/       # Manejo de excepciones
│   │   └── utils/           # Utilidades
│   └── test/java/           # Tests unitarios e integración
├── terraform/               # Infrastructure as Code
│   ├── modules/             # Módulos de Terraform
│   └── scripts/             # Scripts de deployment
├── .github/workflows/       # GitHub Actions CI/CD
├── docker/                  # Docker configs
├── docs/                    # Documentación
└── scripts/                 # Scripts de utilidad
```

---

## 🐳 Docker

### Despliegue escolar (simple)

```bash
./desplegar-escolar.sh
```

### Despliegue moderno (producción)

```bash
./deploy-modern.sh
```

### Servicios

- **API**: http://localhost:8080
- **MySQL**: localhost:3306
- **Documentación**: http://localhost:8080/swagger-ui.html

## 🔒 Seguridad

- ✅ JWT con algoritmo HS256
- ✅ Refresh tokens con rotación
- ✅ Rate limiting por rol
- ✅ Validación de entrada con Bean Validation
- ✅ Protección contra SQL Injection (PreparedStatements)
- ✅ CORS configurado
- ✅ Auditoría de operaciones críticas

## 🎭 Sistema de Roles

20 roles predefinidos con permisos granulares:

| Rol | Nivel | Descripción |
|-----|-------|-------------|
| ADMIN | 1 | Acceso total al sistema |
| DIRECTOR | 2 | Dirección general |
| GERENTE | 3 | Operaciones y reportes |
| FARMACEUTICO | 4 | Gestión farmacéutica |
| CAJERO | 5 | Ventas y cobros |

Ver [MATRIZ-VISUAL-ROLES.md](./MATRIZ-VISUAL-ROLES.md) para jerarquía completa.

## 📊 Estado del Proyecto

### Backend (FarmaControl API)
- ✅ **API REST completa** con 40+ endpoints
- ✅ **222 tests automatizados** (100% pasando)
- ✅ **Documentación completa** (Swagger + Markdown)
- ✅ **Docker ready** con compose para dev y prod
- ✅ **CI/CD configurado** con GitHub Actions
- ✅ **Producción-ready** desplegable en Google Cloud

### Frontend (GestPharma App)
- ✅ **Ionic 7 + Angular 18** completamente funcional
- ✅ **Autenticación dual** (JWT + Google OAuth)
- ✅ **Sistema POS** con carrito de compras
- ✅ **Gestión completa** de inventario, ventas, clientes
- ✅ **Reportes en PDF** con jsPDF
- ✅ **Responsive design** para móvil y tablet

### Integración
- ✅ **Frontend y Backend integrados** y funcionando
- ✅ **Sincronización en tiempo real**
- ✅ **Sistema de roles** implementado end-to-end

## 🤝 Contribuir

Las contribuciones son bienvenidas. Por favor:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add: AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

### Estándares de Código

- Seguir convenciones de Java (camelCase, PascalCase)
- Tests para nuevas features
- Documentar endpoints nuevos
- Mantener cobertura >60%

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver [LICENSE](LICENSE) para más detalles.

## 👥 Autores

Este proyecto fue desarrollado por:

- **Jesús Almanza** - Desarrollo Backend y Base de Datos
- **Jossue Amador** - Desarrollo Frontend y UI/UX
- **Jorge Estrada** - Desarrollo Frontend y UI/UX

## 🙏 Agradecimientos

- Spring Boot por el framework
- JWT para autenticación segura
- JUnit 5 para testing
- Docker para containerización

## 📞 Contacto

Para reportar problemas o solicitar funcionalidades:
- Abre un [Issue](https://github.com/JAlcon00/gestpharmaapp/issues)
- Contacta al equipo de desarrollo

---

⭐ Si este proyecto te fue útil, considera darle una estrella en GitHub

Desarrollado con ❤️ por el equipo de GestPharma
