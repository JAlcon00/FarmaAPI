# 🎭 Jerarquía y Matriz Visual de Roles

## 🏢 Estructura Jerárquica

```
┌─────────────────────────────────────────────────────────────────┐
│                      NIVEL EJECUTIVO                            │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐     │
│  │   ADMIN (1)  │    │ DIRECTOR (2) │    │ GERENTE (3)  │     │
│  │  Acceso Total│    │Alta Dirección│    │  Operaciones │     │
│  └──────────────┘    └──────────────┘    └──────────────┘     │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    NIVEL OPERATIVO PRINCIPAL                    │
│  ┌─────────────────┐  ┌─────────────────┐  ┌────────────────┐ │
│  │FARMACEUTICO (4) │  │   CAJERO (5)    │  │  ALMACEN (6)   │ │
│  │Gestión farmacia │  │Ventas y cobros  │  │Control stock   │ │
│  └─────────────────┘  └─────────────────┘  └────────────────┘ │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    NIVEL SUPERVISIÓN                            │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────┐ │
│  │ENCARGADO_VENTAS  │  │ENCARGADO_COMPRAS │  │SUPERVISOR(12)│ │
│  │      (7)         │  │       (8)        │  │General coord.│ │
│  └──────────────────┘  └──────────────────┘  └──────────────┘ │
│                                                                  │
│  ┌──────────────────┐                                          │
│  │ENCARGADO_INVENT. │                                          │
│  │      (13)        │                                          │
│  └──────────────────┘                                          │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                  NIVEL ADMINISTRATIVO                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │CONTADOR (9)  │  │ AUDITOR (10) │  │  RRHH (11)   │         │
│  │  Finanzas    │  │ Supervisión  │  │   Personal   │         │
│  └──────────────┘  └──────────────┘  └──────────────┘         │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    NIVEL ESPECIALIZADO                          │
│  ┌─────────────────┐  ┌─────────────────┐  ┌────────────────┐ │
│  │RECEPCIONISTA(14)│  │SOPORTE_TEC (15) │  │ANALISTA_DAT(16)│ │
│  │Atención cliente │  │Sistemas IT      │  │Reportes/BI     │ │
│  └─────────────────┘  └─────────────────┘  └────────────────┘ │
│                                                                  │
│  ┌─────────────────┐                                           │
│  │ENCARGADO_CAL(17)│                                           │
│  │Control calidad  │                                           │
│  └─────────────────┘                                           │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    NIVEL ACCESO LIMITADO                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │PRACTICANTE   │  │  TEMPORAL    │  │  INVITADO    │         │
│  │    (18)      │  │    (19)      │  │    (20)      │         │
│  │Aprendizaje   │  │Proyecto corto│  │Solo lectura  │         │
│  └──────────────┘  └──────────────┘  └──────────────┘         │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📊 Matriz de Permisos Visual

### Leyenda
- ✅ = Permiso completo
- 📝 = Solo lectura
- ❌ = Sin acceso

### PRODUCTOS

| Rol | ID | Leer | Crear/Editar | Eliminar |
|-----|----|----|-------------|----------|
| ADMIN | 1 | ✅ | ✅ | ✅ |
| DIRECTOR | 2 | ✅ | ✅ | ✅ |
| GERENTE | 3 | ✅ | ✅ | ✅ |
| FARMACEUTICO | 4 | ✅ | ✅ | ❌ |
| CAJERO | 5 | ✅ | ❌ | ❌ |
| ALMACEN | 6 | ✅ | ✅ | ❌ |
| ENCARGADO_VENTAS | 7 | ✅ | ❌ | ❌ |
| ENCARGADO_COMPRAS | 8 | ✅ | ✅ | ❌ |
| CONTADOR | 9 | ✅ | ❌ | ❌ |
| AUDITOR | 10 | ✅ | ❌ | ❌ |
| RRHH | 11 | ✅ | ❌ | ❌ |
| SUPERVISOR | 12 | ✅ | ❌ | ❌ |
| ENCARGADO_INVENTARIO | 13 | ✅ | ✅ | ❌ |
| RECEPCIONISTA | 14 | ✅ | ❌ | ❌ |
| SOPORTE_TECNICO | 15 | ✅ | ❌ | ❌ |
| ANALISTA_DATOS | 16 | ✅ | ❌ | ❌ |
| ENCARGADO_CALIDAD | 17 | ✅ | ❌ | ❌ |
| PRACTICANTE | 18 | ✅ | ❌ | ❌ |
| TEMPORAL | 19 | ✅ | ❌ | ❌ |
| INVITADO | 20 | ❌ | ❌ | ❌ |

### VENTAS

| Rol | ID | Leer | Crear | Cancelar | Eliminar |
|-----|----|----|-------|----------|----------|
| ADMIN | 1 | ✅ | ✅ | ✅ | ✅ |
| DIRECTOR | 2 | ✅ | ✅ | ✅ | ✅ |
| GERENTE | 3 | ✅ | ✅ | ✅ | ❌ |
| FARMACEUTICO | 4 | ✅ | ✅ | ❌ | ❌ |
| CAJERO | 5 | ✅ | ✅ | ❌ | ❌ |
| ENCARGADO_VENTAS | 7 | ✅ | ✅ | ✅ | ❌ |
| CONTADOR | 9 | ✅ | ❌ | ❌ | ❌ |
| AUDITOR | 10 | ✅ | ❌ | ❌ | ❌ |
| SUPERVISOR | 12 | ✅ | ✅ | ✅ | ❌ |
| ANALISTA_DATOS | 16 | ✅ | ❌ | ❌ | ❌ |
| PRACTICANTE | 18 | ✅ | ❌ | ❌ | ❌ |
| Otros | - | ❌ | ❌ | ❌ | ❌ |

### COMPRAS

| Rol | ID | Leer | Crear | Cancelar |
|-----|----|----|-------|----------|
| ADMIN | 1 | ✅ | ✅ | ✅ |
| DIRECTOR | 2 | ✅ | ✅ | ✅ |
| GERENTE | 3 | ✅ | ✅ | ✅ |
| ALMACEN | 6 | ✅ | ✅ | ❌ |
| ENCARGADO_COMPRAS | 8 | ✅ | ✅ | ✅ |
| CONTADOR | 9 | ✅ | ❌ | ❌ |
| AUDITOR | 10 | ✅ | ❌ | ❌ |
| SUPERVISOR | 12 | ✅ | ✅ | ✅ |
| ENCARGADO_INVENTARIO | 13 | ✅ | ✅ | ❌ |
| ANALISTA_DATOS | 16 | ✅ | ❌ | ❌ |
| Otros | - | ❌ | ❌ | ❌ |

### CLIENTES

| Rol | ID | Leer | Crear/Editar | Eliminar |
|-----|----|----|-------------|----------|
| ADMIN | 1 | ✅ | ✅ | ✅ |
| DIRECTOR | 2 | ✅ | ✅ | ✅ |
| GERENTE | 3 | ✅ | ✅ | ✅ |
| FARMACEUTICO | 4 | ✅ | ✅ | ❌ |
| CAJERO | 5 | ✅ | ✅ | ❌ |
| ENCARGADO_VENTAS | 7 | ✅ | ✅ | ❌ |
| CONTADOR | 9 | ✅ | ❌ | ❌ |
| AUDITOR | 10 | ✅ | ❌ | ❌ |
| RRHH | 11 | ✅ | ❌ | ✅ |
| SUPERVISOR | 12 | ✅ | ✅ | ❌ |
| RECEPCIONISTA | 14 | ✅ | ✅ | ❌ |
| ANALISTA_DATOS | 16 | ✅ | ❌ | ❌ |
| PRACTICANTE | 18 | ✅ | ❌ | ❌ |
| Otros | - | ❌ | ❌ | ❌ |

### PROVEEDORES

| Rol | ID | Leer | Crear/Editar | Eliminar |
|-----|----|----|-------------|----------|
| ADMIN | 1 | ✅ | ✅ | ✅ |
| DIRECTOR | 2 | ✅ | ✅ | ✅ |
| GERENTE | 3 | ✅ | ✅ | ✅ |
| ALMACEN | 6 | ✅ | ✅ | ❌ |
| ENCARGADO_COMPRAS | 8 | ✅ | ✅ | ❌ |
| CONTADOR | 9 | ✅ | ❌ | ❌ |
| AUDITOR | 10 | ✅ | ❌ | ❌ |
| SUPERVISOR | 12 | ✅ | ✅ | ❌ |
| ENCARGADO_INVENTARIO | 13 | ✅ | ✅ | ❌ |
| ANALISTA_DATOS | 16 | ✅ | ❌ | ❌ |
| ENCARGADO_CALIDAD | 17 | ✅ | ❌ | ❌ |
| Otros | - | ❌ | ❌ | ❌ |

### CATEGORÍAS

| Rol | ID | Leer | Crear/Editar | Eliminar |
|-----|----|----|-------------|----------|
| ADMIN | 1 | ✅ | ✅ | ✅ |
| DIRECTOR | 2 | ✅ | ✅ | ✅ |
| GERENTE | 3 | ✅ | ✅ | ✅ |
| FARMACEUTICO | 4 | ✅ | ✅ | ❌ |
| ALMACEN | 6 | ✅ | ✅ | ❌ |
| ENCARGADO_COMPRAS | 8 | ✅ | ✅ | ❌ |
| SUPERVISOR | 12 | ✅ | ✅ | ❌ |
| ENCARGADO_INVENTARIO | 13 | ✅ | ✅ | ❌ |
| ANALISTA_DATOS | 16 | ✅ | ❌ | ❌ |
| ENCARGADO_CALIDAD | 17 | ✅ | ❌ | ❌ |
| PRACTICANTE | 18 | ✅ | ❌ | ❌ |
| Otros | - | ❌ | ❌ | ❌ |

### USUARIOS (Gestión de Personal)

| Rol | ID | Ver | Crear | Editar | Eliminar |
|-----|----|----|-------|--------|----------|
| ADMIN | 1 | ✅ | ✅ | ✅ | ✅ |
| DIRECTOR | 2 | ✅ | ✅ | ✅ | ✅ |
| RRHH | 11 | ✅ | ✅ | ✅ | ✅ |
| Todos los demás | - | ❌ | ❌ | ❌ | ❌ |

### ROLES (Configuración del Sistema)

| Rol | ID | Ver | Crear | Editar | Eliminar |
|-----|----|----|-------|--------|----------|
| ADMIN | 1 | ✅ | ✅ | ✅ | ✅ |
| DIRECTOR | 2 | ✅ | ✅ | ✅ | ✅ |
| Todos los demás | - | ❌ | ❌ | ❌ | ❌ |

### REPORTES

| Tipo de Reporte | Roles con Acceso |
|-----------------|------------------|
| **Reportes de Ventas** | ADMIN, DIRECTOR, GERENTE, ENCARGADO_VENTAS, CONTADOR, SUPERVISOR, ANALISTA_DATOS |
| **Reportes de Compras** | ADMIN, DIRECTOR, GERENTE, ENCARGADO_COMPRAS, CONTADOR, SUPERVISOR, ANALISTA_DATOS |
| **Reportes de Inventario** | ADMIN, DIRECTOR, GERENTE, ALMACEN, CONTADOR, SUPERVISOR, ENCARGADO_INVENTARIO, ANALISTA_DATOS |
| **Reportes Financieros** | ADMIN, DIRECTOR, GERENTE, CONTADOR, AUDITOR, ANALISTA_DATOS |

---

## 🎯 Grupos de Roles por Función

### 👔 Dirección y Gerencia
```
ADMIN (1) ────────────► Acceso total sin restricciones
DIRECTOR (2) ─────────► Control estratégico completo
GERENTE (3) ──────────► Gestión operativa amplia
```

### 💊 Operaciones Farmacéuticas
```
FARMACEUTICO (4) ─────► Productos, ventas, clientes
CAJERO (5) ───────────► Ventas y cobros
ALMACEN (6) ──────────► Inventario y productos
```

### 📦 Cadena de Suministro
```
ENCARGADO_COMPRAS (8) ───► Gestión de compras y proveedores
ENCARGADO_INVENTARIO (13) ► Control de stock
ALMACEN (6) ──────────────► Recepción de mercancía
```

### 💰 Área Financiera
```
CONTADOR (9) ─────► Reportes financieros
AUDITOR (10) ─────► Supervisión y control
```

### 👥 Recursos Humanos
```
RRHH (11) ────────► Gestión de usuarios y personal
```

### 📊 Análisis y Calidad
```
ANALISTA_DATOS (16) ──────► Reportes y BI
ENCARGADO_CALIDAD (17) ───► Control de calidad
```

### 🔧 Soporte y Sistemas
```
SOPORTE_TECNICO (15) ─────► Sistemas y mantenimiento
```

### 👁️ Supervisión General
```
SUPERVISOR (12) ──────────► Supervisión transversal
ENCARGADO_VENTAS (7) ─────► Supervisión de ventas
```

### 🎓 Acceso Limitado
```
PRACTICANTE (18) ─────► Solo lectura y aprendizaje
TEMPORAL (19) ────────► Acceso temporal limitado
INVITADO (20) ────────► Solo lectura muy restringida
```

---

## 📈 Flujo de Escalamiento de Permisos

```
Nivel 5 (Mínimo)    Nivel 4          Nivel 3          Nivel 2         Nivel 1 (Máximo)
  INVITADO     →  PRACTICANTE   →   CAJERO      →   FARMACEUTICO  →    GERENTE
     (20)            (18)            (5)              (4)              (3)
      ↓               ↓               ↓                ↓                 ↓
  Solo vista    Vista+limitado  Ventas básicas  Gestión completa  Control total
                                                   de farmacia       operativo
                                                                         ↓
                                                                    DIRECTOR (2)
                                                                         ↓
                                                                    ADMIN (1)
