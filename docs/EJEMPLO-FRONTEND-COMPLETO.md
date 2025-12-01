# 🎯 Ejemplos Prácticos: Integración Frontend - FarmaControl

## 🚀 Quick Start

### 1. Cambio Inmediato en tu Auth Service

**ANTES (❌ No funciona):**
```typescript
login(credentials: any) {
  return this.http.post('http://localhost:8080/api/auth/login', credentials);
}
```

**DESPUÉS (✅ Funciona):**
```typescript
login(credentials: any) {
  return this.http.post('http://localhost:8080/api/usuarios/auth', credentials);
}
```

---

## 📱 Ejemplo Completo: Ionic + Angular

### auth.service.ts
```typescript
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';

export interface Usuario {
  id: number;
  email: string;
  nombre: string;
  apellido: string;
  rolId: number;
  activo: boolean;
  role: {
    id: number;
    nombre: string;
    descripcion: string;
  };
}

export interface LoginResponse {
  success: boolean;
  message: string;
  data: Usuario;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api';
  private currentUserSubject: BehaviorSubject<Usuario | null>;
  public currentUser: Observable<Usuario | null>;

  constructor(private http: HttpClient) {
    const storedUser = localStorage.getItem('currentUser');
    this.currentUserSubject = new BehaviorSubject<Usuario | null>(
      storedUser ? JSON.parse(storedUser) : null
    );
    this.currentUser = this.currentUserSubject.asObservable();
  }

  /**
   * Login del usuario
   */
  login(email: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/usuarios/auth`, {
      email,
      password
    }).pipe(
      tap(response => {
        if (response.success && response.data) {
          // Guardar usuario en localStorage
          localStorage.setItem('currentUser', JSON.stringify(response.data));
          this.currentUserSubject.next(response.data);
        }
      })
    );
  }

  /**
   * Logout del usuario
   */
  logout(): void {
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  /**
   * Verificar si el usuario está autenticado
   */
  isAuthenticated(): boolean {
    return this.currentUserSubject.value !== null;
  }

  /**
   * Obtener usuario actual
   */
  getCurrentUser(): Usuario | null {
    return this.currentUserSubject.value;
  }

  /**
   * Verificar si el usuario tiene un rol específico
   */
  hasRole(roleId: number): boolean {
    const user = this.currentUserSubject.value;
    return user ? user.rolId === roleId : false;
  }

  /**
   * Verificar si es administrador
   */
  isAdmin(): boolean {
    return this.hasRole(1);
  }
}
```

### login.page.ts
```typescript
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoadingController, AlertController, ToastController } from '@ionic/angular';
import { AuthService } from '../services/auth.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
})
export class LoginPage implements OnInit {
  loginForm: FormGroup;
  showPassword = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private loadingCtrl: LoadingController,
    private alertCtrl: AlertController,
    private toastCtrl: ToastController,
    private fb: FormBuilder
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  ngOnInit() {
    // Si ya está autenticado, redirigir
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/dashboard']);
    }
  }

  /**
   * Manejar login
   */
  async onLogin() {
    if (this.loginForm.invalid) {
      this.showToast('Por favor completa todos los campos correctamente', 'warning');
      return;
    }

    const loading = await this.loadingCtrl.create({
      message: 'Iniciando sesión...',
      spinner: 'crescent'
    });
    await loading.present();

    const { email, password } = this.loginForm.value;

    this.authService.login(email, password).subscribe({
      next: async (response) => {
        await loading.dismiss();

        if (response.success) {
          const user = response.data;
          
          // Mostrar mensaje de bienvenida
          this.showToast(
            `¡Bienvenido ${user.nombre} ${user.apellido}!`,
            'success'
          );

          // Redirigir según el rol
          if (user.rolId === 1) {
            // Admin
            this.router.navigate(['/admin/dashboard']);
          } else if (user.rolId === 3) {
            // Cajero
            this.router.navigate(['/ventas']);
          } else {
            // Otros roles
            this.router.navigate(['/dashboard']);
          }
        } else {
          this.showAlert('Error', 'Credenciales inválidas');
        }
      },
      error: async (error) => {
        await loading.dismiss();
        
        console.error('Error en login:', error);
        
        let errorMessage = 'Ocurrió un error al iniciar sesión';
        
        if (error.status === 401) {
          errorMessage = 'Email o contraseña incorrectos';
        } else if (error.status === 0) {
          errorMessage = 'No se pudo conectar con el servidor';
        }
        
        this.showAlert('Error de Autenticación', errorMessage);
      }
    });
  }

  /**
   * Mostrar alert
   */
  async showAlert(header: string, message: string) {
    const alert = await this.alertCtrl.create({
      header,
      message,
      buttons: ['OK']
    });
    await alert.present();
  }

  /**
   * Mostrar toast
   */
  async showToast(message: string, color: string = 'primary') {
    const toast = await this.toastCtrl.create({
      message,
      duration: 3000,
      position: 'bottom',
      color
    });
    await toast.present();
  }

  /**
   * Toggle mostrar contraseña
   */
  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  /**
   * Login rápido (solo para desarrollo)
   */
  async quickLogin(userType: string) {
    let email = '';
    let password = '';

    switch (userType) {
      case 'admin':
        email = 'test@farmacontrol.com';
        password = 'admin123';
        break;
      case 'farmaceutico':
        email = 'vendedor@farmacontrol.com';
        password = 'vendedor123';
        break;
    }

    this.loginForm.patchValue({ email, password });
    await this.onLogin();
  }
}
```

### login.page.html
```html
<ion-header>
  <ion-toolbar color="primary">
    <ion-title>FarmaControl</ion-title>
  </ion-toolbar>
