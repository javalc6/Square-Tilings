package tiles;
/*
License Information, 2026 Livio (javalc6)

Feel free to modify, re-use this software, please give appropriate
credit by referencing this Github repository.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

IMPORTANT NOTICE
Note that this software is freeware and it is not designed, licensed or
intended for use in mission critical, life support and military purposes.
The use of this software is at the risk of the user.

DO NOT USE THIS SOFTWARE IF YOU DON'T AGREE WITH STATED CONDITIONS.
*/
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

/*
Class 'Tiles' provides static methods to generate a tiling, e.g. to create a Greek Tile use following:

Tiles.drawGreekTile(g2d, colors, x, y, size);

Where g2d is a reference to Graphics2D, x and y are the origin for the tile, size is the size of square tile.
Some methods may have additional parameters to control specific features of the tile.

*/

public class Tiles {

    public static void drawGreekTile(Graphics2D g2d, Color[] colors, int x, int y, int size) {
        g2d.setColor(colors[2]);
        g2d.fillRect(x, y, size, size);

        double step = size / 8.0;
        double[][] path = {{0,1}, {1,1}, {1,7}, {6,7}, {6,4}, {5,4}, {5,6}, {2,6}, {2,1}, {8,1}, {8,2}, {3,2}, {3,5}, {4,5}, {4,3}, {7,3}, {7,8}, {0,8}};
        Path2D p = new Path2D.Double();
        p.moveTo(x + step * path[0][0], y + step * path[0][1]);
        for (int i = 1; i < path.length; i++)
            p.lineTo(x + step * path[i][0], y + step * path[i][1]);
        p.closePath();

        g2d.setColor(colors[1]);
        g2d.fill(p);
        g2d.draw(p);
    }

    public static void drawIslamicStarTile1(Graphics2D g2d, Color[] colors, int x, int y, int size) {
        drawOctagramTile(g2d, colors, x, y, size, 0.384);

        double cx = x + size / 2.0; double cy = y + size / 2.0;
        Path2D star = createStar(cx, cy, size * 0.287, size * 0.375, 8, Math.PI / 8);
        g2d.setColor(colors[3]); g2d.fill(star);

        star = createStar(cx, cy, size * 0.155, size * 0.287, 8, 0);
        g2d.setColor(colors[1]); g2d.fill(star);
    }

    public static void drawIslamicStarTile2(Graphics2D g2d, Color[] colors, int x, int y, int size) {
        double cx = x + size / 2.0; double cy = y + size / 2.0;
        g2d.setColor(colors[3]);
        g2d.fillRect(x, y, size, size);

        Rectangle2D boundary = new Rectangle2D.Double(x, y, size, size);
        g2d.setClip(boundary);

        Path2D star = createStar(cx, cy, size * 0.27, size * 0.5, 8, 0);
        g2d.setColor(colors[1]); g2d.fill(star);

        double r = size * 0.21;
        Path2D[] stars = {
            createStar(x, y, r * 0.5, r, 4, Math.PI/4), createStar(x + size, y, r * 0.5, r, 4, Math.PI/4),
            createStar(x, y + size, r * 0.5, r, 4, Math.PI/4), createStar(x + size, y + size, r * 0.5, r, 4, Math.PI/4),
        };
        for (Path2D s : stars) { g2d.fill(s); g2d.draw(s); }

        Path2D star2 = createStar(cx, cy, size * 0.207, size * 0.27, 8, Math.PI/8);
        g2d.setColor(colors[0]); g2d.fill(star2);

    }

