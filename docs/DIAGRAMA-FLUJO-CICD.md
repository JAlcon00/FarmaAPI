# 🔄 Flujo CI/CD - Diagrama Visual

## 📊 Arquitectura Completa

```
┌─────────────────────────────────────────────────────────────────────┐
│                         DEVELOPER                                    │
│                                                                       │
│  💻 Local Development                                                │
│     ├── feature/nueva-funcionalidad                                  │
│     ├── git add . && git commit                                      │
│     └── git push origin feature/nueva-funcionalidad                  │
└─────────────────────┬───────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────────────┐
│                         GITHUB                                       │
│                                                                       │
│  📁 Repository                                                        │
│     ├── Code (Java/Spring Boot)                                      │
│     ├── terraform/ (Infrastructure as Code)                          │
│     └── .github/workflows/ (CI/CD pipelines)                         │
│                                                                       │
│  ─────────────────────────────────────────────                      │
│                                                                       │
│  🔀 Pull Request Created                                             │
│     │                                                                 │
│     ├─► 🤖 CI Workflow (ci.yml)                                     │
│     │    ├── 🧪 Run 349 tests                                       │
│     │    ├── 📊 Generate coverage report                            │
│     │    ├── 🔍 Code analysis                                       │
│     │    ├── 🔒 Security scan (Trivy)                               │
│     │    └── 🐳 Validate Docker build                               │
│     │                                                                 │
│     └─► 📋 Terraform Plan (terraform-plan.yml)                      │
│          ├── terraform init                                          │
│          ├── terraform plan                                          │
│          └── 💬 Comment on PR with changes                          │
│                                                                       │
│  ─────────────────────────────────────────────                      │
│                                                                       │
│  ✅ PR Merged to main                                                │
│     │                                                                 │
│     └─► 🚀 CD Workflow (deploy.yml)                                 │
│          │                                                            │
│          ├── 1. Checkout code                                        │
│          ├── 2. Authenticate to GCP                                  │
│          ├── 3. Setup Terraform                                      │
│          ├── 4. Generate terraform.tfvars from secrets               │
│          ├── 5. terraform init                                       │
│          ├── 6. terraform plan                                       │
│          ├── 7. terraform apply                                      │
│          │      │                                                     │
│          │      └──────────────────────┐                            │
│          │                              │                            │
│          ├── 8. Wait for API ready      │                            │
│          ├── 9. Test health check       │                            │
│          └── 10. Generate summary       │                            │
│                                          │                            │
└──────────────────────────────────────────┼────────────────────────────┘
                                          │
                                          ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    GOOGLE CLOUD PLATFORM                             │
│                                                                       │
│  🏗️  Terraform Creates/Updates Infrastructure                        │
│                                                                       │
│  ┌─────────────────────────────────────────────────────────┐        │
│  │  VPC Network (production-network)                        │        │
│  │  ├── Subnet: 10.0.0.0/24                                 │        │
│  │  └── Flow logs enabled                                   │        │
│  └─────────────────────────────────────────────────────────┘        │
│                                                                       │
│  ┌─────────────────────────────────────────────────────────┐        │
│  │  Firewall Rules                                          │        │
│  │  ├── allow-ssh (port 22)                                 │        │
│  │  ├── allow-http (ports 80, 443)                          │        │
│  │  ├── allow-api (port 8080)                               │        │
│  │  └── allow-internal (all)                                │        │
│  └─────────────────────────────────────────────────────────┘        │
│                                                                       │
│  ┌─────────────────────────────────────────────────────────┐        │
│  │  Compute Engine VM (production-farmacontrol-api)         │        │
│  │  ├── Machine: e2-medium (2 vCPU, 4 GB RAM)              │        │
│  │  ├── OS: Ubuntu 22.04 LTS                                │        │
│  │  ├── Disk: 20 GB SSD (pd-balanced)                       │        │
│  │  ├── External IP: 34.123.45.67                           │        │
│  │  └── Internal IP: 10.0.0.2                               │        │
│  │                                                            │        │
│  │  📜 Startup Script Executes:                              │        │
│  │     ├── 1. Update system packages                         │        │
│  │     ├── 2. Install Docker + Docker Compose                │        │
│  │     ├── 3. Install Java 17 + Maven                        │        │
│  │     ├── 4. Clone GitHub repo                              │        │
│  │     ├── 5. Create .env.production file                    │        │
│  │     ├── 6. mvn clean package                              │        │
│  │     ├── 7. docker build                                   │        │
│  │     ├── 8. docker compose up -d                           │        │
│  │     └── 9. Configure systemd service                      │        │
│  │                                                            │        │
│  │  🐳 Docker Containers:                                     │        │
│  │     ├── farmacontrol-api (Spring Boot)                    │        │
│  │     │   ├── Port: 8080                                    │        │
│  │     │   ├── Image: farmacontrol-api:latest                │        │
│  │     │   └── Health: /actuator/health                      │        │
│  │     │                                                      │        │
│  │     └── mysql-db (MySQL 8.0)                              │        │
│  │         ├── Port: 3306                                    │        │
│  │         ├── DB: farmacontrol                              │        │
│  │         └── Volume: mysql-data                            │        │
│  └─────────────────────────────────────────────────────────┘        │
│                                                                       │
└─────────────────────┬───────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────────────┐
│                         USERS                                        │
│                                                                       │
│  🌐 Public Access                                                    │
│     ├── API:     http://34.123.45.67:8080/api                       │
│     ├── Swagger: http://34.123.45.67:8080/swagger-ui.html           │
│     ├── Health:  http://34.123.45.67:8080/actuator/health           │
│     └── Mobile:  Ionic/Angular app connects to API                  │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 🔄 Flujo Detallado por Etapa

### 1️⃣ Desarrollo Local → GitHub

```
Developer                    GitHub
   │                            │
   ├── Create feature branch   │
   │   feature/nueva-func       │
   │                            │
   ├── Write code               │
   │   (Java/Spring Boot)       │
   │                            │
   ├── Commit changes           │
   │   git commit -m "..."      │
   │                            │
   └── Push to GitHub ─────────►│
       git push origin feat...   │
