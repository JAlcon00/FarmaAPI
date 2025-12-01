# ============================================================================
# TERRAFORM VARIABLES - FARMACONTROL API
# ============================================================================

# ============================================================================
# GOOGLE CLOUD PROJECT
# ============================================================================

variable "project_id" {
  description = "ID del proyecto de Google Cloud"
  type        = string
}

variable "region" {
  description = "Región de GCP para los recursos"
  type        = string
  default     = "us-central1"
}

variable "zone" {
  description = "Zona de GCP para la VM"
  type        = string
  default     = "us-central1-a"
}

variable "environment" {
  description = "Ambiente de despliegue (dev, staging, production)"
  type        = string
  default     = "production"
  
  validation {
    condition     = contains(["dev", "staging", "production"], var.environment)
    error_message = "El ambiente debe ser: dev, staging o production"
  }
}

# ============================================================================
# COMPUTE ENGINE
# ============================================================================

variable "machine_type" {
  description = "Tipo de máquina para la VM"
  type        = string
  default     = "e2-medium" # 2 vCPU, 4 GB RAM
  
  # Opciones:
  # e2-micro:    0.25-2 vCPU, 1 GB RAM   (~$6/mes)
  # e2-small:    0.5-2 vCPU,  2 GB RAM   (~$13/mes)
  # e2-medium:   2 vCPU,      4 GB RAM   (~$24/mes) ← Recomendado
  # e2-standard: 2 vCPU,      8 GB RAM   (~$49/mes)
}

variable "boot_disk_size" {
  description = "Tamaño del disco en GB"
  type        = number
  default     = 20
}

variable "boot_disk_type" {
  description = "Tipo de disco (pd-standard, pd-ssd, pd-balanced)"
  type        = string
  default     = "pd-balanced" # Balance entre costo y rendimiento
}

# ============================================================================
# NETWORK & SECURITY
# ============================================================================

variable "allowed_ips" {
  description = "IPs permitidas para acceder a la API (0.0.0.0/0 = todo internet)"
  type        = list(string)
  default     = ["0.0.0.0/0"]
  
  # Para restringir acceso a IPs específicas:
  # default = ["YOUR_IP/32", "OFFICE_IP/24"]
}

# ============================================================================
# DATABASE
# ============================================================================

variable "use_cloud_sql" {
  description = "Usar Cloud SQL en lugar de MySQL en Docker (costo adicional)"
  type        = bool
  default     = false
}

variable "db_tier" {
  description = "Tier de Cloud SQL (solo si use_cloud_sql = true)"
  type        = string
  default     = "db-f1-micro" # Tier más económico
  
  # Opciones:
  # db-f1-micro:    1 vCPU,  0.6 GB RAM (~$7/mes)
  # db-g1-small:    1 vCPU,  1.7 GB RAM (~$25/mes)
  # db-n1-standard: 1+ vCPU, 3.75+ GB   (~$50+/mes)
}

variable "db_name" {
  description = "Nombre de la base de datos"
  type        = string
  default     = "farmacontrol"
}

variable "db_user" {
  description = "Usuario de la base de datos"
  type        = string
  default     = "farmacontrol_user"
}

variable "db_password" {
  description = "Contraseña de la base de datos (usar en terraform.tfvars o como variable de entorno)"
  type        = string
  sensitive   = true
}

variable "mysql_root_password" {
  description = "Contraseña root de MySQL (solo para MySQL en Docker)"
  type        = string
  sensitive   = true
}

# ============================================================================
# APPLICATION
# ============================================================================

variable "jwt_secret" {
  description = "Secret para JWT tokens (generar con: openssl rand -base64 64)"
  type        = string
  sensitive   = true
}

variable "jwt_expiration" {
  description = "Tiempo de expiración del JWT en milisegundos"
  type        = number
  default     = 86400000 # 24 horas
}

variable "jwt_refresh_expiration" {
  description = "Tiempo de expiración del refresh token en milisegundos"
  type        = number
  default     = 604800000 # 7 días
}

variable "server_port" {
  description = "Puerto del servidor API"
  type        = number
  default     = 8080
}

# ============================================================================
# GIT REPOSITORY
# ============================================================================

variable "github_repo" {
  description = "URL del repositorio de GitHub (https://github.com/usuario/repo.git)"
  type        = string
}

variable "github_branch" {
  description = "Rama a desplegar"
  type        = string
  default     = "main"
}

# ============================================================================
# DNS (OPCIONAL)
# ============================================================================

variable "domain_name" {
  description = "Dominio para la API (ejemplo: farmacontrol.com) - Dejar vacío si no se usa"
  type        = string
  default     = ""
}

variable "dns_managed_zone" {
  description = "Zona administrada de Cloud DNS (solo si domain_name está configurado)"
  type        = string
  default     = ""
}
