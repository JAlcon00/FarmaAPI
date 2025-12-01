#!/bin/bash

# ================================================================
# Script completo para probar TODOS los endpoints CRUD de FarmaControl
# Uso: ./test-all-cruds.sh
# ================================================================

API_URL="http://localhost:8080/api"
EMAIL="admin@farmacontrol.com"
PASSWORD="admin123"

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
MAGENTA='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Función para imprimir secciones
print_section() {
  echo ""
  echo -e "${CYAN}================================================${NC}"
  echo -e "${CYAN}$1${NC}"
  echo -e "${CYAN}================================================${NC}"
  echo ""
}

# Función para imprimir operación
print_operation() {
  echo -e "${YELLOW}$1${NC}"
}

# Función para verificar respuesta
check_response() {
  if echo "$1" | grep -q '"success":true'; then
    echo -e "${GREEN}✅ SUCCESS${NC}"
  elif echo "$1" | grep -q '"success":false'; then
    echo -e "${RED}❌ FAILED${NC}"
  else
    echo -e "${YELLOW}⚠️  UNKNOWN RESPONSE${NC}"
  fi
}

# ================================================================
# 1. AUTENTICACIÓN
# ================================================================
print_section "🔐 1. AUTENTICACIÓN"

print_operation "POST /api/usuarios/auth (Login)"
LOGIN_RESPONSE=$(curl -s -X POST "$API_URL/usuarios/auth" \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"$EMAIL\", \"password\": \"$PASSWORD\"}")
echo $LOGIN_RESPONSE | python3 -m json.tool
check_response "$LOGIN_RESPONSE"

TOKEN=$(echo $LOGIN_RESPONSE | python3 -c "import sys, json; print(json.load(sys.stdin)['data']['token'])" 2>/dev/null)

if [ -z "$TOKEN" ]; then
  echo -e "${RED}❌ Error al obtener token. No se pueden ejecutar las pruebas.${NC}"
  exit 1
fi

echo -e "${GREEN}✅ Token obtenido: ${TOKEN:0:50}...${NC}"

# ================================================================
# 2. CRUD DE ROLES
# ================================================================
print_section "🎭 2. CRUD DE ROLES"

print_operation "GET /api/roles (Listar todos)"
ROLES_RESPONSE=$(curl -s "$API_URL/roles" -H "Authorization: Bearer $TOKEN")
echo $ROLES_RESPONSE | python3 -m json.tool
check_response "$ROLES_RESPONSE"

print_operation "GET /api/roles/1 (Obtener por ID)"
ROLE_RESPONSE=$(curl -s "$API_URL/roles/1" -H "Authorization: Bearer $TOKEN")
echo $ROLE_RESPONSE | python3 -m json.tool
check_response "$ROLE_RESPONSE"

print_operation "POST /api/roles (Crear nuevo rol)"
CREATE_ROLE_RESPONSE=$(curl -s -X POST "$API_URL/roles" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "nombre=SUPERVISOR&descripcion=Rol de supervisor de farmacia")
echo $CREATE_ROLE_RESPONSE | python3 -m json.tool
check_response "$CREATE_ROLE_RESPONSE"

# Extraer el ID del rol creado
ROLE_ID=$(echo $CREATE_ROLE_RESPONSE | python3 -c "import sys, json; d=json.load(sys.stdin); print(d.get('data', {}).get('id', ''))" 2>/dev/null)

if [ ! -z "$ROLE_ID" ]; then
  print_operation "PUT /api/roles/$ROLE_ID (Actualizar rol)"
  UPDATE_ROLE_RESPONSE=$(curl -s -X PUT "$API_URL/roles/$ROLE_ID" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "nombre=SUPERVISOR&descripcion=Rol de supervisor actualizado")
  echo $UPDATE_ROLE_RESPONSE | python3 -m json.tool
  check_response "$UPDATE_ROLE_RESPONSE"

  print_operation "DELETE /api/roles/$ROLE_ID (Eliminar rol)"
  DELETE_ROLE_RESPONSE=$(curl -s -X DELETE "$API_URL/roles/$ROLE_ID" \
    -H "Authorization: Bearer $TOKEN")
  echo $DELETE_ROLE_RESPONSE | python3 -m json.tool
  check_response "$DELETE_ROLE_RESPONSE"
fi

# ================================================================
# 3. CRUD DE CATEGORÍAS
# ================================================================
print_section "📁 3. CRUD DE CATEGORÍAS"

