-- ============================================================
--  Sistema Librería Isabel — Script de Base de Datos
--  MySQL 8.x | Ejecutar completo antes de iniciar el programa
-- ============================================================
CREATE DATABASE IF NOT EXISTS libreria_isabel;
USE libreria_isabel;

CREATE TABLE IF NOT EXISTS usuarios (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    usuario    VARCHAR(50)  NOT NULL UNIQUE,
    contrasena VARCHAR(100) NOT NULL,
    rol        ENUM('administrador','vendedor') NOT NULL
);

CREATE TABLE IF NOT EXISTS productos (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    codigo       VARCHAR(20)    NOT NULL UNIQUE,
    nombre       VARCHAR(100)   NOT NULL,
    categoria    VARCHAR(50),
    precio       DECIMAL(10,2)  NOT NULL,
    stock_actual INT            NOT NULL DEFAULT 0,
    stock_minimo INT            NOT NULL DEFAULT 5
);

CREATE TABLE IF NOT EXISTS clientes (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    nombre   VARCHAR(100) NOT NULL,
    dni      VARCHAR(8)   NOT NULL UNIQUE,
    telefono VARCHAR(15)
);

CREATE TABLE IF NOT EXISTS ventas (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT            NOT NULL,
    id_usuario INT            NOT NULL,
    fecha      DATE           NOT NULL,
    hora       TIME           NOT NULL,
    total      DECIMAL(10,2)  NOT NULL,
    FOREIGN KEY (id_cliente) REFERENCES clientes(id),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS detalle_venta (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    id_venta        INT            NOT NULL,
    id_producto     INT            NOT NULL,
    cantidad        INT            NOT NULL,
    precio_unitario DECIMAL(10,2)  NOT NULL,
    subtotal        DECIMAL(10,2)  NOT NULL,
    FOREIGN KEY (id_venta)    REFERENCES ventas(id),
    FOREIGN KEY (id_producto) REFERENCES productos(id)
);

-- ── Datos iniciales ──────────────────────────────────────────
INSERT INTO usuarios (usuario,contrasena,rol) VALUES
    ('admin',    'admin123', 'administrador'),
    ('vendedor1','vend123',  'vendedor');

INSERT INTO productos (codigo,nombre,categoria,precio,stock_actual,stock_minimo) VALUES
    ('LIB-001','Cuaderno universitario 100h','Útiles',  3.50, 50,10),
    ('LIB-002','Lapicero Pilot azul',         'Útiles',  1.20,  3, 5),
    ('LIB-003','Resaltador Stabilo',           'Útiles',  4.80, 20, 5),
    ('LIB-004','Calculadora científica Casio', 'Oficina',45.00,  8, 3),
    ('LIB-005','Papel bond A4 x500',           'Oficina',22.00,  2, 5);

INSERT INTO clientes (nombre,dni,telefono) VALUES
    ('Maria Garcia Lopez','45678912','987654321'),
    ('Carlos Perez Rios', '32145678','912345678');
