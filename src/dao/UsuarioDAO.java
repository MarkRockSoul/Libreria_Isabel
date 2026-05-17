package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Usuario;

public class UsuarioDAO {
    private Connection conexion;

    public UsuarioDAO(Connection conn) {
        this.conexion = conn;
    }

    /**
     * RF-01: Valida las credenciales de un usuario
     */
    public Usuario validarLogin(String nombreUsuario, String password) {
        String query = "SELECT * FROM usuarios WHERE nombre_usuario = ? AND password = ? AND activo = TRUE";
        
        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, password);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Usuario usuario = new Usuario(
                    rs.getInt("id_usuario"),
                    rs.getString("nombre_usuario"),
                    rs.getString("password"),
                    rs.getString("rol"),
                    rs.getString("nombres"),
                    rs.getString("apellidos")
                );
                System.out.println("✓ Login exitoso: " + usuario.getNombreCompleto() + " [" + usuario.getRol() + "]");
                return usuario;
            } else {
                System.out.println("✗ Credenciales incorrectas para el usuario: " + nombreUsuario);
                return null;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error al validar login: " + e.getMessage());
            return null;
        }
    }

    /**
     * Inserta un nuevo usuario en la base de datos
     */
    public boolean insertarUsuario(Usuario usuario) {
        String query = "INSERT INTO usuarios (nombre_usuario, password, rol, nombres, apellidos) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setString(1, usuario.getNombreUsuario());
            ps.setString(2, usuario.getPassword());
            ps.setString(3, usuario.getRol());
            ps.setString(4, usuario.getNombres());
            ps.setString(5, usuario.getApellidos());
            
            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("✓ Usuario registrado: " + usuario.getNombreUsuario());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error al insertar usuario: " + e.getMessage());
        }
        return false;
    }

    /**
     * Lista todos los usuarios activos
     */
    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String query = "SELECT * FROM usuarios WHERE activo = TRUE";
        
        try (PreparedStatement ps = conexion.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Usuario u = new Usuario(
                    rs.getInt("id_usuario"),
                    rs.getString("nombre_usuario"),
                    rs.getString("password"),
                    rs.getString("rol"),
                    rs.getString("nombres"),
                    rs.getString("apellidos")
                );
                usuarios.add(u);
            }
        } catch (SQLException e) {
            System.err.println("✗ Error al listar usuarios: " + e.getMessage());
        }
        return usuarios;
    }
}