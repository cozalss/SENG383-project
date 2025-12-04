import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class WishPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JButton btnAdd, btnAction, btnDelete;

    public WishPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(StyleTheme.BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblHeader = new JLabel("Wish List & Rewards");
        lblHeader.setFont(StyleTheme.TITLE_FONT);
        lblHeader.setForeground(StyleTheme.TEXT_DARK);

        String[] cols = {"Wish Item", "Cost", "Req. Level", "Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(model);
        StyleTheme.styleTable(table);

        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                String status = (String) value;
                Color c = StyleTheme.TEXT_LIGHT;
                if ("GRANTED".equals(status)) c = StyleTheme.SUCCESS;
                else if ("REJECTED".equals(status)) c = StyleTheme.DANGER;
                else if ("REQUESTED".equals(status)) c = StyleTheme.PRIMARY;

                final Color finalColor = c;
                JLabel badge = new JLabel(status) {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(finalColor);
                        g2.fillRoundRect(0,0,getWidth(),getHeight(),15,15);
                        super.paintComponent(g);
                    }
                };
                badge.setForeground(Color.WHITE);
                badge.setHorizontalAlignment(CENTER);
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
                    model.addRow(new Object[]{
                            " " + w.getName(),
                            w.getCost(),
                            w.getRequiredLevel(),
                            w.getStatus()
                    });
                }
            } else {
                model.addRow(new Object[]{
                        " " + w.getName(),
                        w.getCost(),
                        w.getRequiredLevel(),
                        w.getStatus()
                });
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

        JTextField nameF = new JTextField();
        JTextField costF = new JTextField();
        JTextField lvlF = new JTextField("1");

        Object[] msg = {"Wish Name:", nameF, "Cost:", costF, "Required Level:", lvlF};

        if (JOptionPane.showConfirmDialog(this, msg, "New Wish", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                int cost = Integer.parseInt(costF.getText());
                int reqLvl = Integer.parseInt(lvlF.getText());
                DataManager.wishList.add(new Wish(nameF.getText(), cost, "REQUESTED", reqLvl));
                DataManager.saveData();
                refreshTable();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid inputs.");
            }
        }
    }

    private void processWish() {
        if ("CHILD".equals(DataManager.currentRole)) return;
        int row = table.getSelectedRow();
        if (row == -1) return;

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

            if (choice == 0) w.setStatus("GRANTED");
            else if (choice == 1) w.setStatus("REJECTED");

            DataManager.saveData();
            refreshTable();
        }
    }

    private void deleteWish() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        String nameOnScreen = ((String) table.getValueAt(row, 0)).trim();
        DataManager.wishList.removeIf(w -> w.getName().equals(nameOnScreen));
        DataManager.saveData();
        refreshTable();
    }
}