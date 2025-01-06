package winterproject2025;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private final int TILE_SIZE = 20;
    private final int WIDTH = 30, HEIGHT = 20;
    private int speed = 100;
    private int score = 0;
    private boolean running = false;
    
    private ArrayList<Point> snake;
    private Point food;
    private char direction = 'R';
    private Timer timer;
    
    public SnakeGame() {
        this.setPreferredSize(new Dimension(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);
        startGame();
    }
    
    public void startGame() {
        snake = new ArrayList<>();
        snake.add(new Point(5, 5));
        snake.add(new Point(4, 5)); // 두 번째 칸
        snake.add(new Point(3, 5)); // 세 번째 칸

        generateFood();
        direction = 'R';
        running = true;
        score = 0;
        speed = 100;
        timer = new Timer(speed, this);
        timer.start();
    }
    
    public void generateFood() {
        Random rand = new Random();
        do {
            food = new Point(rand.nextInt(WIDTH), rand.nextInt(HEIGHT));
        } while (snake.contains(food)); // 뱀의 몸 위에 음식이 생성되지 않도록 처리
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
        }
        
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                running = false;
                timer.stop();
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.setColor(Color.RED);
        g.fillRect(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        
        g.setColor(Color.PINK);
        for (Point p : snake) {
            g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        String scoreText = "Score: " + score;
        FontMetrics metrics = g.getFontMetrics();
        int x = (WIDTH * TILE_SIZE - metrics.stringWidth(scoreText)) / 2;
        int y = 40;
        g.drawString(scoreText, x, y);
        
        if (!running) {
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Game Over! Press R to Restart", WIDTH * TILE_SIZE / 6, HEIGHT * TILE_SIZE / 2);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
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
    
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame();
        
        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}
