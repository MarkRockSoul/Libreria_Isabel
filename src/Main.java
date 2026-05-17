import views.VentanaLogin;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Establecer Look and Feel de macOS Aqua
        try {
            // Intenta usar Aqua L&F de macOS primero
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("mac")) {
                UIManager.setLookAndFeel("com.apple.laf.AquaLookAndFeel");
            } else {
                // Fallback al L&F del sistema para otras plataformas
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        } catch (Exception e) {
            // Si Aqua no está disponible, usar el L&F del sistema
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        // Ejecutar la aplicación en el Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            VentanaLogin ventanaLogin = new VentanaLogin();
            ventanaLogin.setVisible(true);
        });
    }
}