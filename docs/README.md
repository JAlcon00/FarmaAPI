# 🏥 FarmaControl API

## Descripción
API REST minimalista para sistema de control de farmacia desarrollada con **Java + Jakarta EE 10 + Tomcat 10**. 

Arquitectura siguiendo el patrón TypeScript/Express:
```
config/ → model/ → services/ → controller/ → routes/ → main
```

## 🚀 Tecnologías

- **Java 17+** con Jakarta EE 10
- **Tomcat 10+** como contenedor de servlets
- **MySQL 8.0** en Google Cloud Platform
- **Ant** como sistema de construcción
- **Jakarta Servlet API 6.0.0**
- **MySQL Connector/J 8.0.33**

## 📁 Estructura del Proyecto

```
FarmaApi/
├── src/java/
│   ├── config/           # Configuración DB y variables entorno
│   │   ├── DatabaseConfig.java
│   │   ├── EnvConfig.java
│   │   └── TestConnection.java
│   ├── model/            # POJOs de entidades
│   │   ├── Role.java
│   │   ├── Usuario.java
│   │   ├── Categoria.java
│   │   ├── Producto.java
│   │   ├── Proveedor.java
│   │   ├── Cliente.java
│   │   ├── Compra.java
│   │   ├── DetalleCompra.java
│   │   ├── Venta.java
│   │   └── DetalleVenta.java
│   ├── services/         # DAOs con operaciones CRUD
│   │   ├── RoleService.java
│   │   ├── CategoriaService.java
│   │   ├── ProductoService.java
│   │   ├── ProveedorService.java
│   │   ├── ClienteService.java
│   │   ├── CompraService.java
│   │   └── VentaService.java
│   ├── controller/       # Lógica de negocio y validaciones
│   │   ├── RoleController.java
│   │   ├── CategoriaController.java
│   │   ├── ProductoController.java
│   │   ├── ProveedorController.java
│   │   ├── ClienteController.java
│   │   ├── CompraController.java
│   │   └── VentaController.java
│   ├── routes/          # Servlets REST endpoints
│   │   ├── RoleServlet.java
│   │   ├── CategoriaServlet.java
│   │   ├── ProductoServlet.java
│   │   ├── ClienteServlet.java
│   │   └── VentaServlet.java
│   ├── utils/           # Utilidades
│   │   └── JsonResponse.java
│   └── filter/          # Filtros HTTP
│       └── CORSFilter.java
├── web/
│   ├── index.html       # Página de pruebas
│   └── WEB-INF/
│       └── web.xml      # Configuración servlets
├── lib/                 # Dependencias JAR
│   ├── mysql-connector-j-8.0.33.jar
│   └── jakarta.servlet-api-6.0.0.jar
└── build/classes/       # Clases compiladas
```

## 🔗 Endpoints de la API

### 🔐 Roles
```
GET    /api/roles           # Obtener todos los roles
GET    /api/roles/{id}      # Obtener role por ID
POST   /api/roles           # Crear nuevo role
PUT    /api/roles/{id}      # Actualizar role
DELETE /api/roles/{id}      # Eliminar role
```

### 📂 Categorías
```
GET    /api/categorias         # Obtener todas las categorías
GET    /api/categorias/{id}    # Obtener categoría por ID
POST   /api/categorias         # Crear nueva categoría
PUT    /api/categorias/{id}    # Actualizar categoría
DELETE /api/categorias/{id}    # Eliminar categoría
```

### 📦 Productos
```
GET    /api/productos                      # Obtener todos los productos
GET    /api/productos/{id}                 # Obtener producto por ID
GET    /api/productos?categoria={id}       # Productos por categoría
GET    /api/productos?stock=bajo           # Productos con stock bajo
POST   /api/productos                      # Crear nuevo producto
PUT    /api/productos/{id}                 # Actualizar producto
PUT    /api/productos/{id}/stock           # Actualizar solo stock
DELETE /api/productos/{id}                 # Eliminar producto
```

### 👥 Clientes
```
GET    /api/clientes                   # Obtener todos los clientes
GET    /api/clientes/{id}              # Obtener cliente por ID
GET    /api/clientes?search={texto}    # Buscar clientes
POST   /api/clientes                   # Crear nuevo cliente
PUT    /api/clientes/{id}              # Actualizar cliente
DELETE /api/clientes/{id}              # Eliminar cliente
```

