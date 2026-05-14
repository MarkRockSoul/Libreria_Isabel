package dao;

import models.DetalleVenta;
import models.Producto;
import models.Venta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    private Connection conexion;

    public VentaDAO(Connection conn) { this.conexion = conn; }

    // RF-07 | RF-08 | RF-09 — Registrar venta completa con transacción
    public boolean registrarVenta(Venta venta) {
        ProductoDAO productoDAO = new ProductoDAO(conexion);

        for (DetalleVenta det : venta.getDetalles()) {
            Producto p = buscarProductoPorId(det.getIdProducto());
            if (p == null) {
                System.out.println("✗ Producto id=" + det.getIdProducto() + " no existe.");
                return false;
            }
            if (p.getStockActual() < det.getCantidad()) {
                System.out.println("✗ Stock insuficiente: " + p.getNombre()
                        + " | disponible=" + p.getStockActual()
                        + " | solicitado=" + det.getCantidad());
                return false;
            }
        }

        try {
            conexion.setAutoCommit(false);

            String qVenta = "INSERT INTO ventas (id_cliente,id_usuario,fecha,hora,total) VALUES(?,?,?,?,?)";
            int idVenta;
            try (PreparedStatement ps = conexion.prepareStatement(qVenta, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1,    venta.getIdCliente());
                ps.setInt(2,    venta.getIdUsuario());
                ps.setString(3, venta.getFecha());
                ps.setString(4, venta.getHora());
                ps.setDouble(5, venta.getTotal());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("Sin clave generada.");
                    idVenta = keys.getInt(1);
                    venta.setId(idVenta);
                }
            }

            String qDet = "INSERT INTO detalle_venta(id_venta,id_producto,cantidad,precio_unitario,subtotal) VALUES(?,?,?,?,?)";
            for (DetalleVenta det : venta.getDetalles()) {
                det.setIdVenta(idVenta);
                try (PreparedStatement ps = conexion.prepareStatement(qDet)) {
                    ps.setInt(1,    idVenta);
                    ps.setInt(2,    det.getIdProducto());
                    ps.setInt(3,    det.getCantidad());
                    ps.setDouble(4, det.getPrecioUnitario());
                    ps.setDouble(5, det.getSubtotal());
                    ps.executeUpdate();
                }
                Producto p = buscarProductoPorId(det.getIdProducto());
                productoDAO.actualizarStock(det.getIdProducto(), p.getStockActual() - det.getCantidad(), conexion);
            }

            conexion.commit();
            conexion.setAutoCommit(true);
            System.out.println("✓ Venta #" + idVenta + " registrada. Total S/." + String.format("%.2f", venta.getTotal()));
            return true;

        } catch (SQLException ex) {
            System.out.println("✗ Error en venta, revirtiendo: " + ex.getMessage());
            try { conexion.rollback(); conexion.setAutoCommit(true); } catch (SQLException ignored) {}
        }
        return false;
    }

    // RF-10 — Reporte de ventas por rango de fechas
    public List<String[]> reporteVentasPorFecha(String desde, String hasta) {
        List<String[]> filas = new ArrayList<>();
        String q = "SELECT v.id, v.fecha, v.hora, c.nombre AS cliente, u.usuario, v.total "
                 + "FROM ventas v "
                 + "JOIN clientes c ON v.id_cliente=c.id "
                 + "JOIN usuarios u ON v.id_usuario=u.id "
                 + "WHERE v.fecha BETWEEN ? AND ? ORDER BY v.fecha,v.hora";
        try (PreparedStatement ps = conexion.prepareStatement(q)) {
            ps.setString(1, desde);
            ps.setString(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filas.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("fecha"),
                        rs.getString("hora"),
                        rs.getString("cliente"),
                        rs.getString("usuario"),
                        String.format("S/.%.2f", rs.getDouble("total"))
                    });
                }
            }
        } catch (SQLException ex) { System.out.println("Error reporte: " + ex.getMessage()); }
        return filas;
    }

    // RF-15 — Historial de ventas por cliente
    public List<String[]> historialPorCliente(int idCliente) {
        List<String[]> filas = new ArrayList<>();
        String q = "SELECT v.id, v.fecha, v.hora, v.total, "
                 + "p.nombre AS producto, dv.cantidad, dv.precio_unitario, dv.subtotal "
                 + "FROM ventas v "
                 + "JOIN detalle_venta dv ON v.id=dv.id_venta "
                 + "JOIN productos p ON dv.id_producto=p.id "
                 + "WHERE v.id_cliente=? ORDER BY v.fecha DESC, v.id";
        try (PreparedStatement ps = conexion.prepareStatement(q)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filas.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("fecha"),
                        rs.getString("hora"),
                        rs.getString("producto"),
                        String.valueOf(rs.getInt("cantidad")),
                        String.format("S/.%.2f", rs.getDouble("precio_unitario")),
                        String.format("S/.%.2f", rs.getDouble("subtotal")),
                        String.format("S/.%.2f", rs.getDouble("total"))
                    });
                }
            }
        } catch (SQLException ex) { System.out.println("Error historial: " + ex.getMessage()); }
        return filas;
    }

    // Helper: buscar producto por id (para verificar stock)
    public Producto buscarProductoPorId(int id) {
        try (PreparedStatement ps = conexion.prepareStatement("SELECT * FROM productos WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Producto(rs.getInt("id"), rs.getString("codigo"),
                    rs.getString("nombre"), rs.getString("categoria"),
                    rs.getDouble("precio"), rs.getInt("stock_actual"), rs.getInt("stock_minimo"));
            }
        } catch (SQLException ex) { System.out.println("Error buscar producto: " + ex.getMessage()); }
        return null;
    }
}
