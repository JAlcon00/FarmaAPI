# ============================================================================
# COMPUTE MODULE - VARIABLES
# ============================================================================

variable "project_id" {
  description = "ID del proyecto de GCP"
  type        = string
}

variable "zone" {
  description = "Zona para la VM"
  type        = string
}

variable "instance_name" {
  description = "Nombre de la instancia"
  type        = string
}

variable "machine_type" {
  description = "Tipo de máquina"
  type        = string
}

variable "boot_disk_size" {
  description = "Tamaño del disco de arranque en GB"
  type        = number
}

variable "boot_disk_type" {
  description = "Tipo de disco de arranque"
  type        = string
}

variable "image_self_link" {
  description = "Self link de la imagen del sistema operativo"
  type        = string
}

variable "network_self_link" {
  description = "Self link de la red"
  type        = string
}

variable "subnet_self_link" {
  description = "Self link de la subred"
  type        = string
}

variable "metadata" {
  description = "Metadata para la instancia"
  type        = map(string)
  default     = {}
}

variable "tags" {
  description = "Tags de red para la instancia"
  type        = list(string)
  default     = []
}

variable "labels" {
  description = "Labels para la instancia"
  type        = map(string)
  default     = {}
}

variable "service_account_email" {
  description = "Email de la service account (default compute service account si no se especifica)"
  type        = string
  default     = null
}

variable "service_account_scopes" {
  description = "Scopes para la service account"
  type        = list(string)
  default = [
    "https://www.googleapis.com/auth/devstorage.read_only",
    "https://www.googleapis.com/auth/logging.write",
    "https://www.googleapis.com/auth/monitoring.write",
    "https://www.googleapis.com/auth/servicecontrol",
    "https://www.googleapis.com/auth/service.management.readonly",
    "https://www.googleapis.com/auth/trace.append"
  ]
}

variable "preemptible" {
  description = "Si la VM es preemptible (más económica pero puede ser terminada)"
  type        = bool
  default     = false
}

variable "static_ip" {
  description = "IP estática (opcional)"
  type        = string
  default     = null
}