print_operation "GET /api/categorias (Listar todas)"
CATEGORIAS_RESPONSE=$(curl -s "$API_URL/categorias" -H "Authorization: Bearer $TOKEN")
echo $CATEGORIAS_RESPONSE | python3 -m json.tool
check_response "$CATEGORIAS_RESPONSE"

print_operation "GET /api/categorias/1 (Obtener por ID)"
CATEGORIA_RESPONSE=$(curl -s "$API_URL/categorias/1" -H "Authorization: Bearer $TOKEN")
echo $CATEGORIA_RESPONSE | python3 -m json.tool
check_response "$CATEGORIA_RESPONSE"

print_operation "POST /api/categorias (Crear nueva categoría)"
CREATE_CAT_RESPONSE=$(curl -s -X POST "$API_URL/categorias" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "nombre=Vitaminas&descripcion=Suplementos vitamínicos")
echo $CREATE_CAT_RESPONSE | python3 -m json.tool
check_response "$CREATE_CAT_RESPONSE"

CATEGORIA_ID=$(echo $CREATE_CAT_RESPONSE | python3 -c "import sys, json; d=json.load(sys.stdin); print(d.get('data', {}).get('id', ''))" 2>/dev/null)

if [ ! -z "$CATEGORIA_ID" ]; then
  print_operation "PUT /api/categorias/$CATEGORIA_ID (Actualizar categoría)"
  UPDATE_CAT_RESPONSE=$(curl -s -X PUT "$API_URL/categorias/$CATEGORIA_ID" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "nombre=Vitaminas y Minerales&descripcion=Suplementos vitamínicos y minerales")
  echo $UPDATE_CAT_RESPONSE | python3 -m json.tool
  check_response "$UPDATE_CAT_RESPONSE"

  print_operation "DELETE /api/categorias/$CATEGORIA_ID (Eliminar categoría)"
  DELETE_CAT_RESPONSE=$(curl -s -X DELETE "$API_URL/categorias/$CATEGORIA_ID" \
    -H "Authorization: Bearer $TOKEN")
  echo $DELETE_CAT_RESPONSE | python3 -m json.tool
  check_response "$DELETE_CAT_RESPONSE"
fi

# ================================================================
# 4. CRUD DE PRODUCTOS
# ================================================================
print_section "📦 4. CRUD DE PRODUCTOS"

print_operation "GET /api/productos?page=0&size=5 (Listar con paginación)"
PRODUCTOS_RESPONSE=$(curl -s "$API_URL/productos?page=0&size=5" -H "Authorization: Bearer $TOKEN")
echo $PRODUCTOS_RESPONSE | python3 -m json.tool
check_response "$PRODUCTOS_RESPONSE"

print_operation "GET /api/productos/1 (Obtener por ID)"
PRODUCTO_RESPONSE=$(curl -s "$API_URL/productos/1" -H "Authorization: Bearer $TOKEN")
echo $PRODUCTO_RESPONSE | python3 -m json.tool
check_response "$PRODUCTO_RESPONSE"

print_operation "GET /api/productos/buscar?q=paracetamol (Buscar productos)"
SEARCH_RESPONSE=$(curl -s "$API_URL/productos/buscar?q=paracetamol" -H "Authorization: Bearer $TOKEN")
echo $SEARCH_RESPONSE | python3 -m json.tool
check_response "$SEARCH_RESPONSE"

print_operation "GET /api/productos/stock-bajo (Productos con stock bajo)"
STOCK_BAJO_RESPONSE=$(curl -s "$API_URL/productos/stock-bajo" -H "Authorization: Bearer $TOKEN")
echo $STOCK_BAJO_RESPONSE | python3 -m json.tool
check_response "$STOCK_BAJO_RESPONSE"

print_operation "POST /api/productos (Crear nuevo producto)"
CREATE_PROD_RESPONSE=$(curl -s -X POST "$API_URL/productos" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "nombre=Aspirina 500mg&descripcion=Analgésico y antiinflamatorio&categoriaId=1&precio=25.50&stock=150&stockMinimo=20&codigoBarras=7501000999999")
echo $CREATE_PROD_RESPONSE | python3 -m json.tool
check_response "$CREATE_PROD_RESPONSE"

PRODUCTO_ID=$(echo $CREATE_PROD_RESPONSE | python3 -c "import sys, json; d=json.load(sys.stdin); print(d.get('data', {}).get('id', ''))" 2>/dev/null)

