# 🏥 FarmaControl API

> Sistema de gestión farmacéutica profesional con Spring Boot, JWT y MySQL

[![Tests](https://img.shields.io/badge/tests-349%20passing-brightgreen)](./run-tests.sh)
[![Coverage](https://img.shields.io/badge/coverage-65%25%20security-blue)](./ver-cobertura.sh)
[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-green)](https://spring.io/projects/spring-boot)
[![Terraform](https://img.shields.io/badge/Terraform-IaC-7B42BC)](./terraform/)
[![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-2088FF)](./.github/workflows/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

## 🚀 Características

### Aplicación
- ✅ **Autenticación JWT** con refresh tokens
- ✅ **Sistema de roles** con 20 roles predefinidos
- ✅ **Autorización granular** basada en permisos
- ✅ **349 tests automatizados** (100% pasando)
- ✅ **65% cobertura** en Security/JWT
- ✅ **Rate limiting** por rol
- ✅ **Auditoría completa** de operaciones
- ✅ **CORS configurado** para frontend

### DevOps & Cloud
- ✅ **Infrastructure as Code** con Terraform
- ✅ **CI/CD Automático** con GitHub Actions
- ✅ **Deploy en Google Cloud** (10-15 min)
- ✅ **Docker Compose** para desarrollo local
- ✅ **Tests automáticos** en cada PR
- ✅ **Análisis de seguridad** integrado
- ✅ **Preview de cambios** de infraestructura

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

- **Java 17** o superior
- **Maven 3.9+**
- **MySQL 8.0+**
- **Docker** (opcional, para despliegue)

## 📦 Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/farmacontrol-api.git
cd farmacontrol-api
```

### 2. Configurar variables de entorno

```bash
# Copiar plantilla de configuración
cp .env.example .env

# Editar .env con tus credenciales
nano .env
```

### 3. Crear base de datos

```bash
# Conectar a MySQL
mysql -u root -p

# Ejecutar script de inicialización
source src/java/database_schema.sql
```

### 4. Compilar el proyecto

```bash
mvn clean install -DskipTests
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
./run-tests.sh
```

### Ver reporte de cobertura

```bash
./ver-cobertura.sh
```

### Resultados

- **349 tests** pasando (100%)
- **Cobertura Services**: 66%
- **Cobertura Security**: 65%
- **Cobertura Controllers**: 61%

Ver [REPORTE-CALIDAD-FINAL.md](./REPORTE-CALIDAD-FINAL.md) para detalles.

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
- Java 17
- Spring Boot 3.1.5
- Spring Security + JWT
- MySQL 8.0
- Maven 3.9+

**DevOps:**
- Docker + Docker Compose
- Terraform (Infrastructure as Code)
- GitHub Actions (CI/CD)
- Google Cloud Platform (Compute Engine)

**Tests:**
- JUnit 5
- Mockito
- Spring Test
- JaCoCo (Coverage)

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

- ✅ Backend completo y funcional
- ✅ Tests automatizados (349 tests)
- ✅ Documentación completa
- ✅ Docker ready
- ✅ Producción-ready
- ⏳ Frontend en desarrollo (Ionic/Angular)

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
