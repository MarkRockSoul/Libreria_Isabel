# Sistema Librería Isabel — Instrucciones de ejecución
## Requisitos
- Java 17 o superior
- MySQL 8.x activo en localhost:3306
- mysql-connector-j-8.0.33.jar (colocar en carpeta `lib/`)

## Paso 1 — Base de datos
Abrir MySQL Workbench (o consola) y ejecutar:
```
libreria_isabel.sql
```

## Paso 2 — Configurar conexión
Editar `src/dao/ConexionDB.java`:
```java
private static final String USUARIO  = "root";   // su usuario MySQL
private static final String PASSWORD = "root";   // su contraseña MySQL
```

## Paso 3 — Compilar (desde la raíz del proyecto)
**Windows:**
```
javac -cp ".;lib/mysql-connector-j-8.0.33.jar" src/models/*.java src/dao/*.java src/controllers/*.java src/views/*.java src/Main.java -d bin
```
**Linux/Mac:**
```
javac -cp ".:lib/mysql-connector-j-8.0.33.jar" src/models/*.java src/dao/*.java src/controllers/*.java src/views/*.java src/Main.java -d bin
```

## Paso 4 — Ejecutar
**Windows:**
```
java -cp ".;lib/mysql-connector-j-8.0.33.jar;bin" Main
```
**Linux/Mac:**
```
java -cp ".:lib/mysql-connector-j-8.0.33.jar:bin" Main
```

## Estructura del proyecto
```
libreria_isabel/
├── lib/
│   └── mysql-connector-j-8.0.33.jar
├── src/
│   ├── models/        (Usuario, Producto, Cliente, DetalleVenta, Venta)
│   ├── dao/           (ConexionDB, UsuarioDAO, ProductoDAO, ClienteDAO, VentaDAO)
│   ├── controllers/   (ProductoController, VentaController)
│   ├── views/         (LoginView, MainMenuView, ProductosView, VentaView, ReportesView)
│   └── Main.java
├── bin/               (clases compiladas — se genera automáticamente)
├── libreria_isabel.sql
└── README.md
```

## Credenciales de prueba
| Usuario    | Contraseña | Rol            |
|------------|-----------|----------------|
| admin      | admin123  | administrador  |
| vendedor1  | vend123   | vendedor       |
