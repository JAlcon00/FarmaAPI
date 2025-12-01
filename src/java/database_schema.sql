-- ================================================================
-- FARMACONTROL - TABLAS COMPATIBLES CON GOOGLE CLOUD SQL
-- ================================================================
CREATE DATABASE IF NOT EXISTS farmacontrol
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE farmacontrol;

-- 1. Roles
CREATE TABLE roles (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       nombre VARCHAR(50) NOT NULL UNIQUE,
                       descripcion VARCHAR(255),
                       activo BOOLEAN DEFAULT TRUE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. Usuarios
CREATE TABLE usuarios (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          email VARCHAR(100) NOT NULL UNIQUE,
                          password_hash VARCHAR(255) NOT NULL,
                          nombre VARCHAR(100) NOT NULL,
                          apellido VARCHAR(100) NOT NULL,
                          rol_id INT NOT NULL,
                          activo BOOLEAN DEFAULT TRUE,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          FOREIGN KEY (rol_id) REFERENCES roles(id)
);

-- 3. Categorías
CREATE TABLE categorias (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            nombre VARCHAR(100) NOT NULL UNIQUE,
                            descripcion TEXT,
                            activo BOOLEAN DEFAULT TRUE,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 4. Productos
CREATE TABLE productos (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           nombre VARCHAR(200) NOT NULL,
                           descripcion TEXT,
                           categoria_id BIGINT NOT NULL,
                           precio DECIMAL(10,2) NOT NULL,
                           stock INT NOT NULL DEFAULT 0,
                           stock_minimo INT NOT NULL DEFAULT 5,
                           codigo_barras VARCHAR(50) UNIQUE,
                           activo BOOLEAN DEFAULT TRUE,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

-- 5. Proveedores
CREATE TABLE proveedores (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             nombre VARCHAR(200) NOT NULL,
                             rfc VARCHAR(20) UNIQUE,
                             telefono VARCHAR(20),
                             email VARCHAR(100),
                             direccion TEXT,
                             ciudad VARCHAR(100),
                             activo BOOLEAN DEFAULT TRUE,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 6. Clientes
CREATE TABLE clientes (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          apellido VARCHAR(100),
                          telefono VARCHAR(20),
                          email VARCHAR(100),
                          direccion TEXT,
                          fecha_nacimiento DATE,
                          activo BOOLEAN DEFAULT TRUE,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 7. Compras
CREATE TABLE compras (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         proveedor_id BIGINT NOT NULL,
                         usuario_id BIGINT NOT NULL,
                         subtotal DECIMAL(12,2) DEFAULT 0,
                         impuestos DECIMAL(12,2) DEFAULT 0,
                         total DECIMAL(12,2) DEFAULT 0,
                         estado VARCHAR(20) DEFAULT 'PENDIENTE',
                         observaciones TEXT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         FOREIGN KEY (proveedor_id) REFERENCES proveedores(id),
                         FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- 8. Detalle compras
CREATE TABLE detalle_compras (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 compra_id BIGINT NOT NULL,
                                 producto_id BIGINT NOT NULL,
                                 cantidad INT NOT NULL,
                                 precio_unitario DECIMAL(10,2) NOT NULL,
                                 subtotal DECIMAL(12,2),
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 FOREIGN KEY (compra_id) REFERENCES compras(id) ON DELETE CASCADE,
                                 FOREIGN KEY (producto_id) REFERENCES productos(id)
);

-- 9. Ventas
CREATE TABLE ventas (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        cliente_id BIGINT NULL,
                        usuario_id BIGINT NOT NULL,
                        subtotal DECIMAL(12,2) DEFAULT 0,
                        descuento DECIMAL(10,2) DEFAULT 0,
                        impuestos DECIMAL(12,2) DEFAULT 0,
                        total DECIMAL(12,2) DEFAULT 0,
                        metodo_pago VARCHAR(20) DEFAULT 'EFECTIVO',
                        estado VARCHAR(20) DEFAULT 'COMPLETADA',
                        observaciones TEXT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        FOREIGN KEY (cliente_id) REFERENCES clientes(id),
                        FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- 10. Detalle ventas
CREATE TABLE detalle_ventas (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                venta_id BIGINT NOT NULL,
                                producto_id BIGINT NOT NULL,
                                nombre_producto VARCHAR(200) NOT NULL,
                                cantidad INT NOT NULL,
                                precio_unitario DECIMAL(10,2) NOT NULL,
                                subtotal DECIMAL(12,2),
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE CASCADE,
                                FOREIGN KEY (producto_id) REFERENCES productos(id)
);

-- 11. Movimientos de inventario
CREATE TABLE movimientos_inventario (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        producto_id BIGINT NOT NULL,
                                        tipo_movimiento VARCHAR(20) NOT NULL,
                                        cantidad_anterior INT NOT NULL,
                                        cantidad_movimiento INT NOT NULL,
                                        cantidad_nueva INT NOT NULL,
                                        referencia_id BIGINT NULL,
                                        usuario_id BIGINT NOT NULL,
                                        observaciones TEXT,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        FOREIGN KEY (producto_id) REFERENCES productos(id),
                                        FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- Índices mínimos
CREATE INDEX idx_producto_categoria ON productos(categoria_id);
CREATE INDEX idx_ventas_fecha ON ventas(fecha);
CREATE INDEX idx_compras_fecha ON compras(fecha);


USE farmacontrol;

-- ================================================================
-- VISTA: v_productos_info
-- Muestra información general de los productos con su categoría
-- ================================================================
CREATE OR REPLACE VIEW v_productos_info AS
SELECT
    p.id,
    p.nombre AS producto,
    p.descripcion,
    c.nombre AS categoria,
    p.precio,
    p.stock,
    p.stock_minimo,
    CASE
        WHEN p.stock = 0 THEN 'SIN STOCK'
        WHEN p.stock <= p.stock_minimo THEN 'BAJO STOCK'
        ELSE 'OK'
        END AS estado_stock,
    p.codigo_barras,
    p.activo,
    p.created_at,
    p.updated_at
FROM productos p
         INNER JOIN categorias c ON p.categoria_id = c.id;

-- ================================================================
-- VISTA: v_ventas_completas
-- Información detallada de las ventas con cliente y vendedor
-- ================================================================
CREATE OR REPLACE VIEW v_ventas_completas AS
SELECT
    v.id AS venta_id,
    v.fecha,
    CONCAT(COALESCE(c.nombre, 'Cliente'), ' ', COALESCE(c.apellido, 'Anónimo')) AS cliente,
    c.telefono AS cliente_telefono,
    c.email AS cliente_email,
    CONCAT(u.nombre, ' ', u.apellido) AS vendedor,
    r.nombre AS rol_vendedor,
    v.subtotal,
    v.descuento,
    v.impuestos,
    v.total,
    v.metodo_pago,
    v.estado,
    COUNT(dv.id) AS total_items,
    SUM(dv.cantidad) AS total_productos
FROM ventas v
         LEFT JOIN clientes c ON v.cliente_id = c.id
         INNER JOIN usuarios u ON v.usuario_id = u.id
         INNER JOIN roles r ON u.rol_id = r.id
         LEFT JOIN detalle_ventas dv ON v.id = dv.venta_id
GROUP BY v.id, v.fecha, c.nombre, c.apellido, c.telefono, c.email,
         u.nombre, u.apellido, r.nombre,
         v.subtotal, v.descuento, v.impuestos, v.total, v.metodo_pago, v.estado;

-- ================================================================
-- VISTA: v_productos_mas_vendidos
-- Ranking de productos con ventas totales e ingresos generados
-- ================================================================
CREATE OR REPLACE VIEW v_productos_mas_vendidos AS
SELECT
    p.id AS producto_id,
    p.nombre AS producto,
    c.nombre AS categoria,
    SUM(dv.cantidad) AS total_vendido,
    COUNT(DISTINCT dv.venta_id) AS numero_ventas,
    SUM(dv.precio_unitario * dv.cantidad) AS ingresos_generados,
    AVG(dv.precio_unitario) AS precio_promedio
FROM productos p
         INNER JOIN categorias c ON p.categoria_id = c.id
         INNER JOIN detalle_ventas dv ON p.id = dv.producto_id
         INNER JOIN ventas v ON dv.venta_id = v.id
WHERE v.estado = 'COMPLETADA'
GROUP BY p.id, p.nombre, c.nombre
ORDER BY total_vendido DESC;

-- ================================================================
-- VISTA: v_inventario_valorizado
-- Valor total del inventario agrupado por producto y categoría
-- ================================================================
CREATE OR REPLACE VIEW v_inventario_valorizado AS
SELECT
    p.id AS producto_id,
    p.nombre AS producto,
    c.nombre AS categoria,
    p.stock,
    p.precio,
    (p.stock * p.precio) AS valor_inventario,
    p.stock_minimo,
    CASE
        WHEN p.stock = 0 THEN 'SIN_STOCK'
        WHEN p.stock <= p.stock_minimo THEN 'STOCK_BAJO'
        ELSE 'STOCK_NORMAL'
        END AS estado_stock
FROM productos p
         INNER JOIN categorias c ON p.categoria_id = c.id
WHERE p.activo = TRUE;

-- ================================================================
-- TRIGGER 1: después de insertar en detalle_ventas
-- Descuenta del stock y registra el movimiento
-- ================================================================
DROP TRIGGER IF EXISTS trg_descuento_stock_venta;
CREATE TRIGGER trg_descuento_stock_venta
    AFTER INSERT ON detalle_ventas
    FOR EACH ROW
BEGIN
    DECLARE v_stock_actual INT;
    SELECT stock INTO v_stock_actual FROM productos WHERE id = NEW.producto_id;

    UPDATE productos
    SET stock = stock - NEW.cantidad
    WHERE id = NEW.producto_id;

    INSERT INTO movimientos_inventario (
        producto_id, tipo_movimiento, cantidad_anterior,
        cantidad_movimiento, cantidad_nueva, referencia_id, usuario_id, observaciones
    )
    VALUES (
               NEW.producto_id,
               'VENTA',
               v_stock_actual,
               -NEW.cantidad,
               v_stock_actual - NEW.cantidad,
               NEW.venta_id,
               (SELECT usuario_id FROM ventas WHERE id = NEW.venta_id),
               'Salida por venta'
           );
END;

-- ================================================================
-- TRIGGER 2: después de eliminar en detalle_ventas
-- Restaura el stock (por ejemplo, al cancelar una venta)
-- ================================================================
DROP TRIGGER IF EXISTS trg_revertir_stock_venta;
CREATE TRIGGER trg_revertir_stock_venta
    AFTER DELETE ON detalle_ventas
    FOR EACH ROW
BEGIN
    DECLARE v_stock_actual INT;
    SELECT stock INTO v_stock_actual FROM productos WHERE id = OLD.producto_id;

    UPDATE productos
    SET stock = stock + OLD.cantidad
    WHERE id = OLD.producto_id;

    INSERT INTO movimientos_inventario (
        producto_id, tipo_movimiento, cantidad_anterior,
        cantidad_movimiento, cantidad_nueva, referencia_id, usuario_id, observaciones
    )
    VALUES (
               OLD.producto_id,
               'AJUSTE',
               v_stock_actual,
               OLD.cantidad,
               v_stock_actual + OLD.cantidad,
               OLD.venta_id,
               (SELECT usuario_id FROM ventas WHERE id = OLD.venta_id),
               'Reversión de venta'
           );
END;

-- ================================================================
-- TRIGGER 3: después de insertar en detalle_compras
-- Aumenta stock y registra el movimiento
-- ================================================================
DROP TRIGGER IF EXISTS trg_aumentar_stock_compra;
CREATE TRIGGER trg_aumentar_stock_compra
    AFTER INSERT ON detalle_compras
    FOR EACH ROW
BEGIN
    DECLARE v_stock_actual INT;
    SELECT stock INTO v_stock_actual FROM productos WHERE id = NEW.producto_id;

    UPDATE productos
    SET stock = stock + NEW.cantidad
    WHERE id = NEW.producto_id;

    INSERT INTO movimientos_inventario (
        producto_id, tipo_movimiento, cantidad_anterior,
        cantidad_movimiento, cantidad_nueva, referencia_id, usuario_id, observaciones
    )
    VALUES (
               NEW.producto_id,
               'COMPRA',
               v_stock_actual,
               NEW.cantidad,
               v_stock_actual + NEW.cantidad,
               NEW.compra_id,
               (SELECT usuario_id FROM compras WHERE id = NEW.compra_id),
               'Entrada por compra'
           );
END;

-- ================================================================
-- TRIGGER 4: después de eliminar en detalle_compras
-- Disminuye stock si se cancela una compra
-- ================================================================
DROP TRIGGER IF EXISTS trg_revertir_stock_compra;
CREATE TRIGGER trg_revertir_stock_compra
    AFTER DELETE ON detalle_compras
    FOR EACH ROW
BEGIN
    DECLARE v_stock_actual INT;
    SELECT stock INTO v_stock_actual FROM productos WHERE id = OLD.producto_id;

    UPDATE productos
    SET stock = stock - OLD.cantidad
    WHERE id = OLD.producto_id;

    INSERT INTO movimientos_inventario (
        producto_id, tipo_movimiento, cantidad_anterior,
        cantidad_movimiento, cantidad_nueva, referencia_id, usuario_id, observaciones
    )
    VALUES (
               OLD.producto_id,
               'AJUSTE',
               v_stock_actual,
               -OLD.cantidad,
               v_stock_actual - OLD.cantidad,
               OLD.compra_id,
               (SELECT usuario_id FROM compras WHERE id = OLD.compra_id),
               'Reversión de compra'
           );
END;


INSERT INTO roles (nombre, descripcion) VALUES
                                            ('ADMIN', 'Administrador general del sistema'),
                                            ('FARMACEUTICO', 'Controla inventario y medicamentos'),
                                            ('CAJERO', 'Procesa ventas y tickets'),
                                            ('ALMACEN', 'Gestiona existencias y recepción de productos'),
                                            ('GERENTE', 'Supervisa operaciones y reportes'),
                                            ('ASISTENTE', 'Apoyo administrativo'),
                                            ('AUDITOR', 'Revisa movimientos e inventario'),
                                            ('SOPORTE', 'Mantenimiento y soporte técnico'),
                                            ('OPERADOR', 'Operación general del sistema'),
                                            ('SUPERVISOR', 'Monitorea desempeño del personal'),
                                            ('ENCARGADO VENTAS', 'Encargado de ventas al público'),
                                            ('ENCARGADO COMPRAS', 'Encargado de pedidos y proveedores'),
                                            ('ADMIN FINANZAS', 'Gestión contable y fiscal'),
                                            ('DIRECTOR', 'Gestión general y estratégica'),
                                            ('RRHH', 'Gestión de personal'),
                                            ('MARKETING', 'Promociones y campañas'),
                                            ('TESORERIA', 'Control de flujo de efectivo'),
                                            ('ANALISTA', 'Análisis de datos y KPIs'),
                                            ('INTERNO', 'Empleado interno sin permisos críticos'),
                                            ('INVITADO', 'Solo lectura');

INSERT INTO usuarios (email, password_hash, nombre, apellido, rol_id) VALUES
                                                                          ('admin@farmacontrol.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7VPaflcEtFNerYBuPPn1e/NkJpYAoCm', 'Administrador', 'Sistema', 1),
                                                                          ('maria@farmacontrol.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7VPaflcEtFNerYBuPPn1e/NkJpYAoCm', 'María', 'López', 2),
                                                                          ('juan@farmacontrol.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7VPaflcEtFNerYBuPPn1e/NkJpYAoCm', 'Juan', 'Pérez', 3),
                                                                          ('sofia@farmacontrol.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7VPaflcEtFNerYBuPPn1e/NkJpYAoCm', 'Sofía', 'Martínez', 4),
                                                                          ('roberto@farmacontrol.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7VPaflcEtFNerYBuPPn1e/NkJpYAoCm', 'Roberto', 'Gómez', 5),
                                                                          ('karla@farmacontrol.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7VPaflcEtFNerYBuPPn1e/NkJpYAoCm', 'Karla', 'Hernández', 6),
                                                                          ('diego@farmacontrol.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7VPaflcEtFNerYBuPPn1e/NkJpYAoCm', 'Diego', 'Santos', 7),
                                                                          ('elena@farmacontrol.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7VPaflcEtFNerYBuPPn1e/NkJpYAoCm', 'Elena', 'Moreno', 8),
                                                                          ('ricardo@farmacontrol.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7VPaflcEtFNerYBuPPn1e/NkJpYAoCm', 'Ricardo', 'Torres', 9),
                                                                          ('laura@farmacontrol.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7VPaflcEtFNerYBuPPn1e/NkJpYAoCm', 'Laura', 'Reyes', 10),
                                                                          ('fernando@farmacontrol.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7VPaflcEtFNerYBuPPn1e/NkJpYAoCm', 'Fernando', 'García', 11),
                                                                          ('nancy@farmacontrol.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7VPaflcEtFNerYBuPPn1e/NkJpYAoCm', 'Nancy', 'Cruz', 12),
                                                                          ('oscar@farmacontrol.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7VPaflcEtFNerYBuPPn1e/NkJpYAoCm', 'Óscar', 'Mejía', 13),
                                                                          ('raquel@farmacontrol.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7VPaflcEtFNerYBuPPn1e/NkJpYAoCm', 'Raquel', 'Jiménez', 14),
                                                                          ('hugo@farmacontrol.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7VPaflcEtFNerYBuPPn1e/NkJpYAoCm', 'Hugo', 'Delgado', 15),
                                                                          ('cristina@farmacontrol.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7VPaflcEtFNerYBuPPn1e/NkJpYAoCm', 'Cristina', 'Vega', 16),
                                                                          ('rodrigo@farmacontrol.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7VPaflcEtFNerYBuPPn1e/NkJpYAoCm', 'Rodrigo', 'Pineda', 17),
                                                                          ('esteban@farmacontrol.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7VPaflcEtFNerYBuPPn1e/NkJpYAoCm', 'Esteban', 'Silva', 18),
                                                                          ('alejandra@farmacontrol.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7VPaflcEtFNerYBuPPn1e/NkJpYAoCm', 'Alejandra', 'Flores', 19),
                                                                          ('prueba@farmacontrol.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7VPaflcEtFNerYBuPPn1e/NkJpYAoCm', 'Usuario', 'Demo', 20);

INSERT INTO categorias (nombre, descripcion) VALUES
                                                 ('Analgésicos', 'Medicamentos para el dolor'),
                                                 ('Antibióticos', 'Tratamientos antibacterianos'),
                                                 ('Antiinflamatorios', 'Medicamentos para inflamación'),
                                                 ('Antigripales', 'Resfriado y gripe'),
                                                 ('Antihistamínicos', 'Alergias y reacciones cutáneas'),
                                                 ('Vitaminas', 'Suplementos vitamínicos'),
                                                 ('Minerales', 'Complementos minerales'),
                                                 ('Cuidado Personal', 'Higiene y bienestar'),
                                                 ('Primeros Auxilios', 'Curaciones rápidas'),
                                                 ('Control Crónico', 'Tratamientos prolongados'),
                                                 ('Gastrointestinales', 'Tracto digestivo'),
                                                 ('Cardiovasculares', 'Corazón y presión arterial'),
                                                 ('Dermatológicos', 'Cremas y tópicos'),
                                                 ('Neurológicos', 'Sistema nervioso y sueño'),
                                                 ('Endocrinos', 'Control hormonal y diabetes'),
                                                 ('Oftálmicos', 'Cuidado ocular'),
                                                 ('Otorrinolaringológicos', 'Oídos, nariz y garganta'),
                                                 ('Pediátricos', 'Uso infantil'),
                                                 ('Veterinarios', 'Uso animal'),
                                                 ('Naturistas', 'Productos de origen natural');

INSERT INTO productos (nombre, descripcion, categoria_id, precio, stock, stock_minimo, codigo_barras) VALUES
                                                                                                          ('Paracetamol 500mg', 'Analgésico y antipirético', 1, 15.50, 100, 10, '7501000123456'),
                                                                                                          ('Ibuprofeno 400mg', 'Antiinflamatorio', 3, 22.00, 80, 15, '7501000123457'),
                                                                                                          ('Amoxicilina 500mg', 'Antibiótico de amplio espectro', 2, 45.00, 50, 10, '7501000123458'),
                                                                                                          ('Omeprazol 20mg', 'Reductor de acidez gástrica', 11, 35.00, 60, 10, '7501000123459'),
                                                                                                          ('Loratadina 10mg', 'Antialérgico', 5, 28.00, 70, 15, '7501000123460'),
                                                                                                          ('Vitamina C 1g', 'Refuerza sistema inmunológico', 6, 40.00, 100, 10, '7501000123461'),
                                                                                                          ('Calcio + Vitamina D', 'Fortalece huesos', 7, 85.00, 40, 8, '7501000123462'),
                                                                                                          ('Pomada antibiótica', 'Cicatrizante y antiséptica', 13, 55.00, 30, 5, '7501000123463'),
                                                                                                          ('Jarabe para la tos', 'Calma irritación y congestión', 4, 60.00, 20, 5, '7501000123464'),
                                                                                                          ('Insulina 10ml', 'Control de glucosa', 15, 250.00, 15, 5, '7501000123465'),
                                                                                                          ('Aspirina 100mg', 'Anticoagulante leve', 12, 20.00, 100, 10, '7501000123466'),
                                                                                                          ('Melatonina 3mg', 'Regula sueño', 14, 90.00, 25, 5, '7501000123467'),
                                                                                                          ('Crema para quemaduras', 'Alivio tópico', 13, 75.00, 35, 5, '7501000123468'),
                                                                                                          ('Solución oftálmica', 'Lubricante ocular', 16, 65.00, 30, 5, '7501000123469'),
                                                                                                          ('Antiparasitario 10ml', 'Uso veterinario', 19, 30.00, 50, 5, '7501000123470'),
                                                                                                          ('Shampoo medicado', 'Tratamiento dermatológico', 13, 95.00, 20, 5, '7501000123471'),
                                                                                                          ('Alcohol 70%', 'Desinfectante', 9, 25.00, 200, 20, '7501000123472'),
                                                                                                          ('Banda elástica', 'Soporte terapéutico', 9, 18.00, 150, 10, '7501000123473'),
                                                                                                          ('Colágeno 500mg', 'Suplemento antiedad', 6, 120.00, 25, 5, '7501000123474'),
                                                                                                          ('Valeriana 100mg', 'Tranquilizante natural', 20, 85.00, 30, 5, '7501000123475');


INSERT INTO proveedores (nombre, rfc, telefono, email, direccion, ciudad) VALUES
                                                                              ('Distribuidora Farmacéutica SA de CV', 'DFA123456789', '5551234567', 'ventas@distribuidora.com', 'Av. Central 123', 'Ciudad de México'),
                                                                              ('Salud Total SA', 'SAL987654321', '3332221100', 'contacto@saludtotal.com', 'Blvd. Independencia 456', 'Guadalajara'),
                                                                              ('Farmabien SA', 'FAB654321098', '4425556677', 'contacto@farmabien.com', 'Calle Reforma 45', 'Querétaro'),
                                                                              ('MedicPlus SA', 'MED123987456', '8183344556', 'ventas@medicplus.com', 'Av. Constitución 78', 'Monterrey'),
                                                                              ('Botica Moderna', 'BOT908172635', '5583341122', 'info@boticamoderna.mx', 'Paseo de la Reforma 67', 'CDMX'),
                                                                              ('PharmaVida SA', 'PHV321456789', '5556678899', 'soporte@pharmavida.com', 'Calle Hidalgo 19', 'Puebla'),
                                                                              ('Distribuciones Salud', 'DIS111222333', '5551002003', 'ventas@distsalud.com', 'Av. Colón 500', 'Toluca'),
                                                                              ('Medicamentos del Bajío', 'MBJ876543210', '4778899001', 'ventas@bajiofarm.com', 'Blvd. López Mateos 123', 'León'),
                                                                              ('Proveedora Hospitalaria', 'PRH112233445', '9994443322', 'contacto@proveedorahosp.com', 'Calle 60 101', 'Mérida'),
                                                                              ('Laboratorios del Norte', 'LBN554433221', '6649988877', 'info@labnorte.com', 'Av. Revolución 200', 'Tijuana'),
                                                                              ('MedicExpress', 'MEX998877665', '5567788990', 'ventas@medicexpress.com', 'Av. Juárez 88', 'CDMX'),
                                                                              ('BioFarm', 'BIO112233445', '5512233445', 'ventas@biofarm.com', 'Eje Central 210', 'CDMX'),
                                                                              ('MedicalSupplies MX', 'MSM778899001', '4421002233', 'soporte@medicalsup.com', 'Av. Universidad 123', 'Querétaro'),
                                                                              ('Farmacéutica Oriente', 'FAO667788990', '2284445566', 'contacto@farmoriente.com', 'Av. Xalapa 54', 'Xalapa'),
                                                                              ('Farmalife', 'FML223344556', '5519988877', 'contacto@farmalife.com', 'Insurgentes Sur 345', 'CDMX'),
                                                                              ('Farmacia del Centro', 'FDC112233445', '5558889990', 'ventas@farmcentro.com', 'Av. Reforma 999', 'CDMX'),
                                                                              ('Farmaco SA', 'FRM778899665', '8185544332', 'info@farmaco.com', 'Av. Gonzalitos 43', 'Monterrey'),
                                                                              ('Farmainnovar SA', 'FMI223344556', '3337778889', 'ventas@fmainnovar.com', 'Av. Vallarta 203', 'Guadalajara'),
                                                                              ('Salud Viva', 'SAV554433221', '4443332211', 'info@saludviva.com', 'Carr. 57 km 10', 'San Luis Potosí'),
                                                                              ('VitalPharma', 'VPH778899001', '7778889900', 'ventas@vitalpharma.com', 'Calle Hidalgo 76', 'Cuernavaca');




-- Mostrar todas las tablas
SHOW TABLES;

-- Contar registros iniciales
SELECT
    (SELECT COUNT(*) FROM roles) AS roles,
    (SELECT COUNT(*) FROM usuarios) AS usuarios,
    (SELECT COUNT(*) FROM categorias) AS categorias,
    (SELECT COUNT(*) FROM productos) AS productos,
    (SELECT COUNT(*) FROM proveedores) AS proveedores;


INSERT INTO clientes (nombre, apellido, telefono, email, direccion, fecha_nacimiento)
VALUES
    ('Juan', 'Pérez', '5551112233', 'juan.perez@gmail.com', 'Calle Reforma 89', '1990-05-12'),
    ('María', 'López', '5559998877', 'maria.lopez@gmail.com', 'Av. Hidalgo 25', '1988-08-03'),
    ('Carlos', 'Ramírez', '4423344556', 'carlos.ramirez@hotmail.com', 'Morelos 47', '1995-11-20'),
    ('Ana', 'Torres', '5552233445', 'ana.torres@gmail.com', 'Calle Juárez 10', '1992-02-10'),
    ('Fernando', 'García', '5512345678', 'fernando.garcia@gmail.com', 'Calle Sol 22', '1998-10-02');


SELECT * FROM clientes;



-- Crear compra
INSERT INTO compras (proveedor_id, usuario_id, subtotal, impuestos, total, estado)
VALUES (1, 1, 1000.00, 160.00, 1160.00, 'RECIBIDA');

-- Insertar detalle (esto debe disparar el TRIGGER trg_aumentar_stock_compra)
INSERT INTO detalle_compras (compra_id, producto_id, cantidad, precio_unitario, subtotal)
VALUES
    (1, 1, 50, 10.00, 500.00),
    (1, 2, 25, 20.00, 500.00);


SELECT id, nombre, stock FROM productos WHERE id IN (1,2);
SELECT * FROM movimientos_inventario ORDER BY id DESC LIMIT 5;


-- Crear venta
INSERT INTO ventas (cliente_id, usuario_id, subtotal, descuento, impuestos, total, metodo_pago, estado)
VALUES (1, 1, 0, 0, 0, 0, 'EFECTIVO', 'COMPLETADA');

-- Insertar detalle (esto debe activar el TRIGGER trg_descuento_stock_venta)
INSERT INTO detalle_ventas (venta_id, producto_id, nombre_producto, cantidad, precio_unitario, subtotal)
VALUES
    (1, 1, 'Paracetamol 500mg', 3, 15.50, 46.50),
    (1, 2, 'Ibuprofeno 400mg', 2, 22.00, 44.00);

SELECT id, nombre, stock FROM productos WHERE id IN (1,2);
SELECT * FROM movimientos_inventario ORDER BY id DESC LIMIT 5;

DELETE FROM detalle_ventas WHERE venta_id = 1 AND producto_id = 1;

SELECT id, nombre, stock FROM productos WHERE id = 1;
SELECT * FROM movimientos_inventario ORDER BY id DESC LIMIT 3;


-- Vista general de productos
SELECT * FROM v_productos_info LIMIT 10;

-- Vista de ventas completas (con clientes, vendedor, total_items, etc.)
SELECT * FROM v_ventas_completas;

-- Vista de productos más vendidos
SELECT * FROM v_productos_mas_vendidos;

-- Vista de inventario valorizado
SELECT * FROM v_inventario_valorizado ORDER BY valor_inventario DESC;


-- Validar integridad referencial (IDs relacionados)
SELECT v.id AS venta_id, dv.producto_id, p.nombre AS producto, dv.cantidad, p.stock
FROM detalle_ventas dv
         INNER JOIN productos p ON p.id = dv.producto_id
         INNER JOIN ventas v ON v.id = dv.venta_id;

-- Ver movimientos y su relación con usuario y producto
SELECT m.id, m.tipo_movimiento, p.nombre AS producto, u.nombre AS usuario, m.cantidad_movimiento, m.cantidad_nueva
FROM movimientos_inventario m
         INNER JOIN productos p ON p.id = m.producto_id
         INNER JOIN usuarios u ON u.id = m.usuario_id
ORDER BY m.created_at DESC
    LIMIT 10;


-- ================================================================
-- FASE 3: TABLAS DE SEGURIDAD Y AUDITORÍA
-- ================================================================

-- Tabla de Refresh Tokens
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(500) NOT NULL UNIQUE,
    usuario_id BIGINT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    revoked_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_token (token),
    INDEX idx_usuario_id (usuario_id),
    INDEX idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de Auditoría
CREATE TABLE IF NOT EXISTS audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT,
    usuario_email VARCHAR(100),
    accion VARCHAR(50) NOT NULL,
    entidad VARCHAR(50) NOT NULL,
    entidad_id BIGINT,
    detalles TEXT,
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    INDEX idx_usuario_id (usuario_id),
    INDEX idx_accion (accion),
    INDEX idx_entidad (entidad),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de Intentos de Login
CREATE TABLE IF NOT EXISTS login_attempts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    success BOOLEAN NOT NULL,
    failed_reason VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_ip_address (ip_address),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Vista para tokens activos
CREATE OR REPLACE VIEW v_refresh_tokens_activos AS
SELECT 
    rt.id,
    rt.token,
    rt.usuario_id,
    u.email,
    u.nombre,
    rt.expires_at,
    rt.created_at,
    rt.ip_address,
    TIMESTAMPDIFF(HOUR, NOW(), rt.expires_at) AS horas_restantes
FROM refresh_tokens rt
INNER JOIN usuarios u ON rt.usuario_id = u.id
WHERE rt.revoked = FALSE 
  AND rt.expires_at > NOW()
ORDER BY rt.created_at DESC;

-- Vista de auditoría con información de usuario
CREATE OR REPLACE VIEW v_audit_log_completo AS
SELECT 
    al.id,
    al.usuario_id,
    al.usuario_email,
    u.nombre AS usuario_nombre,
    al.accion,
    al.entidad,
    al.entidad_id,
    al.detalles,
    al.ip_address,
    al.created_at
FROM audit_log al
LEFT JOIN usuarios u ON al.usuario_id = u.id
ORDER BY al.created_at DESC;

-- Procedimiento para limpiar tokens expirados
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS sp_limpiar_tokens_expirados()
BEGIN
    DELETE FROM refresh_tokens 
    WHERE expires_at < NOW() 
       OR revoked = TRUE AND revoked_at < DATE_SUB(NOW(), INTERVAL 30 DAY);
END //
DELIMITER ;

-- Procedimiento para revocar todos los tokens de un usuario
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS sp_revocar_tokens_usuario(IN p_usuario_id BIGINT)
BEGIN
    UPDATE refresh_tokens 
    SET revoked = TRUE, 
        revoked_at = NOW() 
    WHERE usuario_id = p_usuario_id 
      AND revoked = FALSE;
END //
DELIMITER ;