### 💰 Ventas
```
GET    /api/ventas                                      # Obtener todas las ventas
GET    /api/ventas/{id}                                 # Obtener venta por ID
GET    /api/ventas/{id}/detalles                        # Obtener detalles de venta
GET    /api/ventas?fechaInicio={yyyy-MM-dd}&fechaFin={yyyy-MM-dd}  # Ventas por rango
POST   /api/ventas                                      # Crear nueva venta
PUT    /api/ventas/{id}/cancelar                        # Cancelar venta
```

## 🛠️ Comandos de Construcción

### Compilación Completa
```bash
cd /Users/yisus/NetBeansProjects/FarmaApi

# Compilar todas las clases
javac -cp "lib/mysql-connector-j-8.0.33.jar:lib/jakarta.servlet-api-6.0.0.jar:src/java" \
      src/java/**/*.java -d build/classes/
```

### Compilación por Capas
```bash
# Config
javac -cp "lib/mysql-connector-j-8.0.33.jar:src/java" src/java/config/*.java -d build/classes/

# Models
javac -cp "src/java" src/java/model/*.java -d build/classes/

# Services
javac -cp "lib/mysql-connector-j-8.0.33.jar:src/java" src/java/services/*.java -d build/classes/

# Controllers
javac -cp "lib/mysql-connector-j-8.0.33.jar:src/java" src/java/controller/*.java -d build/classes/

# Utils
javac -cp "lib/jakarta.servlet-api-6.0.0.jar:src/java" src/java/utils/*.java -d build/classes/

# Routes (Servlets)
javac -cp "lib/mysql-connector-j-8.0.33.jar:lib/jakarta.servlet-api-6.0.0.jar:src/java" \
      src/java/routes/*.java -d build/classes/

# Filters
javac -cp "lib/jakarta.servlet-api-6.0.0.jar:src/java" src/java/filter/*.java -d build/classes/
```

## 🗄️ Base de Datos

**Servidor:** Google Cloud MySQL  
**Host:** 35.225.68.51:3306  
**Base de datos:** farmacontrol  
**Configuración:** Variables de entorno en archivo `.env`

### Variables de Entorno (.env)
```env
DB_HOST=35.225.68.51
DB_PORT=3306
DB_NAME=farmacontrol
DB_USER=tu_usuario
DB_PASSWORD=tu_password
```

## 📊 Datos de Prueba

El sistema incluye datos de prueba pre-cargados:
- **20 roles** definidos
- **20 productos** con diferentes categorías
- **5 clientes** registrados
- **20 proveedores** disponibles
- **Categorías** organizadas

## 🚦 Validaciones Implementadas

### Productos
- Validación de stock mínimo
- Control de stock insuficiente en ventas
- Precio válido (> 0)
- Categoría obligatoria

### Ventas
- Validación de stock disponible
- Cálculo automático de impuestos (16% IVA)
- Descuentos válidos
- Cliente requerido

### Clientes
- Email único y válido (regex)
- Nombre obligatorio
- Búsqueda por nombre/email

## 🔧 Funcionalidades Especiales

### JsonResponse Personalizado
Respuestas REST estandarizadas sin librerías externas:
```java
JsonResponse.success(response, data);
JsonResponse.created(response, newObject);
JsonResponse.badRequest(response, "Error message");
JsonResponse.notFound(response, "Not found");
JsonResponse.internalError(response, "Server error");
```

### CORS Configurado
Filtro CORS automático para permitir peticiones desde cualquier origen.

### Transacciones
Operaciones complejas (ventas con detalles) manejadas en transacciones.

## 🧪 Testing

La página principal (`index.html`) incluye botones de prueba para todos los endpoints principales.

**URL de prueba:** `http://localhost:8080/FarmaApi/`

## 📝 Logs y Debugging

Todos los controladores incluyen validaciones detalladas y mensajes de error específicos para facilitar el debugging.

## 🔮 Próximos Pasos

1. **Autenticación JWT** - Sistema de login y roles
2. **Swagger/OpenAPI** - Documentación automática
3. **Reportes** - Módulo de reportes y estadísticas
4. **Proveedores** - Servlet para gestión de proveedores
5. **Compras** - Servlet para gestión de compras
6. **Auditoria** - Log de todas las operaciones

---

