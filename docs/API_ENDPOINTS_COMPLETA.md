# � API FarmaControl - Documentación Completa de Endpoints

**Versión:** 1.0.0  
**Base URL:** `http://localhost:8080`  
**Formato de respuesta:** JSON  
**Autenticación:** Opcional (según endpoint)  

---

## 📋 Índice de Módulos

1. [👤 Usuarios y Autenticación](#-usuarios-y-autenticación)
2. [🔐 Roles y Permisos](#-roles-y-permisos)
3. [📦 Productos](#-productos)
4. [🏷️ Categorías](#️-categorías)
5. [👥 Clientes](#-clientes)
6. [🏢 Proveedores](#-proveedores)
7. [🛒 Compras](#-compras)
8. [💰 Ventas](#-ventas)
9. [📊 Reportes y Dashboard](#-reportes-y-dashboard)

---

## 🌐 Información General

### Headers Requeridos
```http
Content-Type: application/json
Accept: application/json
```

### Headers CORS (Automáticos)
```http
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With
```

### Códigos de Respuesta HTTP
| Código | Descripción |
|--------|-------------|
| `200` | OK - Operación exitosa |
| `201` | Created - Recurso creado exitosamente |
| `400` | Bad Request - Datos inválidos |
| `401` | Unauthorized - Credenciales inválidas |
| `404` | Not Found - Recurso no encontrado |
| `500` | Internal Server Error - Error interno |

### Formato de Respuestas

#### Respuesta Exitosa
```json
{
  "success": true,
  "data": { ... },
  "message": "Operación realizada correctamente"
}
```

#### Respuesta de Error
```json
{
  "success": false,
  "error": "Descripción del error"
}
```

---

## 👤 Usuarios y Autenticación

### Base URL: `/api/usuarios`

#### 📋 Obtener Todos los Usuarios
```http
GET /api/usuarios
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "nombre": "Administrador",
    "apellido": "Sistema",
    "email": "admin@farmacontrol.com",
    "rolId": 1,
    "activo": true,
    "createdAt": "2025-10-10 15:15:09.0",
    "updatedAt": "2025-10-10 15:15:09.0",
    "role": {
      "id": 1,
      "nombre": "ADMIN",
      "descripcion": "Administrador general del sistema"
    }
  }
]
```

#### 🔍 Obtener Usuario por ID
```http
GET /api/usuarios/{id}
```

**Parámetros:**
- `id` (path) - ID del usuario

**Ejemplo:**
```bash
curl -X GET "http://localhost:8080/api/usuarios/1"
```

#### � Obtener Usuario por Email
```http
GET /api/usuarios/email?email={email}
```

**Parámetros:**
- `email` (query) - Email del usuario

**Ejemplo:**
```bash
curl -X GET "http://localhost:8080/api/usuarios/email?email=admin@farmacontrol.com"
```

#### 👥 Obtener Usuarios por Rol
```http
GET /api/usuarios/role?rol_id={rol_id}
```

**Parámetros:**
- `rol_id` (query) - ID del rol

**Ejemplo:**
```bash
curl -X GET "http://localhost:8080/api/usuarios/role?rol_id=1"
```

#### ➕ Crear Nuevo Usuario
```http
POST /api/usuarios
```

**Body:**
```json
{
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "juan.perez@farmacontrol.com",
  "password": "password123",
  "rol_id": 3,
  "activo": true
}
```

**Ejemplo cURL:**
```bash
curl -X POST "http://localhost:8080/api/usuarios" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan",
    "apellido": "Pérez", 
    "email": "juan.perez@farmacontrol.com",
    "password": "password123",
    "rol_id": 3
  }'
```

#### 🔐 Autenticar Usuario (Login)
```http
POST /api/usuarios/auth
```

**Body:**
```json
{
  "email": "admin@farmacontrol.com",
  "password": "admin123"
}
```

**Respuesta Exitosa:**
```json
{
  "success": true,
  "message": "Autenticación exitosa",
  "data": {
    "id": 1,
    "nombre": "Administrador",
    "apellido": "Sistema",
    "email": "admin@farmacontrol.com",
    "rolId": 1,
    "activo": true,
    "role": {
      "nombre": "ADMIN",
      "descripcion": "Administrador general del sistema"
    }
  }
}
```

#### ✏️ Actualizar Usuario
```http
PUT /api/usuarios/{id}
```

**Body:**
```json
{
  "nombre": "Juan Carlos",
  "apellido": "Pérez López",
  "email": "juan.carlos@farmacontrol.com",
  "rol_id": 3,
  "activo": true
}
```

#### 🔑 Cambiar Contraseña
```http
PUT /api/usuarios/{id}/password
```

**Body:**
```json
{
  "password": "nuevaPassword123"
}
```

#### 🔄 Activar/Desactivar Usuario
```http
PUT /api/usuarios/{id}/toggle
```

#### 🗑️ Eliminar Usuario (Desactivar)
```http
DELETE /api/usuarios/{id}
```

---

## 🔐 Roles y Permisos

### Base URL: `/api/roles`

#### 📋 Obtener Todos los Roles
```http
GET /api/roles
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "nombre": "ADMIN",
    "descripcion": "Administrador general del sistema",
    "activo": true,
    "createdAt": "2025-10-10 15:15:09.0",
    "updatedAt": "2025-10-10 15:15:09.0"
  }
]
```

#### 🔍 Obtener Rol por ID
```http
GET /api/roles/{id}
```

#### ➕ Crear Nuevo Rol
```http
POST /api/roles
```

**Body:**
```json
{
  "nombre": "NUEVO_ROL",
  "descripcion": "Descripción del nuevo rol",
  "activo": true
}
```

#### ✏️ Actualizar Rol
```http
PUT /api/roles/{id}
```

#### 🗑️ Eliminar Rol
```http
DELETE /api/roles/{id}
```

---

## 📦 Productos

### Base URL: `/api/productos`

#### 📋 Obtener Todos los Productos
```http
GET /api/productos
```

**Parámetros opcionales:**
- `categoria_id` (query) - Filtrar por categoría

**Ejemplo:**
```bash
curl -X GET "http://localhost:8080/api/productos?categoria_id=1"
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "nombre": "Paracetamol 500mg",
    "descripcion": "Analgésico y antipirético",
    "precio": 15.50,
    "stock": 100,
    "stockMinimo": 10,
    "categoriaId": 1,
    "activo": true,
    "createdAt": "2025-10-10 15:15:09.0",
    "categoria": {
      "id": 1,
      "nombre": "Analgésicos",
      "descripcion": "Medicamentos para el dolor"
    }
  }
]
```

#### 🔍 Obtener Producto por ID
```http
GET /api/productos/{id}
```

#### ➕ Crear Nuevo Producto
```http
POST /api/productos
```

**Body:**
```json
{
  "nombre": "Ibuprofeno 400mg",
  "descripcion": "Antiinflamatorio no esteroideo",
  "precio": 25.00,
  "stock": 50,
  "stockMinimo": 5,
  "categoria_id": 1
}
```

#### ✏️ Actualizar Producto
```http
PUT /api/productos/{id}
```

#### 📊 Actualizar Stock
```http
PUT /api/productos/{id}/stock
```

**Body:**
```json
{
  "nuevoStock": 75
}
```

#### 🗑️ Eliminar Producto
```http
DELETE /api/productos/{id}
```

---

## 🏷️ Categorías

### Base URL: `/api/categorias`

#### 📋 Obtener Todas las Categorías
```http
GET /api/categorias
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "nombre": "Analgésicos",
    "descripcion": "Medicamentos para el dolor",
    "activo": true,
    "createdAt": "2025-10-10 15:15:09.0"
  }
]
```

#### 🔍 Obtener Categoría por ID
```http
GET /api/categorias/{id}
```

#### ➕ Crear Nueva Categoría
```http
POST /api/categorias
```

**Body:**
```json
{
  "nombre": "Antibióticos",
  "descripcion": "Medicamentos contra infecciones bacterianas",
  "activo": true
}
```

#### ✏️ Actualizar Categoría
```http
PUT /api/categorias/{id}
```

#### 🗑️ Eliminar Categoría
```http
DELETE /api/categorias/{id}
```

---

## � Clientes

### Base URL: `/api/clientes`

#### 📋 Obtener Todos los Clientes
```http
GET /api/clientes
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "nombre": "María",
    "apellido": "González",
    "cedula": "1234567890",
    "telefono": "555-0123",
    "email": "maria.gonzalez@email.com",
    "direccion": "Calle Principal 123",
    "activo": true,
    "createdAt": "2025-10-10 15:15:09.0"
  }
]
```

#### 🔍 Obtener Cliente por ID
```http
GET /api/clientes/{id}
```

#### ➕ Crear Nuevo Cliente
```http
POST /api/clientes
```

**Body:**
```json
{
  "nombre": "Carlos",
  "apellido": "Ruiz",
  "cedula": "9876543210",
  "telefono": "555-0456",
  "email": "carlos.ruiz@email.com",
  "direccion": "Avenida Central 456"
}
```

#### ✏️ Actualizar Cliente
```http
PUT /api/clientes/{id}
```

#### 🗑️ Eliminar Cliente
```http
DELETE /api/clientes/{id}
```

---

## 🏢 Proveedores

### Base URL: `/api/proveedores`

#### 📋 Obtener Todos los Proveedores
```http
GET /api/proveedores
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "nombre": "Laboratorios ABC",
    "contacto": "Juan Pérez",
    "telefono": "555-0789",
    "email": "contacto@labsabc.com",
    "direccion": "Zona Industrial 789",
    "activo": true,
    "createdAt": "2025-10-10 15:15:09.0"
  }
]
```

#### 🔍 Obtener Proveedor por ID
```http
GET /api/proveedores/{id}
```

#### ➕ Crear Nuevo Proveedor
```http
POST /api/proveedores
```

**Body:**
```json
{
  "nombre": "Farmacéuticos XYZ",
  "contacto": "Ana López",
  "telefono": "555-0321",
  "email": "ventas@farmxyz.com",
  "direccion": "Boulevard Comercial 321"
}
```

#### ✏️ Actualizar Proveedor
```http
PUT /api/proveedores/{id}
```

#### 🗑️ Eliminar Proveedor
```http
DELETE /api/proveedores/{id}
```

---

## 🛒 Compras

### Base URL: `/api/compras`

#### 📋 Obtener Todas las Compras
```http
GET /api/compras
```

**Parámetros opcionales:**
- `proveedor_id` (query) - Filtrar por proveedor

**Respuesta:**
```json
[
  {
    "id": 1,
    "proveedorId": 1,
    "usuarioId": 1,
    "fechaCompra": "2025-10-10",
    "total": 1500.00,
    "estado": "COMPLETADA",
    "proveedor": {
      "id": 1,
      "nombre": "Laboratorios ABC"
    },
    "detalles": [
      {
        "id": 1,
        "productoId": 1,
        "cantidad": 50,
        "precioUnitario": 30.00,
        "subtotal": 1500.00,
        "producto": {
          "nombre": "Paracetamol 500mg"
        }
      }
    ]
  }
]
```

#### 🔍 Obtener Compra por ID
```http
GET /api/compras/{id}
```

#### ➕ Crear Nueva Compra
```http
POST /api/compras
```

**Body:**
```json
{
  "proveedor_id": 1,
  "usuario_id": 1,
  "productos": [
    {
      "producto_id": 1,
      "cantidad": 50,
      "precio_unitario": 30.00
    },
    {
      "producto_id": 2,
      "cantidad": 25,
      "precio_unitario": 45.00
    }
  ]
}
```

#### ✏️ Actualizar Compra
```http
PUT /api/compras/{id}
```

#### 🗑️ Eliminar Compra
```http
DELETE /api/compras/{id}
```

---

## 💰 Ventas

### Base URL: `/api/ventas`

#### 📋 Obtener Todas las Ventas
```http
GET /api/ventas
```

**Parámetros opcionales:**
- `cliente_id` (query) - Filtrar por cliente
- `fecha_inicio` (query) - Fecha inicio (YYYY-MM-DD)
- `fecha_fin` (query) - Fecha fin (YYYY-MM-DD)

**Respuesta:**
```json
[
  {
    "id": 1,
    "clienteId": 1,
    "usuarioId": 1,
    "fechaVenta": "2025-10-10",
    "total": 45.50,
    "estado": "COMPLETADA",
    "cliente": {
      "id": 1,
      "nombre": "María González"
    },
    "detalles": [
      {
        "id": 1,
        "productoId": 1,
        "cantidad": 2,
        "precioUnitario": 15.50,
        "subtotal": 31.00,
        "producto": {
          "nombre": "Paracetamol 500mg"
        }
      }
    ]
  }
]
```

#### 🔍 Obtener Venta por ID
```http
GET /api/ventas/{id}
```

#### ➕ Crear Nueva Venta
```http
POST /api/ventas
```

**Body:**
```json
{
  "cliente_id": 1,
  "usuario_id": 1,
  "productos": [
    {
      "producto_id": 1,
      "cantidad": 2
    },
    {
      "producto_id": 3,
      "cantidad": 1
    }
  ]
}
```

#### ✏️ Actualizar Venta
```http
PUT /api/ventas/{id}
```

#### 🗑️ Eliminar Venta
```http
DELETE /api/ventas/{id}
```

---

## 📊 Reportes y Dashboard

### Base URL: `/api/reportes`

#### � Dashboard Principal
```http
GET /api/reportes
```

**Respuesta:**
```json
{
  "estadisticas": {
    "totalVentas": 150,
    "totalCompras": 25,
    "totalProductos": 95,
    "totalClientes": 15,
    "totalProveedores": 20,
    "montoTotalVentas": 45750.50,
    "montoTotalCompras": 125000.00,
    "productosStockBajo": 5
  },
  "fechaGeneracion": "2025-10-11",
  "sistema": "FarmaControl v1.0",
  "descripcion": "Dashboard del sistema de gestión farmacéutica",
  "estado": "Datos actualizados correctamente"
}
```

#### 💰 Reporte de Ventas
```http
GET /api/reportes/ventas
```

**Parámetros opcionales:**
- `fecha_inicio` (query) - Fecha inicio
- `fecha_fin` (query) - Fecha fin
- `cliente_id` (query) - ID del cliente

#### 🛒 Reporte de Compras
```http
GET /api/reportes/compras?proveedor_id={id}
```

**Parámetros:**
- `proveedor_id` (query) - ID del proveedor (requerido)

#### 🏆 Productos Más Vendidos
```http
GET /api/reportes/productos/mas-vendidos
```

#### 📦 Reporte de Inventario
```http
GET /api/reportes/inventario
```

#### ⚠️ Inventario con Stock Bajo
```http
GET /api/reportes/inventario/bajo
```

**Parámetros opcionales:**
- `limite` (query) - Límite de stock (default: 10)

#### 👥 Clientes Frecuentes
```http
GET /api/reportes/clientes
```

---

## 🧪 Ejemplos de Uso Completos

### Flujo de Venta Completo
```bash
# 1. Autenticarse
curl -X POST "http://localhost:8080/api/usuarios/auth" \
  -H "Content-Type: application/json" \
  -d '{"email": "juan@farmacontrol.com", "password": "juan123"}'

# 2. Consultar productos disponibles
curl -X GET "http://localhost:8080/api/productos"

# 3. Verificar cliente
curl -X GET "http://localhost:8080/api/clientes/1"

# 4. Crear la venta
curl -X POST "http://localhost:8080/api/ventas" \
  -H "Content-Type: application/json" \
  -d '{
    "cliente_id": 1,
    "usuario_id": 3,
    "productos": [
      {"producto_id": 1, "cantidad": 2},
      {"producto_id": 5, "cantidad": 1}
    ]
  }'

# 5. Verificar dashboard actualizado
curl -X GET "http://localhost:8080/api/reportes"
```

### Gestión de Inventario
```bash
# 1. Consultar productos con stock bajo
curl -X GET "http://localhost:8080/api/reportes/inventario/bajo?limite=5"

# 2. Crear compra para reponer stock
curl -X POST "http://localhost:8080/api/compras" \
  -H "Content-Type: application/json" \
  -d '{
    "proveedor_id": 1,
    "usuario_id": 1,
    "productos": [
      {"producto_id": 3, "cantidad": 100, "precio_unitario": 25.00}
    ]
  }'

# 3. Verificar stock actualizado
curl -X GET "http://localhost:8080/api/productos/3"
```

---

## 🛠️ Herramientas de Desarrollo

### Testear Endpoints con cURL
Todos los ejemplos incluyen comandos cURL para probar directamente desde terminal.

### Postman Collection
Próximamente: Collection de Postman con todos los endpoints configurados.

### Swagger UI
Acceso futuro en: `http://localhost:8080/swagger-ui.html`

---

## 🔧 Configuración del Frontend

### Headers CORS
El sistema ya tiene CORS habilitado para trabajar con cualquier frontend.

### Formato de Errores
```json
{
  "success": false,
  "error": "Mensaje descriptivo del error"
}
```

### Validaciones
- Email único para usuarios
- Cédula única para clientes
- Stock no puede ser negativo
- Precios deben ser positivos

---

## 📞 Soporte

Para dudas sobre la API:
1. Consultar esta documentación
2. Verificar ejemplos de cURL
3. Revisar logs del servidor
4. Contactar al equipo de desarrollo

---

**📚 FarmaControl API v1.0** - Documentación completa de endpoints  
*Última actualización: 11 de octubre de 2025*