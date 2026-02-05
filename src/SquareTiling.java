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
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import tiles.*;
import tools.*;

/**
 * Swing application to visualize square tilings.
 * UI to select the tile, the size of the tile, colors and to save the image on file.
 *
 * v1.0, 23-01-2026: Square Tiling first release
 *
 */
public class SquareTiling extends JFrame {

    enum TileType {
        USER_MODE("User Mode"),
        GREEK("Greek Pattern"),
        IPATTERN1("Layered Islamic Star"),
        IPATTERN2("Quartered Islamic Star"),
        IPATTERN3("Eightfold Islamic Star"),
        INTERLOCK("Interlocking Squares"),
        OCTAGRAM1("Large Octagram"),
        OCTAGRAM2("Narrow Octagram"),
        OCTAGON("Octagons & Squares"),
        OCTAGON2("Octagon & Rhombus"),
        SQUARES("Squares and Rhombus"),
        TARTAN("Tartan"),
        CROSSED("Crossed Square"),
        INTERLACED("Interlaced Circles"),
        LABYRINTH("Labyrinth  pattern"),
        TRUCHET2("Truchet pattern #2"),
        TRUCHET3("Truchet pattern #3"),
        TRUCHET4("Truchet pattern #4"),
        WANG("Wang tiling"),
        VORONOI("Voronoi tiling"),
        BLCKFRCTL("Block Fractal"),
        HLBRTFRCTL("Hilbert Fractal"),
        POLYGONFRCTL("Polygon Fractal"),
        GRIDSQRCIRCLE("Grid Squares & Circles"),
        SQRSPIRAL("Square Spiral"),
        SPIDERWEB("Spider Web"),
        CHECKERED("Checkered");

        final String title;
        TileType(String title) { this.title = title; }
    }
    private TileType currentType = TileType.GREEK;

    private static int tileSize = 150;

    private static final Color[] colors = {new Color(255, 180, 0),   // Gold
        new Color(0, 20, 60), // Deep Blue
        Color.WHITE,
        new Color(0, 200, 210)};// Cyan
    private Color fillColor = colors[0];
    private final Color colorUserGrid = Color.GRAY;

    enum EditorMode {
        LINE, ARC, FILL
    }
    private final TilingPanel tilingPanel;
    private JButton arcBtn;
    private JButton fillBtn;
    private JButton lineBtn;
    private final JButton backBtn;
    private final JButton deleteBtn;
    private final JButton userBtn;
    private final JButton helpBtn;
    private final JButton galleryBtn;
    private final JComboBox<Color> colorComboBox;
    private final JLabel sizeLabel;
    private final JSlider sizeSlider;

    private EditorMode editorMode = EditorMode.LINE;
    private final Color boundaryColor = Color.BLACK;

    // User Drawing Data
    private final List<DrawingAction> actionHistory = new ArrayList<>();
    private Point dragStart = null;
    private Point currentEndPoint = null;
    private Point arcIntermediatePoint = null;

