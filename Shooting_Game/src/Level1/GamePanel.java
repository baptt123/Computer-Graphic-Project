// GamePanel.java
package Level1;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final int TILE = 16 * 4;

    private Stage stage = Stage.PLAY;
    private boolean gameOver = false;
    private Random random = new Random();

    private BufferedImage backgroundImg;
    private BufferedImage bulletImg, enemyImg;

    private Item player, bullet;
    private int playerScore = 0, playerHealth = 100;
    private ArrayList<Item> enemy = new ArrayList<>();
    private int enemyLength = 1;
    private int enemyCount = 0;

    private int move = 0; // 0 no, 1 is right, -1 is left
    private int shoot = 0; // 0 no, 1 shoot

    private DragonAnimation dragonAnimation;
    private Thread thread;

    public GamePanel() {
        // Load images and initialize game
        try {
            backgroundImg = ImageIO.read(new FileInputStream("D:\\Computer_Graphics_Project\\Shooting_Game\\img\\background.jpg"));
            bulletImg = ImageIO.read(new FileInputStream("D:\\Computer_Graphics_Project\\Shooting_Game\\img\\fireball.png"));
            bulletImg = makeTransparent(bulletImg, Color.WHITE);
            enemyImg = ImageIO.read(new FileInputStream("D:\\Computer_Graphics_Project\\Shooting_Game\\img\\flying_twin_headed_dragon_blue.png"));
            enemyImg = makeTransparent(enemyImg, Color.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        player = new Item();
        player.x = WIDTH / 2 - TILE / 2;
        player.y = HEIGHT - 90;
        player.vx = 8;
        bullet = new Item();
        bullet.vy = 16;

        for (int i = 0; i < enemyLength; i++) {
            enemy.add(new Item());
            enemy.get(i).x = random.nextInt(WIDTH - TILE);
            enemy.get(i).y = -random.nextInt(TILE);
            enemy.get(i).vy = 2;
        }

        // Initialize dragon animation
        dragonAnimation = new DragonAnimation();

        thread = new Thread(this);
        thread.start();

        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.addKeyListener(this);
    }

    private BufferedImage makeTransparent(BufferedImage image, Color color) {
        BufferedImage transparentImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgb = image.getRGB(x, y);
                if (new Color(rgb).equals(color)) {
                    transparentImage.setRGB(x, y, 0x00FFFFFF); // Transparent
                } else {
                    transparentImage.setRGB(x, y, rgb);
                }
            }
        }
        return transparentImage;
    }

    public void update() {
        enemyCount++;
        if (move == 1) player.x += player.vx;
        else if (move == -1) player.x -= player.vx;
        if (shoot == 1) {
            bullet.y -= bullet.vy;
            if (bullet.y < -TILE) shoot = 0;
        }
        if (playerHealth <= 0) {
            playerHealth = 0;
            gameOver = true;
        }
        if (enemyCount % 1000 == 0) {
            enemyLength++;
            Item newEnemy = new Item();
            newEnemy.x = random.nextInt(WIDTH - TILE);
            newEnemy.y = -random.nextInt(TILE);
            newEnemy.vy = 2;
            enemy.add(newEnemy);
        }
        for (int i = 0; i < enemyLength; i++) {
            Item currentEnemy = enemy.get(i);
            currentEnemy.y += currentEnemy.vy;
            if (distance(currentEnemy.x, currentEnemy.y, bullet.x, bullet.y) < TILE) {
                resetEnemy(currentEnemy);
                shoot = 0;
                playerScore++;
            }
            if (distance(currentEnemy.x, currentEnemy.y, player.x, player.y) < TILE) {
                playerHealth -= random.nextInt(15) + 5;
                resetEnemy(currentEnemy);
            }
            if (currentEnemy.y > HEIGHT + TILE) {
                playerHealth -= random.nextInt(25) + 15;
                resetEnemy(currentEnemy);
            }
        }
    }

    private double distance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    private void resetEnemy(Item enemy) {
        enemy.x = random.nextInt(WIDTH - TILE);
        enemy.y = -random.nextInt(TILE);
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
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImg, -50, -30, null);
        if (shoot == 1) g.drawImage(bulletImg, bullet.x, bullet.y, TILE, TILE, null);
        if (!gameOver) dragonAnimation.drawDragon(g, player.x, player.y);
        for (Item enemy : enemy) {
            g.drawImage(enemyImg, enemy.x, enemy.y, TILE, TILE, null);
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 35));
        g.drawString("SCORE  " + playerScore, 5, 40);
        g.drawString("HEALTH " + playerHealth, 5, 85);
        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 55));
            g.drawString("GAME OVER", WIDTH / 2 - 130, HEIGHT / 2);
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
                if (shoot == 0) {
                    bullet.x = player.x;
                    bullet.y = player.y + 20;
                    shoot = 1;
                }
                break;
            case KeyEvent.VK_ENTER:
                if (stage == Stage.PAUSE) {
                    stage = Stage.PLAY;
                } else {
                    stage = Stage.PAUSE;
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_D) {
            move = 0;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}