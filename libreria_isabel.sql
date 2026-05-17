-- Script de creación de base de datos para Librería Isabel
DROP DATABASE IF EXISTS libreria_isabel;
CREATE DATABASE libreria_isabel CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE libreria_isabel;

-- Tabla de Usuarios
CREATE TABLE usuarios (
    id_usuario INT PRIMARY KEY AUTO_INCREMENT,
    nombre_usuario VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol ENUM('ADMINISTRADOR', 'VENDEDOR') NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    activo BOOLEAN DEFAULT TRUE
);

-- Tabla de Clientes
CREATE TABLE clientes (
    id_cliente INT PRIMARY KEY AUTO_INCREMENT,
    dni VARCHAR(8) UNIQUE NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    telefono VARCHAR(15),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de Productos
CREATE TABLE productos (
    id_producto INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    precio DECIMAL(10,2) NOT NULL CHECK(precio > 0),
    stock INT NOT NULL DEFAULT 0 CHECK(stock >= 0),
    stock_minimo INT NOT NULL DEFAULT 5 CHECK(stock_minimo >= 0)
);

-- Tabla de Ventas
CREATE TABLE ventas (
    id_venta INT PRIMARY KEY AUTO_INCREMENT,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2) NOT NULL CHECK(total >= 0),
    id_cliente INT NOT NULL,
    id_usuario INT NOT NULL,
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- Tabla de Detalles de Venta (Composición)
CREATE TABLE detalle_venta (
    id_detalle INT PRIMARY KEY AUTO_INCREMENT,
    id_venta INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL CHECK(cantidad > 0),
    precio_unitario DECIMAL(10,2) NOT NULL CHECK(precio_unitario > 0),
    subtotal DECIMAL(10,2) NOT NULL CHECK(subtotal >= 0),
    FOREIGN KEY (id_venta) REFERENCES ventas(id_venta) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
);

-- Insertar usuario administrador por defecto
INSERT INTO usuarios (nombre_usuario, password, rol, nombres, apellidos) 
VALUES ('admin', 'admin123', 'ADMINISTRADOR', 'Administrador', 'Sistema');

-- Insertar usuario vendedor por defecto
INSERT INTO usuarios (nombre_usuario, password, rol, nombres, apellidos) 
VALUES ('vendedor1', 'vend123', 'VENDEDOR', 'Juan', 'Pérez');

-- Insertar cliente genérico para ventas sin cliente específico
INSERT INTO clientes (dni, nombre, telefono) 
VALUES ('00000000', 'Cliente Anónimo', 'N/A');

-- Datos de prueba - Productos
INSERT INTO productos (codigo, nombre, categoria, precio, stock, stock_minimo) VALUES
('PROD001', 'Cuaderno Cuadriculado A4 100 hojas', 'Cuadernos', 8.50, 50, 10),
('PROD002', 'Lapicero Azul Faber-Castell', 'Escritura', 1.50, 120, 20),
('PROD003', 'Borrador Blanco Staedtler', 'Útiles', 1.00, 80, 15),
('PROD004', 'Lápiz HB Mongol', 'Escritura', 1.20, 150, 30),
('PROD005', 'Corrector Líquido Paper Mate', 'Útiles', 3.50, 40, 10),
('PROD006', 'Resaltador Amarillo Artesco', 'Escritura', 2.50, 25, 10),
('PROD007', 'Tijera Escolar Vinifan', 'Útiles', 5.00, 30, 8),
('PROD008', 'Pegamento en Barra UHU 20g', 'Útiles', 4.50, 60, 12),
('PROD009', 'Regla 30cm Artesco', 'Útiles', 2.00, 45, 10),
('PROD010', 'Archivador Palanca A4', 'Oficina', 12.00, 20, 5);