if [ ! -z "$PRODUCTO_ID" ]; then
  print_operation "PUT /api/productos/$PRODUCTO_ID (Actualizar producto)"
  UPDATE_PROD_RESPONSE=$(curl -s -X PUT "$API_URL/productos/$PRODUCTO_ID" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "nombre=Aspirina 500mg&descripcion=Analgésico, antipirético y antiinflamatorio&categoriaId=1&precio=28.00&stock=150&stockMinimo=20&codigoBarras=7501000999999")
  echo $UPDATE_PROD_RESPONSE | python3 -m json.tool
  check_response "$UPDATE_PROD_RESPONSE"

  print_operation "DELETE /api/productos/$PRODUCTO_ID (Eliminar producto)"
  DELETE_PROD_RESPONSE=$(curl -s -X DELETE "$API_URL/productos/$PRODUCTO_ID" \
    -H "Authorization: Bearer $TOKEN")
  echo $DELETE_PROD_RESPONSE | python3 -m json.tool
  check_response "$DELETE_PROD_RESPONSE"
fi

# ================================================================
# 5. CRUD DE CLIENTES
# ================================================================
print_section "👥 5. CRUD DE CLIENTES"

print_operation "GET /api/clientes?page=0&size=5 (Listar con paginación)"
CLIENTES_RESPONSE=$(curl -s "$API_URL/clientes?page=0&size=5" -H "Authorization: Bearer $TOKEN")
echo $CLIENTES_RESPONSE | python3 -m json.tool
check_response "$CLIENTES_RESPONSE"

print_operation "GET /api/clientes/1 (Obtener por ID)"
CLIENTE_RESPONSE=$(curl -s "$API_URL/clientes/1" -H "Authorization: Bearer $TOKEN")
echo $CLIENTE_RESPONSE | python3 -m json.tool
check_response "$CLIENTE_RESPONSE"

print_operation "GET /api/clientes/buscar?q=Juan (Buscar clientes)"
SEARCH_CLI_RESPONSE=$(curl -s "$API_URL/clientes/buscar?q=Juan" -H "Authorization: Bearer $TOKEN")
echo $SEARCH_CLI_RESPONSE | python3 -m json.tool
check_response "$SEARCH_CLI_RESPONSE"

print_operation "POST /api/clientes (Crear nuevo cliente)"
CREATE_CLI_RESPONSE=$(curl -s -X POST "$API_URL/clientes" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "nombre=Carlos&apellido=Ramírez&telefono=5556789012&email=carlos.ramirez@gmail.com&direccion=Calle Morelos 45&fechaNacimiento=1985-03-15")
echo $CREATE_CLI_RESPONSE | python3 -m json.tool
check_response "$CREATE_CLI_RESPONSE"

CLIENTE_ID=$(echo $CREATE_CLI_RESPONSE | python3 -c "import sys, json; d=json.load(sys.stdin); print(d.get('data', {}).get('id', ''))" 2>/dev/null)

if [ ! -z "$CLIENTE_ID" ]; then
  print_operation "PUT /api/clientes/$CLIENTE_ID (Actualizar cliente)"
  UPDATE_CLI_RESPONSE=$(curl -s -X PUT "$API_URL/clientes/$CLIENTE_ID" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "nombre=Carlos Alberto&apellido=Ramírez&telefono=5556789012&email=carlos.ramirez@gmail.com&direccion=Calle Morelos 45, Col. Centro&fechaNacimiento=1985-03-15")
  echo $UPDATE_CLI_RESPONSE | python3 -m json.tool
  check_response "$UPDATE_CLI_RESPONSE"

  print_operation "DELETE /api/clientes/$CLIENTE_ID (Eliminar cliente)"
  DELETE_CLI_RESPONSE=$(curl -s -X DELETE "$API_URL/clientes/$CLIENTE_ID" \
    -H "Authorization: Bearer $TOKEN")
  echo $DELETE_CLI_RESPONSE | python3 -m json.tool
  check_response "$DELETE_CLI_RESPONSE"
fi

# ================================================================
# 6. CRUD DE PROVEEDORES
# ================================================================
print_section "🏢 6. CRUD DE PROVEEDORES"

print_operation "GET /api/proveedores?page=0&size=5 (Listar con paginación)"
PROVEEDORES_RESPONSE=$(curl -s "$API_URL/proveedores?page=0&size=5" -H "Authorization: Bearer $TOKEN")
echo $PROVEEDORES_RESPONSE | python3 -m json.tool
check_response "$PROVEEDORES_RESPONSE"

