package views;

import javax.swing.*;
import models.Usuario;
import views.buttons.PanelBotonMenu;

import java.awt.*;

public class VentanaPrincipal extends JFrame {
    private Usuario usuarioActual;
    private JLabel lblUsuario;
    private JLabel lblRol;
    
    public VentanaPrincipal(Usuario usuario) {
        this.usuarioActual = usuario;
        configurarVentana();
        inicializarComponentes();
    }
    
    private void configurarVentana() {
        setTitle("Sistema de Gestión - Librería Isabel");
        setSize(900, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);
    }
    
    private void inicializarComponentes() {
        // Panel superior con información del usuario
        JPanel panelSuperior = crearPanelSuperior();
        add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central con menú de opciones
        JPanel panelCentral = crearPanelCentral();
        add(panelCentral, BorderLayout.CENTER);
        
        // Panel inferior con información del sistema
        JPanel panelInferior = crearPanelInferior();
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(6, 90, 130));
        panel.setPreferredSize(new Dimension(0, 90));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        // ========== NUEVO: Icono + Título ==========
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 10));
        panelTitulo.setOpaque(false);

        JLabel lblIcono = new JLabel("📚"); // Usa 📚 o 🏬 según prefieras
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        lblIcono.setForeground(Color.WHITE);

        JLabel lblTitulo = new JLabel("LIBRERÍA ISABEL");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitulo.setForeground(Color.WHITE);

        panelTitulo.add(lblIcono);
        panelTitulo.add(lblTitulo);

        // Información del usuario
        JPanel panelUsuario = new JPanel(new GridLayout(2, 1, 0, 3));
        panelUsuario.setOpaque(false);

        lblUsuario = new JLabel("Usuario: " + usuarioActual.getNombreCompleto());
        lblUsuario.setFont(new Font("Arial", Font.PLAIN, 15));
        lblUsuario.setForeground(Color.WHITE);

        lblRol = new JLabel("Rol: " + usuarioActual.getRol());
        lblRol.setFont(new Font("Arial", Font.PLAIN, 13));
        lblRol.setForeground(new Color(210, 210, 210));

        panelUsuario.add(lblUsuario);
        panelUsuario.add(lblRol);

        panel.add(panelTitulo, BorderLayout.WEST);
        panel.add(panelUsuario, BorderLayout.EAST);

        return panel;
    }
    
    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new GridLayout(3, 3, 25, 25));
        panel.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));
        panel.setBackground(new Color(245, 245, 245));

        // Colores propuestos:
        // Productos: azul oscuro-vibrante
        panel.add(new PanelBotonMenu("📦", "Gestión de Productos", "Administrar productos",
            new Color(44, 62, 80), e->abrirGestionProductos()));
        // Clientes: verde intenso lima
        panel.add(new PanelBotonMenu("👤", "Gestión de Clientes", "Administrar clientes",
            new Color(39, 174, 96), e->abrirGestionClientes()));
        // Venta:   naranja-rojo brillante
        panel.add(new PanelBotonMenu("🛒", "Registrar Venta", "Nueva venta",
            new Color(243, 156, 18), e->abrirRegistroVenta()));
        // Reportes: celeste muy vibrante
        panel.add(new PanelBotonMenu("📊", "Reportes", "Ver reportes y estadísticas",
            new Color(41, 128, 185), e->abrirReportes()));
        // Alertas: rojo intenso (peligro)
        panel.add(new PanelBotonMenu("🚨", "Alertas de Stock", "Productos con stock bajo",
            new Color(192, 57, 43), e->abrirAlertasStock()));
        // Historial: púrpura fuerte
        panel.add(new PanelBotonMenu("📜", "Historial de Ventas", "Consultar ventas",
            new Color(155, 89, 182), e->abrirHistorialVentas()));
        
        // Usuarios: amarillo dorado
        if (usuarioActual.getRol().equals("ADMINISTRADOR")) {
            panel.add(new PanelBotonMenu("🗄", "Gestión de Usuarios", "Administrar usuarios",
                new Color(241, 196, 15), e->abrirGestionUsuarios()));
        } else {
            panel.add(new JPanel());
        }

        // Info: azul claro  
        panel.add(new PanelBotonMenu("ℹ", "Acerca de", "Información del sistema",
            new Color(52, 152, 219), e->mostrarAcercaDe()));
        // Cerrar sesión: gris oscuro
        panel.add(new PanelBotonMenu("🚪", "Cerrar Sesión", "Salir del sistema",
            new Color(52, 73, 94), e->cerrarSesion()));

        return panel;
    }
    
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(6, 90, 130));
        panel.setPreferredSize(new Dimension(0, 45));
        
        JLabel lblInfo = new JLabel("© 2026 Librería Isabel - Sistema de Gestión de Ventas v1.0");
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        
        panel.add(lblInfo);
        return panel;
    }
    
    private void abrirGestionProductos() {
        VentanaProductos ventana = new VentanaProductos(this);
        ventana.setVisible(true);
    }
    
    private void abrirGestionClientes() {
        VentanaClientes ventana = new VentanaClientes(this);
        ventana.setVisible(true);
    }
    
    private void abrirRegistroVenta() {
        VentanaVentas ventana = new VentanaVentas(this, usuarioActual);
        ventana.setVisible(true);
    }
    
    private void abrirReportes() {
        VentanaReportes ventana = new VentanaReportes(this);
        ventana.setVisible(true);
    }
    
    private void abrirAlertasStock() {
        VentanaAlertasStock ventana = new VentanaAlertasStock(this);
        ventana.setVisible(true);
    }
    
    private void abrirHistorialVentas() {
        VentanaHistorialVentas ventana = new VentanaHistorialVentas(this);
        ventana.setVisible(true);
    }
    
    private void abrirGestionUsuarios() {
        VentanaUsuarios ventana = new VentanaUsuarios(this);
        ventana.setVisible(true);
    }
    
    private void mostrarAcercaDe() {
        String mensaje = "Sistema de Gestión de Ventas\n" +
                        "Librería Isabel\n\n" +
                        "Versión: 1.0\n" +
                        "Desarrollado para automatizar:\n" +
                        "- Registro de ventas\n" +
                        "- Control de inventario\n" +
                        "- Gestión de clientes\n" +
                        "- Reportes y alertas\n\n" +
                        "© 2026 Todos los derechos reservados";
        
        JOptionPane.showMessageDialog(this, mensaje, "Acerca de", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de que desea cerrar sesión?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION
        );
        
        if (opcion == JOptionPane.YES_OPTION) {
            dispose();
            VentanaLogin login = new VentanaLogin();
            login.setVisible(true);
        }
    }
}