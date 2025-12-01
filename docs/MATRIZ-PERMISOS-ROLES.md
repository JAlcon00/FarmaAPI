# 📋 Matriz de Permisos por Rol - FarmaControl API

## 🎯 Resumen Ejecutivo

Este documento define el sistema de control de acceso basado en roles (RBAC) implementado en FarmaControl API. El sistema cuenta con **20 roles predefinidos** y permisos granulares sobre **8 módulos principales**.

## 🔐 Roles del Sistema

### Roles Administrativos (IDs 1-3)
| ID | Rol | Descripción | Nivel de Acceso |
|----|-----|-------------|-----------------|
| 1 | `ADMIN` | Administrador del Sistema | **Total** - Acceso completo a todos los módulos |
| 2 | `DIRECTOR` | Director General | **Alto** - Gestión estratégica y supervisión general |
| 3 | `GERENTE` | Gerente de Operaciones | **Alto** - Operaciones, reportes y supervisión |

### Roles Operativos (IDs 4-8)
| ID | Rol | Descripción | Nivel de Acceso |
|----|-----|-------------|-----------------|
| 4 | `FARMACEUTICO` | Farmacéutico Certificado | **Medio-Alto** - Ventas, productos, consultas |
| 5 | `CAJERO` | Cajero de Mostrador | **Medio** - Ventas y consultas básicas |
| 6 | `ALMACEN` | Encargado de Almacén | **Medio** - Productos, compras e inventario |
| 7 | `ENCARGADO_COMPRAS` | Jefe de Compras | **Medio-Alto** - Compras, proveedores, productos |
| 8 | `ENCARGADO_VENTAS` | Jefe de Ventas | **Medio-Alto** - Ventas, clientes, productos |

### Roles Especializados (IDs 9-16)
| ID | Rol | Descripción | Nivel de Acceso |
|----|-----|-------------|-----------------|
| 9 | `CONTADOR` | Contador/Finanzas | **Medio** - Reportes financieros y auditoría |
| 10 | `RRHH` | Recursos Humanos | **Medio** - Gestión de usuarios y personal |
| 11 | `AUDITOR` | Auditor Interno | **Solo Lectura** - Acceso total en modo consulta |
| 12 | `SUPERVISOR` | Supervisor de Turno | **Medio** - Supervisión operativa |
| 13 | `OPERADOR` | Operador de Sistema | **Bajo** - Operaciones básicas |
| 14 | `ASISTENTE` | Asistente Administrativo | **Bajo** - Soporte administrativo |
| 15 | `CONSULTOR` | Consultor Externo | **Solo Lectura** - Consultas limitadas |
| 16 | `SOPORTE` | Soporte Técnico | **Técnico** - Mantenimiento del sistema |

### Roles de Acceso Limitado (IDs 17-20)
| ID | Rol | Descripción | Nivel de Acceso |
|----|-----|-------------|-----------------|
| 17 | `INVITADO` | Usuario Invitado | **Mínimo** - Solo lectura limitada |
| 18 | `CLIENTE_VIP` | Cliente Corporativo | **Externo** - Portal de cliente |
| 19 | `PROVEEDOR_EXTERNO` | Proveedor Externo | **Externo** - Portal de proveedor |
| 20 | `BECARIO` | Becario/Practicante | **Temporal** - Acceso supervisado |

---

## 📊 Matriz Completa de Permisos

### Leyenda
- ✅ **Acceso Completo** (Crear, Leer, Actualizar, Eliminar)
- 📖 **Solo Lectura** (Consultar únicamente)
- 🔨 **Crear/Modificar** (Sin eliminación)
- ❌ **Sin Acceso**

---

## 🛒 MÓDULO: PRODUCTOS

### Permisos Disponibles
- `PRODUCTOS_READ` - Ver listado de productos
- `PRODUCTOS_WRITE` - Crear/Modificar productos
- `PRODUCTOS_DELETE` - Eliminar productos

### Matriz de Acceso

