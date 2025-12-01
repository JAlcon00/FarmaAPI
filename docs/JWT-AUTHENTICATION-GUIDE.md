# 🔐 Sistema de Autenticación JWT - FarmaControl API

## ✅ **Implementación Completada**

Tu API ahora tiene **autenticación profesional con JWT (JSON Web Tokens)** activada.

---

## 🎯 **¿Qué se implementó?**

### **1. Dependencias Agregadas:**
```xml
<!-- JWT (JSON Web Token) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>

<!-- Spring Boot Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Spring Boot Actuator (Health Checks) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!-- Micrometer Prometheus (Métricas) -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

### **2. Clases Creadas:**

#### **`security/JwtTokenProvider.java`**
- ✅ Genera tokens JWT con firma HS256
- ✅ Valida tokens y extrae información
- ✅ Tokens válidos por 24 horas
- ✅ Incluye userId, email, roleId en el token

#### **`filter/JwtAuthenticationFilter.java`**
- ✅ Intercepta todas las peticiones a endpoints protegidos
- ✅ Valida el token en el header `Authorization: Bearer <token>`
- ✅ Rechaza peticiones sin token o con token inválido
- ✅ Inyecta información del usuario en el request

### **3. Métodos Agregados a `JsonResponse`:**
- ✅ `unauthorized()` - Error 401
- ✅ `forbidden()` - Error 403

---

## 📡 **Cómo Usar la API con JWT**

### **Paso 1: Login (Obtener Token)**

```bash
curl -X POST http://localhost:8080/api/usuarios/auth \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@farmacontrol.com",
    "password": "admin123"
  }'
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Autenticación exitosa",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImVtYWlsIjoidGVzdEBmYXJtYWNvbnRyb2wuY29tIiwicm9sZUlkIjoxLCJub21icmUiOiJBZG1pbmlzdHJhZG9yIiwic3ViIjoidGVzdEBmYXJtYWNvbnRyb2wuY29tIiwiaWF0IjoxNzMwODQ3NjAwLCJleHAiOjE3MzA5MzQwMDB9.xyz123",
    "usuario": {
      "id": 1,
      "nombre": "Administrador",
      "email": "test@farmacontrol.com",
      "rolId": 1,
      "activo": true
    },
    "expiresIn": 86400
  }
}
```

### **Paso 2: Usar el Token en Peticiones**

**Guarda el token** y úsalo en el header `Authorization`:

```bash
# Listar productos (requiere token)
curl -X GET http://localhost:8080/api/productos \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImVtYWlsIjoidGVzdEBmYXJtYWNvbnRyb2wuY29tIiwicm9sZUlkIjoxLCJub21icmUiOiJBZG1pbmlzdHJhZG9yIiwic3ViIjoidGVzdEBmYXJtYWNvbnRyb2wuY29tIiwiaWF0IjoxNzMwODQ3NjAwLCJleHAiOjE3MzA5MzQwMDB9.xyz123"
```

```bash
# Crear producto (requiere token)
curl -X POST http://localhost:8080/api/productos \
  -H "Authorization: Bearer <TU_TOKEN_AQUI>" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "nombre=Paracetamol 500mg" \
  -d "precio=15.50" \
  -d "stock=100" \
  -d "categoria_id=1"
