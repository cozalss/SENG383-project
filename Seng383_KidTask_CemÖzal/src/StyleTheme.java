import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class StyleTheme {

    public static final Color PRIMARY = new Color(52, 152, 219);
    public static final Color SECONDARY = new Color(236, 240, 241);
    public static final Color ACCENT = new Color(230, 126, 34);
    public static final Color TEXT_COLOR = new Color(44, 62, 80);
    public static final Color SUCCESS = new Color(46, 204, 113);
    public static final Color DANGER = new Color(231, 76, 60);

    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font DATA_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BTN_FONT = new Font("Segoe UI", Font.BOLD, 14);


    private static void applyBaseStyle(JButton btn) {
        btn.setForeground(Color.WHITE);
        btn.setFont(BTN_FONT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }


    public static void styleButton(JButton btn) {
        applyBaseStyle(btn);
        btn.setBackground(PRIMARY);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(PRIMARY.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(PRIMARY);
            }
        });
    }


    public static void styleButton(JButton btn, Color customColor) {
        applyBaseStyle(btn);
        btn.setBackground(customColor);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(customColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(customColor);
            }
        });
    }


    public static void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(DATA_FONT);
        table.setGridColor(new Color(220, 220, 220));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(174, 214, 241));
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(new Color(52, 73, 94)); // Koyu Lacivert
                label.setForeground(Color.WHITE);
                label.setFont(HEADER_FONT);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.GRAY));
                return label;
            }
        });

        header.setPreferredSize(new Dimension(100, 40));

        // Hücre Ortalama
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
}