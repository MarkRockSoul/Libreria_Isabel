package views;

import models.Usuario;
import javax.swing.*;
import java.awt.*;

// Ventana principal de navegación (post-login)
public class MainMenuView extends JFrame {

    private Usuario sesion;

    public MainMenuView(Usuario sesion) {
        this.sesion = sesion;
        setTitle("Librería Isabel — Menú principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 380);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // ── Encabezado ────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        JLabel lblBienvenida = new JLabel("Bienvenido, " + sesion.getUsuario(), JLabel.LEFT);
        lblBienvenida.setFont(new Font("SansSerif", Font.BOLD, 15));
        JLabel lblRol = new JLabel("Rol: " + sesion.getRol(), JLabel.LEFT);
        lblRol.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblRol.setForeground(Color.GRAY);
        header.add(lblBienvenida, BorderLayout.CENTER);
        header.add(lblRol, BorderLayout.SOUTH);
        panel.add(header, BorderLayout.NORTH);

        // ── Botones de módulos ────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new GridLayout(4, 1, 0, 12));

        JButton btnProductos = crearBoton("📦  Gestión de Productos", new Color(83, 74, 183));
        JButton btnVentas    = crearBoton("🛒  Registrar Venta",       new Color(15, 110, 86));
        JButton btnReportes  = crearBoton("📊  Reportes e Inventario",  new Color(100, 100, 140));
        JButton btnSalir     = crearBoton("🚪  Cerrar sesión",           new Color(180, 60, 40));

        btnPanel.add(btnProductos);
        btnPanel.add(btnVentas);
        btnPanel.add(btnReportes);
        btnPanel.add(btnSalir);
        panel.add(btnPanel, BorderLayout.CENTER);

        // ── Footer ────────────────────────────────────────────────────────
        JLabel footer = new JLabel("© 2025 Librería Isabel — Lima, Perú", JLabel.CENTER);
        footer.setFont(new Font("SansSerif", Font.PLAIN, 10));
        footer.setForeground(Color.GRAY);
        panel.add(footer, BorderLayout.SOUTH);

        add(panel);

        // ── Acciones ──────────────────────────────────────────────────────
        btnProductos.addActionListener(e -> {
            // Solo el administrador puede gestionar productos
            if (sesion.getRol().equals("vendedor")) {
                JOptionPane.showMessageDialog(this,
                    "Solo el administrador puede gestionar productos.",
                    "Acceso restringido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            new ProductosView().setVisible(true);
        });

        btnVentas.addActionListener(e -> new VentaView(sesion).setVisible(true));

        btnReportes.addActionListener(e -> {
            if (sesion.getRol().equals("vendedor")) {
                JOptionPane.showMessageDialog(this,
                    "Solo el administrador puede ver reportes.",
                    "Acceso restringido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            new ReportesView().setVisible(true);
        });

        // RF-12 — Cerrar sesión
        btnSalir.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "¿Desea cerrar sesión?", "Cerrar sesión", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginView().setVisible(true);
            }
        });
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
