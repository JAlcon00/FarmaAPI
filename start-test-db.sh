#!/bin/bash

# Agregar Docker al PATH
export PATH="/Volumes/YisusSSD/Aplicaciones/Docker.app.back/Contents/Resources/bin:$PATH"

echo "🐳 Levantando MySQL en Docker para tests..."

# Levantar MySQL (usando docker compose sin guion)
docker compose -f docker/docker-compose.test.yml up -d

echo "⏳ Esperando a que MySQL esté listo..."

# Esperar hasta 60 segundos
for i in {1..60}; do
    if docker exec farmacontrol-mysql-test mysqladmin ping -h localhost -uroot -proot123 --silent 2>/dev/null; then
        echo "✅ MySQL está listo!"
        break
    fi
    echo "   Intento $i/60..."
    sleep 1
done

# Verificar que la base de datos existe
echo ""
echo "📊 Verificando base de datos..."
docker exec farmacontrol-mysql-test mysql -ufarmacontrol_user -pfarmacontrol_pass -e "SHOW DATABASES;"

echo ""
echo "📋 Tablas creadas:"
docker exec farmacontrol-mysql-test mysql -ufarmacontrol_user -pfarmacontrol_pass farmacontrol -e "SHOW TABLES;"

echo ""
echo "👥 Contando registros:"
docker exec farmacontrol-mysql-test mysql -ufarmacontrol_user -pfarmacontrol_pass farmacontrol -e "
SELECT 
    (SELECT COUNT(*) FROM roles) AS roles,
    (SELECT COUNT(*) FROM usuarios) AS usuarios,
    (SELECT COUNT(*) FROM categorias) AS categorias,
    (SELECT COUNT(*) FROM productos) AS productos,
    (SELECT COUNT(*) FROM proveedores) AS proveedores,
    (SELECT COUNT(*) FROM clientes) AS clientes;
"

echo ""
echo "✅ Base de datos lista para tests en localhost:3307"
echo "   Usuario: farmacontrol_user"
echo "   Password: farmacontrol_pass"
echo "   Database: farmacontrol"
echo ""
echo "Para ejecutar tests:"
echo "   mvn test"
echo ""
echo "Para detener MySQL:"
echo "   docker compose -f docker/docker-compose.test.yml down"