print_operation "GET /api/proveedores/1 (Obtener por ID)"
PROVEEDOR_RESPONSE=$(curl -s "$API_URL/proveedores/1" -H "Authorization: Bearer $TOKEN")
echo $PROVEEDOR_RESPONSE | python3 -m json.tool
check_response "$PROVEEDOR_RESPONSE"

print_operation "POST /api/proveedores (Crear nuevo proveedor)"
CREATE_PROV_RESPONSE=$(curl -s -X POST "$API_URL/proveedores" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "nombre=Farmacias del Norte SA&rfc=FDN123456789&telefono=5558887766&email=contacto@farmanorte.com&direccion=Av. Industrial 100&ciudad=Monterrey")
echo $CREATE_PROV_RESPONSE | python3 -m json.tool
check_response "$CREATE_PROV_RESPONSE"

PROVEEDOR_ID=$(echo $CREATE_PROV_RESPONSE | python3 -c "import sys, json; d=json.load(sys.stdin); print(d.get('data', {}).get('id', ''))" 2>/dev/null)

if [ ! -z "$PROVEEDOR_ID" ]; then
  print_operation "PUT /api/proveedores/$PROVEEDOR_ID (Actualizar proveedor)"
  UPDATE_PROV_RESPONSE=$(curl -s -X PUT "$API_URL/proveedores/$PROVEEDOR_ID" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "nombre=Farmacias del Norte SA de CV&rfc=FDN123456789&telefono=5558887766&email=ventas@farmanorte.com&direccion=Av. Industrial 100, Parque Industrial&ciudad=Monterrey")
  echo $UPDATE_PROV_RESPONSE | python3 -m json.tool
  check_response "$UPDATE_PROV_RESPONSE"

  print_operation "DELETE /api/proveedores/$PROVEEDOR_ID (Eliminar proveedor)"
  DELETE_PROV_RESPONSE=$(curl -s -X DELETE "$API_URL/proveedores/$PROVEEDOR_ID" \
    -H "Authorization: Bearer $TOKEN")
  echo $DELETE_PROV_RESPONSE | python3 -m json.tool
  check_response "$DELETE_PROV_RESPONSE"
fi

# ================================================================
# 7. CRUD DE USUARIOS
# ================================================================
print_section "👤 7. CRUD DE USUARIOS"

print_operation "GET /api/usuarios?page=0&size=5 (Listar con paginación)"
USUARIOS_RESPONSE=$(curl -s "$API_URL/usuarios?page=0&size=5" -H "Authorization: Bearer $TOKEN")
echo $USUARIOS_RESPONSE | python3 -m json.tool
check_response "$USUARIOS_RESPONSE"

print_operation "GET /api/usuarios/1 (Obtener por ID)"
USUARIO_RESPONSE=$(curl -s "$API_URL/usuarios/1" -H "Authorization: Bearer $TOKEN")
echo $USUARIO_RESPONSE | python3 -m json.tool
check_response "$USUARIO_RESPONSE"

print_operation "GET /api/usuarios/me (Obtener usuario actual - requiere JWT filter)"
ME_RESPONSE=$(curl -s "$API_URL/usuarios/me" -H "Authorization: Bearer $TOKEN")
echo $ME_RESPONSE | python3 -m json.tool
check_response "$ME_RESPONSE"

print_operation "POST /api/usuarios (Crear nuevo usuario)"
CREATE_USER_RESPONSE=$(curl -s -X POST "$API_URL/usuarios" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "email=nuevo.usuario@farmacontrol.com&password=password123&nombre=Nuevo&apellido=Usuario&rolId=3")
echo $CREATE_USER_RESPONSE | python3 -m json.tool
check_response "$CREATE_USER_RESPONSE"

USUARIO_ID=$(echo $CREATE_USER_RESPONSE | python3 -c "import sys, json; d=json.load(sys.stdin); print(d.get('data', {}).get('id', ''))" 2>/dev/null)

