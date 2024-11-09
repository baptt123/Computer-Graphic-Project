// DragonAnimation.java
package Level1;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class DragonAnimation {
    private final int DRAGON_WIDTH = 64;     // Chiều rộng mỗi khung của rồng
    private final int DRAGON_HEIGHT = 64;    // Chiều cao mỗi khung của rồng
    private final int TOTAL_FRAMES = 4;      // Tổng số khung trong sprite sheet
    private BufferedImage dragonSpriteSheet; // Ảnh sprite sheet của rồng
    private int currentFrame = 0;            // Khung hiện tại của hoạt ảnh
    private Timer timer;                     // Đếm thời gian để chuyển khung

    public DragonAnimation() {
        loadImage(); // Tải hình ảnh rồng và xử lý trong suốt
        // Tạo bộ đếm thời gian, chuyển khung sau mỗi 100ms
        timer = new Timer(100, e -> currentFrame = (currentFrame + 1) % TOTAL_FRAMES);
        timer.start(); // Bắt đầu hoạt ảnh
    }

    private void loadImage() {
        try {
            // Tải sprite sheet từ file
            dragonSpriteSheet = ImageIO.read(new File("D:\\Computer-Graphic-Project\\Shooting_Game\\img\\dragon.png"));
            // Xóa nền trắng (thay bằng trong suốt)
            dragonSpriteSheet = removeWhiteDragonBackground(dragonSpriteSheet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Hàm vẽ rồng tại vị trí (x, y) trên màn hình
    public void drawDragon(Graphics g, int x, int y) {
        // Tính toán vị trí của khung hiện tại trong sprite sheet
        int srcX = currentFrame * DRAGON_WIDTH;
        int srcY = dragonSpriteSheet.getHeight() - DRAGON_HEIGHT;
        // Vẽ khung hiện tại của rồng tại tọa độ (x, y)
        g.drawImage(dragonSpriteSheet, x, y, x + DRAGON_WIDTH, y + DRAGON_HEIGHT, srcX, srcY, srcX + DRAGON_WIDTH, srcY + DRAGON_HEIGHT, null);
    }

    public static BufferedImage removeWhiteDragonBackground(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Tạo một ảnh mới có hỗ trợ độ trong suốt (alpha channel)
        BufferedImage transparentImage = new BufferedImage(
                width, height, BufferedImage.TYPE_INT_ARGB
        );

        // Xử lý từng pixel
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = image.getRGB(x, y);

                // Tách các kênh màu
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;

                // Kiểm tra pixel có phải màu trắng không
                // Bạn có thể điều chỉnh ngưỡng này để phù hợp với ảnh của bạn
                if (red > 240 && green > 240 && blue > 240) {
                    // Nếu là pixel trắng, đặt nó thành trong suốt
                    transparentImage.setRGB(x, y, 0x00FFFFFF); // Hoàn toàn trong suốt
                } else {
                    // Giữ nguyên màu của pixel gốc
                    transparentImage.setRGB(x, y, pixel);
                }
            }
        }

        return transparentImage;
    }

}
