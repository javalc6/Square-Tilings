import java.awt.Graphics2D;

public interface DrawingAction {
    void draw(Graphics2D g, int size, int tileSize);
}