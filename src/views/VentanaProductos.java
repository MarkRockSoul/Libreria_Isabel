package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import dao.ConexionDB;
import dao.ProductoDAO;
import models.Producto;
import views.buttons.BotonEstilizado;
import views.styles.TableStyler;

import java.awt.*;
import java.sql.Connection;
import java.util.List;

public class VentanaProductos extends JFrame {
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JTextField txtCodigo, txtNombre, txtCategoria, txtPrecio, txtStock, txtStockMinimo;
    private JButton btnAgregar, btnActualizar, btnEliminar, btnLimpiar, btnBuscar;
    private ProductoDAO productoDAO;
    private Producto productoSeleccionado;
    
    public VentanaProductos(JFrame parent) {
        Connection conn = ConexionDB.getConexion();
        productoDAO = new ProductoDAO(conn);
        
        configurarVentana();
        inicializarComponentes();
        cargarProductos();
    }
    
    private void configurarVentana() {
        setTitle("Gestión de Productos");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }
    
    private void inicializarComponentes() {
        // Panel superior 
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        panelTitulo.setBackground(new Color(44, 62, 80)); // Azul oscuro del botón de productos
        panelTitulo.setPreferredSize(new Dimension(0, 56));

        JLabel lblIcono = new JLabel("📦");
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        lblIcono.setForeground(Color.WHITE);

        JLabel lblTitulo = new JLabel("Gestión de Productos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);

        panelTitulo.add(lblIcono);
        panelTitulo.add(lblTitulo);
        
        // Panel de formulario
        JPanel panelFormulario = crearPanelFormulario();
        
        // Panel de tabla
        JPanel panelTabla = crearPanelTabla();
        
        // Panel de botones
        JPanel panelBotones = crearPanelBotones();
        
        // Agregar componentes
        add(panelTitulo, BorderLayout.NORTH);
        add(panelFormulario, BorderLayout.WEST);
        add(panelTabla, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            "Datos del Producto",
            0, 0, new Font("Arial", Font.BOLD, 14), new Color(52, 152, 219)
        ));
        panel.setPreferredSize(new Dimension(320, 0));
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.gridx = 0;
        
        // Código
        gbc.gridy = 0;
        panel.add(new JLabel("Código:"), gbc);
        gbc.gridy = 1;
        txtCodigo = new JTextField(15);
        panel.add(txtCodigo, gbc);
        
        // Nombre
        gbc.gridy = 2;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridy = 3;
        txtNombre = new JTextField(15);
        panel.add(txtNombre, gbc);
        
        // Categoría
        gbc.gridy = 4;
        panel.add(new JLabel("Categoría:"), gbc);
        gbc.gridy = 5;
        txtCategoria = new JTextField(15);
        panel.add(txtCategoria, gbc);
        
        // Precio
        gbc.gridy = 6;
        panel.add(new JLabel("Precio (S/.):"), gbc);
        gbc.gridy = 7;
        txtPrecio = new JTextField(15);
        panel.add(txtPrecio, gbc);
        
        // Stock
        gbc.gridy = 8;
        panel.add(new JLabel("Stock:"), gbc);
        gbc.gridy = 9;
        txtStock = new JTextField(15);
        panel.add(txtStock, gbc);
        
        // Stock Mínimo
        gbc.gridy = 10;
        panel.add(new JLabel("Stock Mínimo:"), gbc);
        gbc.gridy = 11;
        txtStockMinimo = new JTextField(15);
        panel.add(txtStockMinimo, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        
        // Modelo de tabla
        String[] columnas = {"ID", "Código", "Nombre", "Categoría", "Precio", "Stock", "Stock Mín."};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProductos.getTableHeader().setReorderingAllowed(false);
        tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(250);
        
        // Aplicar estilo de macOS a la tabla con color del título
        TableStyler.aplicarEstiloMacOS(tablaProductos, new Color(44, 62, 80));
        
        // Listener para selección
        tablaProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaProductos.getSelectedRow() != -1) {
                cargarProductoSeleccionado();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(Color.WHITE);
        
        btnAgregar = crearBoton("Agregar", new Color(46, 204, 113), e -> agregarProducto());
        btnActualizar = crearBoton("Actualizar", new Color(52, 152, 219), e -> actualizarProducto());
        btnEliminar = crearBoton("Eliminar", new Color(231, 76, 60), e -> eliminarProducto());
        btnBuscar = crearBoton("Buscar", new Color(155, 89, 182), e -> buscarProducto());
        btnLimpiar = crearBoton("Limpiar", new Color(149, 165, 166), e -> limpiarCampos());
        
        panel.add(btnAgregar);
        panel.add(btnActualizar);
        panel.add(btnEliminar);
        panel.add(btnBuscar);
        panel.add(btnLimpiar);
        
        return panel;
    }
    
    private JButton crearBoton(String texto, Color color, java.awt.event.ActionListener listener) {
        JButton boton = BotonEstilizado.crearBotonEstilizado(texto, color, new Dimension(140, 35));
        boton.addActionListener(listener);
        return boton;
    }
    
    private void cargarProductos() {
        modeloTabla.setRowCount(0);
        List<Producto> productos = productoDAO.listar();
        
        for (Producto p : productos) {
            Object[] fila = {
                p.getIdProducto(),
                p.getCodigo(),
                p.getNombre(),
                p.getCategoria(),
                String.format("%.2f", p.getPrecio()),
                p.getStock(),
                p.getStockMinimo()
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private void cargarProductoSeleccionado() {
        int fila = tablaProductos.getSelectedRow();
        if (fila != -1) {
            //int id = (int) modeloTabla.getValueAt(fila, 0);
            String codigo = (String) modeloTabla.getValueAt(fila, 1);
            
            Producto producto = productoDAO.buscarPorCodigo(codigo);
            if (producto != null) {
                productoSeleccionado = producto;
                txtCodigo.setText(producto.getCodigo());
                txtNombre.setText(producto.getNombre());
                txtCategoria.setText(producto.getCategoria());
                txtPrecio.setText(String.valueOf(producto.getPrecio()));
                txtStock.setText(String.valueOf(producto.getStock()));
                txtStockMinimo.setText(String.valueOf(producto.getStockMinimo()));
                txtCodigo.setEditable(false);
            }
        }
    }
    
    private void agregarProducto() {
        try {
            if (!validarCampos()) return;
            
            Producto producto = new Producto();
            producto.setCodigo(txtCodigo.getText().trim());
            producto.setNombre(txtNombre.getText().trim());
            producto.setCategoria(txtCategoria.getText().trim());
            producto.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
            producto.setStock(Integer.parseInt(txtStock.getText().trim()));
            producto.setStockMinimo(Integer.parseInt(txtStockMinimo.getText().trim()));
            
            if (productoDAO.insertar(producto)) {
                JOptionPane.showMessageDialog(this, 
                    "Producto registrado exitosamente.", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                cargarProductos();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al registrar el producto.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Precio, stock y stock mínimo deben ser números válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error de validación", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarProducto() {
        if (productoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            if (!validarCampos()) return;
            
            productoSeleccionado.setNombre(txtNombre.getText().trim());
            productoSeleccionado.setCategoria(txtCategoria.getText().trim());
            productoSeleccionado.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
            productoSeleccionado.setStock(Integer.parseInt(txtStock.getText().trim()));
            productoSeleccionado.setStockMinimo(Integer.parseInt(txtStockMinimo.getText().trim()));
            
            if (productoDAO.actualizar(productoSeleccionado)) {
                JOptionPane.showMessageDialog(this, "Producto actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarProductos();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar el producto.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Precio, stock y stock mínimo deben ser números válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error de validación", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarProducto() {
        if (productoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar el producto: " + productoSeleccionado.getNombre() + "?", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            if (productoDAO.eliminar(productoSeleccionado.getIdProducto())) {
                JOptionPane.showMessageDialog(this, "Producto eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarProductos();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el producto.\nPuede tener ventas asociadas.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void buscarProducto() {
        String termino = JOptionPane.showInputDialog(this, "Ingrese código o nombre del producto:", "Buscar Producto", JOptionPane.QUESTION_MESSAGE);
        
        if (termino != null && !termino.trim().isEmpty()) {
            modeloTabla.setRowCount(0);
            
            // Buscar por código
            Producto porCodigo = productoDAO.buscarPorCodigo(termino.trim());
            if (porCodigo != null) {
                Object[] fila = {
                    porCodigo.getIdProducto(),
                    porCodigo.getCodigo(),
                    porCodigo.getNombre(),
                    porCodigo.getCategoria(),
                    String.format("%.2f", porCodigo.getPrecio()),
                    porCodigo.getStock(),
                    porCodigo.getStockMinimo()
                };
                modeloTabla.addRow(fila);
            } else {
                // Buscar por nombre
                List<Producto> porNombre = productoDAO.buscarPorNombre(termino.trim());
                for (Producto p : porNombre) {
                    Object[] fila = {
                        p.getIdProducto(),
                        p.getCodigo(),
                        p.getNombre(),
                        p.getCategoria(),
                        String.format("%.2f", p.getPrecio()),
                        p.getStock(),
                        p.getStockMinimo()
                    };
                    modeloTabla.addRow(fila);
                }
                
                if (porNombre.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No se encontraron productos.", "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
                    cargarProductos();
                }
            }
        }
    }
    
    private boolean validarCampos() {
        if (txtCodigo.getText().trim().isEmpty() || 
            txtNombre.getText().trim().isEmpty() || 
            txtCategoria.getText().trim().isEmpty() || 
            txtPrecio.getText().trim().isEmpty() || 
            txtStock.getText().trim().isEmpty() || 
            txtStockMinimo.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
    
    private void limpiarCampos() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtCategoria.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        txtStockMinimo.setText("");
        txtCodigo.setEditable(true);
        productoSeleccionado = null;
        tablaProductos.clearSelection();
    }
}
