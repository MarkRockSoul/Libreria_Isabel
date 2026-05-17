package views.styles;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Clase utilitaria para aplicar estilos de macOS a las tablas JTable
 * Proporciona métodos para estilizar tablas con la apariencia nativa de macOS
 */
public class TableStyler {
    
    // Colores de macOS por defecto
    private static final Color DEFAULT_HEADER_BACKGROUND = new Color(230, 230, 230);
    private static final Color ROW_BACKGROUND = Color.WHITE;
    private static final Color ALT_ROW_BACKGROUND = new Color(245, 245, 245);
    private static final Color SELECTED_BACKGROUND = new Color(0, 122, 255);
    private static final Color SELECTED_FOREGROUND = Color.WHITE;
    private static final Color GRID_COLOR = new Color(211, 211, 211);
    private static final int HEADER_HEIGHT = 40;
    
    /**
     * Aplica el estilo de macOS a una tabla JTable con color de header personalizado
     * @param table La tabla a estilizar
     * @param headerColor El color para el header de la tabla
     */
    public static void aplicarEstiloMacOS(JTable table, Color headerColor) {
        // Configurar altura de filas para un aspecto más macOS
        table.setRowHeight(26);
        
        // Configurar colores y fuente del header
        JTableHeader header = table.getTableHeader();
        header.setBackground(headerColor);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("System", Font.BOLD, 13));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, GRID_COLOR));
        header.setPreferredSize(new Dimension(header.getWidth(), HEADER_HEIGHT));
        
        // Configurar renderer del header con altura y centrado
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(headerColor);
                c.setForeground(Color.WHITE);
                setHorizontalAlignment(JLabel.LEFT);
                setVerticalAlignment(JLabel.CENTER);
                setFont(new Font("System", Font.BOLD, 13));
                setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 8));
                return c;
            }
        });
        
        // Configurar colores de grid
        table.setGridColor(GRID_COLOR);
        table.setShowGrid(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        
        // Configurar selección
        table.setSelectionBackground(SELECTED_BACKGROUND);
        table.setSelectionForeground(SELECTED_FOREGROUND);
        
        // Configurar renderer de celdas para alternancia de colores
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(SELECTED_BACKGROUND);
                    c.setForeground(SELECTED_FOREGROUND);
                } else {
                    // Alternancia de colores de fila para mejorar legibilidad
                    if (row % 2 == 0) {
                        c.setBackground(ROW_BACKGROUND);
                    } else {
                        c.setBackground(ALT_ROW_BACKGROUND);
                    }
                    c.setForeground(Color.BLACK);
                }
                
                // Padding en las celdas (mismo left que el header para alineación)
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 8));
                return c;
            }
        };
        
        // Aplicar el renderer a todas las columnas
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumn(table.getColumnName(i)).setCellRenderer(renderer);
        }
        
        // Configuración de fuente de la tabla
        table.setFont(new Font("System", Font.PLAIN, 13));
        table.setForeground(Color.BLACK);
    }
    
    /**
     * Aplica el estilo de macOS a una tabla JTable (con header gris por defecto)
     * @param table La tabla a estilizar
     */
    public static void aplicarEstiloMacOS(JTable table) {
        aplicarEstiloMacOS(table, DEFAULT_HEADER_BACKGROUND);
    }
    
    /**
     * Aplica el estilo de macOS a una tabla dentro de un JScrollPane con color personalizado
     * @param scrollPane El JScrollPane que contiene la tabla
     * @param headerColor El color para el header de la tabla
     */
    public static void aplicarEstiloMacOS(JScrollPane scrollPane, Color headerColor) {
        JTable table = (JTable) scrollPane.getViewport().getView();
        if (table != null) {
            aplicarEstiloMacOS(table, headerColor);
        }
    }
    
    /**
     * Aplica el estilo de macOS a una tabla dentro de un JScrollPane
     * @param scrollPane El JScrollPane que contiene la tabla
     */
    public static void aplicarEstiloMacOS(JScrollPane scrollPane) {
        aplicarEstiloMacOS(scrollPane, DEFAULT_HEADER_BACKGROUND);
    }
}

