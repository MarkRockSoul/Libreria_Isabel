package views;

import controllers.ProductoController;
import models.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// RF-02 | RF-03 | RF-04 | RF-05 | RF-11 | RF-13
public class ProductosView extends JFrame {

    private ProductoController controller = new ProductoController();

    // Tabla
    private JTable             tabla;
    private DefaultTableModel  modelo;

    // Búsqueda
    private JTextField txtBuscar;

    // Formulario
    private JTextField txtCodigo, txtNombre, txtCategoria, txtPrecio, txtStock, txtMinimo;
    private String     codigoOriginalSeleccionado = "";

    // Botones
    private JButton btnNuevo, btnGuardar, btnActualizar, btnEliminar;

    public ProductosView() {
        setTitle("Gestión de Productos");
        setSize(900, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        cargarTabla(controller.listarTodos());
    }

    private void initComponents() {
        setLayout(new BorderLayout(6, 6));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(panelBusqueda(), BorderLayout.NORTH);
        add(panelTabla(),    BorderLayout.CENTER);
        add(panelFormulario(), BorderLayout.SOUTH);
    }

    // ── Panel de búsqueda (RF-05) ─────────────────────────────────────────
    private JPanel panelBusqueda() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        p.setBorder(BorderFactory.createTitledBorder("Buscar producto"));
        txtBuscar = new JTextField(22);
        JButton btnPorCodigo = new JButton("Por código");
        JButton btnPorNombre = new JButton("Por nombre");
        JButton btnTodos     = new JButton("Mostrar todos");

        p.add(new JLabel("Texto:")); p.add(txtBuscar);
        p.add(btnPorCodigo); p.add(btnPorNombre); p.add(btnTodos);

        btnPorCodigo.addActionListener(e -> {
            Producto prod = controller.buscarPorCodigo(txtBuscar.getText().trim());
            if (prod != null) cargarTabla(List.of(prod));
            else { cargarTabla(List.of()); mostrarMensaje("No se encontró el código.", false); }
        });
        btnPorNombre.addActionListener(e -> {
            List<Producto> res = controller.buscarPorNombre(txtBuscar.getText().trim());
            if (res.isEmpty()) mostrarMensaje("No se encontraron productos.", false);
            else cargarTabla(res);
        });
        btnTodos.addActionListener(e -> cargarTabla(controller.listarTodos()));
        return p;
    }

    // ── Panel tabla ───────────────────────────────────────────────────────
    private JPanel panelTabla() {
        String[] cols = {"ID","Código","Nombre","Categoría","Precio","Stock","Mín.","Alerta"};
        modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);

