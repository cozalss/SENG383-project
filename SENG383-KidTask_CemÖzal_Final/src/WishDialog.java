import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class WishDialog extends JDialog {
    private boolean confirmed = false;

    private JTextField nameField;
    private JTextField costField;
    private JTextField levelField;

    public WishDialog(JFrame parent) {
        super(parent, "New Wish", true);
        setUndecorated(true); // Çerçeveyi kaldır
        setBackground(new Color(0,0,0,0)); // Arka plan şeffaf

        setSize(400, 400); // Boyut biraz daha kompakt olabilir
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel = new RoundedPanel(); // Yuvarlak köşe
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel lblTitle = new JLabel("Make a Wish");
        lblTitle.setFont(StyleTheme.TITLE_FONT);
        lblTitle.setForeground(StyleTheme.PRIMARY);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(6, 1, 5, 10));
        formPanel.setOpaque(false);

        nameField = createMaterialField();
        costField = createMaterialField();
        levelField = createMaterialField();
        levelField.setText("1"); // Varsayılan seviye

        formPanel.add(createLabel("Wish Item Name:"));
        formPanel.add(nameField);

        formPanel.add(createLabel("Cost (Points):"));
        formPanel.add(costField);

        formPanel.add(createLabel("Required Level:"));
        formPanel.add(levelField);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // --- BUTONLAR ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton btnCancel = StyleTheme.createModernButton("Cancel", StyleTheme.DANGER);
        JButton btnSave = StyleTheme.createModernButton("Add Wish", StyleTheme.PRIMARY);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> {
            if (validateInputs()) {
                confirmed = true;
                dispose();
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(StyleTheme.TEXT_LIGHT);
        return l;
    }

    private JTextField createMaterialField() {
        JTextField tf = new JTextField();
        tf.setFont(StyleTheme.DATA_FONT);
        tf.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 2, 0, new Color(200, 200, 200)),
                new EmptyBorder(5, 5, 5, 5)));
        tf.setBackground(Color.WHITE);
        return tf;
    }

    private boolean validateInputs() {
        if (nameField.getText().trim().isEmpty()) return false;
        try {
            Integer.parseInt(costField.getText().trim());
            Integer.parseInt(levelField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Cost and Level must be numbers!");
            return false;
        }
        return true;
    }

    public boolean isConfirmed() { return confirmed; }

    public String getWishName() { return nameField.getText(); }
    public int getCost() { return Integer.parseInt(costField.getText()); }
    public int getRequiredLevel() { return Integer.parseInt(levelField.getText()); }

    private static class RoundedPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));

            g2.setColor(StyleTheme.PRIMARY);
            g2.setStroke(new BasicStroke(2));
            g2.draw(new RoundRectangle2D.Double(1, 1, getWidth()-2, getHeight()-2, 30, 30));
            g2.dispose();
        }
    }
}