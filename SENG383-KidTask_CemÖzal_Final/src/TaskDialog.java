import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskDialog extends JDialog {
    private boolean confirmed = false;

    // Input Fields
    private JTextField titleField;
    private JTextField descField;
    private JTextField pointField;
    private JTextField dateField;
    private JComboBox<String> freqBox;

    public TaskDialog(JFrame parent) {
        super(parent, "New Task", true);
        setUndecorated(true); // Standart Windows çerçeveni kaldır
        setBackground(new Color(0,0,0,0)); // Arka planı şeffaf yap (Oval kenarlar için)

        setSize(400, 500);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        // Köşeleri yuvarlatmak için paint override
        mainPanel = new RoundedPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // --- BAŞLIK ---
        JLabel lblTitle = new JLabel("Create New Task");
        lblTitle.setFont(StyleTheme.TITLE_FONT);
        lblTitle.setForeground(StyleTheme.PRIMARY);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(10, 1, 5, 5));
        formPanel.setOpaque(false);

        titleField = createMaterialField();
        descField = createMaterialField();
        pointField = createMaterialField();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateField = createMaterialField();
        dateField.setText(sdf.format(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000)));

        freqBox = new JComboBox<>(new String[]{"ONCE", "DAILY", "WEEKLY"});
        freqBox.setFont(StyleTheme.DATA_FONT);
        freqBox.setBackground(Color.WHITE);
        freqBox.setBorder(new MatteBorder(0,0,2,0, StyleTheme.PRIMARY));

        formPanel.add(createLabel("Task Title:"));
        formPanel.add(titleField);

        formPanel.add(createLabel("Description:"));
        formPanel.add(descField);

        formPanel.add(createLabel("Points (Reward):"));
        formPanel.add(pointField);

        formPanel.add(createLabel("Due Date (YYYY-MM-DD):"));
        formPanel.add(dateField);

        formPanel.add(createLabel("Frequency:"));
        formPanel.add(freqBox);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton btnCancel = StyleTheme.createModernButton("Cancel", StyleTheme.DANGER);
        JButton btnSave = StyleTheme.createModernButton("Save Task", StyleTheme.SUCCESS);

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
        if (titleField.getText().trim().isEmpty()) return false;
        try {
            Integer.parseInt(pointField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Points must be a number!");
            return false;
        }
        return true;
    }

    public boolean isConfirmed() { return confirmed; }

    public String getTaskTitle() { return titleField.getText(); }
    public String getTaskDesc() { return descField.getText(); }
    public int getPoints() { return Integer.parseInt(pointField.getText()); }
    public String getDueDate() { return dateField.getText(); }
    public String getFrequency() { return (String) freqBox.getSelectedItem(); }

    private static class RoundedPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));

            // Mor çerçeve
            g2.setColor(StyleTheme.PRIMARY);
            g2.setStroke(new BasicStroke(2));
            g2.draw(new RoundRectangle2D.Double(1, 1, getWidth()-2, getHeight()-2, 30, 30));
            g2.dispose();
        }
    }
}