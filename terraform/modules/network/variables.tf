# ============================================================================
# NETWORK MODULE - VARIABLES
# ============================================================================

variable "project_id" {
  description = "ID del proyecto de GCP"
  type        = string
}

variable "network_name" {
  description = "Nombre de la red VPC"
  type        = string
}

variable "subnet_name" {
  description = "Nombre de la subred"
  type        = string
}

variable "region" {
  description = "Región para la subred"
  type        = string
}

variable "subnet_cidr" {
  description = "CIDR de la subred"
  type        = string
  default     = "10.0.0.0/24"
}

variable "allowed_ports" {
  description = "Puertos permitidos para el tráfico entrante"
  type        = list(string)
  default     = ["22", "80", "443", "8080"]
}

variable "allowed_source_ranges" {
  description = "Rangos de IP permitidos (0.0.0.0/0 para público)"
  type        = list(string)
  default     = ["0.0.0.0/0"]
}