```

### **Paso 3: Sin Token = Acceso Denegado**

```bash
# Intentar sin token
curl -X GET http://localhost:8080/api/productos
```

**Respuesta:**
```json
{
  "success": false,
  "error": "Token de autenticación requerido"
}
```

---

## 🔓 **Endpoints Públicos (No requieren token)**

Estos endpoints **NO requieren autenticación**:

| Ruta | Descripción |
|------|-------------|
| `POST /api/usuarios/auth` | Login |
| `GET /health` | Health check |
| `GET /actuator/**` | Métricas del sistema |
| `GET /swagger-ui/**` | Documentación Swagger |
| `GET /api-docs/**` | Documentación OpenAPI |

---

## 🔒 **Endpoints Protegidos (Requieren token)**

Todos estos endpoints **requieren el header Authorization**:

| Ruta | Métodos |
|------|---------|
| `/api/productos/**` | GET, POST, PUT, DELETE |
| `/api/ventas/**` | GET, POST, PUT, DELETE |
| `/api/compras/**` | GET, POST, PUT, DELETE |
| `/api/clientes/**` | GET, POST, PUT, DELETE |
| `/api/proveedores/**` | GET, POST, PUT, DELETE |
| `/api/categorias/**` | GET, POST, PUT, DELETE |
| `/api/roles/**` | GET, POST, PUT, DELETE |
| `/api/usuarios/**` | GET, POST, PUT, DELETE (excepto /auth) |
| `/api/reportes/**` | GET |

---

## 🧪 **Testing con Postman/Thunder Client**

### **1. Crear Variable de Entorno:**
```
API_URL = http://localhost:8080
TOKEN = (se llenará después del login)
```

### **2. Request de Login:**
```
POST {{API_URL}}/api/usuarios/auth
Content-Type: application/json

{
  "email": "test@farmacontrol.com",
  "password": "admin123"
}
```

**En Tests (Postman):**
```javascript
const response = pm.response.json();
pm.environment.set("TOKEN", response.data.token);
```

### **3. Request Protegido:**
```
GET {{API_URL}}/api/productos
Authorization: Bearer {{TOKEN}}
```

---

## 📱 **Integración con Frontend (Ionic/Angular)**

### **AuthService (auth.service.ts):**

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';

interface LoginResponse {
  success: boolean;
  message: string;
  data: {
    token: string;
    usuario: any;
    expiresIn: number;
  };
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api';
  private tokenSubject = new BehaviorSubject<string | null>(this.getToken());
  public token$ = this.tokenSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/usuarios/auth`, {
      email,
      password
    }).pipe(
      tap(response => {
        if (response.success && response.data.token) {
          this.setToken(response.data.token);
          this.tokenSubject.next(response.data.token);
        }
      })
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    this.tokenSubject.next(null);
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    return token !== null && !this.isTokenExpired(token);
  }

  private setToken(token: string): void {
    localStorage.setItem('token', token);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  private isTokenExpired(token: string): boolean {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const expiry = payload.exp * 1000;
    return Date.now() >= expiry;
  }
}
```

### **HTTP Interceptor (auth.interceptor.ts):**

```typescript
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();
    
    if (token) {
      const cloned = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`)
      });
      return next.handle(cloned);
    }
    
    return next.handle(req);
  }
}
```

### **Guard de Ruta (auth.guard.ts):**

```typescript
import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(): boolean {
    if (this.authService.isAuthenticated()) {
      return true;
    }
    
    this.router.navigate(['/login']);
    return false;
  }
}
```

---

## ⚙️ **Configuración Avanzada**

### **Cambiar Duración del Token:**

Edita `src/main/resources/application.yml`:

```yaml
jwt:
  secret: MiFarmaControlSecretKeyParaJWT2025DebeSerLargaYSegura256BitsMinimo
  expiration: 86400000  # 24 horas (en milisegundos)
```

Para **1 hora**: `3600000`  
Para **7 días**: `604800000`  
Para **30 días**: `2592000000`

### **Cambiar Clave Secreta (¡IMPORTANTE PARA PRODUCCIÓN!):**

**⚠️ La clave actual es de ejemplo. En producción DEBES cambiarla:**

```yaml
jwt:
  secret: ${JWT_SECRET:TuClaveSecretaSuperSeguraDe256BitsOmasSoloParaProduccion}
```

O por variable de entorno:
```bash
export JWT_SECRET="MiClaveSuperSecretaYLargaParaProduccion2025"
java -jar farmacontrol-api.jar
```

---

## 🔍 **Información en el Token**

El token JWT contiene:

```json
{
  "userId": 1,
  "email": "test@farmacontrol.com",
  "roleId": 1,
  "nombre": "Administrador",
  "sub": "test@farmacontrol.com",
  "iat": 1730847600,  // Fecha de emisión
  "exp": 1730934000   // Fecha de expiración
}
```

**Acceso en los Servlets:**
```java
Long userId = (Long) request.getAttribute("userId");
Integer roleId = (Integer) request.getAttribute("roleId");
String email = (String) request.getAttribute("userEmail");
```

---

## 📊 **Nuevos Endpoints Disponibles**

### **Health Check (Actuator):**
```bash
curl http://localhost:8080/actuator/health
```

**Respuesta:**
```json
{
  "status": "UP"
}
```

### **Métricas Prometheus:**
```bash
curl http://localhost:8080/actuator/prometheus
```

---

## 🚀 **Próximos Pasos de Profesionalización**

### **✅ Completado:**
- [x] Autenticación JWT
- [x] Filtro de autorización
- [x] Health checks básicos
- [x] Métricas con Prometheus

### **🔜 Por Implementar:**

#### **1. Validación de Roles:**
```java
// Decorador para validar roles
@RolesAllowed({"ADMIN", "FARMACEUTICO"})
public void crearProducto() { ... }
```

#### **2. Rate Limiting:**
```java
// Limitar peticiones por IP
@RateLimit(value = 100, duration = Duration.ofMinutes(1))
```

#### **3. Logging Estructurado:**
```java
logger.info("Usuario {} autenticado exitosamente", userId);
```

#### **4. Tests Automatizados:**
```java
@Test
public void testLoginConCredencialesValidas() { ... }
```

#### **5. Paginación:**
```java
GET /api/productos?page=0&size=20&sort=nombre,asc
```

---

## 🎉 **Resultado Final**

Tu API ahora es **significativamente más profesional** con:

✅ Autenticación basada en tokens JWT estándar  
✅ Tokens seguros con firma HS256  
✅ Expiración automática de tokens (24h)  
✅ Endpoints protegidos automáticamente  
✅ Health checks para monitoreo  
✅ Métricas con Prometheus  
✅ Compatible con frontends modernos (React, Angular, Vue, Ionic)  
✅ Listo para desplegar en producción (con ajustes menores)  

---

## 📚 **Recursos Adicionales**

- [JWT.io](https://jwt.io/) - Decodificar tokens
- [JJWT Documentation](https://github.com/jwtk/jjwt) - Librería oficial
- [Spring Security JWT](https://spring.io/guides/gs/securing-web/) - Guía oficial

---

**¿Listo para continuar con más mejoras profesionales?** 🚀
