#!/bin/bash

# 🔐 Script para crear usuarios en FarmaControl API
# Uso: ./crear-usuario.sh email password nombre apellido rol_id

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

API_URL="http://localhost:8080/api"

# Verificar argumentos
if [ $# -lt 5 ]; then
    echo -e "${YELLOW}📝 Uso: $0 <email> <password> <nombre> <apellido> <rol_id>${NC}"
    echo ""
    echo "Ejemplo:"
    echo "  $0 vendedor@farmacontrol.com vendedor123 Juan Pérez 2"
    echo ""
    echo "Roles disponibles:"
    echo "  1  = ADMIN (Administrador general)"
    echo "  2  = FARMACEUTICO (Control de inventario)"
    echo "  3  = CAJERO (Procesa ventas)"
    echo "  4  = ALMACEN (Gestión de existencias)"
    echo "  5  = GERENTE (Supervisa operaciones)"
    echo "  20 = INVITADO (Solo lectura)"
    echo ""
    exit 1
fi

EMAIL=$1
PASSWORD=$2
NOMBRE=$3
APELLIDO=$4
ROL_ID=$5

echo -e "${YELLOW}🔧 Creando usuario...${NC}"
echo "  📧 Email: $EMAIL"
echo "  👤 Nombre: $NOMBRE $APELLIDO"
echo "  🎭 Rol ID: $ROL_ID"
echo ""

# Crear usuario
RESPONSE=$(curl -s -X POST "$API_URL/usuarios" \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"$EMAIL\",
    \"password\": \"$PASSWORD\",
    \"nombre\": \"$NOMBRE\",
    \"apellido\": \"$APELLIDO\",
    \"rol_id\": \"$ROL_ID\",
    \"activo\": \"true\"
  }")

# Verificar respuesta
if echo "$RESPONSE" | grep -q '"success":true'; then
    echo -e "${GREEN}✅ Usuario creado exitosamente!${NC}"
    echo ""
    echo "Credenciales:"
    echo "  📧 Email: $EMAIL"
    echo "  🔑 Password: $PASSWORD"
    echo ""
    
    # Extraer rol
    ROL_NOMBRE=$(echo "$RESPONSE" | grep -o '"nombre":"[^"]*"' | head -2 | tail -1 | cut -d'"' -f4)
    if [ ! -z "$ROL_NOMBRE" ]; then
        echo "  🎭 Rol: $ROL_NOMBRE"
    fi
    
    # Probar login
    echo ""
    echo -e "${YELLOW}🧪 Probando login...${NC}"
    LOGIN_RESPONSE=$(curl -s -X POST "$API_URL/usuarios/auth" \
      -H "Content-Type: application/json" \
      -d "{\"email\": \"$EMAIL\", \"password\": \"$PASSWORD\"}")
    
    if echo "$LOGIN_RESPONSE" | grep -q '"success":true'; then
        echo -e "${GREEN}✅ Login funciona correctamente!${NC}"
    else
        echo -e "${RED}❌ Error al hacer login${NC}"
    fi
else
    echo -e "${RED}❌ Error al crear usuario${NC}"
    echo ""
    ERROR=$(echo "$RESPONSE" | grep -o '"error":"[^"]*"' | cut -d'"' -f4)
    if [ ! -z "$ERROR" ]; then
        echo "Error: $ERROR"
    else
        echo "Respuesta completa:"
        echo "$RESPONSE"
    fi
fi

echo ""
