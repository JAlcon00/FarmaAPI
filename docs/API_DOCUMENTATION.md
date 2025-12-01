# 🏥 FarmaControl API - Documentación para Frontend

## 📋 Información General

- **Base URL**: `http://localhost:8080`
- **Formato de respuesta**: JSON
- **Encoding**: UTF-8
- **Framework**: Spring Boot 3.1.5 con Undertow
- **Base de datos**: MySQL (Google Cloud)

## 🔧 Headers Requeridos

```typescript
headers: {
  'Content-Type': 'application/json',
  'Accept': 'application/json'
}
```

## 🛠️ Configuración Angular/Ionic

### Servicio Base HTTP
```typescript
// src/app/services/api.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = 'http://localhost:8080/api';
  
  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(private http: HttpClient) { }

  // Métodos GET genéricos
  get<T>(endpoint: string): Observable<T> {
    return this.http.get<T>(`${this.baseUrl}${endpoint}`, this.httpOptions);
  }

  // Métodos POST genéricos
  post<T>(endpoint: string, data: any): Observable<T> {
    return this.http.post<T>(`${this.baseUrl}${endpoint}`, data, this.httpOptions);
  }

  // Métodos PUT genéricos
  put<T>(endpoint: string, data: any): Observable<T> {
    return this.http.put<T>(`${this.baseUrl}${endpoint}`, data, this.httpOptions);
  }
}
```

---

## 👥 ENDPOINTS - ROLES

### 📊 GET /api/roles
**Descripción**: Obtiene todos los roles del sistema

**Request**:
```typescript
// Angular/Ionic
this.apiService.get('/roles').subscribe(response => {
  console.log(response);
});
```

**Response** (200 OK):
```json
[
  {
    "id": 1,
    "nombre": "ADMIN",
    "descripcion": "Administrador del sistema",
    "fechaCreacion": "2024-01-15T10:30:00Z"
  },
  {
    "id": 2,
    "nombre": "VENDEDOR",
    "descripcion": "Personal de ventas",
    "fechaCreacion": "2024-01-15T10:30:00Z"
  }
]
```

### ➕ POST /api/roles
**Descripción**: Crea un nuevo rol

**Request**:
```typescript
// Modelo TypeScript
interface CreateRoleRequest {
  nombre: string;
  descripcion: string;
}

// Angular/Ionic
const newRole: CreateRoleRequest = {
  nombre: "GERENTE",
  descripcion: "Gerente de farmacia"
};

this.apiService.post('/roles', newRole).subscribe(response => {
  console.log('Rol creado:', response);
});
```

**Body**:
```json
{
  "nombre": "GERENTE",
  "descripcion": "Gerente de farmacia"
}
```

**Response** (201 Created):
```json
{
  "success": true,
  "message": "Rol creado exitosamente",
  "data": {
    "id": 21,
    "nombre": "GERENTE",
    "descripcion": "Gerente de farmacia"
  }
}
```

---

## 📁 ENDPOINTS - CATEGORÍAS

### 📊 GET /api/categorias
**Descripción**: Obtiene todas las categorías de productos

**Request**:
```typescript
// Angular/Ionic
this.apiService.get('/categorias').subscribe(response => {
  console.log(response);
});
```

**Response** (200 OK):
```json
[
  {
    "id": 1,
    "nombre": "Analgésicos",
    "descripcion": "Medicamentos para el dolor",
    "fechaCreacion": "2024-01-15T10:30:00Z"
  },
  {
    "id": 2,
    "nombre": "Antibióticos",
    "descripcion": "Medicamentos contra infecciones",
    "fechaCreacion": "2024-01-15T10:30:00Z"
  }
]
```

### ➕ POST /api/categorias
**Descripción**: Crea una nueva categoría

**Request**:
```typescript
// Modelo TypeScript
interface CreateCategoryRequest {
  nombre: string;
  descripcion: string;
}

// Angular/Ionic
const newCategory: CreateCategoryRequest = {
  nombre: "Vitaminas",
  descripcion: "Suplementos vitamínicos"
};

this.apiService.post('/categorias', newCategory).subscribe(response => {
  console.log('Categoría creada:', response);
});
```

**Body**:
```json
{
  "nombre": "Vitaminas",
  "descripcion": "Suplementos vitamínicos"
}
```

---

## 💊 ENDPOINTS - PRODUCTOS

### 📊 GET /api/productos
**Descripción**: Obtiene todos los productos del inventario

**Request**:
```typescript
// Angular/Ionic
this.apiService.get('/productos').subscribe(response => {
  console.log(response);
});
```