    public SquareTiling() {
        setTitle("Geometric Square Tiling Generator");
        setSize(1000, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tilingPanel = new TilingPanel();

        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.DARK_GRAY);
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));

        JComboBox<TileType> typeCombo = new JComboBox<>(TileType.values());
        typeCombo.setSelectedItem(currentType);
        typeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof TileType) setText(((TileType) value).title);
                return this;
            }
        });

        typeCombo.addActionListener(e -> {
            currentType = (TileType) typeCombo.getSelectedItem();
            tilingPanel.clearCache();
            updateUILayout();
            repaint();
        });

        JButton showTileBtn = new JButton("Show Tile");
        showTileBtn.addActionListener(e -> showSingleTile());

        JButton saveBtn = new JButton("Save Image");
        saveBtn.addActionListener(e -> saveImage());

        galleryBtn = new JButton("Gallery");
        galleryBtn.addActionListener(e -> showGallery(typeCombo));

        addControl(topPanel, "Tile Pattern:", typeCombo);
        topPanel.add(showTileBtn);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(saveBtn);

        lineBtn = createIconButton("ic_line.png", e -> {
            editorMode = EditorMode.LINE;
            lineBtn.setBackground(Color.LIGHT_GRAY);
            arcBtn.setBackground(null);
            fillBtn.setBackground(null);
        });
        arcBtn = createIconButton("ic_arc.png", e -> {
            editorMode = EditorMode.ARC;
            arcBtn.setBackground(Color.LIGHT_GRAY);
            lineBtn.setBackground(null);
            fillBtn.setBackground(null);
        });
        fillBtn = createIconButton("ic_fill.png", e -> {
            editorMode = EditorMode.FILL;
            fillBtn.setBackground(Color.LIGHT_GRAY);
            lineBtn.setBackground(null);
            arcBtn.setBackground(null);
        });
        backBtn = createIconButton("ic_back.png", e -> {
            if (!actionHistory.isEmpty()) {
                actionHistory.remove(actionHistory.size() - 1);
                tilingPanel.clearCache();
                repaint();
            }
        });
        deleteBtn = createIconButton("ic_delete.png", e -> {
            actionHistory.clear();
            tilingPanel.clearCache();
            repaint();
        });
        userBtn = new JButton("User Mode");
        userBtn.addActionListener(e -> typeCombo.setSelectedItem(TileType.USER_MODE));
        helpBtn = new JButton("?");
        helpBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                SquareTiling.this,
                "Use mouse to draw lines, arcs or fill area.\nSelect mode by clicking either the line or arc or filler icon.\nIn line mode, left click and move mouse, a ghost line is shown, release button.\nIn arc mode, left click to set start of arc, move mouse, a ghost line is shown,\nrelease button to set end of arc; then click for an intermediate point of the arc to draw it.\nIn filler mode, left click and release. Filler color can be changed in the selection list.\nUndo drawings by clicking back icon.\nClear drawing area by clicking X icon.",
                "Help",
                JOptionPane.INFORMATION_MESSAGE
            );
        });

        colorComboBox = new JComboBox<>(colors);
        colorComboBox.setRenderer(new ColorRenderer());
        colorComboBox.addActionListener(e -> {
            Color selectedColor = (Color) colorComboBox.getSelectedItem();
            if (selectedColor != null) {
                fillColor = selectedColor;
                getContentPane().setBackground(selectedColor);
            }
        });

        topPanel.add(lineBtn);
        topPanel.add(arcBtn);
        topPanel.add(fillBtn);
        topPanel.add(backBtn);
        topPanel.add(deleteBtn);
        topPanel.add(colorComboBox);
        topPanel.add(helpBtn);
        topPanel.add(userBtn);
        topPanel.add(galleryBtn);

        JPanel sidebar = new JPanel();
        sidebar.setBackground(Color.DARK_GRAY);
        sidebar.setPreferredSize(new Dimension(80, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(20, 10, 10, 10));

        sizeLabel = new JLabel("Tile Size");
        sizeLabel.setForeground(Color.WHITE);
        sizeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        sizeSlider = new JSlider(JSlider.VERTICAL, 40, 400, 150);
        sizeSlider.setBackground(Color.DARK_GRAY);
        sizeSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        sizeSlider.addChangeListener(e -> {
            tileSize = sizeSlider.getValue();
            tilingPanel.clearCache();
            repaint();
        });

        JLabel colorLabel = new JLabel("Colors");
        colorLabel.setForeground(Color.WHITE);
        colorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        colorLabel.setBorder(new EmptyBorder(20, 0, 10, 0));

        JButton[] colorButtons = {
            createColorButton(colors[0], c -> colors[0] = c),
            createColorButton(colors[1], c -> colors[1] = c),
            createColorButton(colors[2], c -> colors[2] = c),
            createColorButton(colors[3], c -> colors[3] = c)
        };

        sidebar.add(sizeLabel);
        sidebar.add(sizeSlider);
        sidebar.add(colorLabel);
        for (JButton btn : colorButtons) {
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        updateUILayout();

        add(topPanel, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(tilingPanel, BorderLayout.CENTER);
    }

    private void updateUILayout() {
        boolean isUserMode = (currentType == TileType.USER_MODE);
        if (isUserMode)//resize not allowed in user mode
            sizeSlider.setValue(150);
        lineBtn.setVisible(isUserMode);
        arcBtn.setVisible(isUserMode);
        fillBtn.setVisible(isUserMode);
        backBtn.setVisible(isUserMode);
        deleteBtn.setVisible(isUserMode);
        colorComboBox.setVisible(isUserMode);
        helpBtn.setVisible(isUserMode);
        galleryBtn.setVisible(!isUserMode);
        userBtn.setVisible(!isUserMode);
        sizeLabel.setEnabled(!isUserMode);
        sizeSlider.setEnabled(!isUserMode);
        lineBtn.getParent().revalidate();
    }

    private JButton createIconButton(String icon, ActionListener l) {
        JButton button = new JButton(new ImageIcon(SquareTiling.class.getResource(icon)));
        button.setPreferredSize(new Dimension(30, 30));
        button.addActionListener(l);
        return button;
    }

    private void showGallery(JComboBox<TileType> typeCombo) {
        JDialog galleryDialog = new JDialog(this, "Tile Gallery", true);
        galleryDialog.setUndecorated(false);
        galleryDialog.setSize(tileSize * 3 + 100, tileSize * 2 + 100);
        galleryDialog.setLocationRelativeTo(this);

        JPanel gridPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        gridPanel.setBackground(Color.DARK_GRAY);
        gridPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        for (TileType type : TileType.values()) {
            if (type == TileType.USER_MODE && actionHistory.isEmpty()) continue;

            JPanel tilePreview = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    int size = Math.min(getWidth(), getHeight());

                    drawTile(type, g2d, 0, 0, size);

                    g2d.setColor(Color.GREEN);
                    g2d.drawString(type.title, 10, 20);
                }
            };
            tilePreview.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent me) {
                    if (SwingUtilities.isLeftMouseButton(me)) {
                        currentType = type;
                        typeCombo.setSelectedItem(type);
                        tilingPanel.clearCache();

                        updateUILayout();
                        repaint();

                        galleryDialog.dispose();
                    }
                }

                @Override
                public void mouseEntered(MouseEvent me) {}

                @Override
                public void mouseExited(MouseEvent me) {}
            });
            tilePreview.setPreferredSize(new Dimension(tileSize, tileSize));
            tilePreview.setBackground(Color.DARK_GRAY);
            gridPanel.add(tilePreview);
        }

        galleryDialog.add(new JScrollPane(gridPanel));
        galleryDialog.setVisible(true);
    }

    class TilingPanel extends JPanel {
        private BufferedImage cachedTile;
        private TexturePaint tilingPaint;

        public TilingPanel() {
            setBackground(Color.BLACK);
            MouseAdapter ma = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (currentType != TileType.USER_MODE || !SwingUtilities.isLeftMouseButton(e)) return;
                    Point p = new Point(e.getX() % tileSize, e.getY() % tileSize);
                    if (editorMode == EditorMode.FILL)
                        performFill(p);
                    else if (editorMode == EditorMode.LINE || (editorMode == EditorMode.ARC) && !waitingIntermediatePoint()) {
                        dragStart = p; currentEndPoint = p;
                    }
                }
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (currentType == TileType.USER_MODE && editorMode != EditorMode.FILL && dragStart != null) {
                        currentEndPoint = new Point(e.getX() % tileSize, e.getY() % tileSize);
                        repaint();
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (currentType == TileType.USER_MODE && editorMode != EditorMode.FILL) {
                        Point p = new Point(e.getX() % tileSize, e.getY() % tileSize);
                        if (dragStart != null) {
                            if (p.distance(dragStart) > 2) {
                                if (editorMode == EditorMode.LINE)
                                    actionHistory.add(new LineAction(dragStart, p, boundaryColor));
                                else {//editorMode == EditorMode.ARC
                                    actionHistory.add(new ArcAction(dragStart, p, boundaryColor));
//                                  tilingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                                }
                                clearCache();
                            }
                            dragStart = null; currentEndPoint = null;
                            repaint();
                        } else if (editorMode == EditorMode.ARC) {//editorMode == EditorMode.ARC
                            if (waitingIntermediatePoint()) {
                                DrawingAction lastAction = actionHistory.get(actionHistory.size() - 1);
                                ((ArcAction)lastAction).setIntermediatePoint(p);
                                clearCache();
//                              tilingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                                repaint();
                            }
                        }
                    }
                }
            };
            addMouseListener(ma);
            addMouseMotionListener(ma);
        }

        private boolean waitingIntermediatePoint() {
            if (actionHistory.size() > 0) {
                DrawingAction lastAction = actionHistory.get(actionHistory.size() - 1);
                return lastAction instanceof ArcAction && ((ArcAction)lastAction).getIntermediatePoint() == null;
            }
            return false;
        }

        private void performFill(Point p) {
            BufferedImage buffer = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = buffer.createGraphics();
            drawUserTile(g, 0, 0, tileSize); // Draw current state to detect boundaries
            g.dispose();

            BufferedImage fillLayer = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
            floodFill(fillLayer, buffer, p.x, p.y, fillColor);

            actionHistory.add(new FillAction(fillLayer));
            clearCache();
            repaint();
        }

        public void clearCache() {
            cachedTile = null;
            tilingPaint = null;
        }

        private void updateCache() {
            cachedTile = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = cachedTile.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            drawTile(currentType, g2d, 0, 0, tileSize);
            g2d.dispose();

            tilingPaint = new TexturePaint(cachedTile, new Rectangle2D.Double(0, 0, tileSize, tileSize));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (tilingPaint == null) updateCache();

            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            g2d.setPaint(tilingPaint);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            if (currentType == TileType.USER_MODE && editorMode != EditorMode.FILL && dragStart != null && currentEndPoint != null) {
                g2d.setColor(colors[1]);
                g2d.setStroke(new BasicStroke(2f));
                // Show the ghost line on all tiles simultaneously
                for (int y = 0; y < getHeight(); y += tileSize) {
                    for (int x = 0; x < getWidth(); x += tileSize) {
                        g2d.drawLine(x + dragStart.x, y + dragStart.y, x + currentEndPoint.x, y + currentEndPoint.y);
                    }
                }
            }
        }
    }

    private void floodFill(BufferedImage img, BufferedImage mask, int x, int y, Color fillCol) {
        int fillRGB = fillCol.getRGB();
        int boundaryRGB = boundaryColor.getRGB();
        if (mask.getRGB(x, y) == boundaryRGB) return;

        Queue<Point> q = new LinkedList<>();
        q.add(new Point(x, y));
        boolean[][] visited = new boolean[tileSize][tileSize];

        while (!q.isEmpty()) {
            Point curr = q.poll();
            if (curr.x < 0 || curr.x >= tileSize || curr.y < 0 || curr.y >= tileSize) continue;
            if (visited[curr.x][curr.y] || (mask.getRGB(curr.x, curr.y) & 0xFFFFFF) == (boundaryRGB & 0xFFFFFF)) continue;

            visited[curr.x][curr.y] = true;
            img.setRGB(curr.x, curr.y, fillRGB);

            q.add(new Point(curr.x + 1, curr.y));
            q.add(new Point(curr.x - 1, curr.y));
            q.add(new Point(curr.x, curr.y + 1));
            q.add(new Point(curr.x, curr.y - 1));
        }
    }

    private void drawTile(TileType tile, Graphics2D g2d, int x, int y, int size) {
        switch (tile) {
            case USER_MODE: drawUserTile(g2d, x, y, size); break;
            case GREEK: Tiles.drawGreekTile(g2d, colors, x, y, size); break;
            case IPATTERN1: Tiles.drawIslamicStarTile1(g2d, colors, x, y, size); break;
            case IPATTERN2: Tiles.drawIslamicStarTile2(g2d, colors, x, y, size); break;
            case IPATTERN3: Tiles.drawIslamicStarTile3(g2d, colors, x, y, size); break;
            case INTERLOCK: Tiles.drawInterlockingTile(g2d, colors, x, y, size); break;
            case OCTAGRAM1: Tiles.drawOctagramTile(g2d, colors, x, y, size, 0.384); break;
            case OCTAGRAM2: Tiles.drawOctagramTile(g2d, colors, x, y, size, 0.27); break;
            case OCTAGON: Tiles.drawOctagonTile(g2d, colors, x, y, size, 1.0 / (2.0 + Math.sqrt(2))); break;
            case OCTAGON2: Tiles.drawOctagonTile(g2d, colors, x, y, size, 1.0 / (1.0 + Math.sqrt(2))); break;
            case SQUARES: Tiles.drawSquaresTile(g2d, colors, x, y, size); break;
            case TARTAN: Tiles.drawTartanTile(g2d, colors, x, y, size); break;
            case CROSSED: Tiles.drawCrossedTile(g2d, colors, x, y, size); break;
            case INTERLACED: Tiles.drawInterlacedTile(g2d, colors, x, y, size); break;
            case LABYRINTH: Tiles.drawLabyrinthTile(g2d, colors, x, y, size, size / 10.0); break;
            case TRUCHET2: Tiles.drawTruchetTile(g2d, colors, x, y, size, 2, size / 10.0); break;
            case TRUCHET3: Tiles.drawTruchetTile(g2d, colors, x, y, size, 3, size / 10.0); break;
            case TRUCHET4: Tiles.drawTruchetTile(g2d, colors, x, y, size, 4, size / 10.0); break;
            case WANG: Tiles.drawWangTile(g2d, colors, x, y, size, 8); break;
            case VORONOI: Tiles.drawVoronoiTile(g2d, x, y, size, 40); break;
            case BLCKFRCTL: Tiles.drawBlockFractal(g2d, colors, x, y, size, (int) (Math.log(size) / Math.log(3) - 1)); break;
            case HLBRTFRCTL: Tiles.drawHilbertFractal(g2d, colors, x, y, size, (int) (Math.log(size) / Math.log(2) - 4)); break;
            case POLYGONFRCTL: Tiles.drawFractalPolygon(g2d, colors, x, y, size, 8, 3, 0.4); break;
            case GRIDSQRCIRCLE: Tiles.drawGridSquareCircles(g2d, colors, x, y, size, 6); break;
            case SQRSPIRAL: Tiles.drawSquareSpiralTile(g2d, colors, x, y, size, 10); break;
            case SPIDERWEB: Tiles.drawSpiderWebTile(g2d, colors, x, y, size, 16, 8); break;
            case CHECKERED: Tiles.drawCheckeredTile(g2d, colors, x, y, size); break;
            default: break;
        }
    }

    private void drawUserTile(Graphics2D g2d, int x, int y, int size) {
        g2d.setColor(colors[2]);
        g2d.fillRect(x, y, size, size);

        g2d.setColor(colorUserGrid);
        g2d.setStroke(new BasicStroke(1f));
        g2d.drawLine(x, y, x, y + size);
        g2d.drawLine(x, y, x + size, y);

        for (DrawingAction action : actionHistory)
            action.draw(g2d, size, tileSize);
    }


    private void showSingleTile() {
        JDialog dialog = new JDialog(this, currentType.title, true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);

        JPanel previewPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(colors[1]);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                int size = Math.min(getWidth(), getHeight());
                drawTile(currentType, g2d, (getWidth() - size) / 2, (getHeight() - size) / 2, size);
            }
        };
        dialog.add(previewPanel);
        dialog.setVisible(true);
    }

    private JButton createColorButton(Color initial, java.util.function.Consumer<Color> setter) {
        JButton colorBtn = new JButton();
        colorBtn.setPreferredSize(new Dimension(30, 30));
        colorBtn.setBackground(initial);
        colorBtn.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Select Color", initial);
            if (newColor != null) {
                setter.accept(newColor); colorBtn.setBackground(newColor);
                tilingPanel.clearCache(); repaint();
            }
        });
        return colorBtn;
    }

    private void addControl(JPanel panel, String labelText, JComponent comp) {
        JLabel label = new JLabel(labelText); label.setForeground(Color.WHITE);
        panel.add(label); panel.add(comp);
    }

    private void saveImage() {
        BufferedImage image = new BufferedImage(tilingPanel.getWidth(), tilingPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics(); tilingPanel.paintAll(g2d); g2d.dispose();
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if (!f.getName().toLowerCase().endsWith(".png")) f = new File(f.getAbsolutePath() + ".png");
            try { ImageIO.write(image, "png", f); } catch (IOException ex) { ex.printStackTrace(); }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SquareTiling().setVisible(true));
    }
}