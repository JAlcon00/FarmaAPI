#!/bin/bash
# ============================================================================
# TERRAFORM SETUP SCRIPT
# ============================================================================
# Este script ayuda a configurar Terraform por primera vez
# ============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
TERRAFORM_DIR="$PROJECT_ROOT/terraform"

echo "============================================================================"
echo "🔧 Terraform Setup - FarmaControl API"
echo "============================================================================"
echo ""

# ============================================================================
# 1. Verificar prerequisitos
# ============================================================================

echo "📋 Step 1: Verificando prerequisitos..."
echo ""

# Verificar Terraform
if ! command -v terraform &> /dev/null; then
    echo "❌ Terraform no está instalado"
    echo ""
    echo "Instalar con:"
    echo "  macOS:   brew install terraform"
    echo "  Ubuntu:  wget -O- https://apt.releases.hashicorp.com/gpg | sudo gpg --dearmor -o /usr/share/keyrings/hashicorp-archive-keyring.gpg"
    echo "          echo \"deb [signed-by=/usr/share/keyrings/hashicorp-archive-keyring.gpg] https://apt.releases.hashicorp.com \$(lsb_release -cs) main\" | sudo tee /etc/apt/sources.list.d/hashicorp.list"
    echo "          sudo apt update && sudo apt install terraform"
    echo ""
    exit 1
fi

echo "✅ Terraform $(terraform version | head -n 1)"

# Verificar gcloud CLI
if ! command -v gcloud &> /dev/null; then
    echo "⚠️  gcloud CLI no está instalado (opcional pero recomendado)"
    echo ""
    echo "Instalar desde: https://cloud.google.com/sdk/docs/install"
    echo ""
else
    echo "✅ gcloud CLI instalado"
fi

echo ""

# ============================================================================
# 2. Configurar Google Cloud Project
# ============================================================================

echo "📋 Step 2: Configurar Google Cloud Project"
echo ""

read -p "¿Ya tienes un proyecto en Google Cloud? (y/n): " has_project

if [ "$has_project" != "y" ]; then
    echo ""
    echo "Por favor, sigue estos pasos:"
    echo "1. Ve a https://console.cloud.google.com/"
    echo "2. Crea un nuevo proyecto o selecciona uno existente"
    echo "3. Anota el Project ID (no el nombre, el ID)"
    echo ""
    read -p "Presiona Enter cuando estés listo..."
fi

echo ""
read -p "Ingresa tu GCP Project ID: " project_id

if [ -z "$project_id" ]; then
    echo "❌ Project ID es requerido"
    exit 1
fi

echo "✅ Project ID: $project_id"
echo ""

# ============================================================================
# 3. Habilitar APIs necesarias
# ============================================================================

echo "📋 Step 3: Habilitar APIs de Google Cloud"
echo ""

if command -v gcloud &> /dev/null; then
    echo "Habilitando APIs necesarias..."
    
    gcloud config set project "$project_id" 2>/dev/null || true
    
    gcloud services enable compute.googleapis.com --project="$project_id" || echo "⚠️  Error enabling Compute Engine API"
    gcloud services enable servicenetworking.googleapis.com --project="$project_id" || echo "⚠️  Error enabling Service Networking API"
    gcloud services enable sqladmin.googleapis.com --project="$project_id" || echo "⚠️  Error enabling Cloud SQL Admin API (optional)"
    
    echo "✅ APIs habilitadas"
else
    echo "⚠️  gcloud CLI no disponible, habilita manualmente:"
    echo "  - Compute Engine API"
    echo "  - Service Networking API"
    echo "  - Cloud SQL Admin API (opcional)"
    echo ""
    echo "En: https://console.cloud.google.com/apis/library?project=$project_id"
    read -p "Presiona Enter cuando hayas habilitado las APIs..."
fi

echo ""

# ============================================================================
# 4. Crear Service Account para Terraform
# ============================================================================

echo "📋 Step 4: Crear Service Account"
echo ""

SA_NAME="terraform-farmacontrol"
SA_EMAIL="$SA_NAME@$project_id.iam.gserviceaccount.com"

