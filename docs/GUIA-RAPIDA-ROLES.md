# 🚀 Guía Rápida: Sistema de Roles y Permisos

## 📖 Introducción

Esta guía muestra cómo utilizar el sistema de autorización basado en roles implementado en FarmaControl API.

---

## 🎯 Conceptos Básicos

### ¿Qué es un Rol?
Un rol define el cargo o función de un usuario en el sistema (Ej: ADMIN, FARMACEUTICO, CAJERO).

### ¿Qué son los Permisos?
Los permisos determinan qué acciones puede realizar cada rol (Ej: crear productos, cancelar ventas).

---

## 🔑 Roles Disponibles

| ID | Rol | Descripción |
|----|-----|-------------|
| 1 | ADMIN | Administrador total del sistema |
| 2 | DIRECTOR | Dirección general |
| 3 | GERENTE | Gerencia operativa |
| 4 | FARMACEUTICO | Farmacéutico profesional |
| 5 | CAJERO | Cajero de ventas |
| 6 | ALMACEN | Encargado de almacén |
| 7 | ENCARGADO_VENTAS | Supervisor de ventas |
| 8 | ENCARGADO_COMPRAS | Gestor de compras |
| 9 | CONTADOR | Contador/finanzas |
| 10 | AUDITOR | Auditor interno |
| 11 | RRHH | Recursos humanos |
| 12 | SUPERVISOR | Supervisor general |
| 13 | ENCARGADO_INVENTARIO | Control de inventario |
| 14 | RECEPCIONISTA | Recepción |
| 15 | SOPORTE_TECNICO | Soporte técnico |
| 16 | ANALISTA_DATOS | Analista de datos |
| 17 | ENCARGADO_CALIDAD | Control de calidad |
| 18 | PRACTICANTE | Practicante/pasante |
| 19 | TEMPORAL | Empleado temporal |
| 20 | INVITADO | Usuario invitado (solo lectura) |

---

## 📋 Matriz de Permisos Rápida

### PRODUCTOS
| Acción | Roles Permitidos |
|--------|------------------|
| Ver (GET) | Casi todos (excepto INVITADO) |
| Crear/Editar (POST/PUT) | ADMIN, DIRECTOR, GERENTE, FARMACEUTICO, ALMACEN, ENCARGADO_COMPRAS, ENCARGADO_INVENTARIO |
| Eliminar (DELETE) | ADMIN, DIRECTOR, GERENTE |

### VENTAS
| Acción | Roles Permitidos |
|--------|------------------|
| Crear venta | ADMIN, DIRECTOR, GERENTE, FARMACEUTICO, CAJERO, ENCARGADO_VENTAS, SUPERVISOR |
| Cancelar venta | ADMIN, DIRECTOR, GERENTE, ENCARGADO_VENTAS, SUPERVISOR |
| Eliminar venta | ADMIN, DIRECTOR |

### COMPRAS
| Acción | Roles Permitidos |
|--------|------------------|
| Crear compra | ADMIN, DIRECTOR, GERENTE, ALMACEN, ENCARGADO_COMPRAS, SUPERVISOR, ENCARGADO_INVENTARIO |
| Cancelar compra | ADMIN, DIRECTOR, GERENTE, ENCARGADO_COMPRAS, SUPERVISOR |

### CLIENTES
| Acción | Roles Permitidos |
|--------|------------------|
| Crear/Editar | ADMIN, DIRECTOR, GERENTE, FARMACEUTICO, CAJERO, ENCARGADO_VENTAS, SUPERVISOR, RECEPCIONISTA |
| Eliminar | ADMIN, DIRECTOR, GERENTE, RRHH |

### USUARIOS
| Acción | Roles Permitidos |
|--------|------------------|
| Gestionar usuarios | ADMIN, DIRECTOR, RRHH |

### ROLES
| Acción | Roles Permitidos |
|--------|------------------|
| Gestionar roles | ADMIN, DIRECTOR |

---

## 💻 Ejemplos de Uso

### 1️⃣ Login y Obtener Token

