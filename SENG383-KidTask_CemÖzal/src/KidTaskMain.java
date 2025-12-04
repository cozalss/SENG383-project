import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class KidTaskMain extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainContainer = new JPanel(cardLayout);

    private JLabel pointsLabel;
    private JLabel levelLabel;
    private JProgressBar levelProgress;

    private TaskPanel taskPanel;
    private WishPanel wishPanel;

    public KidTaskMain() {
        setTitle("KidTask - Smart Task Manager");
        setSize(1100, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0,0,0,0));
        UIManager.put("TabbedPane.selected", Color.WHITE);

        DataManager.loadData();

        KidTaskLoginUI loginScreen = new KidTaskLoginUI(this);
        mainContainer.add(loginScreen, "LOGIN");

        initDashboard();

        add(mainContainer);
    }

    public void performLogin(String role) {
        DataManager.currentRole = role;
        updateDashboardStats();
        taskPanel.refreshTable();
        wishPanel.refreshTable();
        cardLayout.show(mainContainer, "DASHBOARD");
    }

    private void initDashboard() {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(StyleTheme.BG_COLOR);

        JPanel topBar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, StyleTheme.PRIMARY, getWidth(), 0, new Color(99, 102, 241));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        topBar.setBorder(new EmptyBorder(15, 30, 15, 30));
        topBar.setPreferredSize(new Dimension(0, 80));

        pointsLabel = new JLabel("0 Points");
        pointsLabel.setForeground(Color.WHITE);
        pointsLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        pointsLabel.setIcon(new TextIcon("🏆"));

        JPanel levelPanel = new JPanel(new GridLayout(2, 1));
        levelPanel.setOpaque(false);

        levelLabel = new JLabel("Level 1", SwingConstants.CENTER);
        levelLabel.setForeground(Color.WHITE);
        levelLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        levelProgress = new JProgressBar(0, 100);
        levelProgress.setForeground(StyleTheme.ACCENT);
        levelProgress.setBackground(new Color(255, 255, 255, 50));
        levelProgress.setBorderPainted(false);
        levelProgress.setPreferredSize(new Dimension(300, 10));

        levelPanel.add(levelLabel);
        levelPanel.add(levelProgress);

        JButton btnLogout = new JButton("Log Out");
        btnLogout.setBackground(new Color(255, 255, 255, 30));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1, true));
        btnLogout.setContentAreaFilled(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setPreferredSize(new Dimension(100, 35));
        btnLogout.addActionListener(e -> cardLayout.show(mainContainer, "LOGIN"));

        topBar.add(pointsLabel, BorderLayout.WEST);

        JPanel centerContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerContainer.setOpaque(false);
        centerContainer.add(levelPanel);
        topBar.add(centerContainer, BorderLayout.CENTER);

        topBar.add(btnLogout, BorderLayout.EAST);

        taskPanel = new TaskPanel(this);
        wishPanel = new WishPanel();

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tabs.setBackground(StyleTheme.BG_COLOR);



        tabs.addTab("<html><body style='font-family: Segoe UI Emoji, Segoe UI; padding: 10px 20px'>📋 Tasks</body></html>", taskPanel);
        tabs.addTab("<html><body style='font-family: Segoe UI Emoji, Segoe UI; padding: 10px 20px'>✨ Wish List</body></html>", wishPanel);

        dashboard.add(topBar, BorderLayout.NORTH);
        dashboard.add(tabs, BorderLayout.CENTER);

        mainContainer.add(dashboard, "DASHBOARD");
    }

    public void updateDashboardStats() {
        int points = DataManager.calculateTotalPoints();
        int level = DataManager.calculateLevel();
        int progress = DataManager.calculateProgress();

        pointsLabel.setText(points + " Points");
        levelLabel.setText("Level " + level);
        levelProgress.setValue(progress);
        levelProgress.setToolTipText(progress + "% to next level");
    }

    static class TextIcon implements Icon {
        private String text;
        public TextIcon(String text) { this.text = text; }
        public int getIconWidth() { return 30; }
        public int getIconHeight() { return 30; }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
            g.setColor(Color.WHITE);
            g.drawString(text, x, y + 22);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KidTaskMain().setVisible(true));
    }
}