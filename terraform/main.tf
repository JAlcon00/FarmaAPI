# ============================================================================
# FARMACONTROL API - TERRAFORM CONFIGURATION
# ============================================================================
# Despliegue de infraestructura en Google Cloud Platform
# - Compute Engine VM para la API
# - Cloud SQL MySQL (opcional, comentado por defecto)
# - VPC Network y Firewall rules
# ============================================================================

terraform {
  required_version = ">= 1.5.0"
  
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 5.0"
    }
  }

  # Backend para almacenar el estado en Google Cloud Storage
  # Descomentar después de crear el bucket manualmente
  # backend "gcs" {
  #   bucket = "farmacontrol-terraform-state"
  #   prefix = "terraform/state"
  # }
}

# ============================================================================
# PROVIDER CONFIGURATION
# ============================================================================

provider "google" {
  project = var.project_id
  region  = var.region
  zone    = var.zone
}

# ============================================================================
# DATA SOURCES
# ============================================================================

# Obtener la última imagen de Ubuntu 22.04 LTS
data "google_compute_image" "ubuntu" {
  family  = "ubuntu-2204-lts"
  project = "ubuntu-os-cloud"
}

# ============================================================================
# NETWORK MODULE
# ============================================================================

module "network" {
  source = "./modules/network"

  project_id   = var.project_id
  network_name = "${var.environment}-network"
  subnet_name  = "${var.environment}-subnet"
  region       = var.region

  allowed_ports = [
    "22",   # SSH
    "80",   # HTTP
    "443",  # HTTPS
    "8080", # API
  ]

  allowed_source_ranges = var.allowed_ips
}

# ============================================================================
# COMPUTE MODULE
# ============================================================================

module "compute" {
  source = "./modules/compute"

  project_id = var.project_id
  zone       = var.zone
  
  instance_name  = "${var.environment}-farmacontrol-api"
  machine_type   = var.machine_type
  boot_disk_size = var.boot_disk_size
  boot_disk_type = var.boot_disk_type
  
  image_self_link = data.google_compute_image.ubuntu.self_link
  
  network_self_link = module.network.network_self_link
  subnet_self_link  = module.network.subnet_self_link

  # Metadata para startup script
  metadata = {
    startup-script = templatefile("${path.module}/scripts/startup.sh", {
      db_host                = var.use_cloud_sql ? module.cloud_sql[0].instance_connection_name : "mysql-db"
      db_name                = var.db_name
      db_user                = var.db_user
      db_password            = var.db_password
      mysql_root_password    = var.mysql_root_password
      jwt_secret             = var.jwt_secret
      jwt_expiration         = var.jwt_expiration
      jwt_refresh_expiration = var.jwt_refresh_expiration
      server_port            = var.server_port
      github_repo            = var.github_repo
      github_branch          = var.github_branch
    })
  }

  # Tags para identificación y firewall
  tags = [
    "${var.environment}-api",
    "http-server",
    "https-server"
  ]

  # Labels para organización
  labels = {
    environment = var.environment
    application = "farmacontrol"
    managed_by  = "terraform"
  }
}

# ============================================================================
# CLOUD SQL (OPCIONAL - comentado por defecto)
# ============================================================================
# Para usar Cloud SQL MySQL en lugar de MySQL en Docker:
# 1. Descomentar este módulo
# 2. Configurar var.use_cloud_sql = true
# 3. IMPORTANTE: Cloud SQL tiene costo adicional (~$10-50/mes)

# module "cloud_sql" {
#   count  = var.use_cloud_sql ? 1 : 0
#   source = "./modules/database"
#
#   project_id = var.project_id
#   region     = var.region
#
#   instance_name = "${var.environment}-farmacontrol-mysql"
#   database_version = "MYSQL_8_0"
#   tier = var.db_tier
#
#   database_name = var.db_name
#   user_name     = var.db_user
#   user_password = var.db_password
#
#   network_id = module.network.network_id
#
#   backup_enabled = var.environment == "production"
#   
#   labels = {
#     environment = var.environment
#     application = "farmacontrol"
#     managed_by  = "terraform"
#   }
# }

# ============================================================================
# STATIC IP (OPCIONAL)
# ============================================================================
# Reservar una IP estática para producción
# Descomentar si necesitas una IP fija

# resource "google_compute_address" "api_static_ip" {
#   name   = "${var.environment}-farmacontrol-api-ip"
#   region = var.region
# }

# ============================================================================
# DNS (OPCIONAL)
# ============================================================================
# Si tienes un dominio en Cloud DNS, puedes crear el registro A

# resource "google_dns_record_set" "api" {
#   count        = var.domain_name != "" ? 1 : 0
#   name         = "api.${var.domain_name}."
#   type         = "A"
#   ttl          = 300
#   managed_zone = var.dns_managed_zone
#   rrdatas      = [module.compute.external_ip]
# }
