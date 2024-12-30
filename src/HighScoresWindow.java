import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class HighScoresWindow extends JDialog {
    private static final int WINDOW_WIDTH = 300;
    private static final int WINDOW_HEIGHT = 400;
    private static final String SCORES_FILE = "scores.txt";

    /* scores panel(a new window will be created on pressing the "high scores" button) */
    public HighScoresWindow(JFrame parent) {
        super(parent, "High Scores", true);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.BLACK);
        JLabel titleLabel = new JLabel("High Scores");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel scoresPanel = new JPanel();
        scoresPanel.setLayout(new BoxLayout(scoresPanel, BoxLayout.Y_AXIS));
        scoresPanel.setBackground(Color.BLACK);

        ArrayList<Integer> scores = loadHighScores();
        if (scores.isEmpty()) {
            JLabel noScoresLabel = new JLabel("No scores yet!");
            noScoresLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            noScoresLabel.setForeground(Color.WHITE);
            noScoresLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            scoresPanel.add(noScoresLabel);
        } else {
            for (int i = 0; i < scores.size(); i++) {
                JLabel scoreLabel = new JLabel((i + 1) + ". " + scores.get(i));
                scoreLabel.setFont(new Font("Arial", Font.PLAIN, 18));
                scoreLabel.setForeground(Color.WHITE);
                scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                scoresPanel.add(scoreLabel);
                scoresPanel.add(Box.createVerticalStrut(10));
            }
        }

        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.setMaximumSize(new Dimension(100, 40));
        closeButton.addActionListener(e -> dispose());
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(scoresPanel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(closeButton);
        mainPanel.add(Box.createVerticalStrut(20));
        add(mainPanel);
    }

    private ArrayList<Integer> loadHighScores() {
        ArrayList<Integer> scores = new ArrayList<>();
        File file = new File(SCORES_FILE);
        
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Could not create scores file: " + e.getMessage());
                return scores;
            }
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    scores.add(Integer.parseInt(line.trim()));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid score in file: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not load high scores: " + e.getMessage());
        }
        return scores;
    }
}