</ion-header>

<ion-content class="ion-padding">
  <div class="login-container">
    <!-- Logo -->
    <div class="logo-container">
      <ion-icon name="medical" size="large" color="primary"></ion-icon>
      <h1>FarmaControl</h1>
      <p>Sistema de Gestión Farmacéutica</p>
    </div>

    <!-- Formulario -->
    <form [formGroup]="loginForm" (ngSubmit)="onLogin()">
      <!-- Email -->
      <ion-item>
        <ion-label position="floating">
          <ion-icon name="mail-outline"></ion-icon>
          Email
        </ion-label>
        <ion-input 
          type="email" 
          formControlName="email"
          autocomplete="email"
          inputmode="email">
        </ion-input>
      </ion-item>
      <ion-text color="danger" *ngIf="loginForm.get('email')?.invalid && loginForm.get('email')?.touched">
        <p class="error-text">Email inválido</p>
      </ion-text>

      <!-- Password -->
      <ion-item>
        <ion-label position="floating">
          <ion-icon name="lock-closed-outline"></ion-icon>
          Contraseña
        </ion-label>
        <ion-input 
          [type]="showPassword ? 'text' : 'password'" 
          formControlName="password"
          autocomplete="current-password">
        </ion-input>
        <ion-button 
          slot="end" 
          fill="clear" 
          (click)="togglePasswordVisibility()">
          <ion-icon [name]="showPassword ? 'eye-off-outline' : 'eye-outline'"></ion-icon>
        </ion-button>
      </ion-item>
      <ion-text color="danger" *ngIf="loginForm.get('password')?.invalid && loginForm.get('password')?.touched">
        <p class="error-text">Contraseña debe tener al menos 6 caracteres</p>
      </ion-text>

      <!-- Botón Login -->
      <ion-button 
        expand="block" 
        type="submit" 
        [disabled]="loginForm.invalid"
        class="login-button">
        <ion-icon name="log-in-outline" slot="start"></ion-icon>
        Iniciar Sesión
      </ion-button>
    </form>

    <!-- Botones de desarrollo (remover en producción) -->
    <div class="dev-buttons" *ngIf="true">
      <p class="dev-label">Acceso Rápido (Desarrollo):</p>
      <ion-button 
        expand="block" 
        fill="outline" 
        size="small"
        (click)="quickLogin('admin')">
        👨‍💼 Admin
      </ion-button>
      <ion-button 
        expand="block" 
        fill="outline" 
        size="small"
        (click)="quickLogin('farmaceutico')">
        💊 Farmacéutico
      </ion-button>
    </div>

    <!-- Pie -->
    <div class="footer">
      <p>¿Olvidaste tu contraseña?</p>
      <ion-text color="primary">
        <a>Recuperar Contraseña</a>
      </ion-text>
    </div>
  </div>
</ion-content>
```

### login.page.scss
```scss
.login-container {
  max-width: 400px;
  margin: 0 auto;
  padding-top: 20px;

  .logo-container {
    text-align: center;
    margin-bottom: 40px;

    ion-icon {
      font-size: 80px;
    }

    h1 {
      font-size: 32px;
      font-weight: bold;
      margin: 10px 0;
      color: var(--ion-color-primary);
    }

    p {
      color: var(--ion-color-medium);
      font-size: 14px;
    }
  }

  form {
    margin-bottom: 30px;

    ion-item {
      margin-bottom: 15px;
      --border-radius: 8px;
    }

    .error-text {
      font-size: 12px;
      padding-left: 16px;
      margin-top: 5px;
    }

    .login-button {
      margin-top: 30px;
      --border-radius: 8px;
      height: 50px;
      font-weight: bold;
    }
  }

  .dev-buttons {
    margin-top: 30px;
    padding: 20px;
    background: var(--ion-color-light);
    border-radius: 8px;

    .dev-label {
      font-size: 12px;
      color: var(--ion-color-medium);
      text-align: center;
      margin-bottom: 10px;
    }

    ion-button {
      margin-bottom: 8px;
    }
  }

  .footer {
    text-align: center;
    margin-top: 30px;
    font-size: 14px;
    color: var(--ion-color-medium);

    a {
      text-decoration: none;
      font-weight: bold;
    }
  }
}
```

---

## 🛡️ Auth Guard

### auth.guard.ts
```typescript
import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

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

    // Redirigir al login
    this.router.navigate(['/login']);
    return false;
  }
}
```

### Uso en Routing:
```typescript
const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadChildren: () => import('./login/login.module').then(m => m.LoginPageModule)
  },
  {
    path: 'dashboard',
    loadChildren: () => import('./dashboard/dashboard.module').then(m => m.DashboardPageModule),
    canActivate: [AuthGuard]  // 🔒 Protegida
  }
];
```

---

## 🧪 Testing

### Prueba el Login:
```typescript
// En login.page.ts, agrega estos datos de prueba:
quickLoginAdmin() {
  this.loginForm.patchValue({
    email: 'test@farmacontrol.com',
    password: 'admin123'
  });
  this.onLogin();
}
```

---

## ✅ Checklist de Implementación

- [ ] Actualizar endpoint a `/api/usuarios/auth`
- [ ] Implementar AuthService
- [ ] Crear formulario de login
- [ ] Agregar validaciones
- [ ] Implementar guards de autenticación
- [ ] Guardar usuario en localStorage
- [ ] Manejar errores 401
- [ ] Probar con credenciales de prueba
- [ ] Implementar logout
- [ ] Redirigir según rol del usuario

---

**¡Con esto tu frontend estará completamente integrado!** 🎉
