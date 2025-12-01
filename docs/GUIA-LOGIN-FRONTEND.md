# 🔐 Guía de Integración: Login Frontend

## 🚨 Problema Resuelto

### ❌ Error Anterior:
```
Failed to load resource: the server responded with a status of 401 (Unauthorized)
Error: NG04002: Cannot match any routes. URL Segment: 'auth/login'
```

### ✅ Solución:
Tu frontend estaba intentando conectarse a `/api/auth/login`, pero el endpoint correcto es:
- **`POST /api/usuarios/auth`** ← Este es el endpoint correcto

---

## 📡 Configuración Correcta del API

### Base URL:
```typescript
const API_BASE_URL = 'http://localhost:8080/api';
```

### Endpoint de Login:
```typescript
const LOGIN_ENDPOINT = '/usuarios/auth';  // NO usar /auth/login
```

### URL Completa:
```
http://localhost:8080/api/usuarios/auth
```

---

## 🔧 Implementación en Angular/Ionic

### 1. Service (auth.service.ts)
```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/usuarios/auth`, {
      email: email,
      password: password
    });
  }
}
```

### 2. Component (login.page.ts)
```typescript
import { Component } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

export class LoginPage {
  email: string = '';
  password: string = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onLogin() {
    this.authService.login(this.email, this.password).subscribe({
      next: (response) => {
        if (response.success) {
          console.log('✅ Login exitoso:', response.data);
          
          // Guardar usuario en localStorage
          localStorage.setItem('user', JSON.stringify(response.data));
          localStorage.setItem('token', response.data.id.toString());
          
          // Redirigir al dashboard
          this.router.navigate(['/dashboard']);
        }
      },
      error: (error) => {
        console.error('❌ Error en login:', error);
        alert('Credenciales inválidas');
      }
    });
  }
}
```

### 3. Template (login.page.html)
```html
<ion-content>
  <form (ngSubmit)="onLogin()">
    <ion-item>
      <ion-label position="floating">Email</ion-label>
      <ion-input 
        type="email" 
        [(ngModel)]="email" 
        name="email"
        required>
      </ion-input>
    </ion-item>

    <ion-item>
      <ion-label position="floating">Contraseña</ion-label>
      <ion-input 
        type="password" 
        [(ngModel)]="password" 
        name="password"
        required>
      </ion-input>
    </ion-item>

    <ion-button expand="block" type="submit">
      Iniciar Sesión
    </ion-button>
  </form>
</ion-content>
```

---

## 📋 Respuesta del Servidor

### ✅ Login Exitoso (200 OK):
```json
{
  "success": true,
  "message": "Autenticación exitosa",
  "data": {
    "id": 21,
    "email": "test@farmacontrol.com",
    "nombre": "Usuario",
    "apellido": "Test",
    "rolId": 1,
    "activo": true,
    "role": {
      "id": 1,
      "nombre": "ADMIN",
      "descripcion": "Administrador general del sistema"
    }
  }
}
```

### ❌ Login Fallido (401 Unauthorized):
```json
{
  "success": false,
  "error": "Credenciales inválidas"
}
```

---

## 🔑 Credenciales de Prueba

### Usuario Administrador:
- **Email**: `test@farmacontrol.com`
- **Password**: `admin123`
- **Rol**: ADMIN (ID: 1)

---

## 🧪 Probar desde Terminal (cURL)

```bash
# Login exitoso
curl -X POST http://localhost:8080/api/usuarios/auth \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@farmacontrol.com",
    "password": "admin123"
  }'
```

---

## 👥 Crear Nuevos Usuarios

### Desde la API:
```bash
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "email": "vendedor@farmacontrol.com",
    "password": "vendedor123",
    "nombre": "Juan",
    "apellido": "Pérez",
    "rol_id": "2",
    "activo": "true"
  }'
```

### Roles Disponibles (20 roles):
| ID | Rol | Descripción |
|----|-----|-------------|
| 1  | ADMIN | Administrador general del sistema |
| 2  | FARMACEUTICO | Controla inventario y medicamentos |
| 3  | CAJERO | Procesa ventas y tickets |
| 4  | ALMACEN | Gestiona existencias y recepción |
| 5  | GERENTE | Supervisa operaciones y reportes |
| 6  | ASISTENTE | Apoyo administrativo |
| 7  | AUDITOR | Revisa movimientos e inventario |
| 8  | SOPORTE | Mantenimiento y soporte técnico |
| 9  | OPERADOR | Operación general del sistema |
| 10 | SUPERVISOR | Monitorea desempeño del personal |
| 11 | ENCARGADO VENTAS | Encargado de ventas al público |
| 12 | ENCARGADO COMPRAS | Encargado de pedidos y proveedores |
| 13 | ADMIN FINANZAS | Gestión contable y fiscal |
| 14 | DIRECTOR | Gestión general y estratégica |
| 15 | RRHH | Gestión de personal |
| 16 | MARKETING | Promociones y campañas |
| 17 | TESORERIA | Control de flujo de efectivo |
| 18 | ANALISTA | Análisis de datos y KPIs |
| 19 | INTERNO | Empleado sin permisos críticos |
| 20 | INVITADO | Solo lectura |

---

## 🔧 Solución de Problemas

### Error 401 (Unauthorized):
- ✅ Verificar que el email existe en la base de datos
- ✅ Verificar que la contraseña es correcta
- ✅ Verificar que el usuario está activo (`activo: true`)

### Error 404 (Not Found):
- ❌ Estás usando `/api/auth/login` (incorrecto)
- ✅ Usa `/api/usuarios/auth` (correcto)

### CORS Issues:
El backend ya tiene CORS habilitado, pero si tienes problemas:
```typescript
// En Angular: proxy.conf.json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
```

---

## 📱 Ejemplo Completo con Loading y Alerts

```typescript
import { LoadingController, AlertController } from '@ionic/angular';

export class LoginPage {
  constructor(
    private authService: AuthService,
    private router: Router,
    private loadingCtrl: LoadingController,
    private alertCtrl: AlertController
  ) {}

  async onLogin() {
    // Mostrar loading
    const loading = await this.loadingCtrl.create({
      message: 'Iniciando sesión...'
    });
    await loading.present();

    this.authService.login(this.email, this.password).subscribe({
      next: async (response) => {
        await loading.dismiss();
        
        if (response.success) {
          localStorage.setItem('user', JSON.stringify(response.data));
          this.router.navigate(['/dashboard']);
        }
      },
      error: async (error) => {
        await loading.dismiss();
        
        const alert = await this.alertCtrl.create({
          header: 'Error',
          message: 'Credenciales inválidas',
          buttons: ['OK']
        });
        await alert.present();
      }
    });
  }
}
```

---

## ✅ Checklist de Integración

- [ ] Cambiar endpoint a `/api/usuarios/auth`
- [ ] Usar método POST
- [ ] Enviar `{ email, password }` en el body
- [ ] Verificar que el Content-Type sea `application/json`
- [ ] Manejar respuesta exitosa (`response.success === true`)
- [ ] Manejar errores (401 Unauthorized)
- [ ] Guardar datos del usuario en localStorage
- [ ] Redirigir después del login exitoso

---

**¡Tu backend está 100% funcional y listo para conectar con el frontend!** 🎉