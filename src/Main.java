import dao.*;
import models.*;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 Demuestra en consola los siguientes Requerimientos Funcionales:
 
 RF-01 — Login con usuario y contraseña
 RF-02 — Registrar nuevo producto
 RF-05 — Buscar producto por código y por nombre
 RF-06 — Registrar cliente (incluye validación de DNI duplicado)
 RF-07 — Registrar venta (con verificación de stock)
 RF-08 — Actualizar stock automáticamente tras la venta
 RF-09 — Calcular total automáticamente
 RF-13 — Mostrar alertas de stock mínimo
 RF-10 — Reporte de ventas por rango de fechas
 RF-15 — Historial de ventas por cliente
 */
public class Main {

    public static void main(String[] args) {

        // ── Conexión centralizada ─────────────────────────────────────────────
        Connection conn = ConexionDB.getConexion();
        if (conn == null) {
            System.out.println("No se pudo establecer conexión. Verifique ConexionDB.java");
            return;
        }

        // ── Instanciar DAOs ───────────────────────────────────────────────────
        UsuarioDAO  usuarioDAO  = new UsuarioDAO(conn);
        ProductoDAO productoDAO = new ProductoDAO(conn);
        ClienteDAO  clienteDAO  = new ClienteDAO(conn);
        VentaDAO    ventaDAO    = new VentaDAO(conn);

        separador("DEMO SISTEMA — LIBRERÍA ISABEL");

        // ════════════════════════════════════════════════════════════════════
        // RF-01 — Login con credenciales correctas e incorrectas
        // ════════════════════════════════════════════════════════════════════
        separador("RF-01: Login");

        Usuario sesion = usuarioDAO.login("admin", "admin123");   // Correcto
        usuarioDAO.login("admin", "clave_incorrecta");            // Incorrecto (CA-01.2)
        usuarioDAO.login("inexistente", "1234");                  // Usuario inexistente

        // ════════════════════════════════════════════════════════════════════
        // RF-02 — Registrar nuevo producto
        // ════════════════════════════════════════════════════════════════════
        separador("RF-02: Registrar Producto");

        Producto nuevoProd = new Producto(0, "LIB-099", "Tijera escolar punta roma",
                                          "Útiles", 2.50, 30, 8);
        productoDAO.insertar(nuevoProd);

        // Intento de código duplicado (CA-02.2 adaptado a código único)
        Producto prodDuplicado = new Producto(0, "LIB-001", "Cuaderno A5", "Útiles", 2.00, 10, 3);
        productoDAO.insertar(prodDuplicado);

        // ════════════════════════════════════════════════════════════════════
        // RF-05 — Buscar producto por código y por nombre
        // ════════════════════════════════════════════════════════════════════
        separador("RF-05: Buscar Producto");

        productoDAO.buscarPorCodigo("LIB-001");       // Existe (CA-05.1)
        productoDAO.buscarPorCodigo("LIB-999");       // No existe (CA-05.2)
        productoDAO.buscarPorNombre("Cuaderno");      // Parcial — puede traer varios

        // ════════════════════════════════════════════════════════════════════
        // RF-06 — Registrar cliente (CA-06.1 y CA-06.2)
        // ════════════════════════════════════════════════════════════════════
        separador("RF-06: Registrar Cliente");

        Cliente nuevoCliente = new Cliente(0, "Ana Torres Vega", "76543210", "998877665");
        clienteDAO.insertar(nuevoCliente);                        // Éxito (CA-06.1)
        clienteDAO.insertar(nuevoCliente);                        // DNI duplicado (CA-06.2)

        clienteDAO.listar();

        // ════════════════════════════════════════════════════════════════════
        // RF-07 | RF-08 | RF-09 — Registrar venta con descuento de stock
        // ════════════════════════════════════════════════════════════════════
        separador("RF-07 + RF-08 + RF-09: Registrar Venta");

        // Venta exitosa: cliente id=1, usuario id=1, productos con stock suficiente
        String fechaHoy = LocalDate.now().toString();
        String horaAhora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        Venta venta1 = new Venta(1, 1, fechaHoy, horaAhora);

        // RF-09: el subtotal se calcula automáticamente en agregarDetalle()
        DetalleVenta d1 = new DetalleVenta(1, 3, 3.50);  // id_producto=1, qty=3, precio=3.50
        DetalleVenta d2 = new DetalleVenta(3, 2, 4.80);  // id_producto=3, qty=2, precio=4.80
        venta1.agregarDetalle(d1);
        venta1.agregarDetalle(d2);

        System.out.println("Total calculado automáticamente (RF-09): S/." +
                           String.format("%.2f", venta1.getTotal()));
        ventaDAO.registrarVenta(venta1);  // Éxito (CA-07.1)

        // Venta fallida: stock insuficiente para el producto id=2 (stock=3, pedido=10)
        separador("RF-07 CA-07.2: Venta con stock insuficiente");
        Venta venta2 = new Venta(1, 1, fechaHoy, horaAhora);
        venta2.agregarDetalle(new DetalleVenta(2, 10, 1.20)); // Lapicero: solo 3 en stock
        ventaDAO.registrarVenta(venta2);  // Debe rechazarse (CA-07.2)

        // ════════════════════════════════════════════════════════════════════
        // RF-11 — Inventario con marcador de alerta visual
        // ════════════════════════════════════════════════════════════════════
        separador("RF-11: Inventario Completo");
        productoDAO.listar();

        // ════════════════════════════════════════════════════════════════════
        // RF-13 — Alerta de stock mínimo
        // ════════════════════════════════════════════════════════════════════
        separador("RF-13: Alerta de Stock Mínimo");
        productoDAO.listarConAlertaStock();

        // ════════════════════════════════════════════════════════════════════
        // RF-10 — Reporte de ventas por rango de fechas
        // ════════════════════════════════════════════════════════════════════
        separador("RF-10: Reporte de Ventas por Fecha");
        ventaDAO.reporteVentasPorFecha("2025-01-01", fechaHoy);

        // ════════════════════════════════════════════════════════════════════
        // RF-15 — Historial de compras por cliente
        // ════════════════════════════════════════════════════════════════════
        separador("RF-15: Historial de Compras — Cliente ID=1");
        ventaDAO.historialPorCliente(1);

        separador("FIN DE LA DEMO");
    }

    // ── Utilidad para separar secciones visualmente ───────────────────────
    private static void separador(String titulo) {
        System.out.println("\n" + "═".repeat(65));
        System.out.println("  " + titulo);
        System.out.println("═".repeat(65));
    }
}