```

---

## 🔍 Casos de Uso por Rol

### 🔴 ADMIN (1) - Dios del Sistema
```
✅ TODO sin restricciones
✅ Configurar sistema
✅ Gestionar roles
✅ Gestionar usuarios
✅ Acceso a todos los módulos
```

### 🟠 DIRECTOR (2) - Alta Dirección
```
✅ Casi todo (igual que ADMIN en operaciones)
✅ Gestionar roles
✅ Gestionar usuarios
✅ Eliminar registros críticos
✅ Ver reportes financieros completos
```

### 🟡 GERENTE (3) - Gestión Operativa
```
✅ Productos (crear, editar, eliminar)
✅ Ventas (crear, cancelar)
✅ Compras (crear, cancelar)
✅ Clientes (CRUD completo)
✅ Proveedores (CRUD completo)
✅ Reportes operativos
❌ NO gestionar usuarios/roles
```

### 🟢 FARMACEUTICO (4) - Operación Principal
```
✅ Productos (crear, editar)
✅ Ventas (crear)
✅ Clientes (crear, editar)
✅ Categorías (crear, editar)
❌ NO eliminar nada
❌ NO compras
❌ NO reportes financieros
```

### 🔵 CAJERO (5) - Punto de Venta
```
✅ Ventas (crear)
✅ Clientes (crear, editar)
✅ Ver productos
❌ NO crear/editar productos
❌ NO acceso a compras
❌ NO reportes
```

### 🟣 ALMACEN (6) - Control de Stock
```
✅ Productos (crear, editar)
✅ Compras (crear)
✅ Proveedores (ver, editar)
✅ Inventario
❌ NO ventas directas
❌ NO reportes financieros
```

### ⚪ INVITADO (20) - Acceso Mínimo
```
❌ Solo lectura muy limitada
❌ NO operaciones
❌ NO crear/editar/eliminar nada
```

---

## 💡 Recomendaciones de Asignación

### Farmacia Pequeña (1-5 empleados)
```
1 ADMIN/DUEÑO
1-2 FARMACEUTICO
1-2 CAJERO
```

### Farmacia Mediana (6-15 empleados)
```
1 ADMIN
1 GERENTE
2-3 FARMACEUTICO
2-3 CAJERO
1 ALMACEN
1 CONTADOR
```

### Cadena Grande (15+ empleados)
```
1 ADMIN
1 DIRECTOR
2-3 GERENTE
5-8 FARMACEUTICO
5-10 CAJERO
2-3 ALMACEN
1 ENCARGADO_COMPRAS
1 ENCARGADO_VENTAS
1 ENCARGADO_INVENTARIO
1-2 CONTADOR
1 RRHH
1 SUPERVISOR
1 ANALISTA_DATOS
```

---

## 🎨 Código de Colores

```
🔴 Nivel Ejecutivo    (ADMIN, DIRECTOR)
🟠 Nivel Gerencial    (GERENTE)
🟡 Nivel Operativo    (FARMACEUTICO, CAJERO, ALMACEN)
🟢 Nivel Supervisión  (ENCARGADOS, SUPERVISOR)
🔵 Nivel Especializado (CONTADOR, RRHH, ANALISTA)
🟣 Nivel Soporte      (SOPORTE_TECNICO)
⚪ Nivel Limitado     (PRACTICANTE, TEMPORAL, INVITADO)
```

---

**Última actualización:** 5 de Noviembre, 2024  
**Versión del Sistema:** 1.0.0
