package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    private static final String URL = "jdbc:mysql://localhost:3306/libreria_isabel";
    private static final String USER = "root";
    private static final String PASSWORD = "Mysql1589";
    private static Connection conexion = null;

    /**
     * Obtiene la conexión a la base de datos (Singleton)
     */
    public static Connection getConexion() {
        if (conexion == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexion = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✓ Conexión exitosa a la base de datos: " + conexion.getCatalog());
            } catch (ClassNotFoundException e) {
                System.err.println("✗ Error: Driver MySQL no encontrado.");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("✗ Error al conectar con la base de datos: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return conexion;
    }

    /**
     * Cierra la conexión a la base de datos
     */
    public static void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                conexion = null;
                System.out.println("✓ Conexión cerrada correctamente.");
            } catch (SQLException e) {
                System.err.println("✗ Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}