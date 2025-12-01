# ============================================================================
# COMPUTE MODULE - VM INSTANCE
# ============================================================================

resource "google_compute_instance" "vm_instance" {
  name         = var.instance_name
  machine_type = var.machine_type
  zone         = var.zone

  tags = var.tags
  labels = var.labels

  boot_disk {
    initialize_params {
      image = var.image_self_link
      size  = var.boot_disk_size
      type  = var.boot_disk_type
    }
  }

  network_interface {
    network    = var.network_self_link
    subnetwork = var.subnet_self_link

    access_config {
      # Ephemeral public IP
      # Para IP estática, agregar: nat_ip = var.static_ip
    }
  }

  metadata = var.metadata

  # Permitir que la VM sea eliminada por Terraform
  allow_stopping_for_update = true

  # Service account con permisos mínimos
  service_account {
    email  = var.service_account_email
    scopes = var.service_account_scopes
  }

  # Política de reinicio
  scheduling {
    automatic_restart   = true
    on_host_maintenance = "MIGRATE"
    preemptible         = var.preemptible
  }

  # Habilitar Shielded VM (seguridad adicional)
  shielded_instance_config {
    enable_secure_boot          = true
    enable_vtpm                 = true
    enable_integrity_monitoring = true
  }

  lifecycle {
    create_before_destroy = true
  }
}
