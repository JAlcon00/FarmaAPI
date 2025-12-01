# 🎯 Próximos Pasos - Deploy Automático

## ✅ ¿Qué tienes ahora?

Tu proyecto **FarmaControl API** ahora cuenta con:

✅ **Infrastructure as Code** completa con Terraform
✅ **CI/CD Automático** con GitHub Actions  
✅ **Documentación exhaustiva** de todo el proceso
✅ **3 workflows** de GitHub Actions listos para usar
✅ **Scripts de setup** automatizados
✅ **Módulos de Terraform** organizados y reutilizables

---

## 🚀 Para Empezar a Usar

### Opción 1: Deploy Automático con GitHub Actions (Recomendado)

**Pasos:**

1. **Push tu código a GitHub**
   ```bash
   git add .
   git commit -m "Add Terraform and GitHub Actions CI/CD"
   git push origin main
   ```

2. **Configurar GitHub Secrets** (una sola vez)
   
   Lee la guía: [`docs/GITHUB-ACTIONS-SETUP.md`](./GITHUB-ACTIONS-SETUP.md)
   
   Necesitas configurar:
   - `GCP_PROJECT_ID` - Tu proyecto de Google Cloud
   - `GCP_SA_KEY` - Credenciales JSON de service account
   - `DB_PASSWORD` - Password de MySQL
   - `MYSQL_ROOT_PASSWORD` - Root password
   - `JWT_SECRET` - Secret para tokens (generar con openssl)

3. **Crear Service Account en GCP**
   ```bash
   # Ver pasos detallados en GITHUB-ACTIONS-SETUP.md
   gcloud iam service-accounts create github-actions \
       --display-name="GitHub Actions"
   ```

4. **Hacer un cambio y push**
   ```bash
   git checkout -b test-cicd
   # Hacer algún cambio
   git add .
   git commit -m "Test CI/CD"
   git push origin test-cicd
   ```

5. **Verificar en GitHub Actions**
   - Ve a tu repositorio → **Actions** tab
   - Verás el workflow **CI** ejecutándose
   - Crea un PR → verás **Terraform Plan** si hay cambios en terraform/
   - Haz merge a main → verás **CD** desplegando automáticamente

**Resultado:**
- ✅ Tests automáticos en cada PR
- ✅ Deploy automático en cada merge a main
- ✅ API disponible en Google Cloud en 10-15 minutos

---

### Opción 2: Deploy Manual con Terraform

**Pasos:**

1. **Ejecutar script de setup** (una sola vez)
   ```bash
   ./scripts/setup-terraform.sh
   ```
   
   El script te guiará para:
   - Configurar proyecto de GCP
   - Habilitar APIs necesarias
   - Crear service account
   - Generar `terraform.tfvars`

2. **Revisar plan de cambios**
   ```bash
   cd terraform
   terraform plan
   ```

3. **Aplicar cambios**
   ```bash
   terraform apply
   ```
   
   Confirma escribiendo `yes`

4. **Esperar 10-15 minutos**
   
   Terraform creará:
   - VPC Network
   - Firewall rules
   - VM de Compute Engine
   - La VM clonará, compilará y desplegará tu API

5. **Ver información de deployment**
   ```bash
   terraform output deployment_info
   ```
   
   Output:
   ```json
   {
     "vm_external_ip" = "34.123.45.67"
     "api_url" = "http://34.123.45.67:8080/api"
     "health_check" = "http://34.123.45.67:8080/actuator/health"
     "swagger_ui" = "http://34.123.45.67:8080/swagger-ui.html"
   }
   ```

**Resultado:**
- ✅ Infraestructura creada en Google Cloud
- ✅ API disponible y funcionando
- ✅ Puedes actualizar con `terraform apply`

---

## 📖 Guías Detalladas

