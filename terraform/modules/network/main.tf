# ============================================================================
# NETWORK MODULE - VPC & FIREWALL
# ============================================================================

# VPC Network
resource "google_compute_network" "vpc_network" {
  name                    = var.network_name
  auto_create_subnetworks = false
  project                 = var.project_id
}

# Subnet
resource "google_compute_subnetwork" "subnet" {
  name          = var.subnet_name
  ip_cidr_range = var.subnet_cidr
  region        = var.region
  network       = google_compute_network.vpc_network.id
  project       = var.project_id

  # Habilitar logs de flujo (útil para debugging)
  log_config {
    aggregation_interval = "INTERVAL_5_SEC"
    flow_sampling        = 0.5
    metadata             = "INCLUDE_ALL_METADATA"
  }
}

# Firewall Rules
resource "google_compute_firewall" "allow_internal" {
  name    = "${var.network_name}-allow-internal"
  network = google_compute_network.vpc_network.name
  project = var.project_id

  allow {
    protocol = "icmp"
  }

  allow {
    protocol = "tcp"
    ports    = ["0-65535"]
  }

  allow {
    protocol = "udp"
    ports    = ["0-65535"]
  }

  source_ranges = [var.subnet_cidr]
  priority      = 65534
}

# Permitir SSH desde IPs específicas
resource "google_compute_firewall" "allow_ssh" {
  name    = "${var.network_name}-allow-ssh"
  network = google_compute_network.vpc_network.name
  project = var.project_id

  allow {
    protocol = "tcp"
    ports    = ["22"]
  }

  source_ranges = var.allowed_source_ranges
  target_tags   = ["ssh-enabled"]
  priority      = 1000
}

# Permitir HTTP/HTTPS desde IPs específicas
resource "google_compute_firewall" "allow_http" {
  name    = "${var.network_name}-allow-http"
  network = google_compute_network.vpc_network.name
  project = var.project_id

  allow {
    protocol = "tcp"
    ports    = ["80", "443"]
  }

  source_ranges = var.allowed_source_ranges
  target_tags   = ["http-server", "https-server"]
  priority      = 1000
}

# Permitir acceso a puertos personalizados (API)
resource "google_compute_firewall" "allow_custom_ports" {
  name    = "${var.network_name}-allow-custom"
  network = google_compute_network.vpc_network.name
  project = var.project_id

  allow {
    protocol = "tcp"
    ports    = var.allowed_ports
  }

  source_ranges = var.allowed_source_ranges
  target_tags   = ["api-server"]
  priority      = 1000
}

# Bloquear todo el tráfico de salida a menos que sea explícitamente permitido
# (Opcional - comentado por defecto para no romper instalaciones)
# resource "google_compute_firewall" "deny_all_egress" {
#   name      = "${var.network_name}-deny-all-egress"
#   network   = google_compute_network.vpc_network.name
#   project   = var.project_id
#   direction = "EGRESS"
#
#   deny {
#     protocol = "all"
#   }
#
#   destination_ranges = ["0.0.0.0/0"]
#   priority           = 65535
# }