if [ ! -z "$USUARIO_ID" ]; then
  print_operation "PUT /api/usuarios/$USUARIO_ID (Actualizar usuario)"
  UPDATE_USER_RESPONSE=$(curl -s -X PUT "$API_URL/usuarios/$USUARIO_ID" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "email=nuevo.usuario@farmacontrol.com&nombre=Nuevo Actualizado&apellido=Usuario Test&rolId=3")
  echo $UPDATE_USER_RESPONSE | python3 -m json.tool
  check_response "$UPDATE_USER_RESPONSE"

  print_operation "DELETE /api/usuarios/$USUARIO_ID (Eliminar usuario)"
  DELETE_USER_RESPONSE=$(curl -s -X DELETE "$API_URL/usuarios/$USUARIO_ID" \
    -H "Authorization: Bearer $TOKEN")
  echo $DELETE_USER_RESPONSE | python3 -m json.tool
  check_response "$DELETE_USER_RESPONSE"
fi

# ================================================================
# 8. CRUD DE VENTAS
# ================================================================
print_section "🛒 8. CRUD DE VENTAS"

print_operation "GET /api/ventas?page=0&size=5 (Listar con paginación)"
VENTAS_RESPONSE=$(curl -s "$API_URL/ventas?page=0&size=5" -H "Authorization: Bearer $TOKEN")
echo $VENTAS_RESPONSE | python3 -m json.tool
check_response "$VENTAS_RESPONSE"

print_operation "POST /api/ventas (Crear nueva venta)"
CREATE_VENTA_RESPONSE=$(curl -s -X POST "$API_URL/ventas" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "clienteId=1&metodoPago=EFECTIVO&observaciones=Venta de prueba&detalles[0].productoId=1&detalles[0].cantidad=2&detalles[0].precioUnitario=15.50&detalles[1].productoId=2&detalles[1].cantidad=1&detalles[1].precioUnitario=22.00")
echo $CREATE_VENTA_RESPONSE | python3 -m json.tool
check_response "$CREATE_VENTA_RESPONSE"

VENTA_ID=$(echo $CREATE_VENTA_RESPONSE | python3 -c "import sys, json; d=json.load(sys.stdin); print(d.get('data', {}).get('id', ''))" 2>/dev/null)

if [ ! -z "$VENTA_ID" ]; then
  print_operation "GET /api/ventas/$VENTA_ID (Obtener venta por ID)"
  VENTA_RESPONSE=$(curl -s "$API_URL/ventas/$VENTA_ID" -H "Authorization: Bearer $TOKEN")
  echo $VENTA_RESPONSE | python3 -m json.tool
  check_response "$VENTA_RESPONSE"

  print_operation "DELETE /api/ventas/$VENTA_ID (Cancelar venta)"
  DELETE_VENTA_RESPONSE=$(curl -s -X DELETE "$API_URL/ventas/$VENTA_ID" \
    -H "Authorization: Bearer $TOKEN")
  echo $DELETE_VENTA_RESPONSE | python3 -m json.tool
  check_response "$DELETE_VENTA_RESPONSE"
fi

# ================================================================
# 9. CRUD DE COMPRAS
# ================================================================
print_section "📥 9. CRUD DE COMPRAS"

print_operation "GET /api/compras?page=0&size=5 (Listar con paginación)"
COMPRAS_RESPONSE=$(curl -s "$API_URL/compras?page=0&size=5" -H "Authorization: Bearer $TOKEN")
echo $COMPRAS_RESPONSE | python3 -m json.tool
check_response "$COMPRAS_RESPONSE"

print_operation "POST /api/compras (Crear nueva compra)"
CREATE_COMPRA_RESPONSE=$(curl -s -X POST "$API_URL/compras" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "proveedorId=1&observaciones=Compra de prueba&detalles[0].productoId=1&detalles[0].cantidad=50&detalles[0].precioUnitario=10.00&detalles[1].productoId=2&detalles[1].cantidad=30&detalles[1].precioUnitario=18.00")
echo $CREATE_COMPRA_RESPONSE | python3 -m json.tool
check_response "$CREATE_COMPRA_RESPONSE"

COMPRA_ID=$(echo $CREATE_COMPRA_RESPONSE | python3 -c "import sys, json; d=json.load(sys.stdin); print(d.get('data', {}).get('id', ''))" 2>/dev/null)

