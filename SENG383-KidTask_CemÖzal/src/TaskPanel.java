import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TaskPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private KidTaskMain mainFrame;
    private JButton btnAdd, btnDelete, btnAction;
    private JComboBox<String> filterBox;

    public TaskPanel(KidTaskMain mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(20, 20));
        setBackground(StyleTheme.BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(StyleTheme.BG_COLOR);

        JLabel lblHeader = new JLabel("Task Management");
        lblHeader.setFont(StyleTheme.TITLE_FONT);
        lblHeader.setForeground(StyleTheme.TEXT_DARK);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setBackground(StyleTheme.BG_COLOR);
        filterPanel.add(new JLabel("Filter:"));

        filterBox = new JComboBox<>(new String[]{"All", "DAILY", "WEEKLY", "ONCE"});
        filterBox.addActionListener(e -> refreshTable());
        filterPanel.add(filterBox);

        headerPanel.add(lblHeader, BorderLayout.WEST);
        headerPanel.add(filterPanel, BorderLayout.EAST);

        String[] cols = {"Task Title", "Description", "Points", "Due Date", "Frequency", "Status", "Rating"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) return Integer.class;
                return String.class;
            }
        };

        table = new JTable(model);
        StyleTheme.styleTable(table);

        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                String status = (String) value;
                Color tempColor = Color.GRAY;
                String text = status;

                if ("APPROVED".equals(status)) {
                    tempColor = StyleTheme.SUCCESS;
                    text = "APPROVED";
                } else if ("COMPLETED".equals(status)) {
                    tempColor = StyleTheme.ACCENT;
                    text = "PENDING";
                } else if ("PENDING".equals(status)) {
                    tempColor = StyleTheme.TEXT_LIGHT;
                    text = "TO DO";
                }

                final Color badgeColor = tempColor;
                JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
                badgePanel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);

                JLabel badge = new JLabel(text) {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(badgeColor);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                        FontMetrics fm = g2.getFontMetrics();
                        int x = (getWidth() - fm.stringWidth(getText())) / 2;
                        int y = (getHeight() + fm.getAscent()) / 2 - 2;
                        g2.drawString(getText(), x, y);
                        g2.dispose();
                    }
                };
                badge.setText(text);
                badge.setPreferredSize(new Dimension(100, 22));
                badgePanel.add(badge);
                return badgePanel;
            }
        });

        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                String valStr = value != null ? value.toString() : "";
                int stars = 0;
                try {
                    if(valStr.contains("⭐")) {
                        stars = Integer.parseInt(valStr.split(" ")[0]);
                    } else if (!valStr.equals("-") && !valStr.isEmpty()) {
                        try { stars = Integer.parseInt(valStr); } catch(Exception ignored){}
                    }
                } catch(Exception e){}

                JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);

                if (stars > 0) {
                    String starString = new String(new char[stars]).replace("\0", "⭐");
                    JLabel star = new JLabel(starString);
                    star.setForeground(new Color(255, 193, 7));
                    star.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
                    panel.add(star);
                } else {
                    JLabel dash = new JLabel("-");
                    dash.setForeground(Color.LIGHT_GRAY);
                    panel.add(dash);
                }
                return panel;
            }
        });

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.add(new JScrollPane(table));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnPanel.setBackground(StyleTheme.BG_COLOR);

        btnAdd = StyleTheme.createModernButton(" + Add Task ", StyleTheme.PRIMARY);
        btnAction = StyleTheme.createModernButton(" Complete / Approve ", StyleTheme.SUCCESS);
        btnDelete = StyleTheme.createModernButton(" Delete ", StyleTheme.DANGER);

        btnAdd.addActionListener(e -> addTask());
        btnAction.addActionListener(e -> processTask());
        btnDelete.addActionListener(e -> deleteTask());

        btnPanel.add(btnAdd);
        btnPanel.add(btnAction);
        btnPanel.add(btnDelete);

        add(headerPanel, BorderLayout.NORTH);
        add(tableContainer, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    public void refreshTable() {
        model.setRowCount(0);
        String filter = (String) filterBox.getSelectedItem();

        for (Task t : DataManager.taskList) {
            if (filter != null && !"All".equals(filter)) {
                if (t.getFrequency() == null || !t.getFrequency().equalsIgnoreCase(filter)) {
                    continue;
                }
            }

            model.addRow(new Object[]{
                    " " + t.getTitle(),
                    t.getDescription(),
                    t.getPoints(),
                    t.getDueDate(),
                    t.getFrequency(),
                    t.getStatus(),
                    t.getRating() > 0 ? t.getRating() + " ⭐" : "-"
            });
        }

        if ("CHILD".equals(DataManager.currentRole)) {
            btnAdd.setVisible(false);
            btnDelete.setVisible(false);
        } else {
            btnAdd.setVisible(true);
            btnDelete.setVisible(true);
        }
    }

    private void addTask() {
        if ("CHILD".equals(DataManager.currentRole)) return;

        JTextField titleF = new JTextField();
        JTextField descF = new JTextField();
        JTextField pointF = new JTextField();
        JTextField dateF = new JTextField("2025-12-31");
        JComboBox<String> freqF = new JComboBox<>(new String[]{"ONCE", "DAILY", "WEEKLY"});

        Object[] message = {
                "Title:", titleF,
                "Description:", descF,
                "Points:", pointF,
                "Due Date:", dateF,
                "Frequency:", freqF
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Create New Task", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int pts = Integer.parseInt(pointF.getText());
                DataManager.taskList.add(new Task(
                        titleF.getText(),
                        descF.getText(),
                        pts,
                        "PENDING",
                        0,
                        dateF.getText(),
                        (String) freqF.getSelectedItem()
                ));
                DataManager.saveData();
                refreshTable();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid number format.");
            }
        }
    }

    private void processTask() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        String titleOnScreen = ((String) table.getValueAt(row, 0)).trim();
        Task task = null;

        for(Task t : DataManager.taskList) {
            if(t.getTitle().equals(titleOnScreen)) { task = t; break; }
        }

        if (task == null) return;

        String role = DataManager.currentRole;

        if ("CHILD".equals(role)) {
            if ("PENDING".equals(task.getStatus())) {
                task.setStatus("COMPLETED");
                JOptionPane.showMessageDialog(this, "Marked as completed!");
            }
        }
        else {
            if ("COMPLETED".equals(task.getStatus())) {
                String result = JOptionPane.showInputDialog("Rate this task (1-5):");
                if (result != null) {
                    try {
                        int r = Integer.parseInt(result);
                        if(r < 1) r = 1; if(r > 5) r = 5;
                        task.setRating(r);
                        task.setStatus("APPROVED");
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Please enter a valid number.");
                    }
                }
            }
        }
        DataManager.saveData();
        refreshTable();
        mainFrame.updateDashboardStats();
    }

    private void deleteTask() {
        if ("CHILD".equals(DataManager.currentRole)) return;
        int row = table.getSelectedRow();
        if (row == -1) return;

        String titleOnScreen = ((String) table.getValueAt(row, 0)).trim();
        DataManager.taskList.removeIf(t -> t.getTitle().equals(titleOnScreen));

        DataManager.saveData();
        refreshTable();
        mainFrame.updateDashboardStats();
    }
}