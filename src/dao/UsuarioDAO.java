package dao;

import models.Usuario;
import java.sql.*;

// RF-01 | RF-12 — DAO de Usuario
public class UsuarioDAO {

    private Connection conexion;

    public UsuarioDAO(Connection conn) {
        this.conexion = conn;
    }

    // ── RF-01: Login con usuario y contraseña ────────────────────────────────
    public Usuario login(String usuario, String contrasena) {
        System.out.println("\n====== Login ======");
        Usuario usuarioEncontrado = null;
        String query = "SELECT * FROM usuarios WHERE usuario = ? AND contrasena = ?";

        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setString(1, usuario);
            ps.setString(2, contrasena);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuarioEncontrado = new Usuario(
                        rs.getInt("id"),
                        rs.getString("usuario"),
                        rs.getString("contrasena"),
                        rs.getString("rol")
                    );
                    System.out.println("✓ Acceso concedido. Bienvenido, " + usuarioEncontrado.getUsuario() +
                                       " [" + usuarioEncontrado.getRol() + "]");
                } else {
                    System.out.println("✗ Credenciales incorrectas. Acceso denegado.");
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al intentar iniciar sesión: " + ex.getMessage());
        }
        return usuarioEncontrado;
    }

    // ── Insertar usuario ─────────────────────────────────────────────────────
    public boolean insertar(Usuario usuario) {
        String query = "INSERT INTO usuarios (usuario, contrasena, rol) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setString(1, usuario.getUsuario());
            ps.setString(2, usuario.getContrasena());
            ps.setString(3, usuario.getRol());
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException ex) {
            System.out.println("Error al insertar usuario: " + ex.getMessage());
        }
        return false;
    }
}
