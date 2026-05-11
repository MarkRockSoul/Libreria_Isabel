package dao;

import models.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// RF-06 | RF-15 — DAO de Cliente
public class ClienteDAO {

    private Connection conexion;

    public ClienteDAO(Connection conn) {
        this.conexion = conn;
    }

    // ── RF-06: Registrar cliente ─────────────────────────────────────────────
    public boolean insertar(Cliente cliente) {
        System.out.println("\n====== Registrar Cliente ======");
        String query = "INSERT INTO clientes (nombre, dni, telefono) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getDni());
            ps.setString(3, cliente.getTelefono());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("✓ Cliente registrado: " + cliente.getNombre() + " (DNI: " + cliente.getDni() + ")");
                return true;
            }
        } catch (SQLIntegrityConstraintViolationException ex) {
            System.out.println("✗ El DNI '" + cliente.getDni() + "' ya está registrado.");
        } catch (SQLException ex) {
            System.out.println("Error al registrar cliente: " + ex.getMessage());
        }
        return false;
    }

    // ── Listar todos los clientes ────────────────────────────────────────────
    public List<Cliente> listar() {
        List<Cliente> lista = new ArrayList<>();
        String query = "SELECT * FROM clientes ORDER BY nombre";

        try (PreparedStatement ps = conexion.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            String fmt = "| %-4s | %-30s | %-8s | %-15s |%n";
            System.out.println("\nClientes Registrados");
            System.out.println("-".repeat(67));
            System.out.printf(fmt, "ID", "NOMBRE", "DNI", "TELÉFONO");
            System.out.println("-".repeat(67));

            while (rs.next()) {
                Cliente c = mapearCliente(rs);
                lista.add(c);
                System.out.printf(fmt, c.getId(), c.getNombre(), c.getDni(), c.getTelefono());
            }
            System.out.println("-".repeat(67));
        } catch (SQLException ex) {
            System.out.println("Error al listar clientes: " + ex.getMessage());
        }
        return lista;
    }

    // ── RF-06: Buscar cliente por DNI ────────────────────────────────────────
    public Cliente buscarPorDni(String dni) {
        String query = "SELECT * FROM clientes WHERE dni = ?";
        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapearCliente(rs);
            }
        } catch (SQLException ex) {
            System.out.println("Error al buscar cliente: " + ex.getMessage());
        }
        return null;
    }

    // ── Helper: mapea ResultSet a Cliente ───────────────────────────────────
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        return new Cliente(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("dni"),
            rs.getString("telefono")
        );
    }
}