| Guía | Descripción | Link |
|------|-------------|------|
| **Setup Terraform** | Guía completa paso a paso | [TERRAFORM-SETUP.md](./TERRAFORM-SETUP.md) |
| **Setup GitHub Actions** | Configuración de CI/CD | [GITHUB-ACTIONS-SETUP.md](./GITHUB-ACTIONS-SETUP.md) |
| **Resumen Ejecutivo** | Visión general completa | [RESUMEN-TERRAFORM-GITHUB-ACTIONS.md](./RESUMEN-TERRAFORM-GITHUB-ACTIONS.md) |
| **Diagrama de Flujo** | Arquitectura visual | [DIAGRAMA-FLUJO-CICD.md](./DIAGRAMA-FLUJO-CICD.md) |

---

## 🔍 Checklist de Verificación

### Para GitHub Actions

- [ ] Código pusheado a GitHub
- [ ] GitHub Actions habilitado en el repositorio
- [ ] Service account creada en GCP con permisos:
  - `roles/compute.admin`
  - `roles/iam.serviceAccountUser`
  - `roles/storage.admin`
- [ ] JSON key de service account descargado
- [ ] APIs habilitadas en GCP:
  - Compute Engine API
  - Service Networking API
- [ ] GitHub Secrets configurados:
  - `GCP_PROJECT_ID`
  - `GCP_SA_KEY`
  - `DB_PASSWORD`
  - `MYSQL_ROOT_PASSWORD`
  - `JWT_SECRET`
- [ ] Workflow ejecutado exitosamente
- [ ] API accesible desde internet

### Para Terraform Manual

- [ ] Terraform instalado (`terraform --version`)
- [ ] gcloud CLI instalado (`gcloud --version`)
- [ ] Proyecto de GCP creado
- [ ] Script `setup-terraform.sh` ejecutado
- [ ] `terraform.tfvars` configurado con valores reales
- [ ] Service account creada localmente
- [ ] `terraform init` completado
- [ ] `terraform plan` ejecutado sin errores
- [ ] `terraform apply` completado exitosamente
- [ ] Health check responde: `curl http://IP:8080/actuator/health`

---

## 🎓 Flujo de Trabajo Recomendado

### Desarrollo Diario

```bash
# 1. Crear feature branch
git checkout -b feature/mi-nueva-funcionalidad

# 2. Desarrollar
# ... código ...

# 3. Tests locales
./run-tests.sh

# 4. Commit y push
git add .
git commit -m "Add mi nueva funcionalidad"
git push origin feature/mi-nueva-funcionalidad

# → GitHub Actions ejecuta CI automáticamente
```

### Pull Request

```bash
# 1. Crear PR en GitHub de feature → main

# → GitHub Actions ejecuta:
#   - CI workflow (tests, security)
#   - Terraform Plan (si hay cambios en terraform/)

# 2. Revisar checks y plan de Terraform

# 3. Aprobar y hacer merge
```

### Deploy Automático

```bash
# Al hacer merge a main:

# → GitHub Actions ejecuta CD workflow:
#   1. Terraform apply
#   2. Crea/actualiza infraestructura en GCP
#   3. Espera a que API esté lista
#   4. Verifica health check
#   5. Genera resumen

# ✅ API disponible en 10-15 minutos
```

---

## 💡 Tips y Recomendaciones

### 1. **Usa feature branches**
```bash
# NO hagas commits directos a main
git checkout main  # ❌

# SÍ usa feature branches
git checkout -b feature/nombre  # ✅
```

### 2. **Revisa siempre el Terraform Plan**
Antes de hacer merge de un PR que modifica `terraform/`, revisa el comentario con el plan de cambios.

### 3. **Protege la rama main**
En GitHub: Settings → Branches → Add rule
- ☑️ Require pull request reviews
- ☑️ Require status checks to pass

### 4. **Monitorea los workflows**
- Ve regularmente a Actions tab
- Revisa si hay workflows fallando
- Lee los logs si algo falla

### 5. **Actualiza secrets periódicamente**
Cada 3-6 meses:
```bash
# Generar nuevo JWT_SECRET
openssl rand -base64 64

# Actualizar en GitHub Secrets
# Actualizar en terraform.tfvars (local)
```

