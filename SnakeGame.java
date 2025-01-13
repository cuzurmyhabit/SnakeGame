package winterproject2025;

import javax.swing.*;
import javax.swing.Timer;  // javax.swing.Timer를 명확하게 지정

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private final int TILE_SIZE = 20;
    private final int WIDTH = 30, HEIGHT = 20;
    private int speed = 100;
    private int score = 0;
    private int highScore = 0;
    private boolean running = false;
    private String currentUser = "";
    
    private ArrayList<Point> snake;
    private Point food;
    private char direction = 'R';
    private Timer timer;

    private final File userFile = new File("users.txt");
    private Map<String, String> userPasswords = new HashMap<>();
    private Map<String, Integer> userScores = new HashMap<>();

    public SnakeGame() {
        this.setPreferredSize(new Dimension(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);
        loadUserData();
        login();
        startGame();
    }
    
    private void loadUserData() {
        try {
            if (!userFile.exists()) {
                userFile.createNewFile();
            }
            BufferedReader reader = new BufferedReader(new FileReader(userFile));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    userPasswords.put(parts[0], parts[1]);
                    userScores.put(parts[0], Integer.parseInt(parts[2]));
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveUserData() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(userFile));
            for (String user : userPasswords.keySet()) {
                writer.write(user + "," + userPasswords.get(user) + "," + userScores.getOrDefault(user, 0));
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void login() {
        while (true) {
            String username = JOptionPane.showInputDialog("사용자 이름을 입력하세요:");
            if (username == null || username.trim().isEmpty()) {
                System.exit(0);
            }
            currentUser = username.trim();
            
            if (userPasswords.containsKey(currentUser)) {
                String password = JOptionPane.showInputDialog("비밀번호를 입력하세요:");
                if (password != null && password.equals(userPasswords.get(currentUser))) {
                    highScore = userScores.getOrDefault(currentUser, 0);
                    break;
                } else {
                    JOptionPane.showMessageDialog(null, "비밀번호가 틀렸습니다. 다시 시도하세요.");
                }
            } else {
                int option = JOptionPane.showConfirmDialog(null, 
                    "사용자를 찾을 수 없습니다. 회원가입을 하시겠습니까?", 
                    "회원가입", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    String newPassword = JOptionPane.showInputDialog("비밀번호를 설정하세요:");
                    if (newPassword != null && !newPassword.trim().isEmpty()) {
                        userPasswords.put(currentUser, newPassword);
                        userScores.put(currentUser, 0);
                        highScore = 0;
                        saveUserData();
                        JOptionPane.showMessageDialog(null, "회원가입이 완료되었습니다!");
                        break;
                    }
                } else {
                    System.exit(0);
                }
            }
        }
    }

    public void startGame() {
        if (timer != null) {
            timer.stop();
        }

        snake = new ArrayList<>();
        snake.add(new Point(5, 5));
        snake.add(new Point(4, 5));
        snake.add(new Point(3, 5));

        generateFood();
        direction = 'R';
        running = true;
        score = 0;
        speed = 100;

        if (timer == null) {
            timer = new Timer(speed, this);
        } else {
            timer.setDelay(speed);
        }
        
        timer.start();
    }

    public void generateFood() {
        Random rand = new Random();
        do {
            food = new Point(rand.nextInt(WIDTH), rand.nextInt(HEIGHT));
        } while (snake.contains(food));
    }

    public void move() {
        if (!running) return;

        Point head = snake.get(0);
        Point newHead = new Point(head);

        switch (direction) {
            case 'U': newHead.y--; break;
            case 'D': newHead.y++; break;
            case 'L': newHead.x--; break;
            case 'R': newHead.x++; break;
        }

        if (newHead.equals(food)) {
            snake.add(0, food);
            generateFood();
            score += 1;
            if (speed > 50) {
                speed -= 5;
                timer.setDelay(speed);
            }
        } else {
            snake.add(0, newHead);
            snake.remove(snake.size() - 1);
        }

        checkCollision();
    }

    public void checkCollision() {
        Point head = snake.get(0);

        if (head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT) {
            running = false;
            timer.stop();
            updateHighScore();
        }

        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                running = false;
                timer.stop();
                updateHighScore();
            }
        }
    }

    private void updateHighScore() {
        if (score > highScore) {
            highScore = score;
            userScores.put(currentUser, highScore);
            saveUserData();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        g.setColor(Color.RED);
        g.fillRect(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        g.setColor(Color.GREEN);
        for (Point p : snake) {
            g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
        g.setColor(Color.WHITE);
        g.drawString("사용자: " + currentUser, 10, 30);
        g.drawString("점수: " + score, 10, 50);
        g.drawString("최고 점수: " + highScore, 10, 70);
        
        if (!running) {
            g.setFont(new Font("맑은 고딕", Font.BOLD, 30));
            g.drawString("게임 오버! R 키를 눌러 재시작하세요.", WIDTH * TILE_SIZE / 6, HEIGHT * TILE_SIZE / 2);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP: if (direction != 'D') direction = 'U'; break;
            case KeyEvent.VK_DOWN: if (direction != 'U') direction = 'D'; break;
            case KeyEvent.VK_LEFT: if (direction != 'R') direction = 'L'; break;
            case KeyEvent.VK_RIGHT: if (direction != 'L') direction = 'R'; break;
            case KeyEvent.VK_R: if (!running) startGame(); break;
        }
    }

    @Override public void actionPerformed(ActionEvent e) { move(); repaint(); }
    @Override public void keyReleased(KeyEvent e) { }
    @Override public void keyTyped(KeyEvent e) { }
}
