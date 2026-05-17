package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import dao.ConexionDB;
import dao.ProductoDAO;
import dao.VentaDAO;
import models.Producto;
import models.Venta;
import views.buttons.BotonEstilizado;
import views.styles.TableStyler;

import java.awt.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.List;

public class VentanaReportes extends JFrame {
    private JTable tablaReporte;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotalReporte;
    private VentaDAO ventaDAO;
    private ProductoDAO productoDAO;
    
    public VentanaReportes(JFrame parent) {
        Connection conn = ConexionDB.getConexion();
        ventaDAO = new VentaDAO(conn);
        productoDAO = new ProductoDAO(conn);
        
        configurarVentana();
        inicializarComponentes();
    }
    
    private void configurarVentana() {
        setTitle("Reportes del Sistema");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }
    
    private void inicializarComponentes() {
        // Panel superior
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        panelTitulo.setBackground(new Color(41, 128, 185)); // Azul fuerte del botón de reportes
        panelTitulo.setPreferredSize(new Dimension(0, 56));

        JLabel lblIcono = new JLabel("📊");
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        lblIcono.setForeground(Color.WHITE);

        JLabel lblTitulo = new JLabel("Reportes y Estadísticas");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);

        panelTitulo.add(lblIcono);
        panelTitulo.add(lblTitulo);
        
        // Panel de botones
        JPanel panelBotones = crearPanelBotones();
        
        // Panel de tabla
        JPanel panelTabla = crearPanelTabla();
        
        // Panel inferior con totales
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        panelInferior.setBackground(Color.WHITE);
        lblTotalReporte = new JLabel("");
        lblTotalReporte.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotalReporte.setForeground(new Color(46, 204, 113));
        panelInferior.add(lblTotalReporte);
        
        add(panelTitulo, BorderLayout.NORTH);
        add(panelBotones, BorderLayout.WEST);
        add(panelTabla, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(240, 0));
        
        JButton btnVentasHoy = crearBoton("Ventas del Día", new Color(52, 152, 219), e -> mostrarVentasDelDia());
        JButton btnStockDisponible = crearBoton("Stock Disponible", new Color(46, 204, 113), e -> mostrarStockDisponible());
        JButton btnProductosBajoStock = crearBoton("Productos Bajo Stock", new Color(231, 76, 60), e -> mostrarProductosBajoStock());
        
        panel.add(btnVentasHoy);
        panel.add(btnStockDisponible);
        panel.add(btnProductosBajoStock);
        
        return panel;
    }
    
    private JButton crearBoton(String texto, Color color, java.awt.event.ActionListener listener) {
        JButton boton = BotonEstilizado.crearBotonEstilizado(texto, color, new Dimension(180, 35));
        boton.addActionListener(listener);
        return boton;
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaReporte = new JTable(modeloTabla);
        
        JScrollPane scrollPane = new JScrollPane(tablaReporte);
        
        // Aplicar estilo de macOS a la tabla con color del título
        TableStyler.aplicarEstiloMacOS(tablaReporte, new Color(41, 128, 185));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void mostrarVentasDelDia() {
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnCount(0);
        
        String[] columnas = {"ID Venta", "Fecha y Hora", "Cliente ID", "Usuario ID", "Total"};
        modeloTabla.setColumnIdentifiers(columnas);
        
        List<Venta> ventas = ventaDAO.obtenerVentasDelDia();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        double totalDia = 0.0;
        
        for (Venta v : ventas) {
            Object[] fila = {
                v.getIdVenta(),
                sdf.format(v.getFecha()),
                v.getIdCliente(),
                v.getIdUsuario(),
                "S/. " + String.format("%.2f", v.getTotal())
            };
            modeloTabla.addRow(fila);
            totalDia += v.getTotal();
        }
        
        // Reaplicar estilo después de cambiar el modelo
        TableStyler.aplicarEstiloMacOS(tablaReporte, new Color(41, 128, 185));
        
        lblTotalReporte.setText("Total del Día: S/. " + String.format("%.2f", totalDia) + " | Ventas: " + ventas.size());
        
        if (ventas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay ventas registradas hoy.", "Sin datos", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void mostrarStockDisponible() {
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnCount(0);
        
        String[] columnas = {"Código", "Nombre", "Categoría", "Precio", "Stock", "Stock Mínimo", "Estado"};
        modeloTabla.setColumnIdentifiers(columnas);
        
        List<Producto> productos = productoDAO.listar();
        
        for (Producto p : productos) {
            String estado = p.necesitaReabastecimiento() ? "⚠ BAJO" : "✓ OK";
            Object[] fila = {
                p.getCodigo(),
                p.getNombre(),
                p.getCategoria(),
                "S/. " + String.format("%.2f", p.getPrecio()),
                p.getStock(),
                p.getStockMinimo(),
                estado
            };
            modeloTabla.addRow(fila);
        }
        
        // Reaplicar estilo después de cambiar el modelo
        TableStyler.aplicarEstiloMacOS(tablaReporte, new Color(41, 128, 185));
        
        lblTotalReporte.setText("Total de Productos: " + productos.size());
    }
    
    private void mostrarProductosBajoStock() {
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnCount(0);
        
        String[] columnas = {"Código", "Nombre", "Categoría", "Stock Actual", "Stock Mínimo", "Diferencia"};
        modeloTabla.setColumnIdentifiers(columnas);
        
        List<Producto> productos = productoDAO.obtenerStockBajo();
        
        for (Producto p : productos) {
            int diferencia = p.getStockMinimo() - p.getStock();
            Object[] fila = {
                p.getCodigo(),
                p.getNombre(),
                p.getCategoria(),
                "🔴 " + p.getStock(),
                p.getStockMinimo(),
                "Faltan: " + diferencia
            };
            modeloTabla.addRow(fila);
        }
        
        // Reaplicar estilo después de cambiar el modelo
        TableStyler.aplicarEstiloMacOS(tablaReporte, new Color(41, 128, 185));
        
        lblTotalReporte.setText("Productos con Stock Bajo: " + productos.size());
        
        if (productos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay productos con stock bajo.", "Sin alertas", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
