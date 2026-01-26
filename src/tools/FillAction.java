package tools;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class FillAction implements DrawingAction {
    final static boolean compressImage = false;//enable image compression to reduce memory usage but increase CPU usage

    BufferedImage fillImage;
    byte[] compressedImage;

    public FillAction(BufferedImage img) {
        if (compressImage) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(img, "png", baos);
                compressedImage = baos.toByteArray();
            } catch (IOException ioex) {
            }
        } else {
            this.fillImage = img;
        }
    }

    public void draw(Graphics2D g, int size, int tileSize) {
        if (compressImage) {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(compressedImage);
                fillImage = ImageIO.read(bais);
            } catch (IOException ioex) {
                return;
            }
        }
        g.drawImage(fillImage, 0, 0, size, size, null);
    }
}

