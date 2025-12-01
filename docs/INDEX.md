# 📚 Índice de Documentación - FarmaControl API

## 🚀 Inicio Rápido

### ⚡ Quick Start Guides
- **[../README.md](../README.md)** - README principal del proyecto
- **[RESUMEN-TERRAFORM-GITHUB-ACTIONS.md](./RESUMEN-TERRAFORM-GITHUB-ACTIONS.md)** 🌟 - **Resumen ejecutivo completo**
- **[DIAGRAMA-FLUJO-CICD.md](./DIAGRAMA-FLUJO-CICD.md)** - Diagramas visuales del flujo CI/CD

---

## 🏗️ DevOps y Deployment

### 🔧 Terraform (Infrastructure as Code)
- **[TERRAFORM-SETUP.md](./TERRAFORM-SETUP.md)** 🌟 - **Guía completa de Terraform**
  - Setup inicial (automático y manual)
  - Configuración de variables
  - Comandos básicos
  - Actualización de infraestructura
  - Troubleshooting completo
  - Mejores prácticas de seguridad

- **[../terraform/README.md](../terraform/README.md)** - Quick reference de Terraform
  - Estructura de archivos
  - Recursos creados
  - Variables principales
  - Comandos útiles

### 🤖 GitHub Actions (CI/CD)
- **[GITHUB-ACTIONS-SETUP.md](./GITHUB-ACTIONS-SETUP.md)** � - **Guía completa de CI/CD**
  - Configuración de secrets
  - Workflows explicados (CI, CD, Terraform Plan)
  - Setup paso a paso
  - Flujo de trabajo completo
  - Monitoreo y troubleshooting
  - Customización

### ☁️ Google Cloud Platform
- **[GOOGLE-CLOUD-DEPLOYMENT.md](./GOOGLE-CLOUD-DEPLOYMENT.md)** - Deploy manual en GCP
  - Crear VM en Google Cloud
  - Configuración paso a paso
  - Firewall y networking
  - Post-deployment

---

## �🎯 Guías Principales

### 🔐 Seguridad y Autenticación
- **[JWT-AUTHENTICATION-GUIDE.md](../JWT-AUTHENTICATION-GUIDE.md)** - Guía completa de autenticación JWT
  - Arquitectura de seguridad
  - Generación y validación de tokens
  - Integración con frontend
  - Troubleshooting

### 👥 Sistema de Roles y Permisos
- **[FASE-2-ROLES-COMPLETA.md](../FASE-2-ROLES-COMPLETA.md)** - Sistema completo de roles
  - 20 roles predefinidos
  - Matrices de permisos
  - Implementación técnica
  
- **[MATRIZ-PERMISOS-ROLES.md](../MATRIZ-PERMISOS-ROLES.md)** - Matriz detallada de permisos
  - Permisos por módulo
  - Control de acceso granular
  
- **[MATRIZ-VISUAL-ROLES.md](../MATRIZ-VISUAL-ROLES.md)** - Jerarquía visual de roles
  - Estructura jerárquica
  - Diagrama de roles

- **[GUIA-RAPIDA-ROLES.md](../GUIA-RAPIDA-ROLES.md)** - Guía rápida de uso

### 🚀 Características Avanzadas
- **[FASE-3-AUDITORIA-SEGURIDAD.md](../FASE-3-AUDITORIA-SEGURIDAD.md)** - Sistema de auditoría
  - Auditoría de operaciones
  - Refresh tokens
  - Rate limiting
  - Caché de permisos

- **[FASE-4-MEJORAS-CRITICAS.md](../FASE-4-MEJORAS-CRITICAS.md)** - Mejoras implementadas
  - Spring Data JPA
  - Variables de entorno
  - Optimizaciones

---

## 🎨 Frontend

### 📱 Integración Frontend
- **[GUIA-LOGIN-FRONTEND.md](../GUIA-LOGIN-FRONTEND.md)** - Integración de login
  - Endpoints correctos
  - Manejo de tokens
  - Ejemplos Angular/Ionic

- **[EJEMPLO-FRONTEND-COMPLETO.md](../EJEMPLO-FRONTEND-COMPLETO.md)** - Ejemplos completos
  - Auth service
  - Guards
  - Interceptors

- **[FRONTEND_GUIDE.md](./FRONTEND_GUIDE.md)** - Guía general de frontend

### 📱 Desarrollo Móvil
- **[ROADMAP-IONIC-ANGULAR.md](./ROADMAP-IONIC-ANGULAR.md)** - Roadmap para app móvil
  - Análisis de endpoints
  - Arquitectura sugerida
  - Fases de implementación

---

## 📡 API

- **[API_DOCUMENTATION.md](./API_DOCUMENTATION.md)** - Documentación general de API
- **[API_ENDPOINTS_COMPLETA.md](./API_ENDPOINTS_COMPLETA.md)** - Lista completa de endpoints

