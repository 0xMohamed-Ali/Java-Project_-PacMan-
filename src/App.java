import javax.swing.*;

public class App {
    public static void main(String[] args) {
        // Use SwingUtilities.invokeLater to ensure thread-safe GUI creation
        SwingUtilities.invokeLater(() -> {
            /* 
                initiating the main menu instead of direct launch of the game
            */
            MainMenu menu = new MainMenu();
            menu.setVisible(true);
        });
    }
}