**Response** (200 OK):
```json
[
  {
    "id": 1,
    "nombre": "Paracetamol 500mg",
    "descripcion": "Analgésico y antipirético",
    "precio": 15.50,
    "stock": 100,
    "categoria": {
      "id": 1,
      "nombre": "Analgésicos"
    },
    "proveedor": {
      "id": 1,
      "nombre": "Laboratorios ABC"
    },
    "fechaVencimiento": "2025-12-31",
    "codigoBarras": "7501234567890"
  }
]
```

### 🔍 GET /api/productos?categoria={id}
**Descripción**: Filtra productos por categoría

**Request**:
```typescript
// Angular/Ionic
const categoriaId = 1;
this.apiService.get(`/productos?categoria=${categoriaId}`).subscribe(response => {
  console.log('Productos de la categoría:', response);
});
```

### ⚠️ GET /api/productos?stock=bajo
**Descripción**: Obtiene productos con stock bajo (menos de 10 unidades)

**Request**:
```typescript
// Angular/Ionic
this.apiService.get('/productos?stock=bajo').subscribe(response => {
  console.log('Productos con stock bajo:', response);
});
```

---

## 👤 ENDPOINTS - CLIENTES

### 📊 GET /api/clientes
**Descripción**: Obtiene todos los clientes registrados

**Request**:
```typescript
// Angular/Ionic
this.apiService.get('/clientes').subscribe(response => {
  console.log(response);
});
```

**Response** (200 OK):
```json
[
  {
    "id": 1,
    "nombre": "Juan Pérez",
    "email": "juan.perez@email.com",
    "telefono": "555-1234",
    "direccion": "Calle Principal 123",
    "fechaRegistro": "2024-01-15T10:30:00Z"
  }
]
```

### 🔍 GET /api/clientes?search={termino}
**Descripción**: Busca clientes por nombre

**Request**:
```typescript
// Angular/Ionic
const searchTerm = "Juan";
this.apiService.get(`/clientes?search=${searchTerm}`).subscribe(response => {
  console.log('Clientes encontrados:', response);
});
```

### ➕ POST /api/clientes
**Descripción**: Registra un nuevo cliente

**Request**:
```typescript
// Modelo TypeScript
interface CreateClientRequest {
  nombre: string;
  email: string;
  telefono: string;
  direccion: string;
}

// Angular/Ionic
const newClient: CreateClientRequest = {
  nombre: "María García",
  email: "maria.garcia@email.com",
  telefono: "555-5678",
  direccion: "Avenida Secundaria 456"
};

this.apiService.post('/clientes', newClient).subscribe(response => {
  console.log('Cliente registrado:', response);
});
```

**Body**:
```json
{
  "nombre": "María García",
  "email": "maria.garcia@email.com",
  "telefono": "555-5678",
  "direccion": "Avenida Secundaria 456"
}
```

---

## 🛒 ENDPOINTS - VENTAS

### 📊 GET /api/ventas
**Descripción**: Obtiene todas las ventas registradas

**Request**:
```typescript
// Angular/Ionic
this.apiService.get('/ventas').subscribe(response => {
  console.log(response);
});
```

**Response** (200 OK):
```json
[
  {
    "id": 1,
    "cliente": {
      "id": 1,
      "nombre": "Juan Pérez"
    },
    "fechaVenta": "2024-01-15T14:30:00Z",
    "total": 45.50,
    "estado": "COMPLETADA",
    "detalles": [
      {
        "producto": {
          "id": 1,
          "nombre": "Paracetamol 500mg"
        },
        "cantidad": 2,
        "precioUnitario": 15.50,
        "subtotal": 31.00
      }
    ]
  }
]
```

### 🔍 GET /api/ventas/{id}/detalles
**Descripción**: Obtiene los detalles de una venta específica

**Request**:
```typescript
// Angular/Ionic
const ventaId = 1;
this.apiService.get(`/ventas/${ventaId}/detalles`).subscribe(response => {
  console.log('Detalles de la venta:', response);
});
```

### ➕ POST /api/ventas
**Descripción**: Registra una nueva venta

**Request**:
```typescript
// Modelo TypeScript
interface CreateSaleRequest {
  clienteId: number;
  productos: ProductoVenta[];
}

interface ProductoVenta {
  id: number;
  cantidad: number;
}

// Angular/Ionic
const newSale: CreateSaleRequest = {
  clienteId: 1,
  productos: [
    { id: 1, cantidad: 2 },
    { id: 3, cantidad: 1 }
  ]
};

this.apiService.post('/ventas', newSale).subscribe(response => {
  console.log('Venta registrada:', response);
});
```

**Body**:
```json
{
  "clienteId": 1,
  "productos": [
    { "id": 1, "cantidad": 2 },
    { "id": 3, "cantidad": 1 }
  ]
}
```

---

## 🚀 SERVICIOS ANGULAR ESPECÍFICOS

