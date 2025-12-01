# ============================================================================
# COMPUTE MODULE - OUTPUTS
# ============================================================================

output "instance_name" {
  description = "Nombre de la instancia"
  value       = google_compute_instance.vm_instance.name
}

output "instance_id" {
  description = "ID de la instancia"
  value       = google_compute_instance.vm_instance.instance_id
}

output "external_ip" {
  description = "IP externa de la instancia"
  value       = google_compute_instance.vm_instance.network_interface[0].access_config[0].nat_ip
}

output "internal_ip" {
  description = "IP interna de la instancia"
  value       = google_compute_instance.vm_instance.network_interface[0].network_ip
}

output "self_link" {
  description = "Self link de la instancia"
  value       = google_compute_instance.vm_instance.self_link
}

output "zone" {
  description = "Zona de la instancia"
  value       = google_compute_instance.vm_instance.zone
}
