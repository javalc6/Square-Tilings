import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Color;
import java.awt.BasicStroke;

public class LineAction implements DrawingAction {
	final Point p1;
    final Point p2; final Color col;
	LineAction(Point p1, Point p2, Color col) { this.p1 = p1; this.p2 = p2; this.col = col; }
	public void draw(Graphics2D g, int size, int tileSize) {
		g.setColor(col);
		g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.drawLine(p1.x * size / tileSize, p1.y * size / tileSize, p2.x * size / tileSize, p2.y * size / tileSize);
	}
}
