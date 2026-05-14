package views;

import controllers.ProductoController;
import controllers.VentaController;
import models.Cliente;
import models.DetalleVenta;
import models.Producto;
import models.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// RF-06 | RF-07 | RF-08 | RF-09 — Registro de ventas
public class VentaView extends JFrame {

    private VentaController    ventaCtrl  = new VentaController();
    private ProductoController prodCtrl   = new ProductoController();
    private Usuario            sesion;

    // Datos en memoria
    private Cliente             clienteActual;
    private List<DetalleVenta>  carrito = new ArrayList<>();

    // Panel cliente
    private JTextField txtDni, txtNombreCliente, txtTelefono;
    private JButton    btnBuscarCliente, btnRegistrarCliente;
    private JLabel     lblClienteInfo;

    // Panel producto
    private JTextField txtCodProd, txtCantidad;
    private JLabel     lblProdInfo;
    private JButton    btnBuscarProd, btnAgregarCarrito;

    // Tabla carrito
    private JTable            tablaCarrito;
    private DefaultTableModel modeloCarrito;
    private JLabel            lblTotal;
    private JButton           btnQuitarItem, btnConfirmar, btnLimpiar;

    public VentaView(Usuario sesion) {
        this.sesion = sesion;
        setTitle("Registrar Venta — Usuario: " + sesion.getUsuario());
        setSize(940, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(8, 8));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            panelIzquierdo(), panelCarrito());
        splitPane.setDividerLocation(420);
        add(splitPane, BorderLayout.CENTER);
    }

    // ── Panel izquierdo: cliente + producto ───────────────────────────────
    private JPanel panelIzquierdo() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.add(panelCliente(),  BorderLayout.NORTH);
        p.add(panelProducto(), BorderLayout.CENTER);
        return p;
    }

    // ── Sub-panel cliente (RF-06) ─────────────────────────────────────────
    private JPanel panelCliente() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("1. Cliente"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,4,4,4); g.fill = GridBagConstraints.HORIZONTAL;

        txtDni            = new JTextField(10);
        txtNombreCliente  = new JTextField(16);
        txtTelefono       = new JTextField(12);
        btnBuscarCliente  = new JButton("Buscar por DNI");
        btnRegistrarCliente = new JButton("Registrar cliente");
        lblClienteInfo    = new JLabel(" ");
        lblClienteInfo.setFont(new Font("SansSerif", Font.BOLD, 11));

        g.gridx=0; g.gridy=0; p.add(new JLabel("DNI:"), g);
        g.gridx=1; p.add(txtDni, g);
        g.gridx=2; p.add(btnBuscarCliente, g);

        g.gridx=0; g.gridy=1; p.add(new JLabel("Nombre:"), g);
        g.gridx=1; g.gridwidth=2; p.add(txtNombreCliente, g);

        g.gridwidth=1; g.gridx=0; g.gridy=2; p.add(new JLabel("Teléfono:"), g);
        g.gridx=1; p.add(txtTelefono, g);
        g.gridx=2; p.add(btnRegistrarCliente, g);

        g.gridx=0; g.gridy=3; g.gridwidth=3; p.add(lblClienteInfo, g);

        // Buscar cliente por DNI
        btnBuscarCliente.addActionListener(e -> {
            String dni = txtDni.getText().trim();
            if (dni.isEmpty()) { lblClienteInfo.setText("Ingrese un DNI."); return; }
            Cliente c = ventaCtrl.buscarClientePorDni(dni);
            if (c != null) {
                clienteActual = c;
                txtNombreCliente.setText(c.getNombre());
                txtTelefono.setText(c.getTelefono());
                lblClienteInfo.setForeground(new Color(15,110,86));
                lblClienteInfo.setText("✓ Cliente encontrado (ID=" + c.getId() + ")");
            } else {
                clienteActual = null;
                lblClienteInfo.setForeground(Color.RED);
                lblClienteInfo.setText("✗ DNI no registrado. Complete los datos y registre.");
            }
        });

        // Registrar nuevo cliente
        btnRegistrarCliente.addActionListener(e -> {
            String res = ventaCtrl.registrarCliente(
                txtNombreCliente.getText(), txtDni.getText(), txtTelefono.getText());
            if (res.startsWith("OK")) {
                clienteActual = ventaCtrl.buscarClientePorDni(txtDni.getText().trim());
                lblClienteInfo.setForeground(new Color(15,110,86));
                lblClienteInfo.setText("✓ " + res.substring(4) + " ID=" + (clienteActual != null ? clienteActual.getId() : "?"));
            } else {
                lblClienteInfo.setForeground(Color.RED);
                lblClienteInfo.setText("✗ " + res.substring(7));
            }
        });

        return p;
    }

    // ── Sub-panel producto (RF-07, RF-09) ─────────────────────────────────
    private JPanel panelProducto() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("2. Agregar producto al carrito"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,4,4,4); g.fill = GridBagConstraints.HORIZONTAL;

        txtCodProd      = new JTextField(12);
        txtCantidad     = new JTextField(5);
        lblProdInfo     = new JLabel(" ");
        lblProdInfo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnBuscarProd   = new JButton("Buscar");
        btnAgregarCarrito = new JButton("Agregar al carrito");
        btnAgregarCarrito.setEnabled(false);
        colorearBoton(btnAgregarCarrito, new Color(83, 74, 183));

        g.gridx=0; g.gridy=0; p.add(new JLabel("Código prod.:"), g);
        g.gridx=1; p.add(txtCodProd, g);
        g.gridx=2; p.add(btnBuscarProd, g);

        g.gridx=0; g.gridy=1; p.add(new JLabel("Cantidad:"), g);
        g.gridx=1; p.add(txtCantidad, g);
        g.gridx=2; p.add(btnAgregarCarrito, g);

        g.gridx=0; g.gridy=2; g.gridwidth=3; p.add(lblProdInfo, g);

        // Buscar producto por código
        btnBuscarProd.addActionListener(e -> {
            Producto prod = prodCtrl.buscarPorCodigo(txtCodProd.getText().trim());
            if (prod != null) {
                lblProdInfo.setForeground(prod.tieneAlertaStock() ? Color.ORANGE : new Color(15,110,86));
                lblProdInfo.setText(prod.getNombre() + " — S/." +
                    String.format("%.2f",prod.getPrecio()) + " | Stock: " + prod.getStockActual() +
                    (prod.tieneAlertaStock() ? " ⚠" : ""));
                btnAgregarCarrito.setEnabled(true);
            } else {
                lblProdInfo.setForeground(Color.RED);
                lblProdInfo.setText("✗ Producto no encontrado.");
                btnAgregarCarrito.setEnabled(false);
            }
        });

        // RF-07 / RF-09 — Agregar al carrito con verificación de stock
        btnAgregarCarrito.addActionListener(e -> {
            String codProd = txtCodProd.getText().trim();
            int cant;
            try { cant = Integer.parseInt(txtCantidad.getText().trim()); }
            catch (NumberFormatException ex) {
                lblProdInfo.setForeground(Color.RED);
                lblProdInfo.setText("✗ Cantidad inválida.");
                return;
            }
            if (cant <= 0) { lblProdInfo.setForeground(Color.RED); lblProdInfo.setText("✗ Cantidad debe ser > 0."); return; }

            Producto prod = prodCtrl.buscarPorCodigo(codProd);
            if (prod == null) return;

            // Verificar stock sumando lo que ya está en carrito
            int yaEnCarrito = carrito.stream()
                .filter(d -> d.getIdProducto() == prod.getId())
                .mapToInt(DetalleVenta::getCantidad).sum();

            String check = ventaCtrl.verificarStock(prod.getId(), cant + yaEnCarrito);
            if (!check.equals("OK")) {
                lblProdInfo.setForeground(Color.RED);
                lblProdInfo.setText("✗ " + check.substring(7));
                return;
            }

            // Si ya existe en el carrito, incrementar
            boolean existe = false;
            for (DetalleVenta d : carrito) {
                if (d.getIdProducto() == prod.getId()) {
                    d.setCantidad(d.getCantidad() + cant);
                    d.calcularSubtotal();
                    existe = true; break;
                }
            }
            if (!existe) carrito.add(new DetalleVenta(prod.getId(), cant, prod.getPrecio()));

            actualizarTablaCarrito();
            lblProdInfo.setForeground(new Color(15,110,86));
            lblProdInfo.setText("✓ Agregado: " + prod.getNombre() + " x" + cant);
            txtCodProd.setText(""); txtCantidad.setText(""); txtCodProd.requestFocus();
            btnAgregarCarrito.setEnabled(false);
        });

        return p;
    }

    // ── Panel carrito / confirmación ──────────────────────────────────────
    private JPanel panelCarrito() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBorder(BorderFactory.createTitledBorder("3. Carrito de compra"));

        String[] cols = {"ID Prod","Nombre","Cant.","Precio unit.","Subtotal"};
        modeloCarrito = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaCarrito = new JTable(modeloCarrito);
        tablaCarrito.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        p.add(new JScrollPane(tablaCarrito), BorderLayout.CENTER);

        // Footer con total y botones
        JPanel footer = new JPanel(new BorderLayout(6,6));
        lblTotal = new JLabel("Total: S/.0.00", JLabel.RIGHT);
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTotal.setForeground(new Color(83,74,183));
        footer.add(lblTotal, BorderLayout.NORTH);

        JPanel bots = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnQuitarItem = new JButton("Quitar ítem");
        btnLimpiar    = new JButton("Limpiar todo");
        btnConfirmar  = new JButton("Confirmar venta ✓");
        colorearBoton(btnConfirmar, new Color(15, 110, 86));
        colorearBoton(btnQuitarItem, new Color(180, 60, 40));
        bots.add(btnQuitarItem); bots.add(btnLimpiar); bots.add(btnConfirmar);
        footer.add(bots, BorderLayout.SOUTH);
        p.add(footer, BorderLayout.SOUTH);

        // Quitar ítem seleccionado
        btnQuitarItem.addActionListener(e -> {
            int row = tablaCarrito.getSelectedRow();
            if (row >= 0) { carrito.remove(row); actualizarTablaCarrito(); }
        });

        // Limpiar carrito
        btnLimpiar.addActionListener(e -> { carrito.clear(); actualizarTablaCarrito(); });

        // RF-07 / RF-08 / RF-09 — Confirmar y registrar venta
        btnConfirmar.addActionListener(e -> {
            if (clienteActual == null) {
                JOptionPane.showMessageDialog(this, "Busque o registre un cliente antes de confirmar.",
                    "Sin cliente", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (carrito.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El carrito está vacío.",
                    "Sin productos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            double total = carrito.stream().mapToDouble(DetalleVenta::getSubtotal).sum();
            int confirm = JOptionPane.showConfirmDialog(this,
                "Cliente: " + clienteActual.getNombre() + "\nTotal: S/." + String.format("%.2f",total) + "\n¿Confirmar venta?",
                "Confirmar venta", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            String res = ventaCtrl.registrarVenta(clienteActual.getId(), sesion.getId(), carrito);
            if (res.startsWith("OK")) {
                JOptionPane.showMessageDialog(this, res.substring(4), "Venta registrada", JOptionPane.INFORMATION_MESSAGE);
                carrito.clear(); actualizarTablaCarrito();
                clienteActual = null;
                txtDni.setText(""); txtNombreCliente.setText(""); txtTelefono.setText("");
                lblClienteInfo.setText(" ");
            } else {
                JOptionPane.showMessageDialog(this, res.substring(7), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return p;
    }

    // RF-09 — Actualizar tabla y total en tiempo real
    private void actualizarTablaCarrito() {
        modeloCarrito.setRowCount(0);
        double total = 0;
        for (DetalleVenta d : carrito) {
            Producto p = prodCtrl.buscarPorCodigo(
                prodCtrl.listarTodos().stream()
                    .filter(pr -> pr.getId() == d.getIdProducto())
                    .map(Producto::getCodigo).findFirst().orElse(""));
            String nombre = (p != null) ? p.getNombre() : "ID:" + d.getIdProducto();
            modeloCarrito.addRow(new Object[]{
                d.getIdProducto(), nombre, d.getCantidad(),
                String.format("S/.%.2f", d.getPrecioUnitario()),
                String.format("S/.%.2f", d.getSubtotal())
            });
            total += d.getSubtotal();
        }
        lblTotal.setText("Total: S/." + String.format("%.2f", total));
    }

    private void colorearBoton(JButton btn, Color c) {
        btn.setBackground(c); btn.setForeground(Color.WHITE); btn.setFocusPainted(false);
    }
}
