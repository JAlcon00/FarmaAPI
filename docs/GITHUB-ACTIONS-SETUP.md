# 🤖 GitHub Actions Setup - CI/CD Automático

Guía completa para configurar CI/CD automático con GitHub Actions para FarmaControl API.

---

## 📋 Tabla de Contenidos

1. [¿Qué es GitHub Actions?](#qué-es-github-actions)
2. [Workflows Incluidos](#workflows-incluidos)
3. [Configuración de Secrets](#configuración-de-secrets)
4. [Setup Paso a Paso](#setup-paso-a-paso)
5. [Flujo de Trabajo](#flujo-de-trabajo)
6. [Monitoreo](#monitoreo)
7. [Troubleshooting](#troubleshooting)

---

## 🤔 ¿Qué es GitHub Actions?

**GitHub Actions** es un sistema de **CI/CD** (Continuous Integration/Continuous Deployment) que permite:
- ✅ Ejecutar tests automáticamente en cada push
- ✅ Desplegar a producción automáticamente
- ✅ Generar reportes de cobertura
- ✅ Análisis de seguridad
- ✅ Preview de cambios de infraestructura

### 🆚 Con vs Sin CI/CD

| Aspecto | Sin CI/CD | Con GitHub Actions |
|---------|-----------|-------------------|
| **Tests** | Manual | Automático en cada PR |
| **Deploy** | Manual (30 min) | Automático (5 min) |
| **Errores** | Detectados tarde | Detectados temprano |
| **Rollback** | Difícil | Fácil (revertir commit) |
| **Seguridad** | Manual | Análisis automático |

---

## 📦 Workflows Incluidos

### 1. CI - Tests y Validación (`.github/workflows/ci.yml`)

**Se ejecuta:** En cada push y pull request a `main` o `develop`

**Pasos:**
1. 🧪 Ejecuta tests con MySQL en contenedor
2. 📊 Genera reporte de cobertura (JaCoCo)
3. 🔍 Análisis de código (Maven verify)
4. 🔒 Análisis de seguridad (Trivy)
5. 🐳 Valida que Docker build funcione

**Duración:** ~5-8 minutos

**Ejemplo de output:**
```
✅ Tests: 349 passed
📊 Coverage: 65%
🔍 Lint: Passed
🔒 Security: No vulnerabilities
🐳 Docker: Built successfully
```

### 2. CD - Deploy a Google Cloud (`.github/workflows/deploy.yml`)

**Se ejecuta:** En push a `main` o manualmente

**Pasos:**
1. ☁️ Autenticación en Google Cloud
2. 📦 Setup de Terraform
3. 🔐 Genera `terraform.tfvars` desde secrets
4. 🚀 Ejecuta `terraform apply`
5. ⏳ Espera a que la API esté lista
6. 🧪 Valida deployment con health check
7. 📊 Genera resumen con URLs

**Duración:** ~10-15 minutos

**Ejemplo de output:**
```
🚀 Deployment Successful!
📍 VM IP: 34.123.45.67
🔗 API URL: http://34.123.45.67:8080/api
✅ Health Check: OK
```

### 3. Terraform Plan (`.github/workflows/terraform-plan.yml`)

**Se ejecuta:** En pull requests que modifican archivos de `terraform/`

**Pasos:**
1. 📋 Genera plan de cambios de Terraform
2. 💬 Comenta en el PR con el plan
3. ✅ Valida configuración

**Duración:** ~3-5 minutos

**Ejemplo de comment en PR:**
```
🏗️ Terraform Plan

+ create google_compute_instance.vm
~ update google_compute_firewall.api (in-place)
- destroy google_compute_disk.old

Plan: 1 to add, 1 to change, 1 to destroy
```

---

## 🔐 Configuración de Secrets

### ¿Qué son los GitHub Secrets?

Los **secrets** son variables de entorno encriptadas que se usan en los workflows. **NUNCA** pongas credenciales directamente en los archivos `.yml`.

### Secrets Requeridos

| Secret | Descripción | Cómo Obtenerlo |
|--------|-------------|----------------|
| `GCP_PROJECT_ID` | ID del proyecto GCP | Console → Dashboard |
| `GCP_SA_KEY` | Credenciales JSON | Ver [paso 3](#paso-3-crear-service-account) |
| `DB_PASSWORD` | Password de MySQL | Tu elección (seguro) |
| `MYSQL_ROOT_PASSWORD` | Root password | Tu elección (seguro) |
| `JWT_SECRET` | Secret para JWT | `openssl rand -base64 64` |

### Secrets Opcionales

| Secret | Descripción | Default |
|--------|-------------|---------|
| `GCP_REGION` | Región de GCP | `us-central1` |
| `GCP_ZONE` | Zona de GCP | `us-central1-a` |
| `MACHINE_TYPE` | Tipo de VM | `e2-medium` |
| `DB_NAME` | Nombre de DB | `farmacontrol` |
| `DB_USER` | Usuario de DB | `farmacontrol_user` |
| `SERVER_PORT` | Puerto de API | `8080` |

---

## 🚀 Setup Paso a Paso

### Paso 1: Fork o Push del Repositorio

Si aún no tienes el código en GitHub:

```bash
cd /ruta/a/farmacontrol-api

# Inicializar Git (si no está inicializado)
git init

# Agregar todos los archivos
git add .

# Commit inicial
git commit -m "Initial commit with Terraform and GitHub Actions"

# Agregar remote
git remote add origin https://github.com/TU_USUARIO/farmacontrol-api.git

# Push
git branch -M main
git push -u origin main
```

### Paso 2: Habilitar GitHub Actions

1. Ve a tu repositorio en GitHub
2. Click en la pestaña **Actions**
3. GitHub detectará automáticamente los workflows en `.github/workflows/`
4. Habilita los workflows (si están deshabilitados)

### Paso 3: Crear Service Account en GCP

```bash
# Configurar proyecto
gcloud config set project TU_PROJECT_ID

# Crear service account
gcloud iam service-accounts create github-actions \
    --display-name="GitHub Actions" \
    --description="Service account for GitHub Actions CI/CD"

# Obtener email
PROJECT_ID=$(gcloud config get-value project)
SA_EMAIL="github-actions@${PROJECT_ID}.iam.gserviceaccount.com"

# Asignar permisos
gcloud projects add-iam-policy-binding $PROJECT_ID \
    --member="serviceAccount:${SA_EMAIL}" \
    --role="roles/compute.admin"

gcloud projects add-iam-policy-binding $PROJECT_ID \
    --member="serviceAccount:${SA_EMAIL}" \
    --role="roles/iam.serviceAccountUser"

gcloud projects add-iam-policy-binding $PROJECT_ID \
    --member="serviceAccount:${SA_EMAIL}" \
    --role="roles/storage.admin"

# Generar key (JSON)
gcloud iam service-accounts keys create github-actions-key.json \
    --iam-account="${SA_EMAIL}"

# Ver contenido (para copiar)
cat github-actions-key.json
```

Copia el **contenido completo** del JSON (desde `{` hasta `}`).

### Paso 4: Configurar Secrets en GitHub

1. Ve a tu repositorio en GitHub
2. **Settings** → **Secrets and variables** → **Actions**
3. Click en **New repository secret**

Agrega cada secret:

#### GCP_PROJECT_ID
```
Name: GCP_PROJECT_ID
Value: mi-proyecto-123456
```

#### GCP_SA_KEY
```
Name: GCP_SA_KEY
Value: {
  "type": "service_account",
  "project_id": "mi-proyecto-123456",
  ...
}
```
⚠️ Copia TODO el contenido del JSON

#### DB_PASSWORD
```
Name: DB_PASSWORD
Value: TuPasswordSeguro123!
```

#### MYSQL_ROOT_PASSWORD
```
Name: MYSQL_ROOT_PASSWORD
Value: RootPasswordSeguro456!
```

#### JWT_SECRET
Genera primero:
```bash
openssl rand -base64 64
```

Luego agrega:
```
Name: JWT_SECRET
Value: (output del comando anterior)
```

#### Secrets Opcionales

Si quieres personalizar (sino usa defaults):
```
GCP_REGION: us-west1
GCP_ZONE: us-west1-a
MACHINE_TYPE: e2-standard-2
```

### Paso 5: Habilitar APIs en GCP

```bash
PROJECT_ID=$(gcloud config get-value project)

gcloud services enable compute.googleapis.com --project=$PROJECT_ID
gcloud services enable servicenetworking.googleapis.com --project=$PROJECT_ID
gcloud services enable sqladmin.googleapis.com --project=$PROJECT_ID
```

### Paso 6: Verificar Secrets

En GitHub:
- **Settings** → **Secrets and variables** → **Actions**

Deberías ver al menos:
- ✅ GCP_PROJECT_ID
- ✅ GCP_SA_KEY
- ✅ DB_PASSWORD
- ✅ MYSQL_ROOT_PASSWORD
- ✅ JWT_SECRET

---

## 🔄 Flujo de Trabajo

### Desarrollo con Feature Branch

```bash
# 1. Crear feature branch
git checkout -b feature/nueva-funcionalidad

# 2. Hacer cambios
# ... editar código ...

# 3. Commit
git add .
git commit -m "Add nueva funcionalidad"

# 4. Push
git push origin feature/nueva-funcionalidad
```

**Resultado:**
- ✅ GitHub Actions ejecuta **CI workflow**
- ✅ Tests automáticos
- ✅ Análisis de seguridad
- ✅ Build de Docker

### Pull Request

```bash
# En GitHub, crear Pull Request de feature → main
```

**Resultado:**
- ✅ CI workflow se ejecuta automáticamente
- ✅ Si hay cambios en `terraform/`, ejecuta **Terraform Plan**
- ✅ Comentario en PR con el plan de cambios
- ✅ Status checks deben pasar antes de merge

**Ejemplo de PR checks:**
```
✅ CI - Tests y Validación
✅ Terraform Plan
✅ Security Analysis
✅ Docker Build
```

### Merge a Main (Deploy Automático)

```bash
# En GitHub, hacer merge del PR
```

**Resultado:**
- ✅ CI workflow se ejecuta
- 🚀 **CD workflow se ejecuta automáticamente**
- 🏗️ Terraform despliega a Google Cloud
- ⏳ Espera a que API esté lista
- ✅ Valida deployment
- 📊 Genera resumen

**Timeline:**
```
[0:00] 📥 Checkout code
[0:30] ☁️ Authenticate to GCP
[1:00] 📦 Setup Terraform
[1:30] 🔧 Terraform Init
[2:00] 📋 Terraform Plan
[3:00] 🚀 Terraform Apply
[8:00] ⏳ Wait for API ready
[10:00] 🧪 Test deployment
[10:30] ✅ Deployment successful!
```

### Deploy Manual

Si quieres desplegar sin hacer push:

1. Ve a **Actions**
2. Selecciona **CD - Deploy a Google Cloud**
3. Click en **Run workflow**
4. Selecciona branch `main`
5. Click en **Run workflow**

---

## 📊 Monitoreo

### Ver Ejecuciones

1. Ve a tu repo en GitHub
2. Click en **Actions**
3. Verás todos los workflows ejecutados

### Ver Logs

1. Click en una ejecución específica
2. Click en el job (ej: "🚀 Deploy to Google Cloud")
3. Verás todos los logs paso a paso

### Notificaciones

GitHub te enviará emails automáticamente cuando:
- ❌ Un workflow falla
- ✅ Un deployment se completa

### Status Badges

Agregar al README.md:

```markdown
![CI](https://github.com/TU_USUARIO/farmacontrol-api/workflows/CI%20-%20Tests%20y%20Validación/badge.svg)
![CD](https://github.com/TU_USUARIO/farmacontrol-api/workflows/CD%20-%20Deploy%20a%20Google%20Cloud/badge.svg)
```

Resultado:
![CI](https://img.shields.io/badge/CI-passing-brightgreen)
![CD](https://img.shields.io/badge/CD-deployed-blue)

---

## 🐛 Troubleshooting

### Error: "GCP_SA_KEY secret is invalid"

**Problema:** El JSON de la service account está mal formateado

**Solución:**
```bash
# Ver el archivo JSON
cat github-actions-key.json

# Verificar que sea JSON válido
cat github-actions-key.json | jq .

# Copiar TODO el contenido (desde { hasta })
# Pegarlo directamente en GitHub Secret (sin comillas adicionales)
```

### Error: "Permission denied on project"

**Problema:** La service account no tiene permisos

**Solución:**
```bash
PROJECT_ID="TU_PROJECT_ID"
SA_EMAIL="github-actions@${PROJECT_ID}.iam.gserviceaccount.com"

# Verificar permisos actuales
gcloud projects get-iam-policy $PROJECT_ID \
  --flatten="bindings[].members" \
  --filter="bindings.members:serviceAccount:${SA_EMAIL}"

# Agregar permisos necesarios
gcloud projects add-iam-policy-binding $PROJECT_ID \
    --member="serviceAccount:${SA_EMAIL}" \
    --role="roles/compute.admin"
```

### Error: "API not enabled"

**Problema:** APIs de GCP no están habilitadas

**Solución:**
```bash
gcloud services enable compute.googleapis.com
gcloud services enable servicenetworking.googleapis.com
```

### Tests fallan en GitHub Actions pero pasan localmente

**Problema:** Diferencias en entorno (versiones, timezone, etc.)

**Solución:**
1. Revisar logs en GitHub Actions
2. Verificar versiones de Java (debe ser 17)
3. Verificar variables de entorno en el workflow

```yaml
# En .github/workflows/ci.yml
env:
  DB_HOST: localhost  # ← Verificar
  DB_PORT: 3306
  # ...
```

### Deployment se queda esperando API

**Problema:** La API no inicia en la VM

**Solución:**
```bash
# SSH a la VM desde la terminal
gcloud compute ssh production-farmacontrol-api --zone=us-central1-a

# Ver logs del startup script
sudo tail -f /var/log/farmacontrol-startup.log

# Ver logs de Docker
cd /home/farmacontrol/farmacontrol-api
docker compose -f docker/docker-compose.yml logs -f
```

### Error: "Terraform state locked"

**Problema:** Hay otro deployment corriendo o uno anterior falló

**Solución:**
```bash
# Desde terminal local
cd terraform
terraform force-unlock LOCK_ID

# O esperar 15 minutos (timeout automático)
```

---

## 🔒 Seguridad

### Mejores Prácticas

1. ✅ **NUNCA** pongas secrets en código
```yaml
# ❌ MAL
env:
  DB_PASSWORD: "mi_password_123"

# ✅ BIEN
env:
  DB_PASSWORD: ${{ secrets.DB_PASSWORD }}
```

2. ✅ Usa **minimum permissions** para service accounts
```bash
# Solo lo necesario
roles/compute.admin
roles/iam.serviceAccountUser
```

3. ✅ Rota secrets regularmente
```bash
# Cada 3-6 meses, generar nuevos:
openssl rand -base64 64  # Nuevo JWT_SECRET
```

4. ✅ Revisa **Security** tab en GitHub
   - Dependabot alerts
   - Code scanning
   - Secret scanning

5. ✅ Usa **branch protection rules**
   - Settings → Branches → Add rule
   - ☑️ Require status checks to pass
   - ☑️ Require pull request reviews

---

## 📈 Métricas y Monitoring

### Ver Métricas de Workflows

GitHub Actions proporciona:
- ⏱️ Duración promedio
- 📊 Tasa de éxito/fallo
- 💰 Minutos consumidos

**Ver en:** Settings → Actions → General

### Límites de GitHub Actions

**Free tier:**
- ✅ 2,000 minutos/mes (repositorios públicos)
- ✅ 500 MB de storage

**Costo aproximado:**
- CI workflow: ~6 min × 20 runs/día = 3,600 min/mes
- CD workflow: ~12 min × 5 deploys/día = 1,800 min/mes
- **Total:** ~5,400 min/mes ⚠️ Supera free tier

**Optimizaciones:**
- Ejecutar CI solo en PRs (no en cada push)
- Usar cache de Maven/Docker
- Deploy solo en horarios específicos

---

## 🎯 Customización

### Ejecutar CI solo en PRs

Editar `.github/workflows/ci.yml`:
```yaml
on:
  pull_request:    # Solo PRs
    branches: [ main, develop ]
  # Quitar push:
```

### Deploy solo en horas específicas

Editar `.github/workflows/deploy.yml`:
```yaml
on:
  schedule:
    - cron: '0 0 * * *'  # Diario a medianoche UTC
  workflow_dispatch:     # O manual
```

### Agregar notificación a Slack

Agregar al final de `deploy.yml`:
```yaml
- name: 📢 Notify Slack
  if: always()
  uses: 8398a7/action-slack@v3
  with:
    status: ${{ job.status }}
    webhook_url: ${{ secrets.SLACK_WEBHOOK }}
```

---

## ✅ Checklist de Setup

- [ ] Código pusheado a GitHub
- [ ] Service account creada en GCP
- [ ] JSON key generado
- [ ] Todos los secrets configurados en GitHub
- [ ] APIs habilitadas en GCP
- [ ] GitHub Actions habilitado
- [ ] CI workflow ejecutado exitosamente
- [ ] Terraform plan workflow ejecutado (en PR)
- [ ] CD workflow ejecutado exitosamente
- [ ] API deployada y accesible
- [ ] Status badges agregados al README (opcional)
- [ ] Branch protection rules configuradas (opcional)

---

## 🆘 Obtener Ayuda

1. **Ver logs en GitHub:**
   Actions → Workflow run → Job

2. **SSH a VM deployada:**
```bash
gcloud compute ssh production-farmacontrol-api --zone=us-central1-a
```

3. **Documentación oficial:**
   - [GitHub Actions Docs](https://docs.github.com/actions)
   - [Workflow Syntax](https://docs.github.com/en/actions/reference/workflow-syntax-for-github-actions)

4. **Issues del proyecto**

---

¡Listo para CI/CD automático! 🚀

Cada push a `main` desplegará automáticamente tu aplicación a Google Cloud.