    public static void drawIslamicStarTile3(Graphics2D g2d, Color[] colors, int x, int y, int size) {
        g2d.setColor(colors[1]);
        g2d.fillRect(x, y, size, size);

        Rectangle2D boundary = new Rectangle2D.Double(x, y, size, size);
        g2d.setClip(boundary);

        double cx = x + size / 2.0; double cy = y + size / 2.0;
        g2d.setStroke(new BasicStroke(Math.max(1.5f, size / 80f)));
        g2d.setColor(colors[2]);
        double r = size * 0.15;
        Path2D[] stars = {
            createStar(x, y, r * 0.5, r, 4, Math.PI/4), createStar(x + size, y, r * 0.5, r, 4, Math.PI/4),
            createStar(x, y + size, r * 0.5, r, 4, Math.PI/4), createStar(x + size, y + size, r * 0.5, r, 4, Math.PI/4),
            createStar(cx, y, r * 0.5, r, 4, 0), createStar(cx, y + size, r * 0.5, r, 4, 0),
            createStar(x, cy, r * 0.5, r, 4, 0), createStar(x + size, cy, r * 0.5, r, 4, 0)
        };
        for (Path2D s : stars) { g2d.fill(s); g2d.draw(s); }
        Path2D mainStar = createStar(cx, cy, size * 0.19, size * 0.35, 8, 0);
        g2d.setColor(colors[0]); g2d.fill(mainStar);
        g2d.setColor(colors[2]); g2d.draw(mainStar);
    }

    public static void drawCrossedTile(Graphics2D g2d, Color[] colors, int x, int y, int size) {
        g2d.setColor(colors[1]);
        g2d.fillRect(x, y, size, size);

        g2d.setStroke(new BasicStroke(Math.max(1, size / 30f)));
        g2d.setColor(colors[0]); g2d.drawLine(x, y, x + size, y + size); g2d.drawLine(x + size, y, x, y + size);
        g2d.setColor(colors[2]); g2d.drawRect(x + size/4, y + size/4, size/2, size/2);
    }

    public static void drawInterlacedTile(Graphics2D g2d, Color[] colors, int x, int y, int size) {
        g2d.setColor(colors[1]);
        g2d.fillRect(x, y, size, size);

        g2d.setStroke(new BasicStroke(Math.max(1, size / 40f)));
        g2d.setColor(colors[0]); int r = size / 2;
        g2d.drawArc(x - r, y - r, size, size, 0, -90); g2d.drawArc(x + r, y - r, size, size, 180, 90);
        g2d.drawArc(x - r, y + r, size, size, 0, 90); g2d.drawArc(x + r, y + r, size, size, 180, -90);
        g2d.setColor(colors[2]); g2d.drawOval(x, y, size, size);
    }

    public static void drawLabyrinthTile(Graphics2D g2d, Color[] colors, int x, int y, int size, double cellSize) {
        g2d.setColor(colors[0]);
        g2d.fillRect(x, y, size, size);

        Random random = new Random(0);

        g2d.setColor(colors[1]);
        for (double i = x; i < x + size; i += cellSize) {
            for (double j = y; j < y + size; j += cellSize) {
                if (random.nextBoolean()) {
                    g2d.draw(new Line2D.Double(i, j, (i + cellSize), (j + cellSize)));
                } else {
                    g2d.draw(new Line2D.Double(i, (j + cellSize), (i + cellSize), j));
                }
            }
        }
    }

    public static void drawTruchetTile(Graphics2D g2d, Color[] colors, int x, int y, int size, int type,  double cellSize) {//type: 2 or 3
        g2d.setColor(colors[0]);
        g2d.fillRect(x, y, size, size);

        Random random = new Random(0);

        g2d.setColor(colors[1]);
        for (double i = x; i < x + size; i += cellSize) {
            for (double j = y; j < y + size; j += cellSize) {
                int idx = random.nextInt(type);
                if (idx == 0 || idx == 3) {
                    g2d.draw(new Arc2D.Double(i + cellSize / 2, j - cellSize / 2, cellSize, cellSize, 180, 90, Arc2D.OPEN));
                    g2d.draw(new Arc2D.Double(i - cellSize / 2, j + cellSize / 2, cellSize, cellSize, 0, 90, Arc2D.OPEN));
                }
                if (idx == 1 || idx == 3) {
                    g2d.draw(new Arc2D.Double(i - cellSize / 2, j - cellSize / 2, cellSize, cellSize, 270, 90, Arc2D.OPEN));
                    g2d.draw(new Arc2D.Double(i + cellSize / 2, j + cellSize / 2, cellSize, cellSize, 90, 90, Arc2D.OPEN));
                }
                if (idx == 2) {
                    g2d.draw(new Line2D.Double(i, (j + cellSize / 2), (i + cellSize), (j + cellSize / 2)));
                    g2d.draw(new Line2D.Double((i + cellSize / 2), (j + cellSize), (i + cellSize / 2), j));
                }
            }
        }
    }

