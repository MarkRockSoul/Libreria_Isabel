package views;

import controllers.ProductoController;
import controllers.VentaController;
import models.Cliente;
import models.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

// RF-10 | RF-11 | RF-13 | RF-15 — Ventana de reportes
public class ReportesView extends JFrame {

    private ProductoController prodCtrl  = new ProductoController();
    private VentaController    ventaCtrl = new VentaController();

    public ReportesView() {
        setTitle("Reportes e Inventario");
        setSize(820, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("📊 Ventas por fecha (RF-10)",     tabVentasPorFecha());
        tabs.addTab("📦 Stock disponible (RF-11/13)",  tabStockDisponible());
        tabs.addTab("🧾 Historial por cliente (RF-15)", tabHistorialCliente());
        add(tabs);
    }

    // ── Tab 1: Ventas por fecha (RF-10) ───────────────────────────────────
    private JPanel tabVentasPorFecha() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Filtro de fechas
        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JTextField txtDesde = new JTextField(LocalDate.now().withDayOfMonth(1).toString(), 12);
        JTextField txtHasta = new JTextField(LocalDate.now().toString(), 12);
        JButton    btnGenerar = new JButton("Generar reporte");
        colorearBoton(btnGenerar, new Color(83, 74, 183));
        filtro.add(new JLabel("Desde:")); filtro.add(txtDesde);
        filtro.add(new JLabel("Hasta:")); filtro.add(txtHasta);
        filtro.add(btnGenerar);
        p.add(filtro, BorderLayout.NORTH);

        // Tabla de resultados
        String[] cols = {"ID","Fecha","Hora","Cliente","Vendedor","Total"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        JLabel lblResumen = new JLabel(" ", JLabel.RIGHT);
        lblResumen.setFont(new Font("SansSerif", Font.BOLD, 12));
        p.add(new JScrollPane(tabla), BorderLayout.CENTER);
        p.add(lblResumen, BorderLayout.SOUTH);

        btnGenerar.addActionListener(e -> {
            modelo.setRowCount(0);
            List<String[]> filas = ventaCtrl.reporteVentasPorFecha(
                txtDesde.getText().trim(), txtHasta.getText().trim());
            if (filas.isEmpty()) {
                lblResumen.setText("No hay ventas en el período seleccionado.");
                return;
            }
            double totalGeneral = 0;
            for (String[] f : filas) {
                modelo.addRow(f);
                try { totalGeneral += Double.parseDouble(f[5].replace("S/.","").replace(",",".")); }
                catch (NumberFormatException ignored) {}
            }
            lblResumen.setText(filas.size() + " transacciones  |  Total recaudado: S/."
                + String.format("%.2f", totalGeneral));
        });

        return p;
    }

    // ── Tab 2: Stock disponible (RF-11 y RF-13) ───────────────────────────
    private JPanel tabStockDisponible() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JCheckBox chkSoloAlertas = new JCheckBox("Mostrar solo productos en alerta  ⚠");
        JButton   btnActualizar  = new JButton("Actualizar");
        colorearBoton(btnActualizar, new Color(15, 110, 86));
        controles.add(chkSoloAlertas); controles.add(btnActualizar);
        p.add(controles, BorderLayout.NORTH);

        String[] cols = {"ID","Código","Nombre","Categoría","Precio","Stock","Mín.","Estado"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        // Colorear filas en alerta
        tabla.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                String estado = (String) modelo.getValueAt(row, 7);
                if ("⚠ ALERTA".equals(estado) && !sel) c.setBackground(new Color(255, 230, 230));
                else if (!sel) c.setBackground(Color.WHITE);
                return c;
            }
        });

        JLabel lblInfo = new JLabel(" ", JLabel.RIGHT);
        p.add(new JScrollPane(tabla), BorderLayout.CENTER);
        p.add(lblInfo, BorderLayout.SOUTH);

        Runnable cargar = () -> {
            modelo.setRowCount(0);
            List<Producto> lista = chkSoloAlertas.isSelected()
                ? prodCtrl.listarConAlerta() : prodCtrl.listarTodos();
            for (Producto pr : lista) {
                modelo.addRow(new Object[]{
                    pr.getId(), pr.getCodigo(), pr.getNombre(), pr.getCategoria(),
                    String.format("S/.%.2f", pr.getPrecio()),
                    pr.getStockActual(), pr.getStockMinimo(),
                    pr.tieneAlertaStock() ? "⚠ ALERTA" : "✓ OK"
                });
            }
            long alertas = lista.stream().filter(Producto::tieneAlertaStock).count();
            lblInfo.setText(lista.size() + " productos  |  " + alertas + " en alerta de stock.");
        };

        btnActualizar.addActionListener(e -> cargar.run());
        chkSoloAlertas.addActionListener(e -> cargar.run());
        cargar.run();
        return p;
    }

    // ── Tab 3: Historial por cliente (RF-15) ──────────────────────────────
    private JPanel tabHistorialCliente() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Selector de cliente
        JPanel selector = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JComboBox<String> cbClientes = new JComboBox<>();
        JButton btnCargar = new JButton("Ver historial");
        colorearBoton(btnCargar, new Color(83, 74, 183));
        selector.add(new JLabel("Cliente:")); selector.add(cbClientes); selector.add(btnCargar);
        p.add(selector, BorderLayout.NORTH);

        // Cargar clientes en el combo
        List<Cliente> clientes = ventaCtrl.listarClientes();
        for (Cliente c : clientes) cbClientes.addItem(c.getId() + " — " + c.getNombre() + " (DNI: " + c.getDni() + ")");

        String[] cols = {"Venta ID","Fecha","Hora","Producto","Cant.","Precio unit.","Subtotal","Total venta"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        JLabel lblInfo = new JLabel(" ", JLabel.RIGHT);
        p.add(new JScrollPane(tabla), BorderLayout.CENTER);
        p.add(lblInfo, BorderLayout.SOUTH);

        btnCargar.addActionListener(e -> {
            modelo.setRowCount(0);
            if (cbClientes.getSelectedIndex() < 0 || clientes.isEmpty()) return;
            Cliente seleccionado = clientes.get(cbClientes.getSelectedIndex());
            List<String[]> filas = ventaCtrl.historialPorCliente(seleccionado.getId());
            if (filas.isEmpty()) {
                lblInfo.setText("El cliente no tiene compras registradas.");
                return;
            }
            for (String[] f : filas) modelo.addRow(f);
            lblInfo.setText(filas.size() + " líneas encontradas para " + seleccionado.getNombre() + ".");
        });

        return p;
    }

    private void colorearBoton(JButton btn, Color c) {
        btn.setBackground(c); btn.setForeground(Color.WHITE); btn.setFocusPainted(false);
    }
}