### Servicio de Roles
```typescript
// src/app/services/roles.service.ts
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface Role {
  id: number;
  nombre: string;
  descripcion: string;
  fechaCreacion: string;
}

@Injectable({
  providedIn: 'root'
})
export class RolesService {
  constructor(private apiService: ApiService) { }

  getRoles(): Observable<Role[]> {
    return this.apiService.get<Role[]>('/roles');
  }

  createRole(role: {nombre: string, descripcion: string}): Observable<any> {
    return this.apiService.post('/roles', role);
  }
}
```

### Servicio de Productos
```typescript
// src/app/services/productos.service.ts
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface Producto {
  id: number;
  nombre: string;
  descripcion: string;
  precio: number;
  stock: number;
  categoria: {id: number, nombre: string};
  fechaVencimiento: string;
}

@Injectable({
  providedIn: 'root'
})
export class ProductosService {
  constructor(private apiService: ApiService) { }

  getProductos(): Observable<Producto[]> {
    return this.apiService.get<Producto[]>('/productos');
  }

  getProductosByCategoria(categoriaId: number): Observable<Producto[]> {
    return this.apiService.get<Producto[]>(`/productos?categoria=${categoriaId}`);
  }

  getProductosStockBajo(): Observable<Producto[]> {
    return this.apiService.get<Producto[]>('/productos?stock=bajo');
  }
}
```

### Servicio de Ventas
```typescript
// src/app/services/ventas.service.ts
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface Venta {
  id: number;
  cliente: {id: number, nombre: string};
  fechaVenta: string;
  total: number;
  estado: string;
}

@Injectable({
  providedIn: 'root'
})
export class VentasService {
  constructor(private apiService: ApiService) { }

  getVentas(): Observable<Venta[]> {
    return this.apiService.get<Venta[]>('/ventas');
  }

  createVenta(venta: {clienteId: number, productos: {id: number, cantidad: number}[]}): Observable<any> {
    return this.apiService.post('/ventas', venta);
  }

  getVentaDetalles(ventaId: number): Observable<any> {
    return this.apiService.get(`/ventas/${ventaId}/detalles`);
  }
}
```

---

## 📱 EJEMPLO DE USO EN IONIC

### Página de Productos (productos.page.ts)
```typescript
import { Component, OnInit } from '@angular/core';
import { ProductosService, Producto } from '../services/productos.service';

@Component({
  selector: 'app-productos',
  templateUrl: './productos.page.html',
  styleUrls: ['./productos.page.scss'],
})
export class ProductosPage implements OnInit {
  productos: Producto[] = [];
  loading = false;

  constructor(private productosService: ProductosService) { }

  ngOnInit() {
    this.loadProductos();
  }

  loadProductos() {
    this.loading = true;
    this.productosService.getProductos().subscribe({
      next: (data) => {
        this.productos = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar productos:', error);
        this.loading = false;
      }
    });
  }

  getProductosStockBajo() {
    this.loading = true;
    this.productosService.getProductosStockBajo().subscribe({
      next: (data) => {
        this.productos = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar productos con stock bajo:', error);
        this.loading = false;
      }
    });
  }
}
```

---

## ⚠️ MANEJO DE ERRORES

### Interceptor para manejo global de errores
```typescript
// src/app/interceptors/error.interceptor.ts
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpErrorResponse } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { ToastController } from '@ionic/angular';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(private toastController: ToastController) {}

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        let message = 'Error en el servidor';
        
        if (error.status === 0) {
          message = 'No se puede conectar con el servidor';
        } else if (error.status === 404) {
          message = 'Recurso no encontrado';
        } else if (error.status === 500) {
          message = 'Error interno del servidor';
        }

        this.showErrorToast(message);
        return throwError(() => error);
      })
    );
  }

  async showErrorToast(message: string) {
    const toast = await this.toastController.create({
      message,
      duration: 3000,
      color: 'danger'
    });
    toast.present();
  }
}
```

---

## 🔧 URLs DE PRUEBA

- **Interfaz de pruebas**: `http://localhost:8080/api-tester.html`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **API Docs JSON**: `http://localhost:8080/api-docs`

---

## 📝 NOTAS IMPORTANTES

1. **CORS**: La API está configurada para aceptar requests desde cualquier origen durante desarrollo
2. **Formato de fechas**: Se usa formato ISO 8601 (YYYY-MM-DDTHH:mm:ssZ)
3. **Validación**: Los campos requeridos deben enviarse en cada request
4. **Stock bajo**: Se considera stock bajo cuando hay menos de 10 unidades
5. **Estados de venta**: PENDIENTE, COMPLETADA, CANCELADA

---

## 🚀 PRÓXIMOS PASOS

1. Implementar autenticación JWT
2. Agregar paginación a los endpoints GET
3. Implementar filtros avanzados
4. Agregar endpoints para actualizar y eliminar registros
5. Implementar notificaciones push para stock bajo

---

*Documentación actualizada: 11 de octubre de 2025*
*API Version: 1.0.0*