        // Al seleccionar fila, llenar formulario
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                int row = tabla.getSelectedRow();
                codigoOriginalSeleccionado = modelo.getValueAt(row, 1).toString();
                txtCodigo.setText(codigoOriginalSeleccionado);
                txtNombre.setText(modelo.getValueAt(row, 2).toString());
                txtCategoria.setText(modelo.getValueAt(row, 3).toString());
                txtPrecio.setText(modelo.getValueAt(row, 4).toString().replace("S/.",""));
                txtStock.setText(modelo.getValueAt(row, 5).toString());
                txtMinimo.setText(modelo.getValueAt(row, 6).toString());
                btnActualizar.setEnabled(true);
                btnEliminar.setEnabled(true);
                btnGuardar.setEnabled(false);
            }
        });

        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder("Inventario  ⚠ = stock en alerta"));
        p.add(new JScrollPane(tabla));
        return p;
    }

    // ── Panel formulario ──────────────────────────────────────────────────
    private JPanel panelFormulario() {
        JPanel outer = new JPanel(new BorderLayout(6, 6));
        outer.setBorder(BorderFactory.createTitledBorder("Datos del producto"));

        // Campos
        JPanel campos = new JPanel(new GridLayout(2, 6, 8, 6));
        txtCodigo    = new JTextField(); txtNombre    = new JTextField();
        txtCategoria = new JTextField(); txtPrecio    = new JTextField();
        txtStock     = new JTextField(); txtMinimo    = new JTextField();

        campos.add(label("Código *")); campos.add(txtCodigo);
        campos.add(label("Nombre *")); campos.add(txtNombre);
        campos.add(label("Categoría")); campos.add(txtCategoria);
        campos.add(label("Precio S/. *")); campos.add(txtPrecio);
        campos.add(label("Stock actual *")); campos.add(txtStock);
        campos.add(label("Stock mínimo *")); campos.add(txtMinimo);
        outer.add(campos, BorderLayout.CENTER);

        // Botones
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        btnNuevo      = new JButton("Nuevo");
        btnGuardar    = new JButton("Guardar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar   = new JButton("Eliminar");
        btnGuardar.setEnabled(false);
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);

        colorearBoton(btnGuardar,    new Color(15, 110, 86));
        colorearBoton(btnActualizar, new Color(83, 74, 183));
        colorearBoton(btnEliminar,   new Color(180, 60, 40));

        botones.add(btnNuevo); botones.add(btnGuardar);
        botones.add(btnActualizar); botones.add(btnEliminar);
        outer.add(botones, BorderLayout.SOUTH);

        // ── Acciones de botones ───────────────────────────────────────────

        // RF-02 — Nuevo (habilita guardar, limpia campos)
        btnNuevo.addActionListener(e -> {
            limpiarCampos();
            btnGuardar.setEnabled(true);
            btnActualizar.setEnabled(false);
            btnEliminar.setEnabled(false);
            tabla.clearSelection();
            codigoOriginalSeleccionado = "";
            txtCodigo.requestFocus();
        });

        // RF-02 — Guardar nuevo producto
        btnGuardar.addActionListener(e -> {
            String res = controller.registrar(
                txtCodigo.getText(), txtNombre.getText(), txtCategoria.getText(),
                txtPrecio.getText(), txtStock.getText(), txtMinimo.getText());
            mostrarMensaje(res.substring(4), res.startsWith("OK"));
            if (res.startsWith("OK")) { limpiarCampos(); cargarTabla(controller.listarTodos()); }
        });

        // RF-03 — Actualizar producto seleccionado
        btnActualizar.addActionListener(e -> {
            String res = controller.actualizar(
                codigoOriginalSeleccionado,
                txtCodigo.getText(), txtNombre.getText(), txtCategoria.getText(),
                txtPrecio.getText(), txtStock.getText(), txtMinimo.getText());
            mostrarMensaje(res.substring(4), res.startsWith("OK"));
            if (res.startsWith("OK")) { limpiarCampos(); cargarTabla(controller.listarTodos()); }
        });

        // RF-04 — Eliminar producto seleccionado
        btnEliminar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el producto con código '" + codigoOriginalSeleccionado + "'?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String res = controller.eliminar(codigoOriginalSeleccionado);
                mostrarMensaje(res.substring(4), res.startsWith("OK"));
                if (res.startsWith("OK")) { limpiarCampos(); cargarTabla(controller.listarTodos()); }
            }
        });

        return outer;
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private void cargarTabla(List<Producto> lista) {
        modelo.setRowCount(0);
        for (Producto p : lista) {
            modelo.addRow(new Object[]{
                p.getId(), p.getCodigo(), p.getNombre(), p.getCategoria(),
                String.format("S/.%.2f", p.getPrecio()), p.getStockActual(), p.getStockMinimo(),
                p.tieneAlertaStock() ? "⚠ ALERTA" : "✓ OK"
            });
        }
    }

    private void limpiarCampos() {
        txtCodigo.setText(""); txtNombre.setText(""); txtCategoria.setText("");
        txtPrecio.setText(""); txtStock.setText(""); txtMinimo.setText("");
        codigoOriginalSeleccionado = "";
    }

    private void mostrarMensaje(String msg, boolean exito) {
        JOptionPane.showMessageDialog(this, msg,
            exito ? "Éxito" : "Error",
            exito ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    private JLabel label(String txt) {
        JLabel l = new JLabel(txt); l.setFont(new Font("SansSerif", Font.PLAIN, 11)); return l;
    }

    private void colorearBoton(JButton btn, Color c) {
        btn.setBackground(c); btn.setForeground(Color.WHITE); btn.setFocusPainted(false);
    }
}
