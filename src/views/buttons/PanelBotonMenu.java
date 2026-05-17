package views.buttons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PanelBotonMenu extends JPanel {
    private Color colorBase;
    private Color colorHover;
    private Color colorActual;
    private int radioEsquinas = 32;
    private boolean isHovered = false;

    public PanelBotonMenu(String emoji, String titulo, String descripcion, Color colorBase, ActionListener listener) {
        this.colorBase = colorBase;
        this.colorActual = colorBase;
        this.colorHover = new Color(
            Math.min(255, colorBase.getRed() + 30),
            Math.min(255, colorBase.getGreen() + 30),
            Math.min(255, colorBase.getBlue() + 30)
        );

        setLayout(new GridLayout(2, 1, 0, 0));
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        // Fila 1: Emoji
        JLabel lblIcono = new JLabel(emoji, SwingConstants.CENTER);
        lblIcono.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 38));
        lblIcono.setForeground(Color.WHITE);

        // Fila 2: Panel título y descripción, ambos centrados y alineados
        JPanel panelTexto = new JPanel();
        panelTexto.setLayout(new BoxLayout(panelTexto, BoxLayout.Y_AXIS));
        panelTexto.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDescripcion = new JLabel(descripcion, SwingConstants.CENTER);
        lblDescripcion.setFont(new Font("Arial", Font.PLAIN, 12));
        lblDescripcion.setForeground(new Color(245, 245, 245));
        lblDescripcion.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelTexto.add(lblTitulo);
        panelTexto.add(Box.createVerticalStrut(4));
        panelTexto.add(lblDescripcion);

        add(lblIcono);
        add(panelTexto);

        agregarEfectos(listener);
    }

    private void agregarEfectos(ActionListener listener) {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                colorActual = colorHover;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                colorActual = colorBase;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                colorActual = colorBase.darker();
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isHovered) {
                    colorActual = colorHover;
                } else {
                    colorActual = colorBase;
                }
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (listener != null) {
                    listener.actionPerformed(new java.awt.event.ActionEvent(
                        PanelBotonMenu.this,
                        java.awt.event.ActionEvent.ACTION_PERFORMED,
                        "click"
                    ));
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        // Activar antialiasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Dibujar fondo con bordes redondeados
        g2.setColor(colorActual);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radioEsquinas, radioEsquinas);

        // Dibujar borde del mismo color pero más oscuro
        g2.setColor(colorActual.darker()); // ✅ Automáticamente más oscuro que el fondo
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radioEsquinas, radioEsquinas);

        g2.dispose();

        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 120);
    }
}