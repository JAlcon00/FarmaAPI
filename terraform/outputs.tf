# ============================================================================
# TERRAFORM OUTPUTS - FARMACONTROL API
# ============================================================================

# ============================================================================
# COMPUTE OUTPUTS
# ============================================================================

output "vm_instance_name" {
  description = "Nombre de la instancia de VM"
  value       = module.compute.instance_name
}

output "vm_instance_id" {
  description = "ID de la instancia de VM"
  value       = module.compute.instance_id
}

output "vm_external_ip" {
  description = "IP externa de la VM"
  value       = module.compute.external_ip
}

output "vm_internal_ip" {
  description = "IP interna de la VM"
  value       = module.compute.internal_ip
}

output "vm_self_link" {
  description = "Self link de la VM"
  value       = module.compute.self_link
}

# ============================================================================
# NETWORK OUTPUTS
# ============================================================================

output "network_name" {
  description = "Nombre de la red VPC"
  value       = module.network.network_name
}

output "subnet_name" {
  description = "Nombre de la subred"
  value       = module.network.subnet_name
}

output "firewall_rules" {
  description = "Reglas de firewall creadas"
  value       = module.network.firewall_rule_names
}

# ============================================================================
# DATABASE OUTPUTS (solo si use_cloud_sql = true)
# ============================================================================

# output "cloud_sql_instance_name" {
#   description = "Nombre de la instancia de Cloud SQL"
#   value       = var.use_cloud_sql ? module.cloud_sql[0].instance_name : null
# }

# output "cloud_sql_connection_name" {
#   description = "Connection name para Cloud SQL"
#   value       = var.use_cloud_sql ? module.cloud_sql[0].instance_connection_name : null
# }

# output "cloud_sql_public_ip" {
#   description = "IP pública de Cloud SQL"
#   value       = var.use_cloud_sql ? module.cloud_sql[0].public_ip_address : null
# }

# ============================================================================
# APPLICATION URLS
# ============================================================================

output "api_url" {
  description = "URL de la API"
  value       = "http://${module.compute.external_ip}:${var.server_port}/api"
}

output "swagger_url" {
  description = "URL de Swagger UI"
  value       = "http://${module.compute.external_ip}:${var.server_port}/swagger-ui.html"
}

output "health_check_url" {
  description = "URL del health check"
  value       = "http://${module.compute.external_ip}:${var.server_port}/actuator/health"
}

# ============================================================================
# SSH COMMAND
# ============================================================================

output "ssh_command" {
  description = "Comando para conectar por SSH"
  value       = "gcloud compute ssh ${module.compute.instance_name} --zone=${var.zone} --project=${var.project_id}"
}

# ============================================================================
# DEPLOYMENT INFO
# ============================================================================

output "deployment_info" {
  description = "Información completa del deployment"
  value = {
    environment     = var.environment
    region          = var.region
    zone            = var.zone
    vm_external_ip  = module.compute.external_ip
    api_url         = "http://${module.compute.external_ip}:${var.server_port}/api"
    health_check    = "http://${module.compute.external_ip}:${var.server_port}/actuator/health"
    swagger_ui      = "http://${module.compute.external_ip}:${var.server_port}/swagger-ui.html"
    using_cloud_sql = var.use_cloud_sql
    machine_type    = var.machine_type
    disk_size_gb    = var.boot_disk_size
  }
}
