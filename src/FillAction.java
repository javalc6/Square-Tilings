import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class FillAction implements DrawingAction {
	final BufferedImage fillImage;
	FillAction(BufferedImage img) { this.fillImage = img; }
	public void draw(Graphics2D g, int size, int tileSize) {
		g.drawImage(fillImage, 0, 0, size, size, null);
	}
}