```bash
# Login con credenciales
curl -X POST http://localhost:8080/api/usuarios/auth \
  -H "Content-Type: application/json" \
  -d '{
    "email": "farmaceutico@farma.com",
    "password": "Password123"
  }'
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Autenticación exitosa",
  "data": {
    "usuario": {
      "id": 5,
      "nombre": "Juan Pérez",
      "email": "farmaceutico@farma.com",
      "roleId": 4
    },
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjUsInVzZXJFbWFpbCI6ImZhcm1hY2V1dGljb0BmYXJtYS5jb20iLCJyb2xlSWQiOjQsImlhdCI6MTcwMzQzMjAwMCwiZXhwIjoxNzAzNTE4NDAwfQ..."
  }
}
```

### 2️⃣ Usar el Token en Requests

Guarda el token en una variable:
```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..."
```

### 3️⃣ Operación Permitida: FARMACEUTICO Crea Producto

```bash
curl -X POST http://localhost:8080/api/productos \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Paracetamol 500mg",
    "categoriaId": 1,
    "precio": 25.50,
    "stock": 100
  }'
```

**Respuesta Exitosa (201 Created):**
```json
{
  "success": true,
  "message": "Producto creado exitosamente",
  "data": {
    "id": 45,
    "nombre": "Paracetamol 500mg",
    "categoriaId": 1,
    "precio": 25.50,
    "stock": 100
  }
}
```

### 4️⃣ Operación Denegada: CAJERO Intenta Crear Producto

```bash
# Login como cajero
curl -X POST http://localhost:8080/api/usuarios/auth \
  -d '{"email":"cajero@farma.com","password":"pass123"}'

TOKEN_CAJERO="eyJhbGc..."

# Intentar crear producto
curl -X POST http://localhost:8080/api/productos \
  -H "Authorization: Bearer $TOKEN_CAJERO" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Aspirina 100mg",
    "precio": 30.00
  }'
```

**Respuesta Error (403 Forbidden):**
```json
{
  "success": false,
  "message": "No tienes permisos para realizar esta acción. Rol actual: CAJERO",
  "data": null
}
```

### 5️⃣ Operación Permitida: CAJERO Realiza Venta

```bash
curl -X POST http://localhost:8080/api/ventas \
  -H "Authorization: Bearer $TOKEN_CAJERO" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 10,
    "detalles": [
      {
        "productoId": 45,
        "cantidad": 2,
        "precioUnitario": 25.50
      }
    ]
  }'
```

**Respuesta Exitosa (201 Created):**
```json
{
  "success": true,
  "message": "Venta registrada exitosamente",
  "data": {
    "id": 123,
    "clienteId": 10,
    "total": 51.00,
    "fecha": "2024-11-05T17:30:00"
  }
}
```

### 6️⃣ Solo ADMIN/DIRECTOR: Gestionar Roles

```bash
# Login como ADMIN
TOKEN_ADMIN="..."

# Crear nuevo rol
curl -X POST http://localhost:8080/api/roles \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "COORDINADOR",
    "descripcion": "Coordinador de área"
  }'
```

### 7️⃣ Solo RRHH/ADMIN/DIRECTOR: Crear Usuarios

```bash
# Login como RRHH
TOKEN_RRHH="..."

# Crear nuevo usuario
curl -X POST http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer $TOKEN_RRHH" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "María González",
    "email": "maria@farma.com",
    "password": "Password123",
    "roleId": 5
  }'
```

---

## 🔍 Códigos de Respuesta HTTP

| Código | Significado | Cuándo Ocurre |
|--------|-------------|---------------|
| 200 | OK | Operación exitosa (GET, PUT) |
| 201 | Created | Recurso creado exitosamente (POST) |
| 400 | Bad Request | Datos inválidos en request |
| 401 | Unauthorized | Token JWT inválido o expirado |
| 403 | Forbidden | Usuario sin permisos para la acción |
| 404 | Not Found | Recurso no encontrado |
| 500 | Internal Server Error | Error del servidor |

---

## 🛠️ Troubleshooting

### Error 401: Token Inválido
**Causa:** Token expirado (24 horas) o malformado  
**Solución:** Hacer login nuevamente para obtener nuevo token

