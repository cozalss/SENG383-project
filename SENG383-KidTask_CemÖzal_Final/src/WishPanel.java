import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class WishPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JButton btnAdd, btnAction, btnDelete;
    private KidTaskMain mainFrame;

    public WishPanel(KidTaskMain mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(20, 20));
        setBackground(StyleTheme.BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblHeader = new JLabel("Wish List & Rewards");
        lblHeader.setFont(StyleTheme.TITLE_FONT);
        lblHeader.setForeground(StyleTheme.TEXT_DARK);

        String[] cols = {"Wish Item", "Cost", "Req. Level", "Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1 || columnIndex == 2) return Integer.class;
                return String.class;
            }
        };

        table = new JTable(model);
        StyleTheme.styleTable(table);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);

        table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);

        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                String status = (String) value;
                Color c = StyleTheme.TEXT_LIGHT;
                if ("GRANTED".equals(status)) c = StyleTheme.SUCCESS;
                else if ("REJECTED".equals(status)) c = StyleTheme.DANGER;
                else if ("REQUESTED".equals(status)) c = StyleTheme.PRIMARY;

                final Color finalColor = c;
                final String text = status;

                JLabel badge = new JLabel("") {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        // Arka plan (Yuvarlak Köşe)
                        g2.setColor(finalColor);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                        // Yazı (SADECE BEYAZ OLAN ÇİZİLECEK)
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                        FontMetrics fm = g2.getFontMetrics();
                        int x = (getWidth() - fm.stringWidth(text)) / 2;
                        int y = (getHeight() + fm.getAscent()) / 2 - 2;
                        g2.drawString(text, x, y);

                        g2.dispose();
                    }
                };
                badge.setPreferredSize(new Dimension(100, 22));
                return badge;
            }
        });

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.add(new JScrollPane(table));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(StyleTheme.BG_COLOR);

        btnAdd = StyleTheme.createModernButton(" ✨ Make a Wish ", StyleTheme.PRIMARY);
        btnAction = StyleTheme.createModernButton(" Grant / Reject ", StyleTheme.ACCENT);
        btnDelete = StyleTheme.createModernButton(" Delete ", StyleTheme.DANGER);

        btnAdd.addActionListener(e -> addWish());
        btnAction.addActionListener(e -> processWish());
        btnDelete.addActionListener(e -> deleteWish());

        btnPanel.add(btnAdd);
        btnPanel.add(btnAction);
        btnPanel.add(btnDelete);

        add(lblHeader, BorderLayout.NORTH);
        add(tableContainer, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    public void refreshTable() {
        model.setRowCount(0);
        int childLevel = DataManager.calculateLevel();

        for (Wish w : DataManager.wishList) {
            if ("CHILD".equals(DataManager.currentRole)) {
                if (w.getRequiredLevel() <= childLevel) {
                    model.addRow(new Object[]{ " " + w.getName(), w.getCost(), w.getRequiredLevel(), w.getStatus() });
                }
            } else {
                model.addRow(new Object[]{ " " + w.getName(), w.getCost(), w.getRequiredLevel(), w.getStatus() });
            }
        }

        if ("CHILD".equals(DataManager.currentRole)) {
            btnAdd.setVisible(true);
            btnAction.setVisible(false);
            btnDelete.setVisible(false);
        } else {
            btnAdd.setVisible(false);
            btnAction.setVisible(true);
            btnDelete.setVisible(true);
        }
    }

    private void addWish() {
        if (!"CHILD".equals(DataManager.currentRole)) return;

        WishDialog dialog = new WishDialog(mainFrame);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                DataManager.wishList.add(new Wish(
                        dialog.getWishName(),
                        dialog.getCost(),
                        "REQUESTED",
                        dialog.getRequiredLevel()
                ));
                DataManager.saveData();
                refreshTable();
                mainFrame.showNotification("Wish Added! Hope it comes true! ✨", "SUCCESS");
            } catch (Exception e) {
                mainFrame.showNotification("Error adding wish.", "ERROR");
            }
        }
    }

    private void processWish() {
        if ("CHILD".equals(DataManager.currentRole)) return;
        int row = table.getSelectedRow();
        if (row == -1) {
            mainFrame.showNotification("Please select a wish first!", "ERROR");
            return;
        }

        Wish w = null;
        String nameOnScreen = ((String) table.getValueAt(row, 0)).trim();
        for(Wish wish : DataManager.wishList) {
            if(wish.getName().equals(nameOnScreen)) { w = wish; break; }
        }
        if (w == null) return;

        if ("REQUESTED".equals(w.getStatus())) {
            int choice = JOptionPane.showOptionDialog(this, "Grant this wish?", "Decide",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                    new String[]{"Grant", "Reject"}, "Grant");

            if (choice == 0) {
                w.setStatus("GRANTED");
                // Konfeti kodu kaldırıldı
                mainFrame.showNotification("Wish Granted! You are awesome! 🎁", "SUCCESS");
            }
            else if (choice == 1) {
                w.setStatus("REJECTED");
                mainFrame.showNotification("Wish Rejected.", "WARNING");
            }
            DataManager.saveData();
            refreshTable();
        } else {
            mainFrame.showNotification("This wish is already processed.", "WARNING");
        }
    }

    private void deleteWish() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        String nameOnScreen = ((String) table.getValueAt(row, 0)).trim();
        DataManager.wishList.removeIf(w -> w.getName().equals(nameOnScreen));
        DataManager.saveData();
        refreshTable();
        mainFrame.showNotification("Wish Deleted.", "WARNING");
    }
}