package dao;

import models.DetalleVenta;
import models.Producto;
import models.Venta;
import java.sql.*;

// RF-07 | RF-08 | RF-09 | RF-10 | RF-15 — DAO de Venta
public class VentaDAO {

    private Connection conexion;

    public VentaDAO(Connection conn) {
        this.conexion = conn;
    }

    // ── RF-07 | RF-08 | RF-09: Registrar venta completa con transacción ─────
    public boolean registrarVenta(Venta venta) {
        System.out.println("\n====== Registrar Venta ======");

        // Paso 1: Verificar stock suficiente para todos los productos (RF-07, CA-07.2)
        ProductoDAO productoDAO = new ProductoDAO(conexion);
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto p = buscarProductoPorId(detalle.getIdProducto());
            if (p == null) {
                System.out.println("✗ Producto con id=" + detalle.getIdProducto() + " no existe.");
                return false;
            }
            if (p.getStockActual() < detalle.getCantidad()) {
                System.out.println("✗ Stock insuficiente para '" + p.getNombre() +
                                   "'. Disponible: " + p.getStockActual() +
                                   " | Solicitado: " + detalle.getCantidad());
                return false;
            }
        }

        // Paso 2: Usar transacción para garantizar atomicidad
        try {
            conexion.setAutoCommit(false);

            // Insertar cabecera de venta
            String queryVenta =
                "INSERT INTO ventas (id_cliente, fecha, hora, total) VALUES (?, ?, ?, ?)";
            int idVentaGenerado;

            try (PreparedStatement ps = conexion.prepareStatement(
                    queryVenta, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1,    venta.getIdCliente());
                ps.setString(2, venta.getFecha());
                ps.setString(3, venta.getHora());
                ps.setDouble(4, venta.getTotal());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("No se generó id de venta.");
                    idVentaGenerado = keys.getInt(1);
                    venta.setId(idVentaGenerado);
                }
            }

            // Insertar cada detalle y descontar stock (RF-08)
            String queryDetalle =
                "INSERT INTO detalle_venta (id_venta, id_producto, cantidad, precio_unitario, subtotal) " +
                "VALUES (?, ?, ?, ?, ?)";

            for (DetalleVenta detalle : venta.getDetalles()) {
                detalle.setIdVenta(idVentaGenerado);

                try (PreparedStatement ps = conexion.prepareStatement(queryDetalle)) {
                    ps.setInt(1,    idVentaGenerado);
                    ps.setInt(2,    detalle.getIdProducto());
                    ps.setInt(3,    detalle.getCantidad());
                    ps.setDouble(4, detalle.getPrecioUnitario());
                    ps.setDouble(5, detalle.getSubtotal());
                    ps.executeUpdate();
                }

                // RF-08: Descontar stock automáticamente
                Producto p = buscarProductoPorId(detalle.getIdProducto());
                int nuevoStock = p.getStockActual() - detalle.getCantidad();
                productoDAO.actualizarStock(detalle.getIdProducto(), nuevoStock, conexion);
            }

            conexion.commit();
            conexion.setAutoCommit(true);
            System.out.println("✓ Venta registrada. ID=" + idVentaGenerado +
                               " | Total: S/." + String.format("%.2f", venta.getTotal()));
            return true;

        } catch (SQLException ex) {
            System.out.println("✗ Error en la venta. Revirtiendo cambios...");
            System.out.println(ex.getMessage());
            try { conexion.rollback(); conexion.setAutoCommit(true); }
            catch (SQLException e) { e.printStackTrace(); }
        }
        return false;
    }

    // ── RF-10: Reporte de ventas por fecha ───────────────────────────────────
    public void reporteVentasPorFecha(String desde, String hasta) {
        System.out.println("\n====== Reporte de Ventas: " + desde + " al " + hasta + " ======");
        String query =
            "SELECT v.id, v.fecha, v.hora, c.nombre AS cliente, v.total " +
            "FROM ventas v " +
            "JOIN clientes c ON v.id_cliente = c.id " +
            "WHERE v.fecha BETWEEN ? AND ? " +
            "ORDER BY v.fecha, v.hora";

        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setString(1, desde);
            ps.setString(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {

                String fmt = "| %-5s | %-10s | %-8s | %-25s | %-10s |%n";
                System.out.println("-".repeat(68));
                System.out.printf(fmt, "ID", "FECHA", "HORA", "CLIENTE", "TOTAL");
                System.out.println("-".repeat(68));

                int totalTransacciones = 0;
                double totalGeneral = 0;

                while (rs.next()) {
                    double total = rs.getDouble("total");
                    totalGeneral += total;
                    totalTransacciones++;
                    System.out.printf(fmt,
                        rs.getInt("id"),
                        rs.getString("fecha"),
                        rs.getString("hora"),
                        rs.getString("cliente"),
                        "S/." + String.format("%.2f", total));
                }
                System.out.println("-".repeat(68));

                if (totalTransacciones == 0) {
                    System.out.println("  No hay ventas en el período seleccionado.");
                } else {
                    System.out.println("  Transacciones: " + totalTransacciones +
                                       "  |  Total recaudado: S/." + String.format("%.2f", totalGeneral));
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al generar reporte: " + ex.getMessage());
        }
    }

    // ── RF-15: Historial de ventas por cliente ───────────────────────────────
    public void historialPorCliente(int idCliente) {
        System.out.println("\n====== Historial de Compras del Cliente ID=" + idCliente + " ======");
        String query =
            "SELECT v.id, v.fecha, v.hora, v.total, p.nombre AS producto, " +
            "       dv.cantidad, dv.precio_unitario, dv.subtotal " +
            "FROM ventas v " +
            "JOIN detalle_venta dv ON v.id = dv.id_venta " +
            "JOIN productos p ON dv.id_producto = p.id " +
            "WHERE v.id_cliente = ? ORDER BY v.fecha DESC, v.id";

        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {

                boolean hayResultados = false;
                int idVentaActual = -1;

                while (rs.next()) {
                    hayResultados = true;
                    int idVenta = rs.getInt("id");
                    if (idVenta != idVentaActual) {
                        idVentaActual = idVenta;
                        System.out.println("\n  Venta #" + idVenta + " — " +
                                           rs.getString("fecha") + " " + rs.getString("hora") +
                                           " | Total: S/." + String.format("%.2f", rs.getDouble("total")));
                        System.out.println("  " + "-".repeat(65));
                    }
                    System.out.printf("    %-35s x%2d  S/.%-8.2f = S/.%.2f%n",
                        rs.getString("producto"),
                        rs.getInt("cantidad"),
                        rs.getDouble("precio_unitario"),
                        rs.getDouble("subtotal"));
                }

                if (!hayResultados) {
                    System.out.println("  El cliente no tiene compras registradas.");
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error al consultar historial: " + ex.getMessage());
        }
    }

    // ── Helper: buscar producto por id (para verificar stock) ───────────────
    private Producto buscarProductoPorId(int id) {
        String query = "SELECT * FROM productos WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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
        } catch (SQLException ex) {
            System.out.println("Error al buscar producto por id: " + ex.getMessage());
        }
        return null;
    }
}
