# 🏗️ Terraform - Infrastructure as Code

Configuración de Terraform para desplegar FarmaControl API en Google Cloud Platform.

---

## 📁 Estructura

```
terraform/
├── main.tf              # Configuración principal
├── variables.tf         # Definición de variables
├── outputs.tf           # Outputs del deployment
├── terraform.tfvars     # ⚠️ Valores (gitignored)
├── terraform.tfvars.example  # Template seguro para commit
├── modules/
│   ├── compute/         # Módulo de Compute Engine (VM)
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   ├── network/         # Módulo de VPC y Firewall
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   └── database/        # Módulo de Cloud SQL (opcional)
└── scripts/
    └── startup.sh       # Script de inicialización de VM
```

---

## 🚀 Quick Start

### 1. Prerequisitos

```bash
# Instalar Terraform
brew install terraform  # macOS
# O seguir: https://www.terraform.io/downloads

# Instalar gcloud CLI
brew install --cask google-cloud-sdk  # macOS
# O seguir: https://cloud.google.com/sdk/docs/install

# Verificar
terraform --version
gcloud --version
```

### 2. Setup Automático (Recomendado)

```bash
# Desde la raíz del proyecto
./scripts/setup-terraform.sh
```

El script te guiará para:
- ✅ Configurar proyecto de GCP
- ✅ Habilitar APIs necesarias
- ✅ Crear service account
- ✅ Generar terraform.tfvars
- ✅ Inicializar Terraform

### 3. Setup Manual

```bash
# 1. Copiar template
cd terraform
cp terraform.tfvars.example terraform.tfvars

# 2. Editar con tus valores
nano terraform.tfvars

# 3. Inicializar Terraform
terraform init

# 4. Ver plan de cambios
terraform plan

# 5. Aplicar cambios
terraform apply
```

---

## 📋 Recursos Creados

Este Terraform crea:

| Recurso | Tipo | Descripción |
|---------|------|-------------|
| **VM** | `google_compute_instance` | VM con Ubuntu 22.04 para la API |
| **VPC** | `google_compute_network` | Red privada virtual |
| **Subnet** | `google_compute_subnetwork` | Subred con logs habilitados |
| **Firewall** | `google_compute_firewall` (4 rules) | Reglas para SSH, HTTP, API |
| **Disk** | Boot disk | Disco SSD de 20 GB |

**Total estimado:** ~$30/mes

---

## ⚙️ Variables Principales

| Variable | Descripción | Default |
|----------|-------------|---------|
| `project_id` | ID del proyecto GCP | *requerido* |
| `region` | Región de GCP | `us-central1` |
| `zone` | Zona de GCP | `us-central1-a` |
| `machine_type` | Tipo de VM | `e2-medium` (2 vCPU, 4GB) |
| `boot_disk_size` | Tamaño disco (GB) | `20` |
| `db_password` | Password de DB | *requerido* |
| `jwt_secret` | Secret para JWT | *requerido* |
| `github_repo` | URL del repositorio | *requerido* |

Ver todas las variables en [`variables.tf`](./variables.tf)

---

## 🔐 Secrets y Seguridad

### Archivos Sensibles (NO commitear)

```
terraform/
├── terraform.tfvars          ← Contiene passwords
├── .terraform/               ← Estado local
├── *.tfstate                 ← Estado de infraestructura
└── terraform-sa-key.json     ← Credenciales GCP
```

Todos están en `.gitignore` ✅

### Generar Secrets

```bash
# DB Password (ejemplo)
openssl rand -base64 32

# JWT Secret
openssl rand -base64 64

# MySQL Root Password
openssl rand -base64 32
```

---

## 🎯 Comandos Útiles

### Ver cambios sin aplicar
```bash
terraform plan
```

### Aplicar cambios
```bash
terraform apply
```

### Ver outputs
```bash
terraform output

# Output específico
terraform output vm_external_ip
terraform output api_url
```

### Formatear código
```bash
terraform fmt -recursive
```

### Validar configuración
```bash
terraform validate
```

### Ver estado
```bash
terraform show
```

### Destruir todo
```bash
terraform destroy
```

### SSH a la VM
```bash
$(terraform output -raw ssh_command)
```

---

## 🔄 Actualizar Infraestructura

### Cambiar tipo de máquina

1. Editar `terraform.tfvars`:
```hcl
machine_type = "e2-standard-2"  # Más potente
```

2. Aplicar:
```bash
terraform plan   # Ver cambios
terraform apply  # Aplicar
```

### Cambiar región

⚠️ **Esto destruirá y recreará la VM**

1. Editar `terraform.tfvars`:
```hcl
region = "us-west1"
zone   = "us-west1-a"
```

2. Aplicar:
```bash
terraform apply
```

### Actualizar código de la aplicación

