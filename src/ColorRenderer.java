import javax.swing.*;
import java.awt.*;

public class ColorRenderer extends JLabel implements ListCellRenderer<Color> {
    private final ColorIcon icon = new ColorIcon(30, 20);

    public ColorRenderer() {
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        setIcon(icon);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Color> list,
                                                   Color color,
                                                   int index,
                                                   boolean isSelected,
                                                   boolean cellHasFocus) {

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        icon.setColor(color);
        return this;
    }

    private static class ColorIcon implements Icon {
        private Color color;
        private final int width;
        private final int height;

        public ColorIcon(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (color == null) return;
            Graphics2D g2d = (Graphics2D) g.create();
            try {
                g2d.setColor(color);
                g2d.fillRect(x, y, width, height);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, y, width, height);
            } finally {
                g2d.dispose();
            }
        }

        @Override public int getIconWidth() { return width; }
        @Override public int getIconHeight() { return height; }
    }
}