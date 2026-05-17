package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import dao.ClienteDAO;
import dao.ConexionDB;
import models.Cliente;
import views.buttons.BotonEstilizado;
import views.styles.TableStyler;

import java.awt.*;
import java.sql.Connection;
import java.util.List;

public class VentanaClientes extends JFrame {
    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private JTextField txtDni, txtNombre, txtTelefono;
    private JButton btnAgregar, btnBuscar, btnLimpiar;
    private ClienteDAO clienteDAO;
    
    public VentanaClientes(JFrame parent) {
        Connection conn = ConexionDB.getConexion();
        clienteDAO = new ClienteDAO(conn);
        
        configurarVentana();
        inicializarComponentes();
        cargarClientes();
    }
    
    private void configurarVentana() {
        setTitle("Gestión de Clientes");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }
    
    private void inicializarComponentes() {
        // Panel superior 
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        panelTitulo.setBackground(new Color(39, 174, 96)); // Verde vibrante del menú principal
        panelTitulo.setPreferredSize(new Dimension(0, 56));

        JLabel lblIcono = new JLabel("👤");
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        lblIcono.setForeground(Color.WHITE);

        JLabel lblTitulo = new JLabel("Gestión de Clientes");
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
            BorderFactory.createLineBorder(new Color(46, 204, 113), 2),
            "Datos del Cliente",
            0, 0, new Font("Arial", Font.BOLD, 14), new Color(46, 204, 113)
        ));
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        
        // DNI
        gbc.gridy = 0;
        panel.add(new JLabel("DNI (8 dígitos):"), gbc);
        gbc.gridy = 1;
        txtDni = new JTextField(15);
        panel.add(txtDni, gbc);
        
        // Nombre
        gbc.gridy = 2;
        panel.add(new JLabel("Nombre Completo:"), gbc);
        gbc.gridy = 3;
        txtNombre = new JTextField(15);
        panel.add(txtNombre, gbc);
        
        // Teléfono
        gbc.gridy = 4;
        panel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridy = 5;
        txtTelefono = new JTextField(15);
        panel.add(txtTelefono, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        
        String[] columnas = {"ID", "DNI", "Nombre Completo", "Teléfono"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaClientes.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaClientes.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaClientes.getColumnModel().getColumn(2).setPreferredWidth(300);
        
        JScrollPane scrollPane = new JScrollPane(tablaClientes);
        
        // Aplicar estilo de macOS a la tabla con color del título
        TableStyler.aplicarEstiloMacOS(tablaClientes, new Color(39, 174, 96));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(Color.WHITE);
        
        btnAgregar = crearBoton("Agregar", new Color(46, 204, 113), new Dimension(140, 35), e -> agregarCliente());
        btnBuscar = crearBoton("Buscar por DNI", new Color(155, 89, 182), new Dimension(200, 35), e -> buscarCliente());
        btnLimpiar = crearBoton("Limpiar", new Color(149, 165, 166), new Dimension(140, 35), e -> limpiarCampos());
        
        panel.add(btnAgregar);
        panel.add(btnBuscar);
        panel.add(btnLimpiar);
        
        return panel;
    }
    
    private JButton crearBoton(String texto, Color color, Dimension dimension, java.awt.event.ActionListener listener) {
        JButton boton = BotonEstilizado.crearBotonEstilizado(texto, color, dimension);
        boton.addActionListener(listener);
        return boton;
    }
    
    private void cargarClientes() {
        modeloTabla.setRowCount(0);
        List<Cliente> clientes = clienteDAO.listar();
        
        for (Cliente c : clientes) {
            if (!c.getDni().equals("00000000")) { // Excluir cliente anónimo
                Object[] fila = {
                    c.getIdCliente(),
                    c.getDni(),
                    c.getNombre(),
                    c.getTelefono()
                };
                modeloTabla.addRow(fila);
            }
        }
    }
    
    private void agregarCliente() {
        try {
            if (!validarCampos()) return;
            
            Cliente cliente = new Cliente();
            cliente.setDni(txtDni.getText().trim());
            cliente.setNombre(txtNombre.getText().trim());
            cliente.setTelefono(txtTelefono.getText().trim());
            
            if (clienteDAO.insertar(cliente)) {
                JOptionPane.showMessageDialog(this, 
                    "Cliente registrado exitosamente.", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                cargarClientes();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "El DNI ya está registrado.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error de validación", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void buscarCliente() {
        String dni = JOptionPane.showInputDialog(this, "Ingrese el DNI del cliente:", "Buscar Cliente", JOptionPane.QUESTION_MESSAGE);
        
        if (dni != null && !dni.trim().isEmpty()) {
            Cliente cliente = clienteDAO.buscarPorDNI(dni.trim());
            
            if (cliente != null) {
                txtDni.setText(cliente.getDni());
                txtNombre.setText(cliente.getNombre());
                txtTelefono.setText(cliente.getTelefono());
                
                // Resaltar en la tabla
                for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                    if (modeloTabla.getValueAt(i, 1).equals(cliente.getDni())) {
                        tablaClientes.setRowSelectionInterval(i, i);
                        tablaClientes.scrollRectToVisible(tablaClientes.getCellRect(i, 0, true));
                        break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró cliente con ese DNI.", "No encontrado", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private boolean validarCampos() {
        if (txtDni.getText().trim().isEmpty() || txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "DNI y Nombre son obligatorios.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        String dni = txtDni.getText().trim();
        if (!dni.matches("\\d{8}")) {
            JOptionPane.showMessageDialog(this, "El DNI debe tener exactamente 8 dígitos numéricos.", "DNI inválido", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void limpiarCampos() {
        txtDni.setText("");
        txtNombre.setText("");
        txtTelefono.setText("");
        tablaClientes.clearSelection();
    }
}