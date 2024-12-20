package Level2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import javax.imageio.*;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Random;

public class BossPanel extends JPanel implements Runnable, KeyListener {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final int TILE = 16 * 4;
    private Stage stage = Stage.PLAY;
    private boolean gameOver = false;
    private boolean paused = false;
    private Random random = new Random();

    private BufferedImage backgroundImg;
    private BufferedImage bulletImg, enemyImg;

    private Item player, boss;
    private int playerScore = 0, playerHealth = 100;
    private int bossHealth = 100;

    private int move = 0;       // 0 no, 1 is right, -1 is left
    private int shoot = 0;      // 0 no, 1 shoot

    private DragonAnimation dragonAnimation;
    private ArrayList<Projectile> projectiles;

    private Thread thread;

    public BossPanel() {
        // Load images and initialize game
        try {
            backgroundImg = ImageIO.read(new FileInputStream("img/background.jpg"));

            // Load and remove white background from fireball image
            BufferedImage fireballImage = ImageIO.read(new FileInputStream("img/fireball.png"));
            bulletImg = ImageUtils.removeWhiteBackground(fireballImage);

            // Read enemy image and remove white background
            BufferedImage enemyImage = ImageIO.read(new FileInputStream("img/flying_twin_headed_dragon_blue.png"));
            enemyImg = ImageUtils.removeWhiteBackground(enemyImage);

        } catch (Exception e) {
            e.printStackTrace();
        }

        player = new Item();
        player.x = WIDTH / 2 - TILE / 2;
        player.y = HEIGHT - 90;
        player.vx = 8;

        projectiles = new ArrayList<>();

        // Initialize the boss
        boss = new Item();
        boss.x = WIDTH / 2 - TILE / 2;
        boss.y = 0;
        boss.vx = 0; // Initial horizontal velocity
        boss.vy = 0; // Initial vertical velocity

        // Initialize dragon animation
        dragonAnimation = new DragonAnimation();

        thread = new Thread(this);
        thread.start();

        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.addKeyListener(this);
    }

    public void update() {
        if (paused) return;

        // Update player position
        if (move == 1) player.x += player.vx;
        else if (move == -1) player.x -= player.vx;

        // Update boss random movement
        if (random.nextInt(100) < 2) { // Randomly change direction every few frames
            boss.vx = random.nextInt(7) - 3; // Random vx between -3 and 3
            boss.vy = random.nextInt(7) - 3; // Random vy between -3 and 3
        }

        boss.x += boss.vx;
        boss.y += boss.vy;

        // Keep boss within bounds
        if (boss.x <= 0 || boss.x >= WIDTH - TILE) {
            boss.vx = -boss.vx;
        }

        if (boss.y <= 0 || boss.y >= HEIGHT - TILE) {
            boss.vy = -boss.vy;
        }

        // Shoot fireball from boss every random interval
        if (random.nextInt(100) < 2) { // Random chance to fire
            projectiles.add(new Projectile(boss.x, boss.y, 6, false));
        }

        // Update player fireball position
        if (shoot == 1) {
            projectiles.add(new Projectile(player.x + TILE / 2, player.y, -10, true));
            shoot = 0; // Only shoot once
        }

        // Update projectiles (both player and boss fireballs)
        for (int i = 0; i < projectiles.size(); i++) {
            Projectile p = projectiles.get(i);
            p.y += p.vy;

            // Check if a boss fireball hits the player
            if (!p.isPlayer && p.y + TILE > player.y && p.x + TILE > player.x && p.x < player.x + TILE) {
                playerHealth -= 20; // Reduce player health if hit
                projectiles.remove(i);
                i--;
                if (playerHealth <= 0) {
                    playerHealth = 0;
                    gameOver = true;
                }
            }

            // Check if a player fireball hits the boss
            if (p.isPlayer && p.y < boss.y + TILE && p.x + TILE > boss.x && p.x < boss.x + TILE) {
                bossHealth -= 20; // Reduce boss health if hit
                projectiles.remove(i);
                i--;
                if (bossHealth <= 0) {
                    bossHealth = 0;
                    gameOver = true; // Game over when boss dies
                }
            }
        }

        // Remove off-screen projectiles
        projectiles.removeIf(p -> p.y < 0 || p.y > HEIGHT);

        // Draw everything
        repaint();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        while (thread != null) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            if (delta >= 1) {
                if (!gameOver && stage == Stage.PLAY) {
                    update();
                }
                repaint();
                delta--;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImg, -50, -30, null);

        // Chuyển Graphics thành Graphics2D để dễ quản lý các phép biến đổi
        Graphics2D g2d = (Graphics2D) g;

        // Vẽ các đạn lửa
        for (Projectile p : projectiles) {
            if (p.isPlayer) {
                g2d.drawImage(bulletImg, p.x, p.y, TILE, TILE, null);
            } else {
                // Draw boss fireballs reversed
                AffineTransform tx = new AffineTransform();
                tx.translate(p.x, p.y + TILE);
                tx.scale(1, -1);
                tx.translate(0, -TILE);
                g2d.drawImage(bulletImg, tx, null);
            }
        }

        // Vẽ rồng người chơi
        if (!gameOver) dragonAnimation.drawDragon(g, player.x, player.y);

        // Vẽ boss
        g.drawImage(enemyImg, boss.x, boss.y, 200, 200, null);

        // Hiển thị điểm số và máu
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 35));
        g.drawString("SCORE  " + playerScore, 5, 40);
        g.drawString("HEALTH " + playerHealth, 5, 85);
        g.drawString("BOSS HEALTH " + bossHealth, 5, 130);

        // Hiển thị thông báo kết thúc trò chơi
        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 55));
            if (bossHealth <= 0) {
                g.drawString("YOU WIN!", WIDTH / 2 - 130, HEIGHT / 2);
            } else {
                g.drawString("GAME OVER", WIDTH / 2 - 130, HEIGHT / 2);
            }
        }

        // Hiển thị thông báo tạm dừng
        if (paused) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("TẠM DỪNG, ẤN PHÍM P ĐỂ TIẾP TỤC", WIDTH / 2 - 270, HEIGHT / 2);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
                move = -1;
                break;
            case KeyEvent.VK_D:
                move = 1;
                break;
            case KeyEvent.VK_SPACE:
                shoot = 1;
                break;
            case KeyEvent.VK_P:
                paused = !paused; // Toggle pause state
                break;
            case KeyEvent.VK_ENTER:
                resetGame(); // Reset game when 'Enter' is pressed
                break;
            case KeyEvent.VK_ESCAPE:
                if (gameOver) {
                    System.exit(0); // Exit the game if 'You Win' message is shown
                }
                break;
        }
    }

    private void resetGame() {
        playerHealth = 100;
        bossHealth = 100;
        projectiles.clear();
        player.x = WIDTH / 2 - TILE / 2;
        player.y = HEIGHT - 90;
        boss.x = WIDTH / 2 - TILE / 2;
        boss.y = 0;
        boss.vx = 0;
        boss.vy = 0;
        gameOver = false;
        stage = Stage.PLAY;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_D) {
            move = 0;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }
}
