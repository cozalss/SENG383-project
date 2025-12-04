import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class StyleTheme {

    // --- Modern Pastel Palet ---
    public static final Color BG_COLOR = new Color(245, 247, 250);
    public static final Color CARD_BG = Color.WHITE;

    public static final Color PRIMARY = new Color(59, 130, 246);
    public static final Color ACCENT = new Color(245, 158, 11);
    public static final Color SUCCESS = new Color(16, 185, 129);
    public static final Color DANGER = new Color(239, 68, 68);
    public static final Color TEXT_DARK = new Color(30, 41, 59);
    public static final Color TEXT_LIGHT = new Color(100, 116, 139);

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font DATA_FONT = new Font("Segoe UI Emoji", Font.PLAIN, 15);
    public static final Font BTN_FONT = new Font("Segoe UI Emoji", Font.BOLD, 14);
    public static JButton createModernButton(String text, Color baseColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isRollover()) {
                    g2.setColor(baseColor.darker());
                } else {
                    g2.setColor(baseColor);
                }

                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));

                g2.setColor(Color.WHITE);
                g2.setFont(BTN_FONT);

                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), x, y);

                g2.dispose();
            }
        };

        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 25, 10, 25));
        return btn;
    }


    public static void styleTable(JTable table) {
        table.setRowHeight(45);
        table.setFont(DATA_FONT);
        table.setGridColor(new Color(240, 240, 240));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        table.setSelectionBackground(new Color(230, 240, 255));
        table.setSelectionForeground(TEXT_DARK);

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(Color.WHITE);
                label.setForeground(TEXT_LIGHT);
                label.setFont(HEADER_FONT);
                label.setHorizontalAlignment(JLabel.LEFT);
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));
                label.setPreferredSize(new Dimension(100, 50));
                return label;
            }
        });
    }
}