```

### 2️⃣ Pull Request → CI Pipeline

```
GitHub PR                    CI Workflow (ci.yml)
   │                            │
   ├── PR Created ─────────────►├── Trigger on PR
   │                            │
   │                            ├── 🔧 Setup Environment
   │                            │   ├── Java 17
   │                            │   ├── Maven
   │                            │   └── MySQL container
   │                            │
   │                            ├── 🧪 Run Tests
   │                            │   ├── Unit tests
   │                            │   ├── Integration tests
   │                            │   └── 349 tests total
   │                            │
   │                            ├── 📊 Coverage Report
   │                            │   └── JaCoCo (65%)
   │                            │
   │                            ├── 🔍 Code Analysis
   │                            │   └── mvn verify
   │                            │
   │                            ├── 🔒 Security Scan
   │                            │   └── Trivy
   │                            │
   │                            └── 🐳 Docker Build
   │                                └── Validate Dockerfile
   │                            │
   │◄────── Status: ✅ Passed ──┤
   │                            │
   ├── Show status checks       │
   └── Allow merge              │
```

### 3️⃣ Terraform Plan (si hay cambios en terraform/)

```
GitHub PR                    Terraform Plan (terraform-plan.yml)
   │                            │
   ├── Changes in terraform/ ──►├── Trigger on terraform/*
   │                            │
   │                            ├── 🔐 Auth to GCP
   │                            │
   │                            ├── 📦 Setup Terraform
   │                            │
   │                            ├── terraform init
   │                            │
   │                            ├── terraform plan
   │                            │   └── Generate plan
   │                            │
   │                            └── 💬 Comment on PR
   │                                └── Show changes
   │                            │
   │◄─── Comment with plan ─────┤
   │                            │
   │  "Plan: 1 to add,          │
   │   1 to change,             │
   │   0 to destroy"            │
```

### 4️⃣ Merge → CD Pipeline

```
GitHub main                  CD Workflow (deploy.yml)           GCP
   │                            │                                 │
   ├── Merge PR ───────────────►├── Trigger on push to main     │
   │                            │                                 │
   │                            ├── 📥 Checkout code              │
   │                            │                                 │
   │                            ├── ☁️ Auth to GCP ──────────────►│
   │                            │   (using GCP_SA_KEY)            │
   │                            │                                 │
   │                            ├── 📦 Setup Terraform            │
   │                            │                                 │
   │                            ├── 🔐 Generate tfvars            │
   │                            │   (from GitHub Secrets)         │
   │                            │                                 │
   │                            ├── terraform init                │
   │                            │                                 │
   │                            ├── terraform plan                │
   │                            │                                 │
   │                            ├── terraform apply ─────────────►├── Create/Update
   │                            │   (auto-approve)                │   │
   │                            │                                 │   ├── VPC
   │                            │                                 │   ├── Firewall
   │                            │                                 │   └── VM
   │                            │                                 │       │
   │                            │                                 │       ├── Startup script
   │                            │                                 │       │   ├── Install deps
   │                            │                                 │       │   ├── Clone repo
   │                            │                                 │       │   ├── mvn package
   │                            │                                 │       │   ├── docker build
   │                            │                                 │       │   └── docker up -d
   │                            │                                 │       │
   │                            │                                 │       └── Services running
   │                            │                                 │           ├── API :8080
   │                            │                                 │           └── MySQL :3306
   │                            │                                 │
   │                            ├── ⏳ Wait for API ready         │
   │                            │   (curl health check)           │
   │                            │                                 │
   │                            ├── 🧪 Test deployment ──────────►│
   │                            │   GET /actuator/health          │
   │                            │                                 │
   │                            │◄────── {"status":"UP"} ────────┤
   │                            │                                 │
   │                            └── 📊 Generate Summary           │
   │                                └── Show deployment info      │
   │                            │                                 │
   │◄───── ✅ Deployed ──────────┤                                 │
```

### 5️⃣ Aplicación Funcionando

```
Google Cloud VM              Docker Containers         External Access
      │                            │                         │
      ├── production-farma...      │                         │
      │   Ubuntu 22.04             │                         │
      │   34.123.45.67             │                         │
      │                            │                         │
      ├── Docker Engine ───────────►├── farmacontrol-api     │
      │                            │   Spring Boot           │
      │                            │   Port: 8080 ───────────►├── Users
      │                            │   /api/*                │   HTTP GET/POST
      │                            │   /actuator/health      │   /api/productos
      │                            │                         │
      │                            ├── mysql-db              │
      │                            │   MySQL 8.0             │
      │                            │   Port: 3306            │
      │                            │   farmacontrol DB       │
      │                            │                         │
      └── Systemd Service          │                         │
          (auto-restart)           │                         │
```

---

## 🔁 Ciclo Completo de Actualización

```
1. LOCAL
   Developer makes changes
   ↓
   
2. GITHUB
   Push to feature branch
   ↓
   CI runs automatically
   ↓
   Create Pull Request
   ↓
   Terraform Plan (if infra changes)
   ↓
   Code Review + Approve
   ↓
   Merge to main
   ↓
   
3. GITHUB ACTIONS
   CD Workflow triggers
   ↓
   Authenticate to GCP
   ↓
   Run Terraform
   ↓
   
4. GOOGLE CLOUD
   Create/Update Infrastructure
   ↓
   VM starts with startup script
   ↓
   Clone latest code from GitHub
   ↓
   Compile with Maven
   ↓
   Build Docker image
   ↓
   Start services
   ↓
   
5. PRODUCTION
   API available at http://IP:8080
   ↓
   Users can access
```

---

## 📈 Timeline Típico

```
Tiempo  | Etapa                        | Duración
--------|------------------------------|----------
0:00    | Developer push to GitHub     | Instant
0:01    | CI Workflow starts           | 
0:06    | CI completes (tests pass)    | ~5 min
0:07    | Create PR                    | Manual
0:08    | Terraform Plan (if needed)   | ~3 min
0:10    | PR approved and merged       | Manual
0:11    | CD Workflow starts           |
0:12    | Terraform authenticates      | ~1 min
0:15    | Terraform apply starts       | 
0:20    | VM created and starting      | ~5 min
0:25    | Startup script running       | ~5 min
0:27    | Wait for API ready           | ~2 min
0:28    | Deployment verified          |
0:29    | Summary generated            |
--------|------------------------------|----------
TOTAL:  Deploy completo              | ~15 min
```

---

## 💡 Puntos Clave

### ✅ Automatización

- **No intervención manual** después del merge
- **Todo versionado** en Git
- **Reproducible** en cualquier momento

### ✅ Seguridad

- **Secrets encriptados** en GitHub
- **No credenciales** en código
- **Análisis automático** de vulnerabilidades
- **Firewall** configurado correctamente

### ✅ Confiabilidad

- **Tests automáticos** antes de deploy
- **Preview de cambios** en PRs
- **Rollback fácil** (revertir commit)
- **Health checks** después de deploy

### ✅ Visibilidad

- **Logs completos** de cada etapa
- **Status checks** en PRs
- **Resumen** de cada deployment
- **Notificaciones** automáticas

---

## 🎯 Resultado Final

```
┌──────────────────────────────────────────────────────────┐
│  ✅ SISTEMA COMPLETAMENTE AUTOMATIZADO                    │
│                                                           │
│  1️⃣ Developer: git push origin main                       │
│  2️⃣ GitHub Actions: Run CI + CD                          │
│  3️⃣ Terraform: Deploy infrastructure                     │
│  4️⃣ VM: Clone, compile, build, start                     │
│  5️⃣ API: Available at http://IP:8080                     │
│                                                           │
│  🕐 Total time: ~15 minutes                               │
│  💰 Cost: ~$30/month (GCP)                                │
│  ⚡ Speed: From commit to production in <15min           │
│  🔄 Rollback: Revert commit and re-deploy                │
│  📊 Monitoring: GitHub Actions + GCP Console             │
└──────────────────────────────────────────────────────────┘
```

---

¡Tu pipeline CI/CD está completamente automatizado! 🚀
