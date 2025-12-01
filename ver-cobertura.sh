#!/bin/bash

# Script para abrir el reporte de cobertura de JaCoCo
# Uso: ./ver-cobertura.sh

JACOCO_REPORT="target/site/jacoco/index.html"

echo "🔍 Verificando reporte de JaCoCo..."

if [ ! -f "$JACOCO_REPORT" ]; then
    echo "⚠️  Reporte no encontrado. Generando..."
    mvn test
    echo ""
fi

if [ -f "$JACOCO_REPORT" ]; then
    echo "✅ Abriendo reporte de cobertura..."
    
    # Detectar sistema operativo
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS
        open "$JACOCO_REPORT"
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        # Linux
        xdg-open "$JACOCO_REPORT" 2>/dev/null || sensible-browser "$JACOCO_REPORT"
    elif [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
        # Windows Git Bash
        start "$JACOCO_REPORT"
    else
        echo "❌ Sistema operativo no reconocido"
        echo "📂 Abre manualmente: $JACOCO_REPORT"
        exit 1
    fi
    
    echo "✅ Reporte abierto en el navegador"
else
    echo "❌ Error al generar el reporte"
    exit 1
fi
