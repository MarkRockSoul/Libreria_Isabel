import dao.*;
import models.*;
import views.LoginView;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Punto de entrada del sistema Librería Isabel.
 *
 * ① Ejecuta la demo en consola (RF-01, RF-02, RF-05, RF-06, RF-07/08/09, RF-13, RF-10)
 * ② Lanza la interfaz gráfica Swing
 */
public class Main {

    public static void main(String[] args) {

        // ═══════════════════════════════════════════════════════════════
        // DEMO EN CONSOLA — demuestra los RF con datos reales
        // ═══════════════════════════════════════════════════════════════
        Connection conn = ConexionDB.getConexion();
        if (conn == null) {
            System.out.println("Sin conexión a la BD. Verifique ConexionDB.java y que MySQL esté activo.");
            return;
        }

        UsuarioDAO  usuarioDAO  = new UsuarioDAO(conn);
        ProductoDAO productoDAO = new ProductoDAO(conn);
        ClienteDAO  clienteDAO  = new ClienteDAO(conn);
        VentaDAO    ventaDAO    = new VentaDAO(conn);

        sep("DEMO — SISTEMA LIBRERÍA ISABEL");

        // ── RF-01: Login ─────────────────────────────────────────────────
        sep("RF-01 | Login");
        Usuario sesion = usuarioDAO.login("admin", "admin123");       // correcto
        usuarioDAO.login("admin", "clave_incorrecta");                // incorrecto (CA-01.2)

        // ── RF-02: Registrar producto ────────────────────────────────────
        sep("RF-02 | Registrar Producto");
        productoDAO.insertar(new Producto(0,"LIB-099","Tijera escolar","Útiles",2.50,30,8));
        productoDAO.insertar(new Producto(0,"LIB-001","Duplicado","Útiles",1.0,5,2)); // código duplicado

        // ── RF-05: Buscar producto ────────────────────────────────────────
        sep("RF-05 | Buscar Producto");
        productoDAO.buscarPorCodigo("LIB-001");    // existe
        productoDAO.buscarPorCodigo("LIB-999");    // no existe (CA-05.2)
        productoDAO.buscarPorNombre("Cuaderno");   // búsqueda parcial

        // ── RF-06: Registrar cliente ──────────────────────────────────────
        sep("RF-06 | Registrar Cliente");
        clienteDAO.insertar(new Cliente(0,"Ana Torres Vega","76543210","998877665")); // nuevo
        clienteDAO.insertar(new Cliente(0,"Ana Torres Vega","76543210","998877665")); // DNI duplicado (CA-06.2)
        clienteDAO.listar();

        // ── RF-07 | RF-08 | RF-09: Registrar venta exitosa ───────────────
        sep("RF-07/08/09 | Registrar Venta (stock suficiente)");
        String fecha = LocalDate.now().toString();
        String hora  = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        // id_usuario = 1 (admin), id_cliente = 1 (Maria Garcia)
        Venta venta1 = new Venta(1, sesion != null ? sesion.getId() : 1, fecha, hora);
        venta1.agregarDetalle(new DetalleVenta(1, 3, 3.50)); // RF-09: total calculado automáticamente
        venta1.agregarDetalle(new DetalleVenta(3, 2, 4.80));
        System.out.println("Total calculado (RF-09): S/." + String.format("%.2f", venta1.getTotal()));
        ventaDAO.registrarVenta(venta1); // registra y descuenta stock (RF-08)

        // ── RF-07 CA-07.2: Venta con stock insuficiente ───────────────────
        sep("RF-07 CA-07.2 | Venta con stock insuficiente");
        Venta venta2 = new Venta(1, 1, fecha, hora);
        venta2.agregarDetalle(new DetalleVenta(2, 50, 1.20)); // Lapicero solo tiene 3 en stock
        ventaDAO.registrarVenta(venta2); // debe rechazarse

        // ── RF-11 + RF-13: Inventario y alertas de stock ──────────────────
        sep("RF-11 | Inventario completo");
        productoDAO.listar();
        sep("RF-13 | Alertas de stock mínimo");
        productoDAO.listarConAlertaStock();

        // ── RF-10: Reporte de ventas por fecha ────────────────────────────
        sep("RF-10 | Reporte de Ventas");
        var filas = ventaDAO.reporteVentasPorFecha("2025-01-01", fecha);
        System.out.printf("%-5s %-12s %-8s %-25s %-12s %-10s%n",
            "ID","FECHA","HORA","CLIENTE","VENDEDOR","TOTAL");
        System.out.println("-".repeat(76));
        filas.forEach(f -> System.out.printf("%-5s %-12s %-8s %-25s %-12s %-10s%n",
            f[0],f[1],f[2],f[3],f[4],f[5]));

        // ── RF-15: Historial por cliente ──────────────────────────────────
        sep("RF-15 | Historial del Cliente ID=1");
        var hist = ventaDAO.historialPorCliente(1);
        hist.forEach(f -> System.out.printf("Venta#%-3s %s  %-30s x%s  %s  %s%n",
            f[0],f[1],f[3],f[4],f[5],f[6]));

        sep("FIN DEMO — Iniciando interfaz gráfica...");

        // ═══════════════════════════════════════════════════════════════
        // LANZAR INTERFAZ GRÁFICA SWING
        // ═══════════════════════════════════════════════════════════════
        javax.swing.SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }

    private static void sep(String titulo) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("  " + titulo);
        System.out.println("═".repeat(60));
    }
}