### 6. **Backups de la base de datos**
Considera configurar backups automáticos:
```bash
# SSH a la VM
$(terraform output -raw ssh_command)

# Crear backup
docker exec farmacontrol-mysql mysqldump -uroot -p$MYSQL_ROOT_PASSWORD farmacontrol > backup.sql
```

---

## 🆘 Si Algo Sale Mal

### CI Workflow Falla

1. **Ver logs en GitHub Actions**
   - Actions → Workflow run → Job específico
   
2. **Tests fallan**
   - Ejecutar localmente: `./run-tests.sh`
   - Verificar que pasen antes de push

3. **Docker build falla**
   - Verificar `docker/Dockerfile`
   - Probar build local: `docker build -f docker/Dockerfile .`

### CD Workflow Falla

1. **Error de autenticación en GCP**
   - Verificar `GCP_SA_KEY` en GitHub Secrets
   - Verificar permisos de service account

2. **Terraform apply falla**
   - Leer error en logs
   - Verificar APIs habilitadas en GCP
   - Verificar cuotas de GCP

3. **API no inicia en la VM**
   - SSH a la VM: `gcloud compute ssh production-farmacontrol-api --zone=us-central1-a`
   - Ver logs: `sudo tail -f /var/log/farmacontrol-startup.log`
   - Ver Docker logs: `docker compose logs -f`

### Terraform Local Falla

1. **Error: API not enabled**
   ```bash
   gcloud services enable compute.googleapis.com
   ```

2. **Error: Insufficient Permission**
   - Verificar roles de service account
   - Ejecutar comandos de `setup-terraform.sh` nuevamente

3. **State locked**
   ```bash
   terraform force-unlock LOCK_ID
   ```

**Ver troubleshooting completo:**
- [TERRAFORM-SETUP.md - Troubleshooting](./TERRAFORM-SETUP.md#troubleshooting)
- [GITHUB-ACTIONS-SETUP.md - Troubleshooting](./GITHUB-ACTIONS-SETUP.md#troubleshooting)

---

## 📞 Recursos Adicionales

### Documentación
- [Terraform Google Provider](https://registry.terraform.io/providers/hashicorp/google/latest/docs)
- [GitHub Actions Documentation](https://docs.github.com/actions)
- [Google Cloud Documentation](https://cloud.google.com/docs)

### Comunidad
- [Stack Overflow - Terraform](https://stackoverflow.com/questions/tagged/terraform)
- [Stack Overflow - GitHub Actions](https://stackoverflow.com/questions/tagged/github-actions)
- [Google Cloud Community](https://www.googlecloudcommunity.com/)

### Tools
- [Terraform Registry](https://registry.terraform.io/)
- [GitHub Actions Marketplace](https://github.com/marketplace?type=actions)
- [Google Cloud Console](https://console.cloud.google.com/)

---

## 🎉 ¡Felicidades!

Has configurado un pipeline completo de CI/CD con:

✅ **Tests automáticos** en cada cambio
✅ **Deploy automático** a la nube
✅ **Infrastructure as Code** versionada
✅ **Documentación completa** de todo

**Tu próximo push a `main` desplegará automáticamente a Google Cloud.** 🚀

---

## 🤔 ¿Por dónde empiezo?

### Si prefieres deploy automático → GitHub Actions
1. Lee [`GITHUB-ACTIONS-SETUP.md`](./GITHUB-ACTIONS-SETUP.md)
2. Configura secrets en GitHub
3. Push a main
4. ¡Listo! 🎉

### Si prefieres control manual → Terraform
1. Lee [`TERRAFORM-SETUP.md`](./TERRAFORM-SETUP.md)
2. Ejecuta `./scripts/setup-terraform.sh`
3. `terraform apply`
4. ¡Listo! 🎉

### Si quieres entender todo primero
1. Lee [`RESUMEN-TERRAFORM-GITHUB-ACTIONS.md`](./RESUMEN-TERRAFORM-GITHUB-ACTIONS.md)
2. Ve [`DIAGRAMA-FLUJO-CICD.md`](./DIAGRAMA-FLUJO-CICD.md)
3. Elige opción de deploy

---

**¡Happy Coding!** 🎯