| Rol | Lectura | Crear/Editar | Eliminar | Endpoints Disponibles |
|-----|---------|--------------|----------|----------------------|
| ADMIN | ✅ | ✅ | ✅ | Todos |
| DIRECTOR | ✅ | ✅ | ✅ | Todos |
| GERENTE | ✅ | ✅ | ✅ | Todos |
| FARMACEUTICO | ✅ | ✅ | ❌ | GET, POST, PUT |
| CAJERO | ✅ | ❌ | ❌ | GET |
| ALMACEN | ✅ | ✅ | ❌ | GET, POST, PUT |
| ENCARGADO_COMPRAS | ✅ | ✅ | ❌ | GET, POST, PUT |
| ENCARGADO_VENTAS | ✅ | 🔨 | ❌ | GET, POST, PUT |
| CONTADOR | ✅ | ❌ | ❌ | GET |
| RRHH | ✅ | ❌ | ❌ | GET |
| AUDITOR | ✅ | ❌ | ❌ | GET |
| SUPERVISOR | ✅ | 🔨 | ❌ | GET, POST, PUT |
| OPERADOR | ✅ | ❌ | ❌ | GET |
| ASISTENTE | ✅ | ❌ | ❌ | GET |
| CONSULTOR | ✅ | ❌ | ❌ | GET |
| SOPORTE | ✅ | ❌ | ❌ | GET |
| INVITADO | ❌ | ❌ | ❌ | Ninguno |
| CLIENTE_VIP | ❌ | ❌ | ❌ | Ninguno |
| PROVEEDOR_EXTERNO | ❌ | ❌ | ❌ | Ninguno |
| BECARIO | ✅ | ❌ | ❌ | GET |

**Endpoints:**
- `GET /api/productos` - Requiere `PRODUCTOS_READ`
- `POST /api/productos` - Requiere `PRODUCTOS_WRITE`
- `PUT /api/productos/{id}` - Requiere `PRODUCTOS_WRITE`
- `DELETE /api/productos/{id}` - Requiere `PRODUCTOS_DELETE`

---

## 💰 MÓDULO: VENTAS

### Permisos Disponibles
- `VENTAS_READ` - Ver historial de ventas
- `VENTAS_CREATE` - Realizar ventas
- `VENTAS_CANCEL` - Cancelar/anular ventas

### Matriz de Acceso

| Rol | Lectura | Crear | Cancelar | Endpoints Disponibles |
|-----|---------|-------|----------|----------------------|
| ADMIN | ✅ | ✅ | ✅ | Todos |
| DIRECTOR | ✅ | ✅ | ✅ | Todos |
| GERENTE | ✅ | ✅ | ✅ | Todos |
| FARMACEUTICO | ✅ | ✅ | ✅ | Todos |
| CAJERO | ✅ | ✅ | ❌ | GET, POST |
| ALMACEN | ✅ | ❌ | ❌ | GET |
| ENCARGADO_COMPRAS | ✅ | ❌ | ❌ | GET |
| ENCARGADO_VENTAS | ✅ | ✅ | ✅ | Todos |
| CONTADOR | ✅ | ❌ | ❌ | GET |
| RRHH | ❌ | ❌ | ❌ | Ninguno |
| AUDITOR | ✅ | ❌ | ❌ | GET |
| SUPERVISOR | ✅ | ✅ | 🔨 | GET, POST, PUT |
| OPERADOR | ✅ | ✅ | ❌ | GET, POST |
| ASISTENTE | ✅ | ❌ | ❌ | GET |
| CONSULTOR | ✅ | ❌ | ❌ | GET |
| SOPORTE | ❌ | ❌ | ❌ | Ninguno |
| INVITADO | ❌ | ❌ | ❌ | Ninguno |
| CLIENTE_VIP | ❌ | ❌ | ❌ | Ninguno |
| PROVEEDOR_EXTERNO | ❌ | ❌ | ❌ | Ninguno |
| BECARIO | ✅ | ❌ | ❌ | GET |

**Endpoints:**
- `GET /api/ventas` - Requiere `VENTAS_READ`
- `POST /api/ventas` - Requiere `VENTAS_CREATE`
- `PUT /api/ventas/{id}/cancelar` - Requiere `VENTAS_CANCEL`

