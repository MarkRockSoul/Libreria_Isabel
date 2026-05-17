package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Cliente;

public class ClienteDAO {
    private Connection conexion;

    public ClienteDAO(Connection conn) {
        this.conexion = conn;
    }

    /**
     * RF-06: Inserta un nuevo cliente
     */
    public boolean insertar(Cliente cliente) {
        String query = "INSERT INTO clientes (dni, nombre, telefono) VALUES (?, ?, ?)";
        
        try (PreparedStatement ps = conexion.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.getDni());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getTelefono());
            
            int filas = ps.executeUpdate();
            
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    cliente.setIdCliente(rs.getInt(1));
                }
                System.out.println("✓ Cliente registrado: " + cliente.getNombre() + " [DNI: " + cliente.getDni() + "]");
                return true;
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry
                System.err.println("✗ El DNI ya está registrado: " + cliente.getDni());
            } else {
                System.err.println("✗ Error al insertar cliente: " + e.getMessage());
            }
        }
        return false;
    }

    /**
     * RF-05: Busca un cliente por DNI
     */
    public Cliente buscarPorDNI(String dni) {
        String query = "SELECT * FROM clientes WHERE dni = ?";
        
        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new Cliente(
                    rs.getInt("id_cliente"),
                    rs.getString("dni"),
                    rs.getString("nombre"),
                    rs.getString("telefono")
                );
            } else {
                System.out.println("✗ No se encontró cliente con DNI: " + dni);
            }
        } catch (SQLException e) {
            System.err.println("✗ Error al buscar cliente: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos los clientes
     */
    public List<Cliente> listar() {
        List<Cliente> clientes = new ArrayList<>();
        String query = "SELECT * FROM clientes ORDER BY nombre";
        
        try (PreparedStatement ps = conexion.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Cliente c = new Cliente(
                    rs.getInt("id_cliente"),
                    rs.getString("dni"),
                    rs.getString("nombre"),
                    rs.getString("telefono")
                );
                clientes.add(c);
            }
        } catch (SQLException e) {
            System.err.println("✗ Error al listar clientes: " + e.getMessage());
        }
        return clientes;
    }
}