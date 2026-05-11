package dao;

import models.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// RF-02 | RF-03 | RF-04 | RF-05 | RF-08 | RF-11 | RF-13 — DAO de Producto
public class ProductoDAO {

    private Connection conexion;

    public ProductoDAO(Connection conn) {
        this.conexion = conn;
    }

    // ── RF-02: Registrar producto ─────────────────────────────────────────────
    public boolean insertar(Producto producto) {
        System.out.println("\n====== Registrar Producto ======");
        String query =
            "INSERT INTO productos (codigo, nombre, categoria, precio, stock_actual, stock_minimo) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setString(3, producto.getCategoria());
            ps.setDouble(4, producto.getPrecio());
            ps.setInt(5,    producto.getStockActual());
            ps.setInt(6,    producto.getStockMinimo());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("✓ Producto registrado: " + producto.getNombre());
                return true;
            }
        } catch (SQLIntegrityConstraintViolationException ex) {
            System.out.println("✗ El código '" + producto.getCodigo() + "' ya está en uso.");
        } catch (SQLException ex) {
            System.out.println("Error al registrar producto: " + ex.getMessage());
        }
        return false;
    }

    // ── RF-11: Listar todos los productos ────────────────────────────────────
    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        String query = "SELECT * FROM productos ORDER BY nombre";

        try (PreparedStatement ps = conexion.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            String fmt = "| %-5s | %-12s | %-35s | %-10s | %7s | %7s | %6s |%n";
            System.out.println("\nInventario de Productos — Librería Isabel");
            System.out.println("-".repeat(97));
            System.out.printf(fmt, "ID", "CÓDIGO", "NOMBRE", "CATEGORÍA", "PRECIO", "STOCK", "MÍNIMO");
            System.out.println("-".repeat(97));

            while (rs.next()) {
                Producto p = mapearProducto(rs);
                lista.add(p);
                String alerta = p.tieneAlertaStock() ? " ⚠" : "";
                System.out.printf(fmt,
                    p.getId(), p.getCodigo(), p.getNombre(), p.getCategoria(),
                    String.format("S/.%.2f", p.getPrecio()),
                    p.getStockActual() + alerta, p.getStockMinimo());
            }
            System.out.println("-".repeat(97));
            System.out.println("  ⚠ = stock en nivel de alerta (stock actual ≤ stock mínimo)");
        } catch (SQLException ex) {
            System.out.println("Error al listar productos: " + ex.getMessage());
        }
        return lista;
    }

    // ── RF-05: Buscar por código exacto ──────────────────────────────────────
    public Producto buscarPorCodigo(String codigo) {
        System.out.println("\n====== Buscar Producto por Código ======");
        String query = "SELECT * FROM productos WHERE codigo = ?";

        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Producto p = mapearProducto(rs);
                    System.out.println("✓ Encontrado: " + p.getNombre() + " | Stock: " + p.getStockActual());
                    return p;
                } else {
                    System.out.println("✗ No se encontraron productos con el código: " + codigo);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al buscar producto: " + ex.getMessage());
        }
        return null;
    }

    // ── RF-05: Buscar por nombre (parcial) ───────────────────────────────────
    public List<Producto> buscarPorNombre(String nombre) {
        System.out.println("\n====== Buscar Producto por Nombre ======");
        List<Producto> lista = new ArrayList<>();
        String query = "SELECT * FROM productos WHERE nombre LIKE ?";

        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearProducto(rs));
            }

            if (lista.isEmpty()) {
                System.out.println("✗ No se encontraron productos con el nombre: " + nombre);
            } else {
                System.out.println("✓ Se encontraron " + lista.size() + " resultado(s):");
                lista.forEach(p ->
                    System.out.println("   [" + p.getCodigo() + "] " + p.getNombre() +
                                       " — S/." + p.getPrecio() + " | Stock: " + p.getStockActual()));
            }
        } catch (SQLException ex) {
            System.out.println("Error al buscar por nombre: " + ex.getMessage());
        }
        return lista;
    }

    // ── RF-03: Actualizar producto ───────────────────────────────────────────
    public boolean actualizar(String codigoOriginal, Producto producto) {
        System.out.println("\n====== Actualizar Producto ======");
        String query =
            "UPDATE productos SET codigo=?, nombre=?, categoria=?, precio=?, " +
            "stock_actual=?, stock_minimo=? WHERE codigo=?";

        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setString(1, producto.getCodigo());
            ps.setString(2, producto.getNombre());
            ps.setString(3, producto.getCategoria());
            ps.setDouble(4, producto.getPrecio());
            ps.setInt(5,    producto.getStockActual());
            ps.setInt(6,    producto.getStockMinimo());
            ps.setString(7, codigoOriginal);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("✓ Producto actualizado correctamente.");
                return true;
            } else {
                System.out.println("✗ Código no encontrado: " + codigoOriginal);
            }
        } catch (SQLIntegrityConstraintViolationException ex) {
            System.out.println("✗ El código '" + producto.getCodigo() + "' ya está en uso.");
        } catch (SQLException ex) {
            System.out.println("Error al actualizar producto: " + ex.getMessage());
        }
        return false;
    }

    // ── RF-04: Eliminar producto ─────────────────────────────────────────────
    public boolean eliminar(String codigo) {
        System.out.println("\n====== Eliminar Producto ======");
        // Verificar si el producto tiene ventas asociadas (RF-04, CA-04.2)
        String queryCheck = "SELECT COUNT(*) FROM detalle_venta dv " +
                            "JOIN productos p ON dv.id_producto = p.id WHERE p.codigo = ?";

        try (PreparedStatement psCheck = conexion.prepareStatement(queryCheck)) {
            psCheck.setString(1, codigo);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("✗ No se puede eliminar: el producto tiene ventas asociadas.");
                    return false;
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al verificar ventas: " + ex.getMessage());
            return false;
        }

        String query = "DELETE FROM productos WHERE codigo = ?";
        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setString(1, codigo);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("✓ Producto eliminado correctamente.");
                return true;
            } else {
                System.out.println("✗ Código no encontrado: " + codigo);
            }
        } catch (SQLException ex) {
            System.out.println("Error al eliminar producto: " + ex.getMessage());
        }
        return false;
    }

    // ── RF-08: Actualizar stock después de una venta (uso interno) ──────────
    public boolean actualizarStock(int idProducto, int nuevaCantidad, Connection conn) {
        String query = "UPDATE productos SET stock_actual = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, nuevaCantidad);
            ps.setInt(2, idProducto);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Error al actualizar stock: " + ex.getMessage());
        }
        return false;
    }

    // ── RF-13: Listar productos con alerta de stock mínimo ──────────────────
    public List<Producto> listarConAlertaStock() {
        System.out.println("\n====== ⚠ Productos con Alerta de Stock ======");
        List<Producto> lista = new ArrayList<>();
        String query = "SELECT * FROM productos WHERE stock_actual <= stock_minimo ORDER BY stock_actual";

        try (PreparedStatement ps = conexion.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapearProducto(rs));

            if (lista.isEmpty()) {
                System.out.println("✓ Todos los productos tienen stock suficiente.");
            } else {
                System.out.println("  Se encontraron " + lista.size() + " producto(s) en alerta:");
                String fmt = "  | %-12s | %-35s | %10s | %10s |%n";
                System.out.printf(fmt, "CÓDIGO", "NOMBRE", "STOCK ACT.", "STOCK MÍN.");
                System.out.println("  " + "-".repeat(76));
                lista.forEach(p ->
                    System.out.printf(fmt, p.getCodigo(), p.getNombre(),
                                      p.getStockActual(), p.getStockMinimo()));
            }
        } catch (SQLException ex) {
            System.out.println("Error al listar alertas de stock: " + ex.getMessage());
        }
        return lista;
    }

    // ── Helper: mapea un ResultSet a un Producto ────────────────────────────
    private Producto mapearProducto(ResultSet rs) throws SQLException {
        return new Producto(
            rs.getInt("id"),
            rs.getString("codigo"),
            rs.getString("nombre"),
            rs.getString("categoria"),
            rs.getDouble("precio"),
            rs.getInt("stock_actual"),
            rs.getInt("stock_minimo")
        );
    }
}