---

## 📦 MÓDULO: COMPRAS

### Permisos Disponibles
- `COMPRAS_READ` - Ver historial de compras
- `COMPRAS_CREATE` - Crear órdenes de compra
- `COMPRAS_CANCEL` - Cancelar compras

### Matriz de Acceso

| Rol | Lectura | Crear | Cancelar | Endpoints Disponibles |
|-----|---------|-------|----------|----------------------|
| ADMIN | ✅ | ✅ | ✅ | Todos |
| DIRECTOR | ✅ | ✅ | ✅ | Todos |
| GERENTE | ✅ | ✅ | ✅ | Todos |
| FARMACEUTICO | ✅ | 🔨 | ❌ | GET, POST |
| CAJERO | ❌ | ❌ | ❌ | Ninguno |
| ALMACEN | ✅ | ✅ | ❌ | GET, POST |
| ENCARGADO_COMPRAS | ✅ | ✅ | ✅ | Todos |
| ENCARGADO_VENTAS | ✅ | ❌ | ❌ | GET |
| CONTADOR | ✅ | ❌ | ❌ | GET |
| RRHH | ❌ | ❌ | ❌ | Ninguno |
| AUDITOR | ✅ | ❌ | ❌ | GET |
| SUPERVISOR | ✅ | 🔨 | ❌ | GET, POST |
| OPERADOR | ✅ | ❌ | ❌ | GET |
| ASISTENTE | ✅ | ❌ | ❌ | GET |
| CONSULTOR | ✅ | ❌ | ❌ | GET |
| SOPORTE | ❌ | ❌ | ❌ | Ninguno |
| INVITADO | ❌ | ❌ | ❌ | Ninguno |
| CLIENTE_VIP | ❌ | ❌ | ❌ | Ninguno |
| PROVEEDOR_EXTERNO | ❌ | ❌ | ❌ | Ninguno |
| BECARIO | ✅ | ❌ | ❌ | GET |

**Endpoints:**
- `GET /api/compras` - Requiere `COMPRAS_READ`
- `POST /api/compras` - Requiere `COMPRAS_CREATE`
- `PUT /api/compras/{id}/cancelar` - Requiere `COMPRAS_CANCEL`

---

## 👥 MÓDULO: CLIENTES

### Permisos Disponibles
- `CLIENTES_READ` - Ver listado de clientes
- `CLIENTES_WRITE` - Crear/Modificar clientes
- `CLIENTES_DELETE` - Eliminar clientes

### Matriz de Acceso

| Rol | Lectura | Crear/Editar | Eliminar | Endpoints Disponibles |
|-----|---------|--------------|----------|----------------------|
| ADMIN | ✅ | ✅ | ✅ | Todos |
| DIRECTOR | ✅ | ✅ | ✅ | Todos |
| GERENTE | ✅ | ✅ | ✅ | Todos |
| FARMACEUTICO | ✅ | ✅ | ❌ | GET, POST, PUT |
| CAJERO | ✅ | ✅ | ❌ | GET, POST, PUT |
| ALMACEN | ✅ | ❌ | ❌ | GET |
| ENCARGADO_COMPRAS | ✅ | ❌ | ❌ | GET |
| ENCARGADO_VENTAS | ✅ | ✅ | ❌ | GET, POST, PUT |
| CONTADOR | ✅ | ❌ | ❌ | GET |
| RRHH | ❌ | ❌ | ❌ | Ninguno |
| AUDITOR | ✅ | ❌ | ❌ | GET |
| SUPERVISOR | ✅ | ✅ | ❌ | GET, POST, PUT |
| OPERADOR | ✅ | 🔨 | ❌ | GET, POST, PUT |
| ASISTENTE | ✅ | ✅ | ❌ | GET, POST, PUT |
| CONSULTOR | ✅ | ❌ | ❌ | GET |
| SOPORTE | ❌ | ❌ | ❌ | Ninguno |
| INVITADO | ❌ | ❌ | ❌ | Ninguno |
| CLIENTE_VIP | ❌ | ❌ | ❌ | Ninguno |
| PROVEEDOR_EXTERNO | ❌ | ❌ | ❌ | Ninguno |
| BECARIO | ✅ | ❌ | ❌ | GET |

