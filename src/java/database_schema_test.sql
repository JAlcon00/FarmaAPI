-- ================================================================
-- FARMACONTROL - SCHEMA SIMPLIFICADO PARA TESTS
-- (Sin triggers ni procedimientos almacenados)
-- ================================================================

USE farmacontrol;

-- 1. Roles
CREATE TABLE IF NOT EXISTS roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. Usuarios
CREATE TABLE IF NOT EXISTS usuarios (
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
CREATE TABLE IF NOT EXISTS categorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 4. Productos
CREATE TABLE IF NOT EXISTS productos (
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
CREATE TABLE IF NOT EXISTS proveedores (
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
CREATE TABLE IF NOT EXISTS clientes (
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
CREATE TABLE IF NOT EXISTS compras (
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
CREATE TABLE IF NOT EXISTS detalle_compras (
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
CREATE TABLE IF NOT EXISTS ventas (
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
CREATE TABLE IF NOT EXISTS detalle_ventas (
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

-- 11. Refresh Tokens
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
    INDEX idx_usuario_id (usuario_id)
);

-- Índices
CREATE INDEX idx_producto_categoria ON productos(categoria_id);
CREATE INDEX idx_ventas_fecha ON ventas(fecha);
CREATE INDEX idx_compras_fecha ON compras(fecha);

-- Datos iniciales
INSERT INTO roles (nombre, descripcion) VALUES
    ('ADMIN', 'Administrador general del sistema'),
    ('FARMACEUTICO', 'Controla inventario y medicamentos'),
    ('CAJERO', 'Procesa ventas y tickets');

INSERT INTO usuarios (email, password_hash, nombre, apellido, rol_id) VALUES
    ('admin@farmacontrol.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7VPaflcEtFNerYBuPPn1e/NkJpYAoCm', 'Admin', 'Sistema', 1),
    ('farmaceutico@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7VPaflcEtFNerYBuPPn1e/NkJpYAoCm', 'María', 'López', 2),
    ('cajero@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7VPaflcEtFNerYBuPPn1e/NkJpYAoCm', 'Juan', 'Pérez', 3);

INSERT INTO categorias (nombre, descripcion) VALUES
    ('Analgésicos', 'Medicamentos para el dolor'),
    ('Antibióticos', 'Tratamientos antibacterianos'),
    ('Antiinflamatorios', 'Medicamentos para inflamación');

INSERT INTO productos (nombre, descripcion, categoria_id, precio, stock, stock_minimo, codigo_barras) VALUES
    ('Paracetamol 500mg', 'Analgésico y antipirético', 1, 15.50, 100, 10, '7501000123456'),
    ('Ibuprofeno 400mg', 'Antiinflamatorio', 3, 22.00, 80, 15, '7501000123457'),
    ('Amoxicilina 500mg', 'Antibiótico de amplio espectro', 2, 45.00, 50, 10, '7501000123458');

INSERT INTO proveedores (nombre, rfc, telefono, email, ciudad) VALUES
    ('Distribuidora Farmacéutica SA', 'DFA123456789', '5551234567', 'ventas@distribuidora.com', 'CDMX');

INSERT INTO clientes (nombre, apellido, telefono, email) VALUES
    ('Juan', 'Pérez', '5551112233', 'juan.perez@gmail.com'),
    ('María', 'López', '5559998877', 'maria.lopez@gmail.com');
