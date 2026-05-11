package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String URL      = "jdbc:mysql://localhost:3306/libreria_isabel";
    private static final String USUARIO  = "root";
    private static final String PASSWORD = "Mysql1589";

    // Retorna una conexión activa a la base de datos
    public static Connection getConexion() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (SQLException ex) {
            System.out.println("ERROR: No se pudo conectar a la base de datos.");
            System.out.println(ex.getMessage());
        }
        return conn;
    }
}
