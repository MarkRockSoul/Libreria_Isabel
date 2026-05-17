package views.buttons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class BotonEstilizado extends JButton {
    
    private Color colorBase;
    private Color colorHover;
    private int radioEsquinas = 15;
    
    /**
     * Constructor privado para uso interno
     */
    private BotonEstilizado(String texto, Color colorBase, Dimension dimension, int radioEsquinas) {
        super(texto);
        this.colorBase = colorBase;
        this.radioEsquinas = radioEsquinas;
        this.colorHover = new Color(
            Math.min(255, colorBase.getRed() + 30),
            Math.min(255, colorBase.getGreen() + 30),
            Math.min(255, colorBase.getBlue() + 30)
        );
        
        setPreferredSize(dimension);
        setFont(new Font("Arial", Font.BOLD, 15));
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Configuración compatible con macOS
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        
        agregarEfectoHover();
    }
    
    /**
     * Método estático para crear botones estilizados
     */
    public static JButton crearBotonEstilizado(String texto, Color colorBase, Dimension dimension) {
        return new BotonEstilizado(texto, colorBase, dimension, 15);
    }
    
    /**
     * Método estático con radio de esquinas personalizado
     */
    public static JButton crearBotonEstilizado(String texto, Color colorBase, Dimension dimension, int radioEsquinas) {
        return new BotonEstilizado(texto, colorBase, dimension, radioEsquinas);
    }
    
    /**
     * Agrega el efecto hover al botón
     */
    private void agregarEfectoHover() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(colorHover);
                    repaint();
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(colorBase);
                    repaint();
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(colorBase.darker());
                    repaint();
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(colorHover);
                    repaint();
                }
            }
        });
        
        setBackground(colorBase);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        // Activar antialiasing para bordes suaves
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Dibujar fondo con bordes redondeados
        if (getModel().isPressed()) {
            g2.setColor(colorBase.darker());
        } else if (getModel().isRollover()) {
            g2.setColor(colorHover);
        } else {
            g2.setColor(getBackground());
        }
        
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radioEsquinas, radioEsquinas);
        
        // Dibujar borde más delgado
        g2.setColor(getBackground().darker());
        g2.setStroke(new BasicStroke(1)); // ✅ Cambiado de 2 a 1
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radioEsquinas, radioEsquinas);
        
        g2.dispose();
        
        // Dibujar texto
        super.paintComponent(g);
    }
    
    @Override
    protected void paintBorder(Graphics g) {
        // No pintar borde por defecto, ya se dibuja en paintComponent
    }
    
    @Override
    public boolean contains(int x, int y) {
        // Hacer que el área de clic coincida con la forma redondeada
        if (radioEsquinas == 0) {
            return super.contains(x, y);
        }
        
        RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radioEsquinas, radioEsquinas);
        return shape.contains(x, y);
    }
}