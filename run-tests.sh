#!/bin/bash
# Script para ejecutar tests con configuración de MySQL Docker

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$SCRIPT_DIR/.env"
ENV_BACKUP="$SCRIPT_DIR/.env.backup"
ENV_TEST="$SCRIPT_DIR/src/test/resources/.env"

echo "🧪 Preparando entorno de test..."

# 1. Hacer backup del .env actual
if [ -f "$ENV_FILE" ]; then
    echo "📦 Haciendo backup de .env original..."
    cp "$ENV_FILE" "$ENV_BACKUP"
fi

# 2. Copiar configuración de test
echo "🔧 Configurando credenciales de Docker MySQL..."
cp "$ENV_TEST" "$ENV_FILE"

# 3. Verificar que MySQL Docker esté corriendo
echo "🐳 Verificando MySQL Docker..."
if ! docker ps | grep -q "farmacontrol-mysql-test"; then
    echo "⚠️  MySQL Docker no está corriendo. Iniciando..."
    ./start-test-db.sh
fi

# 4. Ejecutar los tests
echo ""
echo "▶️  Ejecutando tests..."
echo "════════════════════════════════════════════════════"
mvn clean test "$@"
TEST_RESULT=$?

# 5. Restaurar .env original
echo ""
echo "🔄 Restaurando configuración original..."
if [ -f "$ENV_BACKUP" ]; then
    mv "$ENV_BACKUP" "$ENV_FILE"
    echo "✅ .env restaurado"
else
    echo "⚠️  No se encontró backup de .env"
fi

# 6. Mostrar resultado
echo ""
if [ $TEST_RESULT -eq 0 ]; then
    echo "✅ Tests completados exitosamente"
else
    echo "❌ Tests fallaron con código $TEST_RESULT"
fi

exit $TEST_RESULT
