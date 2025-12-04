import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class WishPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    private JButton btnAdd;
    private JButton btnAction;
    private JButton btnDelete; // YENİ: Silme butonu tanımı

    public WishPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Wish Item", "Cost (Points)", "Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        StyleTheme.styleTable(table);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);

        btnAdd = new JButton("Make a Wish");
        btnAction = new JButton("Grant / Reject");
        btnDelete = new JButton("Delete Wish"); // YENİ: Buton oluşturma

        StyleTheme.styleButton(btnAdd);
        StyleTheme.styleButton(btnAction);
        StyleTheme.styleButton(btnDelete, StyleTheme.DANGER); // YENİ: Kırmızı (Danger) stil

        btnAdd.addActionListener(e -> addWish());
        btnAction.addActionListener(e -> processWish());
        btnDelete.addActionListener(e -> deleteWish()); // YENİ: Aksiyon ekleme

        btnPanel.add(btnAdd);
        btnPanel.add(btnAction);
        btnPanel.add(btnDelete); // YENİ: Panele ekleme

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
        add(new JLabel("Wish List"), BorderLayout.NORTH);
    }

    public void refreshTable() {
        // 1. Tablo Verilerini Yenile
        model.setRowCount(0);
        for (Wish w : DataManager.wishList) {
            model.addRow(new Object[]{w.getName(), w.getCost(), w.getStatus()});
        }

        // Rol Kontrolü ve Buton Görünürlüğü
        if ("CHILD".equals(DataManager.currentRole)) {
            btnAdd.setVisible(true);
            btnAction.setVisible(false);
            btnDelete.setVisible(false); // Çocuk silme yapamaz
        } else {
            // PARENT veya TEACHER ise
            btnAdd.setVisible(false);
            btnAction.setVisible(true);
            btnDelete.setVisible(true); // Yetişkinler silebilir
        }
    }

    private void addWish() {
        if (!"CHILD".equals(DataManager.currentRole)) {
            JOptionPane.showMessageDialog(this, "Only the Child can make wishes!");
            return;
        }

        JTextField nameF = new JTextField();
        JTextField costF = new JTextField();
        Object[] msg = {"Wish Name:", nameF, "Cost:", costF};

        if (JOptionPane.showConfirmDialog(this, msg, "New Wish", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                int cost = Integer.parseInt(costF.getText());
                DataManager.wishList.add(new Wish(nameF.getText(), cost, "REQUESTED"));
                DataManager.saveData();
                refreshTable();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid cost. Please enter a number.");
            }
        }
    }

    private void processWish() {
        if ("CHILD".equals(DataManager.currentRole)) return;

        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a wish to process.");
            return;
        }

        Wish w = DataManager.wishList.get(row);
        if ("REQUESTED".equals(w.getStatus())) {
            Object[] options = {"Grant", "Reject", "Cancel"};
            int choice = JOptionPane.showOptionDialog(this,
                    "Do you want to grant this wish?\nItem: " + w.getName() + "\nCost: " + w.getCost() + " pts",
                    "Decide Wish",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);

            if (choice == 0) w.setStatus("GRANTED");
            else if (choice == 1) w.setStatus("REJECTED");

            DataManager.saveData();
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "This wish has already been processed.");
        }
    }

    // YENİ: Dilek Silme Metodu
    private void deleteWish() {
        // Çocukların silmesini engelle (zaten buton gizli ama güvenlik için)
        if ("CHILD".equals(DataManager.currentRole)) return;

        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a wish to delete.");
            return;
        }

        Wish w = DataManager.wishList.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this wish?\nItem: " + w.getName(),
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            DataManager.removeWish(row); // DataManager'daki metodu çağır
            refreshTable();
            JOptionPane.showMessageDialog(this, "Wish deleted successfully.");
        }
    }
}