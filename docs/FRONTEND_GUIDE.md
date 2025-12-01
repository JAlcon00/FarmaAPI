# 🚀 FarmaControl API - Guía Rápida para Frontend

## ⚡ Primeros Pasos

### 🔗 URL Base
```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

### 🌐 Configuración CORS
**✅ CORS ya habilitado** - No necesitas configuración adicional.

---

## 📝 Headers Estándar

```javascript
const headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json'
};
```

---

## 🔐 Autenticación

### Login de Usuario
```javascript
const login = async (email, password) => {
  const response = await fetch(`${API_BASE_URL}/usuarios/auth`, {
    method: 'POST',
    headers,
    body: JSON.stringify({ email, password })
  });
  
  const data = await response.json();
  
  if (data.success) {
    // Guardar usuario en localStorage o state management
    localStorage.setItem('user', JSON.stringify(data.data));
    return data.data;
  } else {
    throw new Error(data.error);
  }
};

// Ejemplo de uso
try {
  const user = await login('admin@farmacontrol.com', 'admin123');
  console.log('Usuario logueado:', user);
} catch (error) {
  console.error('Error de login:', error.message);
}
```

---

## 📋 Operaciones CRUD Básicas

### 1. Obtener Lista de Elementos
```javascript
const getItems = async (endpoint) => {
  const response = await fetch(`${API_BASE_URL}/${endpoint}`, {
    method: 'GET',
    headers
  });
  return response.json();
};

// Ejemplos
const productos = await getItems('productos');
const clientes = await getItems('clientes');
const ventas = await getItems('ventas');
```

### 2. Obtener Elemento por ID
```javascript
const getItemById = async (endpoint, id) => {
  const response = await fetch(`${API_BASE_URL}/${endpoint}/${id}`, {
    method: 'GET',
    headers
  });
  return response.json();
};

// Ejemplo
const producto = await getItemById('productos', 1);
```

### 3. Crear Nuevo Elemento
```javascript
const createItem = async (endpoint, data) => {
  const response = await fetch(`${API_BASE_URL}/${endpoint}`, {
    method: 'POST',
    headers,
    body: JSON.stringify(data)
  });
  return response.json();
};

// Ejemplo: Crear cliente
const nuevoCliente = await createItem('clientes', {
  nombre: 'Carlos',
  apellido: 'Ruiz',
  cedula: '1234567890',
  telefono: '555-0123',
  email: 'carlos@email.com',
  direccion: 'Calle 123'
});
```

### 4. Actualizar Elemento
```javascript
const updateItem = async (endpoint, id, data) => {
  const response = await fetch(`${API_BASE_URL}/${endpoint}/${id}`, {
    method: 'PUT',
    headers,
    body: JSON.stringify(data)
  });
  return response.json();
};
```

### 5. Eliminar Elemento
```javascript
const deleteItem = async (endpoint, id) => {
  const response = await fetch(`${API_BASE_URL}/${endpoint}/${id}`, {
    method: 'DELETE',
    headers
  });
  return response.json();
};
```

---

## 🛒 Operaciones Específicas

### Dashboard Principal
```javascript
const getDashboard = async () => {
  const response = await fetch(`${API_BASE_URL}/reportes`, {
    method: 'GET',
    headers
  });
  return response.json();
};

// Respuesta esperada:
// {
//   "estadisticas": {
//     "totalVentas": 150,
//     "totalProductos": 95,
//     "montoTotalVentas": 45750.50,
//     "productosStockBajo": 5
//   }
// }
```

### Crear Venta Completa
```javascript
const crearVenta = async (clienteId, usuarioId, productos) => {
  const ventaData = {
    cliente_id: clienteId,
    usuario_id: usuarioId,
    productos: productos.map(p => ({
      producto_id: p.id,
      cantidad: p.cantidad
    }))
  };
  
  const response = await fetch(`${API_BASE_URL}/ventas`, {
    method: 'POST',
    headers,
    body: JSON.stringify(ventaData)
  });
  
  return response.json();
};

// Ejemplo de uso
const nuevaVenta = await crearVenta(1, 3, [
  { id: 1, cantidad: 2 },
  { id: 5, cantidad: 1 }
]);
```

### Buscar Productos por Categoría
```javascript
const getProductosByCategoria = async (categoriaId) => {
  const response = await fetch(`${API_BASE_URL}/productos?categoria_id=${categoriaId}`, {
    method: 'GET',
    headers
  });
  return response.json();
};
```

### Consultar Stock Bajo
```javascript
const getStockBajo = async (limite = 10) => {
  const response = await fetch(`${API_BASE_URL}/reportes/inventario/bajo?limite=${limite}`, {
    method: 'GET',
    headers
  });
  return response.json();
};
```

---

## 📊 Componentes React de Ejemplo

### Hook para API
```javascript
import { useState, useEffect } from 'react';