**Endpoints:**
- `GET /api/clientes` - Requiere `CLIENTES_READ`
- `POST /api/clientes` - Requiere `CLIENTES_WRITE`
- `PUT /api/clientes/{id}` - Requiere `CLIENTES_WRITE`
- `DELETE /api/clientes/{id}` - Requiere `CLIENTES_DELETE`

---

## 🏢 MÓDULO: PROVEEDORES

### Permisos Disponibles
- `PROVEEDORES_READ` - Ver listado de proveedores
- `PROVEEDORES_WRITE` - Crear/Modificar proveedores
- `PROVEEDORES_DELETE` - Eliminar proveedores

### Matriz de Acceso

| Rol | Lectura | Crear/Editar | Eliminar | Endpoints Disponibles |
|-----|---------|--------------|----------|----------------------|
| ADMIN | ✅ | ✅ | ✅ | Todos |
| DIRECTOR | ✅ | ✅ | ✅ | Todos |
| GERENTE | ✅ | ✅ | ✅ | Todos |
| FARMACEUTICO | ✅ | ❌ | ❌ | GET |
| CAJERO | ❌ | ❌ | ❌ | Ninguno |
| ALMACEN | ✅ | 🔨 | ❌ | GET, POST, PUT |
| ENCARGADO_COMPRAS | ✅ | ✅ | ❌ | GET, POST, PUT |
| ENCARGADO_VENTAS | ✅ | ❌ | ❌ | GET |
| CONTADOR | ✅ | ❌ | ❌ | GET |
| RRHH | ❌ | ❌ | ❌ | Ninguno |
| AUDITOR | ✅ | ❌ | ❌ | GET |
| SUPERVISOR | ✅ | ❌ | ❌ | GET |
| OPERADOR | ✅ | ❌ | ❌ | GET |
| ASISTENTE | ✅ | 🔨 | ❌ | GET, POST, PUT |
| CONSULTOR | ✅ | ❌ | ❌ | GET |
| SOPORTE | ❌ | ❌ | ❌ | Ninguno |
| INVITADO | ❌ | ❌ | ❌ | Ninguno |
| CLIENTE_VIP | ❌ | ❌ | ❌ | Ninguno |
| PROVEEDOR_EXTERNO | ❌ | ❌ | ❌ | Ninguno |
| BECARIO | ✅ | ❌ | ❌ | GET |

**Endpoints:**
- `GET /api/proveedores` - Requiere `PROVEEDORES_READ`
- `POST /api/proveedores` - Requiere `PROVEEDORES_WRITE`

---

## 📁 MÓDULO: CATEGORÍAS

### Permisos Disponibles
- `CATEGORIAS_READ` - Ver categorías
- `CATEGORIAS_WRITE` - Crear/Modificar categorías
- `CATEGORIAS_DELETE` - Eliminar categorías

### Matriz de Acceso

| Rol | Lectura | Crear/Editar | Eliminar | Endpoints Disponibles |
|-----|---------|--------------|----------|----------------------|
| ADMIN | ✅ | ✅ | ✅ | Todos |
| DIRECTOR | ✅ | ✅ | ✅ | Todos |
| GERENTE | ✅ | ✅ | ✅ | Todos |
| FARMACEUTICO | ✅ | 🔨 | ❌ | GET, POST, PUT |
| CAJERO | ✅ | ❌ | ❌ | GET |
| ALMACEN | ✅ | 🔨 | ❌ | GET, POST, PUT |
| ENCARGADO_COMPRAS | ✅ | 🔨 | ❌ | GET, POST, PUT |
| ENCARGADO_VENTAS | ✅ | 🔨 | ❌ | GET, POST, PUT |
| CONTADOR | ✅ | ❌ | ❌ | GET |
| RRHH | ❌ | ❌ | ❌ | Ninguno |
| AUDITOR | ✅ | ❌ | ❌ | GET |
| SUPERVISOR | ✅ | 🔨 | ❌ | GET, POST, PUT |
| OPERADOR | ✅ | ❌ | ❌ | GET |
| ASISTENTE | ✅ | ❌ | ❌ | GET |
| CONSULTOR | ✅ | ❌ | ❌ | GET |
| SOPORTE | ✅ | ❌ | ❌ | GET |
| INVITADO | ❌ | ❌ | ❌ | Ninguno |
| CLIENTE_VIP | ❌ | ❌ | ❌ | Ninguno |
| PROVEEDOR_EXTERNO | ❌ | ❌ | ❌ | Ninguno |
| BECARIO | ✅ | ❌ | ❌ | GET |

