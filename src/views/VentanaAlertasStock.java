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

public class VentanaAlertasStock extends JFrame {
    private JTable tablaAlertas;
    private DefaultTableModel modeloTabla;
    private ProductoDAO productoDAO;
    
    public VentanaAlertasStock(JFrame parent) {
        Connection conn = ConexionDB.getConexion();
        productoDAO = new ProductoDAO(conn);
        
        configurarVentana();
        inicializarComponentes();
        cargarAlertas();
    }
    
    private void configurarVentana() {
        setTitle("Alertas de Stock Bajo");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }
    
    private void inicializarComponentes() {
        // Panel superior con alerta
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(231, 76, 60));
        panelTitulo.setPreferredSize(new Dimension(0, 80));
        panelTitulo.setLayout(new BorderLayout());

        JPanel filaSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 22, 10));
        filaSuperior.setOpaque(false);

        JLabel lblIcono = new JLabel("🚨");
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        lblIcono.setForeground(Color.WHITE);

        JLabel lblTitulo = new JLabel("Alertas de Stock Bajo");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);

        filaSuperior.add(lblIcono);
        filaSuperior.add(lblTitulo);

        JLabel lblSubtitulo = new JLabel("Productos que necesitan reabastecimiento urgente", SwingConstants.LEFT);
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 15));
        lblSubtitulo.setForeground(new Color(255, 220, 220));
        lblSubtitulo.setBorder(BorderFactory.createEmptyBorder(0, 26, 5, 0)); // left indent

        panelTitulo.add(filaSuperior, BorderLayout.NORTH);
        panelTitulo.add(lblSubtitulo, BorderLayout.CENTER);
        
        // Panel de tabla
        JPanel panelTabla = crearPanelTabla();
        
        // Panel inferior con botones
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelInferior.setBackground(Color.WHITE);
        
        JButton btnActualizar  = BotonEstilizado.crearBotonEstilizado("Actualizar Alertas", new Color(52, 152, 219), new Dimension(170, 35));
        btnActualizar.addActionListener(e -> cargarAlertas());
        
        JButton btnCerrar = BotonEstilizado.crearBotonEstilizado("Cerrar", new Color(149, 165, 166), new Dimension(170, 35));
        btnCerrar.addActionListener(e -> dispose());
        
        panelInferior.add(btnActualizar);
        panelInferior.add(btnCerrar);
        
        add(panelTitulo, BorderLayout.NORTH);
        add(panelTabla, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        
        String[] columnas = {"⚠", "Código", "Nombre", "Categoría", "Stock Actual", "Stock Mínimo", "Faltante"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaAlertas = new JTable(modeloTabla);
        tablaAlertas.getColumnModel().getColumn(0).setPreferredWidth(40);
        tablaAlertas.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaAlertas.getColumnModel().getColumn(2).setPreferredWidth(300);
        
        // Aplicar estilo de macOS con color del título (rojo)
        TableStyler.aplicarEstiloMacOS(tablaAlertas, new Color(231, 76, 60));
        
        // Colorear las filas con fondo rojo claro para alertas
        tablaAlertas.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(new Color(255, 230, 230));
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaAlertas);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void cargarAlertas() {
        modeloTabla.setRowCount(0);
        List<Producto> productosAlerta = productoDAO.obtenerStockBajo();
        
        for (Producto p : productosAlerta) {
            int faltante = p.getStockMinimo() - p.getStock();
            Object[] fila = {
                "🔴",
                p.getCodigo(),
                p.getNombre(),
                p.getCategoria(),
                p.getStock(),
                p.getStockMinimo(),
                faltante > 0 ? faltante : 0
            };
            modeloTabla.addRow(fila);
        }
        
        if (productosAlerta.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "✓ No hay productos con stock bajo.\nTodos los productos tienen stock suficiente.", 
                "Sin alertas", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Mostrar alerta de sonido del sistema
            Toolkit.getDefaultToolkit().beep();
        }
    }
}
