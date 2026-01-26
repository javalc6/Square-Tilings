package tools;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.geom.Arc2D;

public class ArcAction implements DrawingAction {
	final Point start;
    Point intermediate = null; 
    final Point stop;
	final Color col;

	public ArcAction(Point start, Point stop, Color col) { 
		this.start = start; 
		this.stop = stop; 
		this.col = col; 
	}

	public void setIntermediatePoint(Point p) {
		intermediate = p;
	}

	public Point getIntermediatePoint() {
		return intermediate;
	}

	public void draw(Graphics2D g, int size, int tileSize) {
		g.setColor(col);
		g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		if (intermediate != null) {
			double x1 = start.x * size / tileSize;
			double y1 = start.y * size / tileSize;
			double x2 = intermediate.x * size / tileSize;
			double y2 = intermediate.y * size / tileSize;
			double x3 = stop.x * size / tileSize;
			double y3 = stop.y * size / tileSize;

			double det = 2 * (x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2));
			
			if (Math.abs(det) > 1e-9) {
				double centerX = ((x1*x1 + y1*y1) * (y2 - y3) + (x2*x2 + y2*y2) * (y3 - y1) + (x3*x3 + y3*y3) * (y1 - y2)) / det;
				double centerY = ((x1*x1 + y1*y1) * (x3 - x2) + (x2*x2 + y2*y2) * (x1 - x3) + (x3*x3 + y3*y3) * (x2 - x1)) / det;
				double radius = Math.sqrt(Math.pow(x1 - centerX, 2) + Math.pow(y1 - centerY, 2));

				double a1 = Math.toDegrees(Math.atan2(centerY - y1, x1 - centerX));
				double a2 = Math.toDegrees(Math.atan2(centerY - y2, x2 - centerX));
				double a3 = Math.toDegrees(Math.atan2(centerY - y3, x3 - centerX));

				double extent = getNetSweep(a1, a2) + getNetSweep(a2, a3);

				Arc2D.Double arc = new Arc2D.Double(centerX - radius, centerY - radius,	radius * 2, radius * 2,	a1, extent, Arc2D.OPEN);

				g.draw(arc);
				return;
			}
		}
		g.drawLine(start.x * size / tileSize, start.y * size / tileSize, stop.x * size / tileSize, stop.y * size / tileSize);
	}

	private double getNetSweep(double start, double end) {
		double diff = (end - start) % 360;
		if (diff > 180) diff -= 360;
		if (diff < -180) diff += 360;
		return diff;
	}

}
