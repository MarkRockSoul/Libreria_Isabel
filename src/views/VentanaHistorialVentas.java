package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import dao.ClienteDAO;
import dao.ConexionDB;
import dao.VentaDAO;
import models.Cliente;
import models.Venta;
import views.buttons.BotonEstilizado;
import views.styles.TableStyler;

import java.awt.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.List;

public class VentanaHistorialVentas extends JFrame {
    private JTable tablaHistorial;
    private DefaultTableModel modeloTabla;
    private JTextField txtDniCliente;
    private JLabel lblInformacionCliente;
    private ClienteDAO clienteDAO;
    private VentaDAO ventaDAO;
    
    public VentanaHistorialVentas(JFrame parent) {
        Connection conn = ConexionDB.getConexion();
        clienteDAO = new ClienteDAO(conn);
        ventaDAO = new VentaDAO(conn);
        
        configurarVentana();
        inicializarComponentes();
    }
    
    private void configurarVentana() {
        setTitle("Historial de Ventas por Cliente");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }
    
    private void inicializarComponentes() {
        // Panel superior 
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        panelTitulo.setBackground(new Color(155, 89, 182)); // Color púrpura del menú principal
        panelTitulo.setPreferredSize(new Dimension(0, 56));

        JLabel lblIcono = new JLabel("📜");
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        lblIcono.setForeground(Color.WHITE);

        JLabel lblTitulo = new JLabel("Historial de Ventas por Cliente");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);

        panelTitulo.add(lblIcono);
        panelTitulo.add(lblTitulo);
        
        // Panel de búsqueda
        JPanel panelBusqueda = crearPanelBusqueda();
        
        // Panel de información del cliente
        JPanel panelInfoCliente = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panelInfoCliente.setBackground(new Color(240, 240, 240));
        panelInfoCliente.setBorder(BorderFactory.createTitledBorder("Información del Cliente"));
        
        lblInformacionCliente = new JLabel("Busque un cliente para ver su historial");
        lblInformacionCliente.setFont(new Font("Arial", Font.PLAIN, 13));
        panelInfoCliente.add(lblInformacionCliente);
        
        // Panel de tabla
        JPanel panelTabla = crearPanelTabla();
        
        // Panel central combinado
        JPanel panelCentral = new JPanel(new BorderLayout(5, 5));
        panelCentral.setBackground(Color.WHITE);
        panelCentral.add(panelInfoCliente, BorderLayout.NORTH);
        panelCentral.add(panelTabla, BorderLayout.CENTER);
        
        add(panelTitulo, BorderLayout.NORTH);
        add(panelBusqueda, BorderLayout.WEST);
        add(panelCentral, BorderLayout.CENTER);
    }
    
    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(250, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        
        JLabel lblTitulo = new JLabel("Buscar Cliente");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridy = 0;
        panel.add(lblTitulo, gbc);
        
        gbc.gridy = 1;
        panel.add(new JLabel("DNI del Cliente:"), gbc);
        
        txtDniCliente = new JTextField(15);
        gbc.gridy = 2;
        panel.add(txtDniCliente, gbc);
        
        JButton btnBuscar = BotonEstilizado.crearBotonEstilizado("Buscar", new Color(46, 204, 113), new Dimension(140, 35));
        btnBuscar.addActionListener(e -> buscarHistorial());
        gbc.gridy = 3;
        panel.add(btnBuscar, gbc);
        
        JButton btnLimpiar = BotonEstilizado.crearBotonEstilizado("Limpiar", new Color(149, 165, 166), new Dimension(140, 35));
        btnLimpiar.addActionListener(e -> limpiar());
        gbc.gridy = 4;
        panel.add(btnLimpiar, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        
        String[] columnas = {"ID Venta", "Fecha y Hora", "Total", "Usuario ID"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaHistorial = new JTable(modeloTabla);
        tablaHistorial.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaHistorial.getColumnModel().getColumn(1).setPreferredWidth(200);
        
        JScrollPane scrollPane = new JScrollPane(tablaHistorial);
        
        // Aplicar estilo de macOS a la tabla con color del título
        TableStyler.aplicarEstiloMacOS(tablaHistorial, new Color(155, 89, 182));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void buscarHistorial() {
        String dni = txtDniCliente.getText().trim();
        
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un DNI.", "Campo vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Buscar cliente
        Cliente cliente = clienteDAO.buscarPorDNI(dni);
        
        if (cliente == null) {
            JOptionPane.showMessageDialog(this, "No se encontró cliente con ese DNI.", "Cliente no encontrado", JOptionPane.WARNING_MESSAGE);
            limpiar();
            return;
        }
        
        lblInformacionCliente.setText("Cliente: " + cliente.getNombre() + " | DNI: " + cliente.getDni() + " | Tel: " + cliente.getTelefono());
        
        // Cargar historial
        cargarHistorial(cliente.getIdCliente());
    }
    
    private void cargarHistorial(int idCliente) {
        modeloTabla.setRowCount(0);
        List<Venta> historial = ventaDAO.obtenerHistorialCliente(idCliente);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        double totalHistorico = 0.0;
        
        for (Venta v : historial) {
            Object[] fila = {
                v.getIdVenta(),
                sdf.format(v.getFecha()),
                "S/. " + String.format("%.2f", v.getTotal()),
                v.getIdUsuario()
            };
            modeloTabla.addRow(fila);
            totalHistorico += v.getTotal();
        }
        
        if (historial.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "El cliente no tiene compras registradas.", 
                "Sin historial", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Total de compras: " + historial.size() + "\n" +
                "Monto histórico total: S/. " + String.format("%.2f", totalHistorico), 
                "Resumen", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void limpiar() {
        txtDniCliente.setText("");
        lblInformacionCliente.setText("Busque un cliente para ver su historial");
        modeloTabla.setRowCount(0);
    }
}
