package controllers;

import dao.ConexionDB;
import dao.ClienteDAO;
import dao.VentaDAO;
import models.Cliente;
import models.DetalleVenta;
import models.Producto;
import models.Venta;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VentaController {

    private VentaDAO   ventaDAO;
    private ClienteDAO clienteDAO;

    public VentaController() {
        Connection conn = ConexionDB.getConexion();
        this.ventaDAO   = new VentaDAO(conn);
        this.clienteDAO = new ClienteDAO(conn);
    }

    // RF-06 — Registrar cliente
    public String registrarCliente(String nombre, String dni, String telefono) {
        if (nombre.isBlank() || dni.isBlank())
            return "ERROR: Nombre y DNI son obligatorios.";
        if (dni.trim().length() != 8)
            return "ERROR: El DNI debe tener exactamente 8 dígitos.";
        try {
            Long.parseLong(dni.trim());
        } catch (NumberFormatException e) {
            return "ERROR: El DNI debe contener solo dígitos.";
        }
        Cliente c = new Cliente(0, nombre.trim(), dni.trim(), telefono.trim());
        return clienteDAO.insertar(c)
            ? "OK: Cliente registrado."
            : "ERROR: El DNI '" + dni + "' ya está registrado.";
    }

    // RF-06 — Buscar cliente por DNI
    public Cliente buscarClientePorDni(String dni) {
        return clienteDAO.buscarPorDni(dni.trim());
    }

    public List<Cliente> listarClientes() {
        return clienteDAO.listar();
    }

    // RF-07 | RF-08 | RF-09 — Registrar venta completa
    public String registrarVenta(int idCliente, int idUsuario, List<DetalleVenta> detalles) {
        if (detalles == null || detalles.isEmpty())
            return "ERROR: Agregue al menos un producto al carrito.";

        String fecha = LocalDate.now().toString();
        String hora  = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        Venta venta = new Venta(idCliente, idUsuario, fecha, hora);
        for (DetalleVenta d : detalles) venta.agregarDetalle(d);

        return ventaDAO.registrarVenta(venta)
            ? "OK: Venta #" + venta.getId() + " registrada. Total: S/." + String.format("%.2f", venta.getTotal())
            : "ERROR: No se pudo registrar la venta (verifique el stock).";
    }

    // RF-07 — Verificar stock antes de agregar al carrito
    public String verificarStock(int idProducto, int cantidadSolicitada) {
        Producto p = ventaDAO.buscarProductoPorId(idProducto);
        if (p == null) return "ERROR: Producto no encontrado.";
        if (p.getStockActual() < cantidadSolicitada)
            return "ERROR: Stock insuficiente. Disponible: " + p.getStockActual();
        return "OK";
    }

    // RF-10 — Reporte de ventas por fecha
    public List<String[]> reporteVentasPorFecha(String desde, String hasta) {
        return ventaDAO.reporteVentasPorFecha(desde, hasta);
    }

    // RF-15 — Historial por cliente
    public List<String[]> historialPorCliente(int idCliente) {
        return ventaDAO.historialPorCliente(idCliente);
    }
}
