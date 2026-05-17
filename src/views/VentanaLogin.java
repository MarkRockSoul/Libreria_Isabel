package views;

import javax.swing.*;
import dao.ConexionDB;
import dao.UsuarioDAO;
import models.Usuario;
import views.buttons.BotonEstilizado;
import java.awt.*;
import java.sql.Connection;

public class VentanaLogin extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;
    private UsuarioDAO usuarioDAO;
    
    public VentanaLogin() {
        // Establecer conexión
        Connection conn = ConexionDB.getConexion();
        if (conn == null) {
            JOptionPane.showMessageDialog(null, 
                "Error al conectar con la base de datos.\nVerifique la configuración.", 
                "Error de Conexión", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        usuarioDAO = new UsuarioDAO(conn);
        configurarVentana();
        inicializarComponentes();
    }
    
    private void configurarVentana() {
        setTitle("Login - Librería Isabel");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);
    }
    
    private void inicializarComponentes() {
        // Panel superior con icono y título
        JPanel panelSuperior = new JPanel();
        panelSuperior.setBackground(new Color(6, 90, 130));
        panelSuperior.setPreferredSize(new Dimension(0, 100));
        panelSuperior.setLayout(new GridBagLayout());
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbcTitulo = new GridBagConstraints();
        gbcTitulo.gridx = 0;
        gbcTitulo.insets = new Insets(5, 0, 5, 0);
        
        // Panel para icono + título en la misma línea
        JPanel panelTituloIcono = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        panelTituloIcono.setOpaque(false);
        
        JLabel lblIcono = new JLabel("📚");
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        lblIcono.setForeground(Color.WHITE);
        
        JLabel lblTitulo = new JLabel("LIBRERÍA ISABEL");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        
        panelTituloIcono.add(lblIcono);
        panelTituloIcono.add(lblTitulo);
        
        gbcTitulo.gridy = 0;
        panelSuperior.add(panelTituloIcono, gbcTitulo);
        
        JLabel lblSubtitulo = new JLabel("Sistema de Gestión de Ventas");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 16));
        lblSubtitulo.setForeground(new Color(220, 220, 220));
        gbcTitulo.gridy = 1;
        panelSuperior.add(lblSubtitulo, gbcTitulo);
        
        // Panel central con formulario
        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBackground(Color.WHITE);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(40, 60, 30, 60));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 15, 5, 15);
        gbc.gridx = 0;
        
        // Usuario
        gbc.gridy = 0;
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 15));
        panelCentral.add(lblUsuario, gbc);
        
        gbc.gridy = 1;
        txtUsuario = crearTextFieldRedondeado(20);
        panelCentral.add(txtUsuario, gbc);
        
        // Contraseña
        gbc.gridy = 2;
        gbc.insets = new Insets(8, 15, 5, 15);
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 15));
        panelCentral.add(lblPassword, gbc);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 15, 5, 15);
        txtPassword = crearPasswordFieldRedondeado(20);
        panelCentral.add(txtPassword, gbc);
        
        // Botón de iniciar sesión
        gbc.gridy = 4;
        gbc.insets = new Insets(25, 15, 10, 15);
        btnIngresar = BotonEstilizado.crearBotonEstilizado(
            "Iniciar Sesión", 
            new Color(6, 90, 130), 
            new Dimension(280, 45)
        );
        btnIngresar.addActionListener(e -> iniciarSesion());
        panelCentral.add(btnIngresar, gbc);
        
        // Panel inferior con información
        JPanel panelInferior = new JPanel();
        panelInferior.setBackground(new Color(245, 245, 245));
        panelInferior.setPreferredSize(new Dimension(0, 50));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblInfo = new JLabel("💡 Usuario demo: admin / admin123");
        lblInfo.setFont(new Font("Arial", Font.ITALIC, 12));
        lblInfo.setForeground(new Color(100, 100, 100));
        panelInferior.add(lblInfo);
        
        // Agregar paneles
        add(panelSuperior, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
        
        // Enter en campos para login
        txtUsuario.addActionListener(e -> txtPassword.requestFocus());
        txtPassword.addActionListener(e -> iniciarSesion());
    }
    
    /**
     * Crea un JTextField con bordes redondeados
     */
    private JTextField crearTextFieldRedondeado(int columnas) {
        JTextField field = new JTextField(columnas) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(200, 200, 200));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(280, 40));
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        return field;
    }
    
    /**
     * Crea un JPasswordField con bordes redondeados
     */
    private JPasswordField crearPasswordFieldRedondeado(int columnas) {
        JPasswordField field = new JPasswordField(columnas) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(200, 200, 200));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(280, 40));
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        return field;
    }
    
    private void iniciarSesion() {
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, ingrese usuario y contraseña.", 
                "Campos vacíos", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validar credenciales
        Usuario usuarioValidado = usuarioDAO.validarLogin(usuario, password);
        
        if (usuarioValidado != null) {
            // Login exitoso
            dispose();
            VentanaPrincipal ventanaPrincipal = new VentanaPrincipal(usuarioValidado);
            ventanaPrincipal.setVisible(true);
        } else {
            // Login fallido
            JOptionPane.showMessageDialog(this, 
                "Usuario o contraseña incorrectos.", 
                "Error de autenticación", 
                JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtUsuario.requestFocus();
        }
    }
    
    public static void main(String[] args) {
        // Configuración específica para macOS
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", "Librería Isabel");
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            VentanaLogin login = new VentanaLogin();
            login.setVisible(true);
        });
    }
}