    public static void drawWangTile(Graphics2D g2d, Color[] colors, int x, int y, int size, int gridSize) {
        double tileSize = (double)size / gridSize;
        int[][][] grid = generateWangTiling(gridSize);
        for (int row = 0; row < gridSize; row++)
            for (int col = 0; col < gridSize; col++)
                drawWangSingleTile(g2d, colors, x + col * tileSize, y + row * tileSize, grid[row][col], tileSize);
    }

    private static void drawWangSingleTile(Graphics2D g2d, Color[] colors, double x, double y, int[] tile, double tileSize) {
        double mid = tileSize * 0.5;

        g2d.setColor(colors[tile[1]]);
        Path2D path = createTriangle(x, y, x + mid, y + mid, x + tileSize, y);
        g2d.fill(path);
        g2d.setColor(colors[tile[0]]);
        path = createTriangle(x + tileSize, y, x + mid, y + mid, x + tileSize, y + tileSize);
        g2d.fill(path);
        g2d.setColor(colors[tile[3]]);
        path = createTriangle(x, y + tileSize, x + mid, y + mid, x + tileSize, y + tileSize);
        g2d.fill(path);
        g2d.setColor(colors[tile[2]]);
        path = createTriangle(x, y, x + mid, y + mid, x, y + tileSize);
        g2d.fill(path);
    }