---

## 🧪 Testing

- **[TESTS-README.md](./TESTS-README.md)** - Guía de tests automatizados
  - Cómo ejecutar tests
  - Cobertura actual
  - Estructura de tests

- **[REPORTE-CALIDAD-FINAL.md](./REPORTE-CALIDAD-FINAL.md)** - Reporte de calidad del código
  - Métricas de cobertura
  - Estadísticas de tests
  - Estado del proyecto

---

## 📦 Archivo Histórico

Los documentos de desarrollo y resúmenes de fases están en [`archive/`](./archive/):
- Resúmenes de fases de desarrollo
- Estados históricos del proyecto
- Tareas completadas
- Análisis y diagnósticos temporales

**Total**: 14 documentos históricos

---

## 🗂️ Estructura de Documentación

```
docs/
├── INDEX.md (este archivo)
├── README.md (documentación principal)
│
├── 🔐 Seguridad
│   ├── JWT-AUTHENTICATION-GUIDE.md
│   ├── FASE-2-ROLES-COMPLETA.md
│   ├── FASE-3-AUDITORIA-SEGURIDAD.md
│   ├── MATRIZ-PERMISOS-ROLES.md
│   ├── MATRIZ-VISUAL-ROLES.md
│   └── GUIA-RAPIDA-ROLES.md
│
├── 🎨 Frontend
│   ├── GUIA-LOGIN-FRONTEND.md
│   ├── EJEMPLO-FRONTEND-COMPLETO.md
│   ├── FRONTEND_GUIDE.md
│   └── ROADMAP-IONIC-ANGULAR.md
│
├── 📡 API
│   ├── API_DOCUMENTATION.md
│   └── API_ENDPOINTS_COMPLETA.md
│
├── 🧪 Testing
│   ├── TESTS-README.md
│   └── REPORTE-CALIDAD-FINAL.md
│
├── 🚀 Implementación
│   └── FASE-4-MEJORAS-CRITICAS.md
│
└── 📦 archive/
    └── (14 documentos históricos)
```

---

## 🔍 Buscar Documentación

### Por Tema

| Necesitas... | Ver... |
|--------------|--------|
| **Implementar login** | [GUIA-LOGIN-FRONTEND.md](./GUIA-LOGIN-FRONTEND.md) |
| **Entender roles** | [GUIA-RAPIDA-ROLES.md](./GUIA-RAPIDA-ROLES.md) |
| **JWT completo** | [JWT-AUTHENTICATION-GUIDE.md](./JWT-AUTHENTICATION-GUIDE.md) |
| **Lista de endpoints** | [API_ENDPOINTS_COMPLETA.md](./API_ENDPOINTS_COMPLETA.md) |
| **Ejecutar tests** | [TESTS-README.md](./TESTS-README.md) |
| **App móvil** | [ROADMAP-IONIC-ANGULAR.md](./ROADMAP-IONIC-ANGULAR.md) |
| **Permisos detallados** | [MATRIZ-PERMISOS-ROLES.md](./MATRIZ-PERMISOS-ROLES.md) |
| **Estado del proyecto** | [REPORTE-CALIDAD-FINAL.md](./REPORTE-CALIDAD-FINAL.md) |

### Por Rol

| Rol | Documentos Recomendados |
|-----|-------------------------|
| **Frontend Developer** | GUIA-LOGIN-FRONTEND.md, EJEMPLO-FRONTEND-COMPLETO.md, API_ENDPOINTS_COMPLETA.md |
| **Backend Developer** | JWT-AUTHENTICATION-GUIDE.md, FASE-2-ROLES-COMPLETA.md, TESTS-README.md |
| **Mobile Developer** | ROADMAP-IONIC-ANGULAR.md, GUIA-LOGIN-FRONTEND.md, API_ENDPOINTS_COMPLETA.md |
| **DevOps** | FASE-4-MEJORAS-CRITICAS.md, TESTS-README.md |
| **QA Tester** | TESTS-README.md, REPORTE-CALIDAD-FINAL.md |
| **Project Manager** | REPORTE-CALIDAD-FINAL.md, FASE-2-ROLES-COMPLETA.md |

---

## 📝 Contribuir a la Documentación

Si encuentras errores o quieres mejorar la documentación:

1. Los archivos están en formato Markdown
2. Sigue el estilo existente
3. Agrega ejemplos de código cuando sea relevante
4. Actualiza este índice si añades nuevos documentos

---

## 🔄 Última Actualización

**Fecha**: 12 de noviembre de 2025  
**Versión**: 1.0.0  
**Estado**: ✅ Documentación completa y organizada

---

⭐ Para la documentación principal, ver [README.md](./README.md)