const useApi = (endpoint) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const response = await fetch(`${API_BASE_URL}/${endpoint}`, {
          headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
          }
        });
        const result = await response.json();
        setData(result);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [endpoint]);

  return { data, loading, error };
};

// Uso del hook
const ProductosComponent = () => {
  const { data: productos, loading, error } = useApi('productos');

  if (loading) return <div>Cargando productos...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div>
      {productos.map(producto => (
        <div key={producto.id}>
          <h3>{producto.nombre}</h3>
          <p>Precio: ${producto.precio}</p>
          <p>Stock: {producto.stock}</p>
        </div>
      ))}
    </div>
  );
};
```

### Componente de Login
```javascript
import React, { useState } from 'react';

const LoginComponent = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await fetch(`${API_BASE_URL}/usuarios/auth`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password })
      });

      const data = await response.json();

      if (data.success) {
        localStorage.setItem('user', JSON.stringify(data.data));
        // Redirigir al dashboard
        window.location.href = '/dashboard';
      } else {
        alert(data.error);
      }
    } catch (error) {
      alert('Error de conexión');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleLogin}>
      <input
        type="email"
        placeholder="Email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        required
      />
      <input
        type="password"
        placeholder="Contraseña"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        required
      />
      <button type="submit" disabled={loading}>
        {loading ? 'Ingresando...' : 'Ingresar'}
      </button>
    </form>
  );
};
```

---

## 🔍 Manejo de Errores

### Función de Manejo Global
```javascript
const handleApiResponse = async (response) => {
  const data = await response.json();
  
  if (!response.ok) {
    throw new Error(data.error || 'Error en la respuesta del servidor');
  }
  
  if (data.success === false) {
    throw new Error(data.error || 'Operación fallida');
  }
  
  return data;
};

// Uso
try {
  const response = await fetch(`${API_BASE_URL}/productos`);
  const data = await handleApiResponse(response);
  console.log('Productos:', data);
} catch (error) {
  console.error('Error:', error.message);
}
```

---

## 🎨 Estados de Respuesta

### Códigos HTTP Esperados
```javascript
const HTTP_STATUS = {
  OK: 200,           // Operación exitosa
  CREATED: 201,      // Recurso creado
  BAD_REQUEST: 400,  // Datos inválidos
  UNAUTHORIZED: 401, // No autenticado
  NOT_FOUND: 404,    // Recurso no encontrado
  SERVER_ERROR: 500  // Error interno
};
```

### Validación de Campos
```javascript
const validateProducto = (producto) => {
  const errors = [];
  
  if (!producto.nombre || producto.nombre.trim() === '') {
    errors.push('El nombre es requerido');
  }
  
  if (!producto.precio || producto.precio <= 0) {
    errors.push('El precio debe ser mayor a 0');
  }
  
  if (!producto.stock || producto.stock < 0) {
    errors.push('El stock no puede ser negativo');
  }
  
  return errors;
};
```

---

## 📱 Configuración para Diferentes Frameworks

### Vue.js
```javascript
// En main.js o plugin
app.config.globalProperties.$api = {
  baseURL: 'http://localhost:8080/api',
  
  async get(endpoint) {
    const response = await fetch(`${this.baseURL}/${endpoint}`);
    return response.json();
  },
  
  async post(endpoint, data) {
    const response = await fetch(`${this.baseURL}/${endpoint}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
    return response.json();
  }
};
```

### Angular Service
```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FarmaControlService {
  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getProductos(): Observable<any> {
    return this.http.get(`${this.baseUrl}/productos`);
  }

  createCliente(cliente: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/clientes`, cliente);
  }

  getDashboard(): Observable<any> {
    return this.http.get(`${this.baseUrl}/reportes`);
  }
}
```

---

## 🧪 Testing de Endpoints

### Pruebas con cURL (Para desarrollo)
```bash
# Test rápido de conectividad
curl -X GET "http://localhost:8080/api/productos" \
  -H "Content-Type: application/json"

# Test de login
curl -X POST "http://localhost:8080/api/usuarios/auth" \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@farmacontrol.com", "password": "admin123"}'

# Test de dashboard
curl -X GET "http://localhost:8080/api/reportes"
```

### Datos de Prueba Disponibles
```javascript
// Usuarios de prueba
const testUsers = [
  { email: 'admin@farmacontrol.com', password: 'admin123' },
  { email: 'juan@farmacontrol.com', password: 'juan123' },
  { email: 'maria@farmacontrol.com', password: 'maria123' }
];

// Base de datos contiene:
// - 20 usuarios con roles
// - 20 proveedores
// - 50+ productos en varias categorías
// - Clientes y transacciones de ejemplo
```

---

## 📞 Contacto y Soporte

- **API Docs Completas:** `/docs/API_ENDPOINTS_COMPLETA.md`
- **Estado del servidor:** `http://localhost:8080` (debe estar corriendo)
- **Base de datos:** MySQL Cloud (ya configurada)

---

**🚀 FarmaControl API** - Lista para integración frontend  
*Guía rápida v1.0 - Octubre 2025*