# 🏥 FarmaControl API

**API REST minimalista para sistema de control farmacéutico**

![Tests](https://img.shields.io/badge/tests-349%20passing-brightgreen)
![Coverage](https://img.shields.io/badge/coverage-65%25%20security-blue)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-green)

## 🚀 Inicio Rápido

```bash
# Desarrollo local
mvn spring-boot:run

# Docker (producción)
./deploy-modern.sh

# Ejecutar tests
./run-tests.sh

# Ver cobertura
./ver-cobertura.sh
```

## 📊 Estado del Proyecto

- ✅ **349 tests** pasando (100% success rate)
- ✅ **66% cobertura** en Services
- ✅ **65% cobertura** en Security/JWT
- ✅ **61% cobertura** en Controllers
- ✅ **6.8s** tiempo de ejecución
- ✅ **Producción-ready**

Ver [Reporte de Calidad Completo](./REPORTE-CALIDAD-FINAL.md)

## �📡 Endpoints
- **API**: http://localhost:8080/api
- **Interfaz de Pruebas**: http://localhost:8080/api-tester.html
- **Swagger**: http://localhost:8080/swagger-ui.html

## 📚 Documentación
- [API Completa](docs/API_DOCUMENTATION.md)
- [README Detallado](docs/README.md)
- [Reporte de Tests](./REPORTE-CALIDAD-FINAL.md)
- [Guía de Tests](./TESTS-README.md)

## 🧪 Testing

### Ejecutar Tests
```bash
# Todos los tests
./run-tests.sh

# Con Maven
mvn test

# Con cobertura
mvn clean test
```

### Ver Reporte de Cobertura
```bash
# Abrir reporte JaCoCo
./ver-cobertura.sh

# O manualmente
open target/site/jacoco/index.html
```

### Base de Datos de Tests
```bash
# Iniciar MySQL Docker
./start-test-db.sh

# Detener
docker stop farmacontrol-test-db
```

---
*Versión optimizada - Spring Boot 3.1.5 + Undertow*

