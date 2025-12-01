#!/bin/bash

# Script para probar todos los endpoints de la API FarmaControl
# Uso: ./test-endpoints.sh

API_URL="http://localhost:8080/api"
EMAIL="admin@farmacontrol.com"
PASSWORD="admin123"

echo "🔐 Obteniendo token de autenticación..."
LOGIN_RESPONSE=$(curl -s -X POST "$API_URL/usuarios/auth" \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"$EMAIL\", \"password\": \"$PASSWORD\"}")

TOKEN=$(echo $LOGIN_RESPONSE | python3 -c "import sys, json; print(json.load(sys.stdin)['data']['token'])" 2>/dev/null)

if [ -z "$TOKEN" ]; then
  echo "❌ Error al obtener token"
  echo $LOGIN_RESPONSE | python3 -m json.tool
  exit 1
fi

echo "✅ Token obtenido: ${TOKEN:0:50}..."
echo ""
echo "================================================"
echo ""

# 1. Productos
echo "📦 1. GET /api/productos (todos los productos)"
curl -s "$API_URL/productos?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
echo ""
echo "------------------------------------------------"
echo ""

# 2. Producto específico
echo "🔍 2. GET /api/productos/1 (producto específico)"
curl -s "$API_URL/productos/1" \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
echo ""
echo "------------------------------------------------"
echo ""

# 3. Categorías
echo "📁 3. GET /api/categorias (todas las categorías)"
curl -s "$API_URL/categorias" \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
echo ""
echo "------------------------------------------------"
echo ""

# 4. Categoría específica
echo "🏷️  4. GET /api/categorias/1 (categoría específica)"
curl -s "$API_URL/categorias/1" \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
echo ""
echo "------------------------------------------------"
echo ""

# 5. Ventas
echo "🛒 5. GET /api/ventas (todas las ventas)"
curl -s "$API_URL/ventas?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
echo ""
echo "------------------------------------------------"
echo ""

# 6. Usuarios
echo "👥 6. GET /api/usuarios (todos los usuarios)"
curl -s "$API_URL/usuarios?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
echo ""
echo "------------------------------------------------"
echo ""

# 7. Usuario específico
echo "👤 7. GET /api/usuarios/1 (usuario específico)"
curl -s "$API_URL/usuarios/1" \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
echo ""
echo "------------------------------------------------"
echo ""

# 8. Roles
echo "🎭 8. GET /api/roles (todos los roles)"
curl -s "$API_URL/roles" \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
echo ""
echo "------------------------------------------------"
echo ""

# 9. Búsqueda de productos
echo "🔎 9. GET /api/productos/buscar?q=paracetamol (búsqueda)"
curl -s "$API_URL/productos/buscar?q=paracetamol" \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
echo ""
echo "------------------------------------------------"
echo ""

# 10. Productos con stock bajo
echo "⚠️  10. GET /api/productos/stock-bajo (productos con stock bajo)"
curl -s "$API_URL/productos/stock-bajo" \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
echo ""
echo "================================================"
echo ""
echo "✅ Prueba de endpoints completada"
