import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Arc2D;

public class KidTaskMain extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainContainer = new JPanel(cardLayout);

    private JLabel pointsLabel;

    private CircularProgressBar circularLevelProgress;

    private TaskPanel taskPanel;
    private WishPanel wishPanel;
    private EffectPanel effectPanel;

    public KidTaskMain() {
        setTitle("KidTask - Smart Task Manager");
        setSize(1100, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0,0,0,0));
        UIManager.put("TabbedPane.selected", Color.WHITE);

        effectPanel = new EffectPanel();
        setGlassPane(effectPanel);
        effectPanel.setVisible(true);

        DataManager.loadData();

        KidTaskLoginUI loginScreen = new KidTaskLoginUI(this);
        mainContainer.add(loginScreen, "LOGIN");

        initDashboard();
        add(mainContainer);
    }

    public void showNotification(String message, String type) {
        Color color = StyleTheme.PRIMARY;
        if ("SUCCESS".equals(type)) color = StyleTheme.SUCCESS;
        else if ("ERROR".equals(type)) color = StyleTheme.DANGER;
        else if ("WARNING".equals(type)) color = StyleTheme.ACCENT;
        effectPanel.showToast(message, color);
    }

    public void performLogin(String role) {
        DataManager.currentRole = role;
        updateDashboardStats();
        taskPanel.refreshTable();
        wishPanel.refreshTable();
        cardLayout.show(mainContainer, "DASHBOARD");
        showNotification("Welcome back, " + role + "! 👋", "SUCCESS");
    }

    private void initDashboard() {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(StyleTheme.BG_COLOR);

        JPanel topBar = new JPanel(new BorderLayout()) {
            float hue = 0.7f;
            boolean hueUp = true;
            Timer colorTimer;

            {
                colorTimer = new Timer(100, e -> {
                    if (hueUp) hue += 0.001f; else hue -= 0.001f;
                    if (hue > 0.76f) hueUp = false;
                    if (hue < 0.68f) hueUp = true;
                    repaint();
                });
                colorTimer.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color animatedColor1 = Color.getHSBColor(hue, 0.7f, 0.9f);
                Color animatedColor2 = new Color(99, 102, 241);

                GradientPaint gp = new GradientPaint(0, 0, animatedColor1, getWidth(), 0, animatedColor2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        topBar.setBorder(new EmptyBorder(10, 30, 10, 30));
        topBar.setPreferredSize(new Dimension(0, 100));

        pointsLabel = new JLabel("0 Points");
        pointsLabel.setForeground(Color.WHITE);
        pointsLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        pointsLabel.setIcon(new TextIcon("🏆"));

        circularLevelProgress = new CircularProgressBar();
        circularLevelProgress.setPreferredSize(new Dimension(75, 75));
        circularLevelProgress.setBackground(new Color(0,0,0,0));

        JPanel centerContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerContainer.setOpaque(false);
        centerContainer.add(circularLevelProgress);

        JButton btnLogout = StyleTheme.createModernButton("Log Out", StyleTheme.DANGER);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setPreferredSize(new Dimension(100, 35));
        btnLogout.addActionListener(e -> {
            cardLayout.show(mainContainer, "LOGIN");
            showNotification("Logged out successfully. 👋", "SUCCESS");
        });

        topBar.add(pointsLabel, BorderLayout.WEST);
        topBar.add(centerContainer, BorderLayout.CENTER);
        topBar.add(btnLogout, BorderLayout.EAST);

        taskPanel = new TaskPanel(this);
        wishPanel = new WishPanel(this);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        tabs.setBackground(StyleTheme.BG_COLOR);

        tabs.addTab("<html><body style='font-family: Segoe UI Emoji; padding: 10px 20px'>📋 Tasks</body></html>", taskPanel);
        tabs.addTab("<html><body style='font-family: Segoe UI Emoji; padding: 10px 20px'>✨ Wish List</body></html>", wishPanel);

        dashboard.add(topBar, BorderLayout.NORTH);
        dashboard.add(tabs, BorderLayout.CENTER);
        mainContainer.add(dashboard, "DASHBOARD");
    }

    public void updateDashboardStats() {
        int points = DataManager.calculateTotalPoints();
        int newLevel = DataManager.calculateLevel();
        int targetProgress = DataManager.calculateProgress();

        pointsLabel.setText(points + " Points");

        int oldLevel = circularLevelProgress.getCurrentLevel();
        if (newLevel > oldLevel && oldLevel != 0) {
            effectPanel.startConfetti();
            showNotification("LEVEL UP! Amazing! 🎉", "WARNING");
        }

        circularLevelProgress.updateProgress(targetProgress, newLevel);
    }

    // --- Circular Progress Bar ---
    static class CircularProgressBar extends JPanel {
        private int progress = 0;
        private int currentLevel = 1;
        private int displayLevel = 1;
        private Timer animTimer;
        private boolean isLevelingUp = false;

        public CircularProgressBar() { setOpaque(false); }

        public void updateProgress(int targetProgress, int newLevel) {
            if (newLevel > this.currentLevel) {
                this.isLevelingUp = true;
            } else {
                this.isLevelingUp = false;
                this.displayLevel = newLevel;
            }
            this.currentLevel = newLevel;

            if (animTimer != null && animTimer.isRunning()) animTimer.stop();

            animTimer = new Timer(15, e -> {
                if (isLevelingUp) {
                    if (progress < 100) {
                        progress += 2;
                        if (progress > 100) progress = 100;
                    } else {
                        progress = 0;
                        displayLevel = currentLevel;
                        isLevelingUp = false;
                    }
                } else {
                    if (progress < targetProgress) progress++;
                    else if (progress > targetProgress) progress--;
                    else ((Timer)e.getSource()).stop();
                }
                repaint();
            });
            animTimer.start();
        }

        public int getCurrentLevel() { return currentLevel; }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight());
            int strokeWidth = 6;
            int radius = (size - strokeWidth) / 2;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            g2.setColor(new Color(255, 255, 255, 50));
            g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawOval(x + strokeWidth/2, y + strokeWidth/2, radius * 2, radius * 2);

            g2.setColor(StyleTheme.ACCENT);
            int angle = (int) ((progress / 100.0) * 360);
            g2.draw(new Arc2D.Double(x + strokeWidth/2, y + strokeWidth/2, radius * 2, radius * 2, 90, -angle, Arc2D.OPEN));

            String lvlText = "LVL " + displayLevel;
            String percentText = progress + "%";

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            FontMetrics fm = g2.getFontMetrics();
            int tx = (getWidth() - fm.stringWidth(lvlText)) / 2;
            int ty = (getHeight() / 2) - 2;
            g2.drawString(lvlText, tx, ty);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            fm = g2.getFontMetrics();
            tx = (getWidth() - fm.stringWidth(percentText)) / 2;
            ty = (getHeight() / 2) + 12;
            g2.setColor(new Color(255,255,255, 200));
            g2.drawString(percentText, tx, ty);
        }
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