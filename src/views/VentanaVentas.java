package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import dao.ClienteDAO;
import dao.ConexionDB;
import dao.ProductoDAO;
import dao.VentaDAO;
import models.Cliente;
import models.DetalleVenta;
import models.Producto;
import models.Usuario;
import models.Venta;
import views.buttons.BotonEstilizado;
import java.awt.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VentanaVentas extends JFrame {
    private JTextField txtBuscarProducto, txtCantidad, txtDniCliente;
    private JLabel lblClienteNombre, lblTotal;
    private JTable tablaCarrito;
    private DefaultTableModel modeloCarrito;
    private JButton btnAgregarCarrito, btnQuitarCarrito, btnRegistrarVenta, btnBuscarCliente;
    
    private ProductoDAO productoDAO;
    private ClienteDAO clienteDAO;
    private VentaDAO ventaDAO;
    
    private Usuario usuarioActual;
    private Cliente clienteSeleccionado;
    private List<DetalleVenta> carrito;
    private double totalVenta;
    
    public VentanaVentas(JFrame parent, Usuario usuario) {
        this.usuarioActual = usuario;
        this.carrito = new ArrayList<>();
        this.totalVenta = 0.0;
        
        Connection conn = ConexionDB.getConexion();
        productoDAO = new ProductoDAO(conn);
        clienteDAO = new ClienteDAO(conn);
        ventaDAO = new VentaDAO(conn);
        
        configurarVentana();
        inicializarComponentes();
    }
    
    private void configurarVentana() {
        setTitle("Registro de Venta");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }
    
    private void inicializarComponentes() {
        // Panel superior
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        panelTitulo.setBackground(new Color(243, 156, 18)); // Naranja brillante del botón de ventas
        panelTitulo.setPreferredSize(new Dimension(0, 56));

        JLabel lblIcono = new JLabel("🛒");
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        lblIcono.setForeground(Color.WHITE);

        JLabel lblTitulo = new JLabel("Registro de Venta");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);

        panelTitulo.add(lblIcono);
        panelTitulo.add(lblTitulo);
                
        // Panel central
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelCentral.setBackground(Color.WHITE);
        
        // Subpanel de cliente y producto
        JPanel panelSuperior = crearPanelSuperior();
        
        // Tabla del carrito
        JPanel panelCarrito = crearPanelCarrito();
        
        // Panel de total
        JPanel panelTotal = crearPanelTotal();
        
        panelCentral.add(panelSuperior, BorderLayout.NORTH);
        panelCentral.add(panelCarrito, BorderLayout.CENTER);
        panelCentral.add(panelTotal, BorderLayout.SOUTH);
        
        // Panel de botones
        JPanel panelBotones = crearPanelBotones();
        
        add(panelTitulo, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Panel de cliente
        JPanel panelCliente = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelCliente.setBackground(new Color(240, 240, 240));
        panelCliente.setBorder(BorderFactory.createTitledBorder("Cliente"));
        
        panelCliente.add(new JLabel("DNI:"));
        txtDniCliente = new JTextField(10);
        panelCliente.add(txtDniCliente);
        
        btnBuscarCliente = BotonEstilizado.crearBotonEstilizado("Buscar", new Color(46, 204, 113), new Dimension(100, 35));
        btnBuscarCliente.addActionListener(e -> buscarCliente());
        panelCliente.add(btnBuscarCliente);
        
        lblClienteNombre = new JLabel("Cliente no seleccionado");
        lblClienteNombre.setFont(new Font("Arial", Font.BOLD, 13));
        lblClienteNombre.setForeground(new Color(231, 76, 60));
        panelCliente.add(lblClienteNombre);
        
        // Panel de producto
        JPanel panelProducto = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelProducto.setBackground(new Color(240, 240, 240));
        panelProducto.setBorder(BorderFactory.createTitledBorder("Agregar Producto"));
        
        panelProducto.add(new JLabel("Código/Nombre:"));
        txtBuscarProducto = new JTextField(15);
        panelProducto.add(txtBuscarProducto);
        
        panelProducto.add(new JLabel("Cantidad:"));
        txtCantidad = new JTextField(5);
        txtCantidad.setText("1");
        panelProducto.add(txtCantidad);
        
        btnAgregarCarrito = BotonEstilizado.crearBotonEstilizado("Agregar al Carrito", new Color(52, 152, 219), new Dimension(170, 35));
        btnAgregarCarrito.addActionListener(e -> agregarAlCarrito());
        panelProducto.add(btnAgregarCarrito);
        
        panel.add(panelCliente);
        panel.add(panelProducto);
        
        return panel;
    }
    
    private JPanel crearPanelCarrito() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Carrito de Compras"));
        
        String[] columnas = {"Código", "Producto", "Precio Unit.", "Cantidad", "Subtotal"};
        modeloCarrito = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaCarrito = new JTable(modeloCarrito);
        tablaCarrito.setRowHeight(25);
        tablaCarrito.getColumnModel().getColumn(0).setPreferredWidth(100);
        tablaCarrito.getColumnModel().getColumn(1).setPreferredWidth(300);
        
        JScrollPane scrollPane = new JScrollPane(tablaCarrito);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelTotal() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        panel.setBackground(Color.WHITE);
        
        JLabel lblTotalTexto = new JLabel("TOTAL:");
        lblTotalTexto.setFont(new Font("Arial", Font.BOLD, 18));
        
        lblTotal = new JLabel("S/. 0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 24));
        lblTotal.setForeground(new Color(46, 204, 113));
        
        panel.add(lblTotalTexto);
        panel.add(lblTotal);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(Color.WHITE);
        
        btnQuitarCarrito = BotonEstilizado.crearBotonEstilizado(
            "Quitar del Carrito", 
            new Color(231, 76, 60), 
            new Dimension(180, 40)
        );
        btnQuitarCarrito.addActionListener(e -> quitarDelCarrito());
        
        btnRegistrarVenta = BotonEstilizado.crearBotonEstilizado(
            "Registrar Venta", 
            new Color(46, 204, 113), 
            new Dimension(180, 40)
        );
        btnRegistrarVenta.addActionListener(e -> registrarVenta());
        
        JButton btnLimpiar = BotonEstilizado.crearBotonEstilizado(
            "Limpiar Carrito", 
            new Color(149, 165, 166), 
            new Dimension(180, 40)
        );
        btnLimpiar.addActionListener(e -> limpiarCarrito());
        
        panel.add(btnQuitarCarrito);
        panel.add(btnLimpiar);
        panel.add(btnRegistrarVenta);
        
        return panel;
    }
    
    private void buscarCliente() {
        String dni = txtDniCliente.getText().trim();
        
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un DNI.", "Campo vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Cliente cliente = clienteDAO.buscarPorDNI(dni);
        
        if (cliente != null) {
            clienteSeleccionado = cliente;
            lblClienteNombre.setText("Cliente: " + cliente.getNombre());
            lblClienteNombre.setForeground(new Color(46, 204, 113));
        } else {
            // Ofrecer registrar nuevo cliente
            int opcion = JOptionPane.showConfirmDialog(this, 
                "Cliente no encontrado. ¿Desea registrarlo?", 
                "Cliente no encontrado", 
                JOptionPane.YES_NO_OPTION);
            
            if (opcion == JOptionPane.YES_OPTION) {
                registrarNuevoCliente(dni);
            }
        }
    }
    
    private void registrarNuevoCliente(String dni) {
        String nombre = JOptionPane.showInputDialog(this, "Ingrese el nombre completo del cliente:", "Registrar Cliente", JOptionPane.QUESTION_MESSAGE);
        
        if (nombre != null && !nombre.trim().isEmpty()) {
            String telefono = JOptionPane.showInputDialog(this, "Ingrese el teléfono (opcional):", "Registrar Cliente", JOptionPane.QUESTION_MESSAGE);
            
            try {
                Cliente nuevoCliente = new Cliente();
                nuevoCliente.setDni(dni);
                nuevoCliente.setNombre(nombre.trim());
                nuevoCliente.setTelefono(telefono != null ? telefono.trim() : "");
                
                if (clienteDAO.insertar(nuevoCliente)) {
                    clienteSeleccionado = nuevoCliente;
                    lblClienteNombre.setText("Cliente: " + nuevoCliente.getNombre());
                    lblClienteNombre.setForeground(new Color(46, 204, 113));
                    JOptionPane.showMessageDialog(this, "Cliente registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void agregarAlCarrito() {
        String termino = txtBuscarProducto.getText().trim();
        String cantidadStr = txtCantidad.getText().trim();
        
        if (termino.isEmpty() || cantidadStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int cantidad = Integer.parseInt(cantidadStr);
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0.", "Cantidad inválida", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Buscar producto
            Producto producto = productoDAO.buscarPorCodigo(termino);
            if (producto == null) {
                List<Producto> productos = productoDAO.buscarPorNombre(termino);
                if (productos.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Producto no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                } else if (productos.size() == 1) {
                    producto = productos.get(0);
                } else {
                    // Múltiples resultados - mostrar selector
                    producto = seleccionarProducto(productos);
                    if (producto == null) return;
                }
            }
            
            // Verificar stock
            if (!producto.validarStock(cantidad)) {
                JOptionPane.showMessageDialog(this, 
                    "Stock insuficiente. Disponible: " + producto.getStock(), 
                    "Stock insuficiente", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Crear detalle
            DetalleVenta detalle = new DetalleVenta();
            detalle.setIdProducto(producto.getIdProducto());
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.calcularSubtotal();
            
            carrito.add(detalle);
            
            // Agregar a la tabla
            Object[] fila = {
                producto.getCodigo(),
                producto.getNombre(),
                String.format("%.2f", detalle.getPrecioUnitario()),
                detalle.getCantidad(),
                String.format("%.2f", detalle.getSubtotal())
            };
            modeloCarrito.addRow(fila);
            
            // Actualizar total
            actualizarTotal();
            
            // Limpiar campos
            txtBuscarProducto.setText("");
            txtCantidad.setText("1");
            txtBuscarProducto.requestFocus();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Producto seleccionarProducto(List<Producto> productos) {
        String[] opciones = new String[productos.size()];
        for (int i = 0; i < productos.size(); i++) {
            Producto p = productos.get(i);
            opciones[i] = p.getCodigo() + " - " + p.getNombre() + " (Stock: " + p.getStock() + ")";
        }
        
        String seleccion = (String) JOptionPane.showInputDialog(
            this,
            "Se encontraron varios productos. Seleccione uno:",
            "Seleccionar Producto",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]
        );
        
        if (seleccion != null) {
            for (int i = 0; i < opciones.length; i++) {
                if (opciones[i].equals(seleccion)) {
                    return productos.get(i);
                }
            }
        }
        
        return null;
    }
    
    private void quitarDelCarrito() {
        int fila = tablaCarrito.getSelectedRow();
        
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto del carrito.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        carrito.remove(fila);
        modeloCarrito.removeRow(fila);
        actualizarTotal();
    }
    
    private void limpiarCarrito() {
        if (carrito.isEmpty()) return;
        
        int confirmacion = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de limpiar el carrito?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            carrito.clear();
            modeloCarrito.setRowCount(0);
            actualizarTotal();
        }
    }
    
    private void registrarVenta() {
        // Validaciones
        if (clienteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente.", "Cliente no seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío.", "Carrito vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Crear venta
        Venta venta = new Venta();
        venta.setFecha(new Date());
        venta.setIdCliente(clienteSeleccionado.getIdCliente());
        venta.setIdUsuario(usuarioActual.getIdUsuario());
        venta.setTotal(totalVenta);
        
        // Registrar venta
        if (ventaDAO.registrarVenta(venta, carrito)) {
            JOptionPane.showMessageDialog(this, 
                "Venta registrada exitosamente.\n" +
                "ID Venta: " + venta.getIdVenta() + "\n" +
                "Total: S/. " + String.format("%.2f", venta.getTotal()), 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Limpiar todo
            carrito.clear();
            modeloCarrito.setRowCount(0);
            clienteSeleccionado = null;
            lblClienteNombre.setText("Cliente no seleccionado");
            lblClienteNombre.setForeground(new Color(231, 76, 60));
            txtDniCliente.setText("");
            actualizarTotal();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error al registrar la venta.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarTotal() {
        totalVenta = 0.0;
        for (DetalleVenta detalle : carrito) {
            totalVenta += detalle.getSubtotal();
        }
        lblTotal.setText("S/. " + String.format("%.2f", totalVenta));
    }
}
