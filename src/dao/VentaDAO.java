package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.DetalleVenta;
import models.Venta;

public class VentaDAO {
    private Connection conexion;

    public VentaDAO(Connection conn) {
        this.conexion = conn;
    }

    /**
     * RF-07, RF-08, RF-09: Registra una venta completa con sus detalles
     * y actualiza el stock automáticamente
     */
    public boolean registrarVenta(Venta venta, List<DetalleVenta> detalles) {
        String queryVenta = "INSERT INTO ventas (fecha, total, id_cliente, id_usuario) VALUES (?, ?, ?, ?)";
        String queryDetalle = "INSERT INTO detalle_venta (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        String queryActualizarStock = "UPDATE productos SET stock = stock - ? WHERE id_producto = ?";
        
        try {
            // Deshabilitar autocommit para transacción
            conexion.setAutoCommit(false);
            
            // 1. Insertar la venta
            PreparedStatement psVenta = conexion.prepareStatement(queryVenta, Statement.RETURN_GENERATED_KEYS);
            psVenta.setTimestamp(1, new Timestamp(venta.getFecha().getTime()));
            psVenta.setDouble(2, venta.getTotal());
            psVenta.setInt(3, venta.getIdCliente());
            psVenta.setInt(4, venta.getIdUsuario());
            
            int filasVenta = psVenta.executeUpdate();
            
            if (filasVenta > 0) {
                ResultSet rs = psVenta.getGeneratedKeys();
                if (rs.next()) {
                    int idVenta = rs.getInt(1);
                    venta.setIdVenta(idVenta);
                    
                    // 2. Insertar los detalles y actualizar stock
                    PreparedStatement psDetalle = conexion.prepareStatement(queryDetalle);
                    PreparedStatement psStock = conexion.prepareStatement(queryActualizarStock);
                    
                    for (DetalleVenta detalle : detalles) {
                        // Insertar detalle
                        psDetalle.setInt(1, idVenta);
                        psDetalle.setInt(2, detalle.getIdProducto());
                        psDetalle.setInt(3, detalle.getCantidad());
                        psDetalle.setDouble(4, detalle.getPrecioUnitario());
                        psDetalle.setDouble(5, detalle.getSubtotal());
                        psDetalle.executeUpdate();
                        
                        // Actualizar stock
                        psStock.setInt(1, detalle.getCantidad());
                        psStock.setInt(2, detalle.getIdProducto());
                        psStock.executeUpdate();
                    }
                    
                    // Confirmar transacción
                    conexion.commit();
                    System.out.println("✓ Venta registrada exitosamente - ID: " + idVenta + " | Total: S/. " + String.format("%.2f", venta.getTotal()));
                    return true;
                }
            }
            
            conexion.rollback();
            return false;
            
        } catch (SQLException e) {
            try {
                conexion.rollback();
                System.err.println("✗ Error al registrar venta (rollback ejecutado): " + e.getMessage());
            } catch (SQLException ex) {
                System.err.println("✗ Error en rollback: " + ex.getMessage());
            }
            return false;
        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("✗ Error al restaurar autocommit: " + e.getMessage());
            }
        }
    }

    /**
     * RF-10: Lista ventas por fecha específica
     */
    public List<Venta> listarVentasPorFecha(Date fecha) {
        List<Venta> ventas = new ArrayList<>();
        String query = "SELECT * FROM ventas WHERE DATE(fecha) = ? ORDER BY fecha DESC";
        
        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setDate(1, new java.sql.Date(fecha.getTime()));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Venta v = new Venta(
                    rs.getInt("id_venta"),
                    rs.getTimestamp("fecha"),
                    rs.getDouble("total"),
                    rs.getInt("id_cliente"),
                    rs.getInt("id_usuario")
                );
                ventas.add(v);
            }
        } catch (SQLException e) {
            System.err.println("✗ Error al listar ventas por fecha: " + e.getMessage());
        }
        return ventas;
    }

    /**
     * RF-10: Obtiene ventas del día actual
     */
    public List<Venta> obtenerVentasDelDia() {
        return listarVentasPorFecha(new Date(System.currentTimeMillis()));
    }

    /**
     * RF-15: Obtiene el historial de compras de un cliente
     */
    public List<Venta> obtenerHistorialCliente(int idCliente) {
        List<Venta> ventas = new ArrayList<>();
        String query = "SELECT * FROM ventas WHERE id_cliente = ? ORDER BY fecha DESC";
        
        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Venta v = new Venta(
                    rs.getInt("id_venta"),
                    rs.getTimestamp("fecha"),
                    rs.getDouble("total"),
                    rs.getInt("id_cliente"),
                    rs.getInt("id_usuario")
                );
                ventas.add(v);
            }
        } catch (SQLException e) {
            System.err.println("✗ Error al obtener historial del cliente: " + e.getMessage());
        }
        return ventas;
    }

    /**
     * Obtiene los detalles de una venta específica
     */
    public List<DetalleVenta> obtenerDetallesVenta(int idVenta) {
        List<DetalleVenta> detalles = new ArrayList<>();
        String query = "SELECT * FROM detalle_venta WHERE id_venta = ?";
        
        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setInt(1, idVenta);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                DetalleVenta d = new DetalleVenta(
                    rs.getInt("id_detalle"),
                    rs.getInt("id_venta"),
                    rs.getInt("id_producto"),
                    rs.getInt("cantidad"),
                    rs.getDouble("precio_unitario")
                );
                detalles.add(d);
            }
        } catch (SQLException e) {
            System.err.println("✗ Error al obtener detalles de venta: " + e.getMessage());
        }
        return detalles;
    }
}