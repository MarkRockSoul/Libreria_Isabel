package views;

import controllers.VentaController;
import dao.ConexionDB;
import dao.UsuarioDAO;
import models.Usuario;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

// RF-01 | RF-12 — Ventana de inicio de sesión
public class LoginView extends JFrame {

    private JTextField     txtUsuario;
    private JPasswordField txtContrasena;
    private JLabel         lblError;
    private JButton        btnIngresar;

    public LoginView() {
        setTitle("Librería Isabel — Iniciar sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 280);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // ── Título ────────────────────────────────────────────────────────
        JLabel lblTitulo = new JLabel("📚  Librería Isabel", JLabel.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2; panel.add(lblTitulo, gbc);

        JLabel lblSub = new JLabel("Sistema de Ventas e Inventario", JLabel.CENTER);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblSub.setForeground(Color.GRAY);
        gbc.gridy=1; panel.add(lblSub, gbc);

        // ── Campos ────────────────────────────────────────────────────────
        gbc.gridwidth=1; gbc.gridy=2; gbc.gridx=0;
        panel.add(new JLabel("Usuario:"), gbc);
        txtUsuario = new JTextField(16);
        gbc.gridx=1; panel.add(txtUsuario, gbc);

        gbc.gridy=3; gbc.gridx=0;
        panel.add(new JLabel("Contraseña:"), gbc);
        txtContrasena = new JPasswordField(16);
        gbc.gridx=1; panel.add(txtContrasena, gbc);

        // ── Botón ─────────────────────────────────────────────────────────
        btnIngresar = new RoundedButton("Iniciar sesión");
        btnIngresar.setBackground(new Color(83, 74, 183));
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridy=4; gbc.gridx=0; gbc.gridwidth=2; panel.add(btnIngresar, gbc);

        // ── Mensaje de error ──────────────────────────────────────────────
        lblError = new JLabel("", JLabel.CENTER);
        lblError.setForeground(Color.RED);
        lblError.setFont(new Font("SansSerif", Font.PLAIN, 11));
        gbc.gridy=5; panel.add(lblError, gbc);

        add(panel);

        // ── Acción del botón ──────────────────────────────────────────────
        btnIngresar.addActionListener(e -> iniciarSesion());
        // Enter en el campo contraseña también dispara el login
        txtContrasena.addActionListener(e -> iniciarSesion());
    }

    private void iniciarSesion() {
        String user = txtUsuario.getText().trim();
        String pass = new String(txtContrasena.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            lblError.setText("Ingrese usuario y contraseña.");
            return;
        }

        // Lógica de negocio en DAO/Controller, NO en la vista (MVC)
        Connection conn = ConexionDB.getConexion();
        if (conn == null) {
            lblError.setText("Sin conexión a la base de datos.");
            return;
        }
        UsuarioDAO dao = new UsuarioDAO(conn);
        Usuario sesion = dao.login(user, pass);

        if (sesion != null) {
            dispose();
            new MainMenuView(sesion).setVisible(true);  // pasa la sesión al menú
        } else {
            lblError.setText("Usuario o contraseña incorrectos.");
            txtContrasena.setText("");
        }
    }
}
