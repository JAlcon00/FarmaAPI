# ============================================================================
# NETWORK MODULE - OUTPUTS
# ============================================================================

output "network_name" {
  description = "Nombre de la red VPC"
  value       = google_compute_network.vpc_network.name
}

output "network_id" {
  description = "ID de la red VPC"
  value       = google_compute_network.vpc_network.id
}

output "network_self_link" {
  description = "Self link de la red VPC"
  value       = google_compute_network.vpc_network.self_link
}

output "subnet_name" {
  description = "Nombre de la subred"
  value       = google_compute_subnetwork.subnet.name
}

output "subnet_id" {
  description = "ID de la subred"
  value       = google_compute_subnetwork.subnet.id
}

output "subnet_self_link" {
  description = "Self link de la subred"
  value       = google_compute_subnetwork.subnet.self_link
}

output "subnet_cidr" {
  description = "CIDR de la subred"
  value       = google_compute_subnetwork.subnet.ip_cidr_range
}

output "firewall_rule_names" {
  description = "Nombres de las reglas de firewall creadas"
  value = [
    google_compute_firewall.allow_internal.name,
    google_compute_firewall.allow_ssh.name,
    google_compute_firewall.allow_http.name,
    google_compute_firewall.allow_custom_ports.name,
  ]
}