### Error 403: Permisos Insuficientes
**Causa:** Tu rol no tiene permisos para la acción  
**Solución:** Contactar al administrador para cambio de rol o usar cuenta con permisos adecuados

### Error 400: Datos Inválidos
**Causa:** Request JSON mal formado o campos requeridos faltantes  
**Solución:** Verificar formato JSON y campos obligatorios

---

## 📊 Casos de Uso Comunes

### Caso 1: Farmacia Pequeña (3 usuarios)

```javascript
// Usuario 1: Dueño/Administrador
{
  "nombre": "Carlos Ramírez",
  "email": "carlos@farma.com",
  "roleId": 1  // ADMIN
}

// Usuario 2: Farmacéutico
{
  "nombre": "Ana López",
  "email": "ana@farma.com",
  "roleId": 4  // FARMACEUTICO
}

// Usuario 3: Auxiliar
{
  "nombre": "Luis Torres",
  "email": "luis@farma.com",
  "roleId": 5  // CAJERO
}
```

**Permisos:**
- Carlos: Todo
- Ana: Ventas, productos, clientes
- Luis: Solo ventas

### Caso 2: Cadena de Farmacias (10+ usuarios)

```javascript
// Nivel Directivo
{ "roleId": 2 }  // DIRECTOR
{ "roleId": 3 }  // GERENTE

// Nivel Operativo
{ "roleId": 4 }  // FARMACEUTICO (x3)
{ "roleId": 5 }  // CAJERO (x4)

// Nivel Administrativo
{ "roleId": 8 }  // ENCARGADO_COMPRAS
{ "roleId": 9 }  // CONTADOR
{ "roleId": 11 } // RRHH
```

### Caso 3: Farmacia con Sistema de Turnos

```javascript
// Turno Mañana: Farmacéutico + Cajero
// Turno Tarde: Farmacéutico + Cajero
// Turno Noche: Solo Farmacéutico (hace ambas funciones)

// Encargado de Inventario (revisión semanal)
{ "roleId": 13 }

// Supervisor (supervisión diaria)
{ "roleId": 12 }
```

---

## 📝 Checklist de Validaciones

Antes de cada operación, el sistema verifica:

✅ Token JWT válido  
✅ Token no expirado  
✅ Usuario existe en BD  
✅ Rol del usuario está activo  
✅ Rol tiene permisos para la acción específica  

---

## 🎓 Mejores Prácticas

### 1. Principio de Menor Privilegio
Asigna el rol con menos permisos necesarios para la función.

❌ **Mal:** Dar rol ADMIN a todos  
✅ **Bien:** Cajeros con rol CAJERO, farmacéuticos con rol FARMACEUTICO

### 2. Rotación de Tokens
Los tokens expiran en 24 horas. Implementa refresh automático en frontend.

### 3. Auditoría
Revisa logs periódicamente para detectar intentos de acceso no autorizado.

### 4. Cambio de Contraseñas
Política de cambio cada 90 días para usuarios con roles críticos (ADMIN, DIRECTOR, RRHH).

---

## 🔐 Seguridad

### Tokens JWT
- **Algoritmo:** HS256
- **Expiración:** 24 horas
- **Contenido:** userId, email, roleId
- **Almacenamiento:** LocalStorage (frontend), no almacenar en BD

### Permisos
- Validados en **cada request**
- No se confía en el frontend
- Validación server-side obligatoria

---

## 📞 Soporte

Para dudas sobre permisos o roles:
1. Consultar esta guía
2. Revisar `FASE-2-ROLES-COMPLETA.md` para detalles técnicos
3. Contactar al equipo de desarrollo

---

## ✅ Resumen de Comandos

```bash
# 1. Login
curl -X POST http://localhost:8080/api/usuarios/auth \
  -H "Content-Type: application/json" \
  -d '{"email":"user@farma.com","password":"pass"}'

# 2. Guardar token
TOKEN="..."

# 3. Usar token en operaciones
curl -X POST http://localhost:8080/api/productos \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Producto","precio":100}'

# 4. Ver respuestas
# 201 = Éxito
# 403 = Sin permisos
# 401 = Token inválido
```

---

**¡Sistema listo para usar!** 🎉

**Última actualización:** 5 de Noviembre, 2024