if command -v gcloud &> /dev/null; then
    echo "Creando service account: $SA_NAME..."
    
    # Crear service account
    gcloud iam service-accounts create "$SA_NAME" \
        --display-name="Terraform FarmaControl" \
        --description="Service account for Terraform deployments" \
        --project="$project_id" 2>/dev/null || echo "⚠️  Service account ya existe"
    
    # Asignar roles
    echo "Asignando permisos..."
    gcloud projects add-iam-policy-binding "$project_id" \
        --member="serviceAccount:$SA_EMAIL" \
        --role="roles/compute.admin" --quiet
    
    gcloud projects add-iam-policy-binding "$project_id" \
        --member="serviceAccount:$SA_EMAIL" \
        --role="roles/iam.serviceAccountUser" --quiet
    
    gcloud projects add-iam-policy-binding "$project_id" \
        --member="serviceAccount:$SA_EMAIL" \
        --role="roles/storage.admin" --quiet
    
    # Crear key
    KEY_FILE="$PROJECT_ROOT/terraform-sa-key.json"
    
    if [ ! -f "$KEY_FILE" ]; then
        echo "Generando key file..."
        gcloud iam service-accounts keys create "$KEY_FILE" \
            --iam-account="$SA_EMAIL" \
            --project="$project_id"
        
        echo "✅ Key guardada en: $KEY_FILE"
        echo "⚠️  IMPORTANTE: Este archivo contiene credenciales sensibles"
    else
        echo "✅ Key file ya existe: $KEY_FILE"
    fi
else
    echo "Por favor, crea manualmente:"
    echo "1. Ve a IAM & Admin > Service Accounts"
    echo "2. Crea service account: $SA_NAME"
    echo "3. Asigna roles: Compute Admin, Service Account User, Storage Admin"
    echo "4. Crea y descarga una JSON key"
    echo "5. Guarda el archivo como: terraform-sa-key.json"
    echo ""
    read -p "Presiona Enter cuando hayas creado la service account..."
fi

echo ""

# ============================================================================
# 5. Crear terraform.tfvars
# ============================================================================

echo "📋 Step 5: Configurar terraform.tfvars"
echo ""

cd "$TERRAFORM_DIR"

if [ -f "terraform.tfvars" ]; then
    echo "⚠️  terraform.tfvars ya existe"
    read -p "¿Quieres sobrescribirlo? (y/n): " overwrite
    if [ "$overwrite" != "y" ]; then
        echo "Saltando creación de terraform.tfvars"
        echo ""
    else
        rm terraform.tfvars
    fi
fi

if [ ! -f "terraform.tfvars" ]; then
    echo "Configurando variables..."
    echo ""
    
    read -p "Región (default: us-central1): " region
    region=${region:-us-central1}
    
    read -p "Zona (default: ${region}-a): " zone
    zone=${zone:-${region}-a}
    
    read -p "DB Password: " -s db_password
    echo ""
    read -p "MySQL Root Password: " -s mysql_root_password
    echo ""
    
    echo "Generando JWT Secret..."
    jwt_secret=$(openssl rand -base64 64 | tr -d '\n')
    
    read -p "GitHub Repo URL: " github_repo
    
    cat > terraform.tfvars <<EOF
# Generado por setup-terraform.sh
project_id  = "$project_id"
region      = "$region"
zone        = "$zone"
environment = "production"

machine_type   = "e2-medium"
boot_disk_size = 20
boot_disk_type = "pd-balanced"

allowed_ips = ["0.0.0.0/0"]

use_cloud_sql = false

db_name     = "farmacontrol"
db_user     = "farmacontrol_user"
db_password = "$db_password"
mysql_root_password = "$mysql_root_password"

jwt_secret             = "$jwt_secret"
jwt_expiration         = 86400000
jwt_refresh_expiration = 604800000

server_port = 8080

github_repo   = "$github_repo"
github_branch = "main"

domain_name      = ""
dns_managed_zone = ""
EOF

    chmod 600 terraform.tfvars
    echo "✅ terraform.tfvars creado"
fi

echo ""

# ============================================================================
# 6. Inicializar Terraform
# ============================================================================

echo "📋 Step 6: Inicializar Terraform"
echo ""

terraform init

echo "✅ Terraform inicializado"
echo ""

# ============================================================================
# 7. Validar configuración
# ============================================================================

echo "📋 Step 7: Validar configuración"
echo ""

terraform validate

echo "✅ Configuración válida"
echo ""

# ============================================================================
# RESUMEN
# ============================================================================

echo "============================================================================"
echo "✅ SETUP COMPLETADO"
echo "============================================================================"
echo ""
echo "📁 Archivos creados:"
echo "  - terraform.tfvars (variables de configuración)"
if [ -f "$PROJECT_ROOT/terraform-sa-key.json" ]; then
    echo "  - terraform-sa-key.json (credenciales GCP)"
fi
echo ""
echo "🚀 Próximos pasos:"
echo ""
echo "1. Revisar configuración:"
echo "   cd terraform && terraform plan"
echo ""
echo "2. Desplegar infraestructura:"
echo "   terraform apply"
echo ""
echo "3. Ver outputs:"
echo "   terraform output"
echo ""
echo "⚠️  IMPORTANTE:"
echo "  - terraform.tfvars y terraform-sa-key.json están en .gitignore"
echo "  - NUNCA commitees estos archivos a Git"
echo "  - Para GitHub Actions, configura GitHub Secrets"
echo ""
