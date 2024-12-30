import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenu extends JFrame {
    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 500;

    public MainMenu() {
        setTitle("Pac-Man Game");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        /* creating main menu panel*/
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.BLACK);
        JLabel titleLabel = new JLabel("PAC-MAN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setOpaque(false);
        JButton playButton = createMenuButton("Play Game");
        JButton scoresButton = createMenuButton("High Scores");
        JButton exitButton = createMenuButton("Exit");
        /* handling buttons */
        playButton.addActionListener(e -> startGame());
        scoresButton.addActionListener(e -> showHighScores());
        exitButton.addActionListener(e -> System.exit(0));
        buttonsPanel.add(Box.createVerticalStrut(50));
        buttonsPanel.add(playButton);
        buttonsPanel.add(Box.createVerticalStrut(20));
        buttonsPanel.add(scoresButton);
        buttonsPanel.add(Box.createVerticalStrut(20));
        buttonsPanel.add(exitButton);
        mainPanel.add(Box.createVerticalStrut(50));
        mainPanel.add(titleLabel);
        mainPanel.add(buttonsPanel);

        add(mainPanel);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 50));
        button.setBackground(Color.YELLOW);
        button.setFocusPainted(false);
        return button;
    }

    private void startGame() {
        this.setVisible(false);
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pac-Man");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    MainMenu.this.setVisible(true);
                }
            });
            
            try {
                PacMan pacmanPanel = new PacMan();
                frame.add(pacmanPanel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                pacmanPanel.requestFocusInWindow();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading game resources: " + ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                this.setVisible(true);
            }
        });
    }

    private void showHighScores() {
        HighScoresWindow scores_window = new HighScoresWindow(this);
        scores_window.setVisible(true);
    }
}