**Endpoints:**
- `GET /api/categorias` - Requiere `CATEGORIAS_READ`
- `POST /api/categorias` - Requiere `CATEGORIAS_WRITE`
- `PUT /api/categorias/{id}` - Requiere `CATEGORIAS_WRITE`
- `DELETE /api/categorias/{id}` - Requiere `CATEGORIAS_DELETE`

---

## 👤 MÓDULO: USUARIOS

### Permisos Disponibles
- `USUARIOS_READ` - Ver usuarios del sistema
- `USUARIOS_WRITE` - Crear/Modificar usuarios
- `USUARIOS_DELETE` - Eliminar usuarios

### Matriz de Acceso

| Rol | Lectura | Crear/Editar | Eliminar | Endpoints Disponibles |
|-----|---------|--------------|----------|----------------------|
| ADMIN | ✅ | ✅ | ✅ | Todos |
| DIRECTOR | ✅ | ✅ | ✅ | Todos |
| GERENTE | ✅ | ❌ | ❌ | GET |
| FARMACEUTICO | ❌ | ❌ | ❌ | Ninguno |
| CAJERO | ❌ | ❌ | ❌ | Ninguno |
| ALMACEN | ❌ | ❌ | ❌ | Ninguno |
| ENCARGADO_COMPRAS | ❌ | ❌ | ❌ | Ninguno |
| ENCARGADO_VENTAS | ❌ | ❌ | ❌ | Ninguno |
| CONTADOR | ❌ | ❌ | ❌ | Ninguno |
| RRHH | ✅ | ✅ | ❌ | GET, POST, PUT |
| AUDITOR | ✅ | ❌ | ❌ | GET |
| SUPERVISOR | ❌ | ❌ | ❌ | Ninguno |
| OPERADOR | ❌ | ❌ | ❌ | Ninguno |
| ASISTENTE | ❌ | ❌ | ❌ | Ninguno |
| CONSULTOR | ❌ | ❌ | ❌ | Ninguno |
| SOPORTE | ✅ | ❌ | ❌ | GET |
| INVITADO | ❌ | ❌ | ❌ | Ninguno |
| CLIENTE_VIP | ❌ | ❌ | ❌ | Ninguno |
| PROVEEDOR_EXTERNO | ❌ | ❌ | ❌ | Ninguno |
| BECARIO | ❌ | ❌ | ❌ | Ninguno |

**Endpoints:**
- `GET /api/usuarios` - Requiere `USUARIOS_READ`
- `POST /api/usuarios` - Requiere `USUARIOS_WRITE`
- `PUT /api/usuarios/{id}` - Requiere `USUARIOS_WRITE`
- `DELETE /api/usuarios/{id}` - Requiere `USUARIOS_DELETE`

---

## 🎭 MÓDULO: ROLES

### Permisos Disponibles
- `ROLES_MANAGE` - Gestión completa de roles

### Matriz de Acceso

| Rol | Gestión Roles | Endpoints Disponibles |
|-----|---------------|----------------------|
| ADMIN | ✅ | Todos (GET, POST, PUT, DELETE) |
| DIRECTOR | ✅ | Todos (GET, POST, PUT, DELETE) |
| **Todos los demás** | ❌ | Solo GET /api/roles (lectura) |