    private static int[][][] generateWangTiling(int gridSize) {
        Random random = new Random(0);

        int n = 2;
        int[][] tileSet = new int[n * n * n * n][];
        int i = 0;
//0=right, 1=top, 2=left, 3=bottom
        for (int t = 0; t < n; t++)
            for (int r = 0; r < n; r++)
                for (int b = 0; b < n; b++)
                    for (int l = 0; l < n; l++)
                        tileSet[i++] = new int[]{t, r, b, l};

        int[][][] grid = new int[gridSize][gridSize][4];
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int topConstraint = row > 0 ? grid[row - 1][col][3] : random.nextInt(n);
                int leftConstraint = col > 0 ? grid[row][col - 1][0] : random.nextInt(n);
                Integer rightConstraint = (col == gridSize - 1) ? grid[row][0][2] : null;
                Integer bottomConstraint = (row == gridSize - 1) ? grid[0][col][1] : null;

                ArrayList<int[]> validTiles = new ArrayList<>();
                for (int[] t : tileSet)
                    if (t[1] == topConstraint && t[2] == leftConstraint && (rightConstraint == null || t[0] == rightConstraint) && (bottomConstraint == null || t[3] == bottomConstraint))
                        validTiles.add(t);
                grid[row][col] = validTiles.get(random.nextInt(validTiles.size()));
            }
        }
        return grid;
    }

    public static void drawOctagramTile(Graphics2D g2d, Color[] colors, int x, int y, int size, double factor) {
        g2d.setColor(colors[1]);
        g2d.fillRect(x, y, size, size);

        double cx = x + size / 2.0; double cy = y + size / 2.0;
        Path2D star = createStar(cx, cy, size * factor, size * 0.5, 8, 0);
        g2d.setColor(colors[0]); g2d.fill(star);
        g2d.setColor(colors[2]); g2d.setStroke(new BasicStroke(Math.max(1, size / 60f)));
        g2d.draw(star);
    }

    public static void drawOctagonTile(Graphics2D g2d, Color[] colors, int x, int y, int size, double factor) {
        g2d.setColor(colors[1]);
        g2d.fillRect(x, y, size, size);

        double cx = x + size / 2.0; double cy = y + size / 2.0;
        Path2D octagon = createPolygon(cx, cy, size * factor, 8, 0);
        g2d.setColor(colors[0]); g2d.fill(octagon);
        g2d.draw(octagon);

        Rectangle2D boundary = new Rectangle2D.Double(x, y, size, size);
        g2d.setClip(boundary);

        double offset = factor * (1.0 + 1.0 / Math.sqrt(2)) * size;
        double[][] pos = {{0, offset}, {0, -offset}, {-offset, 0}, {offset, 0}};
        for (int k = 0; k < 4; k++) {
            Path2D square = createPolygon(cx + pos[k][0], cy + pos[k][1], size * factor, 4, 0);
            g2d.setColor(colors[3]); g2d.fill(square);
            g2d.draw(square);
        }
    }

    public static void drawCheckeredTile(Graphics2D g2d, Color[] colors, int x, int y, int size) {
        int half = size / 2;
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++) {
                g2d.setColor(i == j ? colors[1] : colors[2]);
                g2d.fillRect(x + half * i, y + half * j, half, half);
            }
    }

    public static void drawTartanTile(Graphics2D g2d, Color[] colors, int x, int y, int size) {
        g2d.setColor(colors[3]);
        g2d.fillRect(x, y, size, size);

        Color semiTransparentcolor = new Color(colors[1].getRed(), colors[1].getGreen(), colors[1].getBlue(), 128);

        int half = size / 2;
        g2d.setColor(semiTransparentcolor);
        g2d.fillRect(x, y, half, size);
        g2d.fillRect(x, y, size, half);

        int quarter = half / 2;
        g2d.setColor(colors[0]);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(x + quarter, y, x + quarter, y + size);
        g2d.drawLine(x, y + quarter, x + size, y + quarter);
    }

    public static void drawInterlockingTile(Graphics2D g2d, Color[] colors, int x, int y, int size) {
        g2d.setColor(colors[2]);
        g2d.fillRect(x, y, size, size);

        int stroke = size / 16;
        int stroke2 = stroke * 2;
        int stroke3 = stroke * 3;
        int dim = size - stroke2 * 2;

        g2d.setStroke(new BasicStroke(Math.max(1, stroke)));

//vertical lines
        g2d.setColor(colors[0]);
        int x1 = x + dim / 2 + stroke;
        int x2 = x + dim / 2 + stroke3;
        g2d.drawLine(x1, y, x1, y + size);
        g2d.drawLine(x2, y, x2, y + size);
//horizontal lines
        g2d.setColor(colors[3]);
        int y1 = y + dim / 2 + stroke;
        int y2 = y + dim / 2 + stroke3;
        g2d.drawLine(x, y1, x + size, y1);
        g2d.drawLine(x, y2, x + size, y2);

        int arc = dim / 3;
//rounded square
        g2d.setColor(Color.RED);
        RoundRectangle2D loop = new RoundRectangle2D.Double(x + stroke2, y + stroke2, dim, dim, arc, arc);
        g2d.draw(loop);

        int r = arc / 2;
//bottom-left arc
        g2d.setColor(colors[1]);
        Arc2D quarter = new Arc2D.Double(x + stroke3 - r - 1, y + dim - r + stroke, arc, arc, 90, -90, Arc2D.OPEN);
        g2d.draw(quarter);
//bottom-right arc
        quarter = new Arc2D.Double(x + stroke - r + dim, y + dim - r + stroke, arc, arc, 180, -90, Arc2D.OPEN);
        g2d.draw(quarter);
//top-right arc
        quarter = new Arc2D.Double(x + stroke - r + dim, y + stroke3 - r, arc, arc, 270, -90, Arc2D.OPEN);
        g2d.draw(quarter);
//top-left arc
        quarter = new Arc2D.Double(x + stroke3 - r, y + stroke3 - r, arc, arc, 0, -90, Arc2D.OPEN);
        g2d.draw(quarter);
//patches
        g2d.drawLine(x, y + stroke3 + r, x + stroke2 + 1, y + stroke3 + r);
        g2d.drawLine(x, y + dim - r + stroke, x + stroke, y + dim - r + stroke);
        g2d.drawLine(x + stroke3 + r, y, x + stroke3 + r, y + stroke);
        g2d.drawLine(x + stroke3 + r, y + dim + stroke2, x + stroke3 + r, y + size);
        g2d.drawLine(x + dim - stroke, y, x + dim - stroke, y + stroke2 + 1);
        g2d.drawLine(x + dim - stroke, y + dim + stroke3, x + dim - stroke, y + size);
        g2d.drawLine(x + dim + stroke3, y + stroke3 + r, x + size, y + stroke3 + r);
        g2d.drawLine(x + size, y + dim - r + stroke, x + dim + stroke2, y + dim - r + stroke);

        g2d.setColor(colors[0]);
        g2d.drawLine(x1, y + stroke, x1, y + stroke3);
        g2d.drawLine(x1, y2, x1, y2 + stroke);
        g2d.drawLine(x2, y + dim + stroke, x2, y + dim + stroke3);
        g2d.drawLine(x2, y1, x2, y1 + stroke);

        g2d.setColor(colors[3]);
        g2d.drawLine(x + dim, y1, x + dim + stroke3, y1);
        g2d.drawLine(x + stroke, y2, x + stroke3, y2);
    }

    public static void drawSquaresTile(Graphics2D g2d, Color[] colors, int x, int y, int size) {
        g2d.setColor(colors[1]);
        g2d.fillRect(x, y, size, size);

        double cx = x + size / 2.0; double cy = y + size / 2.0;

        double a = Math.PI / 6;
        double cosT = Math.cos(a);
        double sinT = Math.sin(a);

        double side = size / (2 * (cosT + sinT));
        double offset = (side / 2.0) * (cosT + sinT);

        g2d.setColor(colors[3]);
        Path2D path = createSquare(cx - offset, cy - offset, side, a);
        g2d.fill(path);
        path = createSquare(cx + offset, cy - offset, side, -a);
        g2d.fill(path);
        path = createSquare(cx - offset, cy + offset, side, -a);
        g2d.fill(path);
        path = createSquare(cx + offset, cy + offset, side, a);
        g2d.fill(path);
    }

    public static void drawBlockFractal(Graphics2D g2d, Color[] colors, int x, int y, int size, int n) {
        g2d.setColor(colors[2]);
        g2d.fillRect(x, y, size, size);

        if (n < 1) n = 1;

        g2d.setColor(colors[1]);
        drawBlockFractal(g2d, x, y, size, n);
    }

    private static void drawBlockFractal(Graphics2D g2d, float x, float y, float size, int level) {
        if (level > 0) {
            float s3 = size / 3;
            drawBlockFractal(g2d, x, y, s3, level - 1);
            drawBlockFractal(g2d, x + 2 * s3, y, s3, level - 1);
            drawBlockFractal(g2d, x + s3, y + s3, s3, level - 1);
            drawBlockFractal(g2d, x, y + 2 * s3, s3, level - 1);
            drawBlockFractal(g2d, x + 2 * s3, y + 2 * s3, s3, level - 1);
        } else g2d.fillRect((int)x, (int)y, (int)size, (int)size);
    }

    public static void drawHilbertFractal(Graphics2D g2d, Color[] colors, int x, int y, int size, int n) {
        g2d.setColor(colors[2]);
        g2d.fillRect(x, y, size, size);

        if (n < 1) n = 1;

        double step = size / Math.pow(2, n);

        g2d.setColor(colors[1]);
        // start bottom-left, facing right
        hilbert(g2d, n, 1, 0, step, x + step / 2, y + size - step / 2);
    }

    public static double[] hilbert(Graphics2D g2d, int level, int rot, int dir, double step, double x, double y) {
// note: rot: +1 or -1; dir: 0=right, 1=up, 2=left, 3=down
        if (level > 0) {
            dir = (dir + rot) % 4;
            double[] pos = hilbert(g2d, level - 1, -rot, dir, step, x, y);
            pos = forward(g2d, dir, step, pos[0], pos[1]);
            dir = (dir - rot + 4) % 4;
            pos = hilbert(g2d, level - 1, rot, dir, step, pos[0], pos[1]);
            pos = forward(g2d, dir, step, pos[0], pos[1]);
            pos = hilbert(g2d, level - 1, rot, dir, step, pos[0], pos[1]);
            dir = (dir - rot + 4) % 4;
            pos = forward(g2d, dir, step, pos[0], pos[1]);
            pos = hilbert(g2d, level - 1, -rot, dir, step, pos[0], pos[1]);
            x = pos[0];
            y = pos[1];
        }
        return new double[]{x, y};
    }

    private static double[] forward(Graphics2D g2d, int dir, double step, double x, double y) {
        double nx = x, ny = y;
        switch (dir) {
            case 0: nx += step; break;
            case 1: ny -= step; break;
            case 2: nx -= step; break;
            case 3: ny += step; break;
        }
        g2d.drawLine((int) x, (int) y, (int) nx, (int) ny);
        return new double[]{nx, ny};
    }

    public static void drawGridSquareCircles(Graphics2D g2d, Color[] colors, int x, int y, int size, int n) {
        g2d.setColor(colors[3]);
        g2d.fillRect(x, y, size, size);

        int half = size / 2;
        int tileSize = half / (n + 1);

        g2d.setStroke(new BasicStroke(Math.max(1, tileSize / 12)));

        drawGridSquareCircles(g2d, colors, n, tileSize, x + tileSize / 2, y + tileSize / 2, tileSize / 7, true);
        drawGridSquareCircles(g2d, colors, n, tileSize, x + half + tileSize / 2, y + tileSize / 2, tileSize / 7, false);
        drawGridSquareCircles(g2d, colors, n, tileSize, x + tileSize / 2, y + half + tileSize / 2, tileSize / 7, false);
        drawGridSquareCircles(g2d, colors, n, tileSize, x + half + tileSize / 2, y + half + tileSize / 2, tileSize / 7, true);
    }

    private static void drawGridSquareCircles(Graphics2D g2d, Color[] colors, int n, int tileSize, int offset_x, int offset_y, int r, boolean mirror) {
        Color color1 = colors[1];
        Color color2 = colors[2];

        int d = r * 2;
        // Alternating thick grid lines
        for (int row = 0; row <= n; row++) {
            int y = offset_y + (row * tileSize);
            for (int col = 0; col <= n; col++) {
                int x = offset_x + (col * tileSize);
                g2d.setColor((row + col) % 2 == 0 ? color1 : color2);
                if (col < n)
                    g2d.drawLine(x, y, x + tileSize, y);
                if (mirror) {
                    if (row > 0)
                        g2d.drawLine(x, y, x, y - tileSize);
                } else {
                    if (row < n)
                        g2d.drawLine(x, y, x, y + tileSize);
                }
            }
        }

        // 2-sector circles at each intersection
        boolean invert = false;
        for (int row = 0; row <= n; row++) {
            int centerY = offset_y + (row * tileSize);
            for (int col = 0; col <= n; col++) {
                int centerX = offset_x + (col * tileSize);
                for (int angle = 0; angle < 360; angle += 180) {
                    g2d.setColor((angle == 0) == invert ? color1 : color2);
                    g2d.fillArc(centerX - r, centerY - r, d, d, angle + (mirror ? 135 : 45), 180);
                }
                invert = !invert;
            }
        }
    }

    public static void drawSquareSpiralTile(Graphics2D g2d, Color[] colors, int x, int y, int size, int increment) {
        g2d.setColor(colors[2]);
        g2d.fillRect(x, y, size, size);

        g2d.setStroke(new BasicStroke(Math.max(1, increment)));

        x += size / 2;
        y += size / 2;
        int side = increment;
        int turns = size / increment - 1;

        g2d.setColor(colors[1]);
        for (int i = 0; i < turns; i++) {
            float hue = (float) i / turns;

            switch (i % 4) {
                case 0: g2d.drawLine(x, y, x + side, y); x += side; break;
                case 1: g2d.drawLine(x, y, x, y + side); y += side; break;
                case 2: g2d.drawLine(x, y, x - side, y); x -= side; break;
                case 3: g2d.drawLine(x, y, x, y - side); y -= side; break;
            }
            side += increment;
        }
    }

    public static void drawSpiderWebTile(Graphics2D g2d, Color[] colors, int x, int y, int size, int sectors, int rings) {
        g2d.setColor(colors[2]);
        g2d.fillRect(x, y, size, size);

        int cx = x + size / 2;
        int cy = y + size / 2;
        double r = size / 2.0;
        double angle_step = 2 * Math.PI / sectors;

        g2d.setColor(colors[1]);
        g2d.setStroke(new BasicStroke(Math.max(1.5f, size / 80f)));

        for (int i = 0; i < sectors; i++) {
        // radial lines
            double angle = i * angle_step;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

			double dist = Math.min(
				Math.abs(cos) > 1e-9 ? r / Math.abs(cos) : Double.POSITIVE_INFINITY,
				Math.abs(sin) > 1e-9 ? r / Math.abs(sin) : Double.POSITIVE_INFINITY
			);

            g2d.drawLine((int)cx, (int)cy, (int) (cx + cos * dist), (int) (cy + sin * dist));

        // concentric rings
            double cos2 = Math.cos(angle + angle_step);
            double sin2 = Math.sin(angle + angle_step);
            for (int j = 1; j <= rings; j++) {
                double currentRadius = r * j / rings;

                int x1 = (int) (cx + cos * currentRadius);
                int y1 = (int) (cy + sin * currentRadius);
                int x2 = (int) (cx + cos2 * currentRadius);
                int y2 = (int) (cy + sin2 * currentRadius);

                g2d.drawLine(x1, y1, x2, y2);
            }
        }
    }

    public static Path2D createStar(double cx, double cy, double in, double out, int pts, double rotation) {
        Path2D p = new Path2D.Double();
        for (int i = 0; i < 2 * pts; i++) {
            double r = (i % 2 == 0) ? out : in;
            double a = i * Math.PI / pts + rotation;
            double px = cx + Math.cos(a) * r, py = cy + Math.sin(a) * r;
            if (i == 0)
                p.moveTo(px, py);
            else p.lineTo(px, py);
        }
        p.closePath();
        return p;
    }

    public static Path2D createPolygon(double cx, double cy, double side_length, int n_sides, double rotation) {
        Path2D p = new Path2D.Double();
        double r = side_length * 0.5 / Math.sin(Math.PI / n_sides);
        rotation -= Math.PI / n_sides;
        for (int i = 0; i < n_sides; i++) {
            double a = 2.0 * i * Math.PI / n_sides + rotation;
            double px = cx + Math.cos(a) * r, py = cy + Math.sin(a) * r;
            if (i == 0)
                p.moveTo(px, py);
            else p.lineTo(px, py);
        }
        p.closePath();
        return p;
    }

    public static Path2D createSquare(double cx, double cy, double s, double angle) {
        double half = s / 2.0;
        double[][] reference_corners = {{-half, -half}, { half, -half}, { half,  half}, {-half,  half}};

        Path2D path = new Path2D.Double();

        double cosA = Math.cos(angle);
        double sinA = Math.sin(angle);
        for (int i = 0; i < 4; i++) {
            double relX = reference_corners[i][0];
            double relY = reference_corners[i][1];

            double x = cx + relX * cosA - relY * sinA;
            double y = cy + relX * sinA + relY * cosA;

            if (i == 0) path.moveTo(x, y);
            else path.lineTo(x, y);
        }
        path.closePath();

        return path;
    }

    private static Path2D.Double createTriangle(double ax, double ay, double bx, double by, double cx, double cy) {
        Path2D.Double path = new Path2D.Double();

        path.moveTo(ax, ay);
        path.lineTo(bx, by);
        path.lineTo(cx, cy);
        path.closePath();

        return path;
    }

}