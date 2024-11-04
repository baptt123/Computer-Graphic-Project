package Level2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

class DragonAnimation {
    private final int DRAGON_WIDTH = 64;
    private final int DRAGON_HEIGHT = 64;
    private final int TOTAL_FRAMES = 4;
    private Image dragonSpriteSheet;
    private int currentFrame = 0;
    private javax.swing.Timer timer;

    public DragonAnimation() {
        loadImage();
        timer = new javax.swing.Timer(100, e -> {
            currentFrame = (currentFrame + 1) % TOTAL_FRAMES;
        });
        timer.start();
    }

    private void loadImage() {
        try {
            // Tải hình ảnh rồng từ file
            BufferedImage spriteSheet = ImageIO.read(new File("D:\\Computer_Graphics_Project\\Shooting_Game\\img\\dragon.png"));
            // Xóa nền trắng
            dragonSpriteSheet = ImageUtils.removeWhiteDragonBackground(spriteSheet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void drawDragon(Graphics g, int x, int y) {
        int srcX = currentFrame * DRAGON_WIDTH;
        int srcY = dragonSpriteSheet.getHeight(null) - DRAGON_HEIGHT;
        g.drawImage(dragonSpriteSheet, x, y, x + DRAGON_WIDTH, y + DRAGON_HEIGHT, srcX, srcY, srcX + DRAGON_WIDTH, srcY + DRAGON_HEIGHT, null);
    }
}