**Endpoints:**
- `GET /api/roles` - Acceso público (autenticado)
- `POST /api/roles` - Requiere `ROLES_MANAGE`
- `PUT /api/roles/{id}` - Requiere `ROLES_MANAGE`
- `DELETE /api/roles/{id}` - Requiere `ROLES_MANAGE`

---

## 🔧 Implementación Técnica

### Archivos Clave

```
src/java/
├── security/
│   ├── JwtTokenProvider.java       # Generación y validación de JWT
│   └── RolePermissions.java        # Definición de roles y permisos
├── filter/
│   └── JwtAuthenticationFilter.java # Filtro de autenticación
├── utils/
│   └── AuthorizationHelper.java    # Validación de permisos
└── routes/
    ├── ProductoServlet.java        # ✅ Validación integrada
    ├── VentaServlet.java           # ✅ Validación integrada
    ├── CompraServlet.java          # ✅ Validación integrada
    ├── ClienteServlet.java         # ✅ Validación integrada
    ├── ProveedorServlet.java       # ✅ Validación integrada
    ├── CategoriaServlet.java       # ✅ Validación integrada
    ├── UsuarioServlet.java         # ✅ Validación integrada
    └── RoleServlet.java            # ✅ Validación integrada
```

### Ejemplo de Uso en Código

```java
@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    
    enableCORS(response);
    
    // Verificar autorización
    if (!AuthorizationHelper.checkRoles(request, response, 
            RolePermissions.PRODUCTOS_WRITE)) {
        return; // 403 Forbidden automático
    }
    
    // Lógica del endpoint...
}
```

### Flujo de Autorización

```
1. Usuario envía petición con JWT token
   ↓
2. JwtAuthenticationFilter valida token
   ↓
3. Filter inyecta userId, email, roleId en request
   ↓
4. Servlet llama AuthorizationHelper.checkRoles()
   ↓
5. Helper verifica si roleId está en el Set de permisos
   ↓
6. Si NO autorizado → 403 Forbidden automático
   Si autorizado → Continúa ejecución
```

---

## 📝 Notas Importantes

### Seguridad
- ✅ Todos los endpoints protegidos requieren JWT válido
- ✅ Token expira en 24 horas
- ✅ Respuestas 403 Forbidden automáticas
- ✅ Sin exposición de información sensible en errores

### Endpoints Públicos
```
/api/usuarios/auth    # Login - no requiere autenticación
/health               # Health check
/actuator/*          # Métricas Prometheus
```

### Casos Especiales
- **ADMIN y DIRECTOR**: Acceso completo a todos los módulos
- **AUDITOR**: Solo lectura en todos los módulos
- **INVITADO**: Sin acceso a API (solo frontend público)
- **BECARIO**: Solo lectura limitada para aprendizaje

---

## 🧪 Testing

### Ejemplo de prueba con cURL

```bash
# 1. Login para obtener token
curl -X POST http://localhost:8081/api/usuarios/auth \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "email=admin@farma.com&password=admin123"

# Respuesta:
# {
#   "success": true,
#   "data": {
#     "token": "eyJhbGciOiJIUzI1NiJ9...",
#     "usuario": {...}
#   }
# }

# 2. Usar token en petición protegida
curl -X POST http://localhost:8081/api/productos \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "nombre=Aspirina&precio=50.00&categoriaId=1"

# 3. Intentar sin permisos (ej: CAJERO creando producto)
# Respuesta: 403 Forbidden
# {
#   "success": false,
#   "message": "No tienes permisos para realizar esta acción"
# }
```

---

## 📚 Referencias

- [JWT Authentication Guide](JWT-AUTHENTICATION-GUIDE.md)
- [Guía Rápida de Roles](GUIA-RAPIDA-ROLES.md)
- [API Documentation](docs/API_DOCUMENTATION.md)

---

## 📞 Soporte

Para dudas o problemas con permisos:
1. Verificar que el JWT incluye `roleId` correcto
2. Consultar esta matriz para confirmar permisos
3. Revisar logs del servidor para detalles de rechazo

---

**Última actualización**: Noviembre 2024  
**Versión**: 1.0.0  
**Sistema**: FarmaControl API - Fase 2 Completa
