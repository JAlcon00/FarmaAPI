# 🏗️ Terraform Setup - FarmaControl API

Guía completa para configurar y usar Terraform para desplegar FarmaControl API en Google Cloud.

---

## 📋 Tabla de Contenidos

1. [¿Qué es Terraform?](#qué-es-terraform)
2. [Prerequisitos](#prerequisitos)
3. [Instalación](#instalación)
4. [Setup Inicial](#setup-inicial)
5. [Configuración](#configuración)
6. [Comandos Básicos](#comandos-básicos)
7. [Despliegue](#despliegue)
8. [Actualizar Infraestructura](#actualizar-infraestructura)
9. [Destruir Recursos](#destruir-recursos)
10. [Troubleshooting](#troubleshooting)

---

## 🤔 ¿Qué es Terraform?

**Terraform** es una herramienta de **Infrastructure as Code (IaC)** que te permite:
- ✅ Definir infraestructura en archivos de configuración
- ✅ Versionarla en Git
- ✅ Reproducir ambientes idénticos (dev, staging, prod)
- ✅ Automatizar despliegues
- ✅ Ver cambios antes de aplicarlos (plan)

### 🆚 Terraform vs Manual

| Aspecto | Manual | Terraform |
|---------|--------|-----------|
| **Tiempo** | 30-60 min | 5-10 min |
| **Errores** | Propenso | Consistente |
| **Reproducible** | ❌ | ✅ |
| **Versionado** | ❌ | ✅ |
| **Auditoria** | ❌ | ✅ |

---

## 📋 Prerequisitos

### 1. Cuenta de Google Cloud
- Crear cuenta en https://console.cloud.google.com/
- Habilitar facturación (free tier incluye $300 de crédito)
- Crear un proyecto nuevo

### 2. Software necesario

**macOS:**
```bash
# Homebrew
brew install terraform
brew install --cask google-cloud-sdk
```

**Ubuntu/Debian:**
```bash
# Terraform
wget -O- https://apt.releases.hashicorp.com/gpg | sudo gpg --dearmor -o /usr/share/keyrings/hashicorp-archive-keyring.gpg
echo "deb [signed-by=/usr/share/keyrings/hashicorp-archive-keyring.gpg] https://apt.releases.hashicorp.com $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/hashicorp.list
sudo apt update && sudo apt install terraform

# gcloud CLI
curl https://sdk.cloud.google.com | bash
exec -l $SHELL
```

**Windows:**
```powershell
# Con Chocolatey
choco install terraform
choco install gcloudsdk
```

### 3. Verificar instalación

```bash
terraform --version
gcloud --version
```

---

## 🚀 Setup Inicial

### Método 1: Script Automatizado (Recomendado)

```bash
# Ejecutar script de setup
./scripts/setup-terraform.sh
```

El script te guiará paso a paso para:
1. ✅ Verificar prerequisitos
2. ✅ Configurar proyecto de GCP
3. ✅ Habilitar APIs necesarias
4. ✅ Crear service account
5. ✅ Generar credenciales
6. ✅ Crear terraform.tfvars
7. ✅ Inicializar Terraform

### Método 2: Setup Manual

#### Paso 1: Configurar gcloud

```bash
# Autenticar
gcloud auth login

# Listar proyectos
gcloud projects list

# Configurar proyecto
gcloud config set project TU_PROJECT_ID
```

#### Paso 2: Habilitar APIs

```bash
# Compute Engine API
gcloud services enable compute.googleapis.com

# Service Networking API
gcloud services enable servicenetworking.googleapis.com

# Cloud SQL API (opcional)
gcloud services enable sqladmin.googleapis.com
```

#### Paso 3: Crear Service Account

```bash
# Crear service account
gcloud iam service-accounts create terraform-farmacontrol \
    --display-name="Terraform FarmaControl" \
    --description="Service account for Terraform deployments"

# Asignar permisos
PROJECT_ID=$(gcloud config get-value project)
SA_EMAIL="terraform-farmacontrol@${PROJECT_ID}.iam.gserviceaccount.com"

gcloud projects add-iam-policy-binding $PROJECT_ID \
    --member="serviceAccount:${SA_EMAIL}" \
    --role="roles/compute.admin"

gcloud projects add-iam-policy-binding $PROJECT_ID \
    --member="serviceAccount:${SA_EMAIL}" \
    --role="roles/iam.serviceAccountUser"

gcloud projects add-iam-policy-binding $PROJECT_ID \
    --member="serviceAccount:${SA_EMAIL}" \
    --role="roles/storage.admin"

# Generar key
gcloud iam service-accounts keys create terraform-sa-key.json \
    --iam-account="${SA_EMAIL}"

echo "✅ Key guardada en: terraform-sa-key.json"
```

⚠️ **IMPORTANTE**: `terraform-sa-key.json` contiene credenciales sensibles. NUNCA la subas a Git.

#### Paso 4: Configurar Variables

```bash
cd terraform

# Copiar template
cp terraform.tfvars.example terraform.tfvars

# Editar con tus valores
nano terraform.tfvars
```

**terraform.tfvars** (ejemplo):
```hcl
project_id  = "mi-proyecto-123456"
region      = "us-central1"
zone        = "us-central1-a"
environment = "production"

machine_type   = "e2-medium"
boot_disk_size = 20

db_name     = "farmacontrol"
db_user     = "farmacontrol_user"
db_password = "TU_PASSWORD_SEGURO_123"
mysql_root_password = "TU_ROOT_PASSWORD_456"

# Generar con: openssl rand -base64 64
jwt_secret = "TU_JWT_SECRET_GENERADO"

github_repo   = "https://github.com/TU_USUARIO/farmacontrol-api.git"
github_branch = "main"
```

#### Paso 5: Inicializar Terraform

```bash
cd terraform
terraform init
```

---

## ⚙️ Configuración

### Estructura de Archivos

```
terraform/
├── main.tf              # Configuración principal
├── variables.tf         # Definición de variables
├── outputs.tf           # Outputs del deployment
├── terraform.tfvars     # ⚠️ Valores (gitignored)
├── terraform.tfvars.example  # Template
├── modules/
│   ├── compute/         # Módulo de VM
│   ├── network/         # Módulo de VPC/Firewall
│   └── database/        # Módulo de Cloud SQL (opcional)
└── scripts/
    └── startup.sh       # Script de inicialización de VM
```

### Variables Principales

| Variable | Descripción | Valor Default |
|----------|-------------|---------------|
| `project_id` | ID del proyecto GCP | *requerido* |
| `region` | Región de GCP | `us-central1` |
| `zone` | Zona de GCP | `us-central1-a` |
| `machine_type` | Tipo de VM | `e2-medium` |
| `boot_disk_size` | Tamaño disco (GB) | `20` |
| `db_password` | Password de DB | *requerido* |
| `jwt_secret` | Secret para JWT | *requerido* |
| `github_repo` | URL del repo | *requerido* |

### Costos Estimados

| Recurso | Tipo | Costo/mes (USD) |
|---------|------|-----------------|
| VM | e2-medium | ~$24 |
| Disco | 20 GB SSD | ~$3 |
| IP externa | Estándar | ~$3 |
| **Total** | | **~$30** |

---

## 🎯 Comandos Básicos

### Ver cambios sin aplicar (recomendado antes de apply)

```bash
cd terraform
terraform plan
```

### Aplicar cambios

```bash
terraform apply
```

Te pedirá confirmación. Escribe `yes` para continuar.

### Ver outputs

```bash
terraform output

# Output específico
terraform output vm_external_ip
```

### Ver estado

```bash
terraform show
```

### Formatear código

```bash
terraform fmt -recursive
```

### Validar configuración

```bash
terraform validate
```

---

## 🚀 Despliegue

### Primera vez

```bash
cd terraform

# 1. Ver qué se va a crear
terraform plan

# 2. Revisar el plan cuidadosamente

# 3. Aplicar cambios
terraform apply

# 4. Esperar 5-10 minutos

# 5. Ver la información de deployment
terraform output deployment_info
```

**Output esperado:**
```json
{
  "api_url" = "http://34.123.45.67:8080/api"
  "environment" = "production"
  "health_check" = "http://34.123.45.67:8080/actuator/health"
  "machine_type" = "e2-medium"
  "region" = "us-central1"
  "vm_external_ip" = "34.123.45.67"
  ...
}
```

### Verificar deployment

```bash
# Obtener IP
VM_IP=$(terraform output -raw vm_external_ip)

# Health check
curl http://$VM_IP:8080/actuator/health

# Swagger UI
open http://$VM_IP:8080/swagger-ui.html
```

### SSH a la VM

```bash
# Opción 1: Con terraform output
$(terraform output -raw ssh_command)

# Opción 2: Con gcloud
gcloud compute ssh production-farmacontrol-api --zone=us-central1-a

# Una vez dentro
docker ps                    # Ver contenedores
docker compose logs -f       # Ver logs
systemctl status farmacontrol # Ver servicio
```

---

## 🔄 Actualizar Infraestructura

### Cambiar tipo de máquina

1. Editar `terraform.tfvars`:
```hcl
machine_type = "e2-standard-2"  # 2 vCPU, 8 GB RAM
```

2. Aplicar cambios:
```bash
terraform plan    # Ver cambios
terraform apply   # Aplicar
```

Terraform mostrará:
```
~ update in-place
  ~ machine_type: "e2-medium" => "e2-standard-2"
```

### Cambiar variables de aplicación

Si cambias `db_password`, `jwt_secret`, etc., Terraform recreará la VM:

```
-/+ destroy and then create replacement
```

Esto es porque las variables se inyectan en el startup script.

### Actualizar código de la aplicación

El código se clona desde GitHub al iniciar la VM. Para actualizar:

**Opción 1: Forzar recreación**
```bash
terraform taint module.compute.google_compute_instance.vm_instance
terraform apply
```

**Opción 2: SSH y pull manual**
```bash
# Conectar a VM
gcloud compute ssh production-farmacontrol-api --zone=us-central1-a

# Pull y rebuild
cd /home/farmacontrol/farmacontrol-api
git pull origin main
mvn clean package -DskipTests
docker build -f docker/Dockerfile -t farmacontrol-api:latest .
docker compose -f docker/docker-compose.yml --env-file .env.production up -d --force-recreate
```

---

## 💥 Destruir Recursos

### ⚠️ CUIDADO: Esto elimina TODA la infraestructura

```bash
cd terraform

# Ver qué se va a destruir
terraform plan -destroy

# Destruir (con confirmación)
terraform destroy

# O forzar sin confirmación (peligroso)
terraform destroy -auto-approve
```

Esto eliminará:
- ❌ VM
- ❌ Discos
- ❌ IPs
- ❌ Firewall rules
- ❌ VPC network

**No eliminará**:
- ✅ Proyecto de GCP
- ✅ Service accounts
- ✅ APIs habilitadas

---

## 🐛 Troubleshooting

### Error: "API not enabled"

```
Error: Error creating instance: googleapi: Error 403: 
Compute Engine API has not been used in project...
```

**Solución:**
```bash
gcloud services enable compute.googleapis.com --project=TU_PROJECT_ID
```

### Error: "Insufficient Permission"

```
Error: Error creating instance: googleapi: Error 403: 
Required 'compute.instances.create' permission
```

**Solución:**
Verificar que la service account tenga el rol `roles/compute.admin`:
```bash
gcloud projects get-iam-policy TU_PROJECT_ID \
  --flatten="bindings[].members" \
  --filter="bindings.members:serviceAccount:terraform-farmacontrol@*"
```

### Error: "Quota exceeded"

```
Error: Error creating instance: googleapi: Error 429: 
Quota 'CPUS' exceeded.
```

**Solución:**
1. Ve a https://console.cloud.google.com/iam-admin/quotas
2. Busca "Compute Engine API"
3. Solicita aumento de cuota

O cambia a una región con más disponibilidad:
```hcl
region = "us-west1"
zone   = "us-west1-a"
```

### La VM se crea pero la API no inicia

```bash
# SSH a la VM
gcloud compute ssh production-farmacontrol-api --zone=us-central1-a

# Ver logs del startup script
sudo tail -f /var/log/farmacontrol-startup.log

# Ver logs de Docker
docker compose -f /home/farmacontrol/farmacontrol-api/docker/docker-compose.yml logs
```

**Problemas comunes:**
- Compilación de Maven falla → Verificar `pom.xml`
- Docker build falla → Verificar `Dockerfile`
- Variables incorrectas → Revisar `.env.production` en la VM

### Terraform state locked

```
Error: Error acquiring the state lock
```

**Solución:**
```bash
# Si estás seguro que no hay otro apply corriendo
terraform force-unlock LOCK_ID
```

---

## 🔒 Seguridad

### Mejores Prácticas

1. ✅ **NUNCA** commitees archivos sensibles:
   - `terraform.tfvars`
   - `*-sa-key.json`
   - `.env.production`

2. ✅ Usa **variables de entorno** para CI/CD:
```bash
export TF_VAR_db_password="..."
export TF_VAR_jwt_secret="..."
terraform apply
```

3. ✅ Rota credenciales regularmente:
```bash
# Generar nuevo JWT secret
openssl rand -base64 64
```

4. ✅ Usa **backend remoto** para el estado:
```hcl
terraform {
  backend "gcs" {
    bucket = "farmacontrol-terraform-state"
    prefix = "terraform/state"
  }
}
```

Crear bucket:
```bash
gsutil mb -p TU_PROJECT_ID -l us-central1 gs://farmacontrol-terraform-state
gsutil versioning set on gs://farmacontrol-terraform-state
```

---

## 📚 Recursos Adicionales

- [Terraform Documentation](https://www.terraform.io/docs)
- [Google Cloud Provider](https://registry.terraform.io/providers/hashicorp/google/latest/docs)
- [Terraform Best Practices](https://www.terraform-best-practices.com/)
- [Google Cloud Pricing Calculator](https://cloud.google.com/products/calculator)

---

## 🆘 Obtener Ayuda

1. **Ver documentación inline:**
```bash
terraform -help
terraform plan -help
```

2. **Ver schema de recursos:**
```bash
terraform providers schema -json | jq '.provider_schemas'
```

3. **Issues del proyecto:**
GitHub Issues del repositorio

---

## ✅ Checklist de Deployment

- [ ] Terraform instalado
- [ ] gcloud CLI instalado y autenticado
- [ ] Proyecto de GCP creado
- [ ] APIs habilitadas
- [ ] Service account creada con permisos
- [ ] `terraform-sa-key.json` descargado
- [ ] `terraform.tfvars` configurado
- [ ] `terraform init` ejecutado
- [ ] `terraform plan` revisado
- [ ] `terraform apply` completado exitosamente
- [ ] Health check responde
- [ ] API accesible desde internet
- [ ] GitHub Actions configurado (opcional)

---

¡Listo para desplegar! 🚀
