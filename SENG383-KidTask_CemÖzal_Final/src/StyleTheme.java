import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class StyleTheme {

    public static final Color BG_COLOR = new Color(230, 224, 255);
    public static final Color CARD_BG = Color.WHITE;

    public static final Color PRIMARY = new Color(124, 58, 237);
    public static final Color ACCENT = new Color(245, 158, 11);
    public static final Color SUCCESS = new Color(16, 185, 129);
    public static final Color DANGER = new Color(239, 68, 68);

    public static final Color TEXT_DARK = new Color(45, 20, 60);
    public static final Color TEXT_LIGHT = new Color(100, 116, 139);

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font DATA_FONT = new Font("Segoe UI Emoji", Font.PLAIN, 15);
    public static final Font BTN_FONT = new Font("Segoe UI Emoji", Font.BOLD, 14);

    public static JButton createModernButton(String text, Color baseColor) {
        JButton btn = new JButton(text) {
            private Point rippleCenter;
            private int rippleRadius = 0;
            private Timer rippleTimer;
            private float rippleAlpha = 0.3f;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                g2.setColor(baseColor);
                if (getModel().isRollover()) {
                    g2.setColor(baseColor.darker());
                }
                RoundRectangle2D.Float shape = new RoundRectangle2D.Float(0, 0, w, h, 20, 20);
                g2.fill(shape);

                if (rippleCenter != null && rippleRadius > 0) {
                    g2.setClip(shape);

                    g2.setColor(new Color(1f, 1f, 1f, rippleAlpha));
                    int r = rippleRadius;
                    g2.fillOval(rippleCenter.x - r, rippleCenter.y - r, r * 2, r * 2);
                }

                g2.setClip(null);
                g2.setColor(Color.WHITE);
                g2.setFont(BTN_FONT);
                FontMetrics fm = g2.getFontMetrics();
                int x = (w - fm.stringWidth(getText())) / 2;
                int y = (h + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), x, y);

                g2.dispose();
            }
            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        rippleCenter = e.getPoint();
                        rippleRadius = 0;
                        rippleAlpha = 0.4f;

                        if (rippleTimer != null && rippleTimer.isRunning()) rippleTimer.stop();

                        rippleTimer = new Timer(10, evt -> {
                            rippleRadius += 4;
                            if (rippleAlpha > 0.01f) rippleAlpha -= 0.01f; // Yavaşça sönme
                            else rippleAlpha = 0;

                            if (rippleAlpha <= 0 || rippleRadius > getWidth() * 1.5) {
                                ((Timer)evt.getSource()).stop();
                                rippleRadius = 0;
                            }
                            repaint();
                        });
                        rippleTimer.start();
                    }
                });
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

        table.setSelectionBackground(new Color(200, 190, 255));
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