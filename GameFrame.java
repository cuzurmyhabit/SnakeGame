package winterproject2025;

import javax.swing.*;

public class GameFrame extends JFrame {
    public GameFrame() {
        setTitle("Snake Game");
        SnakeGame game = new SnakeGame();
        add(game);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
    }
}
