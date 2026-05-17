package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import dao.ConexionDB;
import dao.UsuarioDAO;
import models.Usuario;
import views.buttons.BotonEstilizado;
import views.styles.TableStyler;

import java.awt.*;
import java.sql.Connection;
import java.util.List;

public class VentanaUsuarios extends JFrame {
    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;
    private JTextField txtNombreUsuario, txtNombres, txtApellidos, txtPassword;
    private JComboBox<String> cbRol;
    private JButton btnAgregar, btnLimpiar;
    private UsuarioDAO usuarioDAO;
    
    public VentanaUsuarios(JFrame parent) {
        Connection conn = ConexionDB.getConexion();
        usuarioDAO = new UsuarioDAO(conn);
        
        configurarVentana();
        inicializarComponentes();
        cargarUsuarios();
    }
    
    private void configurarVentana() {
        setTitle("Gestión de Usuarios");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }
    
    private void inicializarComponentes() {
        // Panel superior
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        panelTitulo.setBackground(new Color(241, 196, 15)); // Amarillo dorado menú usuarios
        panelTitulo.setPreferredSize(new Dimension(0, 56));

        JLabel lblIcono = new JLabel("🗄");
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        lblIcono.setForeground(Color.WHITE);

        JLabel lblTitulo = new JLabel("Gestión de Usuarios");
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
        
        add(panelTitulo, BorderLayout.NORTH);
        add(panelFormulario, BorderLayout.WEST);
        add(panelTabla, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(149, 165, 166), 2),
            "Datos del Usuario",
            0, 0, new Font("Arial", Font.BOLD, 14), new Color(149, 165, 166)
        ));
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.gridx = 0;
        
        // Nombre de usuario
        gbc.gridy = 0;
        panel.add(new JLabel("Nombre de Usuario:"), gbc);
        gbc.gridy = 1;
        txtNombreUsuario = new JTextField(15);
        panel.add(txtNombreUsuario, gbc);
        
        // Contraseña
        gbc.gridy = 2;
        panel.add(new JLabel("Contraseña:"), gbc);
        gbc.gridy = 3;
        txtPassword = new JPasswordField(15);
        panel.add(txtPassword, gbc);
        
        // Nombres
        gbc.gridy = 4;
        panel.add(new JLabel("Nombres:"), gbc);
        gbc.gridy = 5;
        txtNombres = new JTextField(15);
        panel.add(txtNombres, gbc);
        
        // Apellidos
        gbc.gridy = 6;
        panel.add(new JLabel("Apellidos:"), gbc);
        gbc.gridy = 7;
        txtApellidos = new JTextField(15);
        panel.add(txtApellidos, gbc);
        
        // Rol
        gbc.gridy = 8;
        panel.add(new JLabel("Rol:"), gbc);
        gbc.gridy = 9;
        cbRol = new JComboBox<>(new String[]{"ADMINISTRADOR", "VENDEDOR"});
        panel.add(cbRol, gbc);
        
        return panel;
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        
        String[] columnas = {"ID", "Usuario", "Nombres", "Apellidos", "Rol"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaUsuarios = new JTable(modeloTabla);
        tablaUsuarios.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaUsuarios.getColumnModel().getColumn(1).setPreferredWidth(120);
        tablaUsuarios.getColumnModel().getColumn(2).setPreferredWidth(150);
        tablaUsuarios.getColumnModel().getColumn(3).setPreferredWidth(150);
        
        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        
        // Aplicar estilo de macOS a la tabla con color del título
        TableStyler.aplicarEstiloMacOS(tablaUsuarios, new Color(241, 196, 15));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(Color.WHITE);
        
        btnAgregar = BotonEstilizado.crearBotonEstilizado("Agregar Usuario", new Color(46, 204, 113), new Dimension(200, 35));
        btnAgregar.addActionListener(e -> agregarUsuario());
        
        btnLimpiar = BotonEstilizado.crearBotonEstilizado("Limpiar Campos", new Color(149, 165, 166), new Dimension(200, 35));
        btnLimpiar.addActionListener(e -> limpiarCampos());
        
        panel.add(btnAgregar);
        panel.add(btnLimpiar);
        
        return panel;
    }
    
    private void cargarUsuarios() {
        modeloTabla.setRowCount(0);
        List<Usuario> usuarios = usuarioDAO.listarUsuarios();
        
        for (Usuario u : usuarios) {
            Object[] fila = {
                u.getIdUsuario(),
                u.getNombreUsuario(),
                u.getNombres(),
                u.getApellidos(),
                u.getRol()
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private void agregarUsuario() {
        try {
            if (!validarCampos()) return;
            
            Usuario usuario = new Usuario();
            usuario.setNombreUsuario(txtNombreUsuario.getText().trim());
            usuario.setPassword(txtPassword.getText().trim());
            usuario.setNombres(txtNombres.getText().trim());
            usuario.setApellidos(txtApellidos.getText().trim());
            usuario.setRol((String) cbRol.getSelectedItem());
            
            if (usuarioDAO.insertarUsuario(usuario)) {
                JOptionPane.showMessageDialog(this, 
                    "Usuario registrado exitosamente.", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                cargarUsuarios();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al registrar el usuario.\nEl nombre de usuario puede estar en uso.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error de validación", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validarCampos() {
        if (txtNombreUsuario.getText().trim().isEmpty() || 
            txtPassword.getText().trim().isEmpty() || 
            txtNombres.getText().trim().isEmpty() || 
            txtApellidos.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (txtPassword.getText().trim().length() < 4) {
            JOptionPane.showMessageDialog(this, "La contraseña debe tener al menos 4 caracteres.", "Contraseña inválida", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void limpiarCampos() {
        txtNombreUsuario.setText("");
        txtPassword.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        cbRol.setSelectedIndex(0);
    }
}