El código se clona al iniciar la VM. Para actualizar:

**Opción 1: Recrear VM**
```bash
terraform taint module.compute.google_compute_instance.vm_instance
terraform apply
```

**Opción 2: SSH y pull manual**
```bash
# Ver comando SSH
terraform output ssh_command

# Conectar
$(terraform output -raw ssh_command)

# Una vez dentro
cd /home/farmacontrol/farmacontrol-api
git pull origin main
mvn clean package -DskipTests
docker compose -f docker/docker-compose.yml up -d --force-recreate
```

---

## 🐳 Ver Estado de la Aplicación

```bash
# SSH a la VM
$(terraform output -raw ssh_command)

# Ver contenedores
docker ps

# Ver logs
docker compose -f /home/farmacontrol/farmacontrol-api/docker/docker-compose.yml logs -f

# Ver servicio systemd
systemctl status farmacontrol

# Ver logs del startup
sudo tail -f /var/log/farmacontrol-startup.log
```

---

## 📊 Outputs Disponibles

Después de `terraform apply`, puedes acceder:

```bash
# IP externa
terraform output vm_external_ip
# Output: 34.123.45.67

# URL de la API
terraform output api_url
# Output: http://34.123.45.67:8080/api

# Health check
terraform output health_check_url
# Output: http://34.123.45.67:8080/actuator/health

# Swagger UI
terraform output swagger_url
# Output: http://34.123.45.67:8080/swagger-ui.html

# Comando SSH
terraform output ssh_command
# Output: gcloud compute ssh production-farmacontrol-api --zone=us-central1-a --project=mi-proyecto

# Información completa
terraform output deployment_info
# Output: JSON con toda la info
```

---

## 🔍 Troubleshooting

### Error: "API not enabled"
```bash
gcloud services enable compute.googleapis.com --project=TU_PROJECT_ID
```

### Error: "Insufficient Permission"
```bash
# Verificar roles de la service account
gcloud projects get-iam-policy TU_PROJECT_ID
```

### La VM se crea pero la API no inicia
```bash
# SSH y ver logs
$(terraform output -raw ssh_command)
sudo tail -f /var/log/farmacontrol-startup.log
```

### Terraform state locked
```bash
terraform force-unlock LOCK_ID
```

---

## 🔗 Módulos

### Compute Module (`modules/compute/`)

Crea la VM de Compute Engine con:
- Ubuntu 22.04 LTS
- Tipo de máquina configurable
- Disco SSD
- Startup script automático
- Shielded VM habilitado
- Labels para organización

### Network Module (`modules/network/`)

Crea la infraestructura de red:
- VPC personalizada
- Subnet con logs habilitados
- Firewall rules:
  - SSH (puerto 22)
  - HTTP/HTTPS (puertos 80, 443)
  - API (puerto 8080)
  - Internal traffic

### Database Module (`modules/database/`) - Opcional

⚠️ **Comentado por defecto** (tiene costo adicional)

Para usar Cloud SQL en lugar de MySQL en Docker:
1. Descomentar módulo en `main.tf`
2. Configurar `use_cloud_sql = true` en `terraform.tfvars`

Crea:
- Cloud SQL MySQL 8.0
- Base de datos
- Usuario
- Backups automáticos (en producción)

**Costo adicional:** ~$10-50/mes según tier

---

## 📚 Documentación Adicional

- **[TERRAFORM-SETUP.md](../docs/TERRAFORM-SETUP.md)** - Guía completa de setup
- **[GITHUB-ACTIONS-SETUP.md](../docs/GITHUB-ACTIONS-SETUP.md)** - CI/CD automático
- [Terraform Google Provider](https://registry.terraform.io/providers/hashicorp/google/latest/docs)
- [Google Cloud Documentation](https://cloud.google.com/docs)

---

## 🆘 Obtener Ayuda

```bash
# Help de Terraform
terraform -help
terraform plan -help

# Ver schema de recursos
terraform providers schema -json

# Logs de gcloud
gcloud logging read "resource.type=gce_instance" --limit 50
```

---

## ✅ Checklist

Antes de `terraform apply`:

- [ ] `terraform.tfvars` configurado con valores reales
- [ ] Service account creada en GCP
- [ ] APIs habilitadas (Compute Engine, Service Networking)
- [ ] `terraform init` ejecutado
- [ ] `terraform plan` revisado sin errores
- [ ] Credenciales GCP configuradas

Después de `terraform apply`:

- [ ] VM creada y corriendo
- [ ] Health check responde: `curl $(terraform output -raw health_check_url)`
- [ ] API accesible: `curl $(terraform output -raw api_url)/productos`
- [ ] Swagger UI funciona: abrir `$(terraform output -raw swagger_url)`

---

¡Listo para desplegar! 🚀
