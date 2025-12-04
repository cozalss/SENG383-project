import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TaskPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private KidTaskMain mainFrame;

    private JButton btnAdd;
    private JButton btnDelete;
    private JButton btnAction;

    public TaskPanel(KidTaskMain mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Task Title", "Description", "Points", "Status", "Rating"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        StyleTheme.styleTable(table);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);

        btnAdd = new JButton("Add Task");
        btnAction = new JButton("Complete / Approve");
        btnDelete = new JButton("Delete");

        StyleTheme.styleButton(btnAdd);
        StyleTheme.styleButton(btnAction);
        StyleTheme.styleButton(btnDelete, StyleTheme.DANGER);
        btnAdd.addActionListener(e -> addTask());
        btnAction.addActionListener(e -> processTask());
        btnDelete.addActionListener(e -> deleteTask());

        btnPanel.add(btnAdd);
        btnPanel.add(btnAction);
        btnPanel.add(btnDelete);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
        add(new JLabel("Task Management"), BorderLayout.NORTH);
    }

    public void refreshTable() {
        model.setRowCount(0);
        for (Task t : DataManager.taskList) {
            String statusDisplay = t.getStatus();
            if("APPROVED".equals(t.getStatus())) statusDisplay = "✅ Approved";
            if("COMPLETED".equals(t.getStatus())) statusDisplay = "⏳ Pending Approval";

            model.addRow(new Object[]{
                    t.getTitle(),
                    t.getDescription(),
                    t.getPoints(),
                    statusDisplay,
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

        Object[] message = {
                "Title:", titleF,
                "Description:", descF,
                "Points (Numeric):", pointF
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Create New Task", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int pts = Integer.parseInt(pointF.getText());
                DataManager.taskList.add(new Task(titleF.getText(), descF.getText(), pts, "PENDING", 0));
                DataManager.saveData();
                refreshTable();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Points must be a number.");
            }
        }
    }

    private void processTask() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a task first.");
            return;
        }

        Task task = DataManager.taskList.get(row);
        String role = DataManager.currentRole;

        if ("CHILD".equals(role)) {
            if ("PENDING".equals(task.getStatus())) {
                task.setStatus("COMPLETED");
                JOptionPane.showMessageDialog(this, "Great job! Task marked as completed.");
            } else {
                JOptionPane.showMessageDialog(this, "You can only complete pending tasks.");
            }
        }
        else if ("PARENT".equals(role) || "TEACHER".equals(role)) {
            if ("COMPLETED".equals(task.getStatus())) {
                String[] ratings = {"1", "2", "3", "4", "5"};
                String result = (String) JOptionPane.showInputDialog(this, "Rate this task:", "Approval",
                        JOptionPane.QUESTION_MESSAGE, null, ratings, "5");

                if (result != null) {
                    task.setRating(Integer.parseInt(result));
                    task.setStatus("APPROVED");
                    JOptionPane.showMessageDialog(this, "Task Approved! Points added.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "You can only approve tasks marked as 'Pending Approval'.");
            }
        }

        DataManager.saveData();
        refreshTable();
        mainFrame.updateDashboardStats();
    }

    private void deleteTask() {
        if ("CHILD".equals(DataManager.currentRole)) return;

        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this task?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            DataManager.removeTask(row);
            refreshTable();
            mainFrame.updateDashboardStats();
            JOptionPane.showMessageDialog(this, "Task deleted successfully.");
        }
    }

}
