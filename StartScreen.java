package winterproject2025;

import javax.swing.*;
import java.awt.*;

public class StartScreen extends JFrame {
    public StartScreen() {
        setTitle("Snake Game");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 10, 10));

        JButton startButton = new JButton("게임 시작");
        JButton infoButton = new JButton("게임 설명");

        startButton.addActionListener(e -> {
            dispose(); // 현재 창 닫기
            new GameFrame(); // 게임 창 열기
        });

        infoButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "뱀을 이동하여 사과를 먹으세요!\n방향키로 조작하고, R 키로 재시작할 수 있습니다.", "게임 설명", JOptionPane.INFORMATION_MESSAGE);
        });

        panel.add(startButton);
        panel.add(infoButton);

        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new StartScreen();
    }
}
