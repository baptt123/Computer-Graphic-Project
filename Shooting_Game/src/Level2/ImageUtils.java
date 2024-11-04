package Level2;

import java.awt.image.BufferedImage;

public class ImageUtils {
        public static BufferedImage removeWhiteBackground(BufferedImage image) {
            int width = image.getWidth();
            int height = image.getHeight();

            BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int pixel = image.getRGB(x, y);

                    // Lấy giá trị màu của pixel
                    int alpha = (pixel >> 24) & 0xff;
                    int red = (pixel >> 16) & 0xff;
                    int green = (pixel >> 8) & 0xff;
                    int blue = pixel & 0xff;

                    // Nếu màu là trắng (255, 255, 255), đặt nó thành trong suốt
                    if (red == 255 && green == 255 && blue == 255) {
                        newImage.setRGB(x, y, 0x00FFFFFF);  // Pixel trong suốt
                    } else {
                        newImage.setRGB(x, y, pixel);  // Giữ nguyên pixel không phải màu trắng
                    }
                }
            }

            return newImage;
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
    public static BufferedImage makeTransparent(BufferedImage image) {
        // Tạo một BufferedImage mới có hỗ trợ độ trong suốt (ARGB)
        BufferedImage transparentImage = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        // Xử lý từng pixel
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                // Lấy màu của pixel hiện tại
                int rgb = image.getRGB(x, y);

                // Tách các thành phần màu
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                // Kiểm tra nếu pixel là màu trắng (255,255,255)
                if (red == 255 && green == 255 && blue == 255) {
                    // Đặt pixel thành trong suốt hoàn toàn
                    transparentImage.setRGB(x, y, 0x00000000);
                } else {
                    // Giữ nguyên màu và đặt alpha là 255 (không trong suốt)
                    int alpha = 0xFF;
                    int newColor = (alpha << 24) | (rgb & 0x00FFFFFF);
                    transparentImage.setRGB(x, y, newColor);
                }
            }
        }

        return transparentImage;
    }
    }