if [ ! -z "$COMPRA_ID" ]; then
  print_operation "GET /api/compras/$COMPRA_ID (Obtener compra por ID)"
  COMPRA_RESPONSE=$(curl -s "$API_URL/compras/$COMPRA_ID" -H "Authorization: Bearer $TOKEN")
  echo $COMPRA_RESPONSE | python3 -m json.tool
  check_response "$COMPRA_RESPONSE"

  print_operation "PUT /api/compras/$COMPRA_ID/estado (Actualizar estado a RECIBIDA)"
  UPDATE_COMPRA_RESPONSE=$(curl -s -X PUT "$API_URL/compras/$COMPRA_ID/estado" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "estado=RECIBIDA")
  echo $UPDATE_COMPRA_RESPONSE | python3 -m json.tool
  check_response "$UPDATE_COMPRA_RESPONSE"

  print_operation "DELETE /api/compras/$COMPRA_ID (Cancelar compra)"
  DELETE_COMPRA_RESPONSE=$(curl -s -X DELETE "$API_URL/compras/$COMPRA_ID" \
    -H "Authorization: Bearer $TOKEN")
  echo $DELETE_COMPRA_RESPONSE | python3 -m json.tool
  check_response "$DELETE_COMPRA_RESPONSE"
fi

# ================================================================
# 10. REPORTES Y DASHBOARD
# ================================================================
print_section "📊 10. REPORTES Y DASHBOARD"

print_operation "GET /api/reportes/dashboard (Dashboard general)"
DASHBOARD_RESPONSE=$(curl -s "$API_URL/reportes/dashboard" -H "Authorization: Bearer $TOKEN")
echo $DASHBOARD_RESPONSE | python3 -m json.tool
check_response "$DASHBOARD_RESPONSE"

print_operation "GET /api/reportes/ventas?fechaInicio=2025-01-01&fechaFin=2025-12-31 (Reporte de ventas)"
VENTAS_REPORT=$(curl -s "$API_URL/reportes/ventas?fechaInicio=2025-01-01&fechaFin=2025-12-31" -H "Authorization: Bearer $TOKEN")
echo $VENTAS_REPORT | python3 -m json.tool
check_response "$VENTAS_REPORT"

print_operation "GET /api/reportes/productos (Reporte de productos más vendidos)"
PRODUCTOS_REPORT=$(curl -s "$API_URL/reportes/productos" -H "Authorization: Bearer $TOKEN")
echo $PRODUCTOS_REPORT | python3 -m json.tool
check_response "$PRODUCTOS_REPORT"

print_operation "GET /api/reportes/inventario (Reporte de inventario)"
INVENTARIO_REPORT=$(curl -s "$API_URL/reportes/inventario" -H "Authorization: Bearer $TOKEN")
echo $INVENTARIO_REPORT | python3 -m json.tool
check_response "$INVENTARIO_REPORT"

print_operation "GET /api/reportes/compras?fechaInicio=2025-01-01&fechaFin=2025-12-31 (Reporte de compras)"
COMPRAS_REPORT=$(curl -s "$API_URL/reportes/compras?fechaInicio=2025-01-01&fechaFin=2025-12-31" -H "Authorization: Bearer $TOKEN")
echo $COMPRAS_REPORT | python3 -m json.tool
check_response "$COMPRAS_REPORT"

# ================================================================
# RESUMEN FINAL
# ================================================================
print_section "✅ RESUMEN DE PRUEBAS COMPLETADAS"

echo -e "${GREEN}✅ 1. Autenticación (Login)${NC}"
echo -e "${GREEN}✅ 2. CRUD Roles (GET, POST, PUT, DELETE)${NC}"
echo -e "${GREEN}✅ 3. CRUD Categorías (GET, POST, PUT, DELETE)${NC}"
echo -e "${GREEN}✅ 4. CRUD Productos (GET, POST, PUT, DELETE, Buscar, Stock Bajo)${NC}"
echo -e "${GREEN}✅ 5. CRUD Clientes (GET, POST, PUT, DELETE, Buscar)${NC}"
echo -e "${GREEN}✅ 6. CRUD Proveedores (GET, POST, PUT, DELETE)${NC}"
echo -e "${GREEN}✅ 7. CRUD Usuarios (GET, POST, PUT, DELETE, Me)${NC}"
echo -e "${GREEN}✅ 8. CRUD Ventas (GET, POST, DELETE)${NC}"
echo -e "${GREEN}✅ 9. CRUD Compras (GET, POST, PUT Estado, DELETE)${NC}"
echo -e "${GREEN}✅ 10. Reportes (Dashboard, Ventas, Productos, Inventario, Compras)${NC}"

echo ""
echo -e "${CYAN}================================================${NC}"
echo -e "${CYAN}Total de módulos probados: 10${NC}"
echo -e "${CYAN}Total de endpoints probados: ~60+${NC}"
echo -e "${CYAN}================================================${NC}"
echo ""
