import javax.swing.*;
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
        setSize(950, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

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

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(StyleTheme.PRIMARY);
        topBar.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        pointsLabel = new JLabel("Total Points: 0");
        pointsLabel.setForeground(Color.WHITE);
        pointsLabel.setFont(StyleTheme.HEADER_FONT);

        levelLabel = new JLabel("Level: 1");
        levelLabel.setForeground(Color.WHITE);
        levelLabel.setFont(StyleTheme.HEADER_FONT);

        levelProgress = new JProgressBar(0, 100);
        levelProgress.setForeground(StyleTheme.ACCENT);
        levelProgress.setBackground(Color.WHITE);
        levelProgress.setStringPainted(true);
        levelProgress.setPreferredSize(new Dimension(200, 25));

        JPanel statsRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statsRight.setOpaque(false);
        statsRight.add(levelLabel);
        statsRight.add(Box.createHorizontalStrut(10));
        statsRight.add(levelProgress);

        JButton btnLogout = new JButton("Logout");
        StyleTheme.styleButton(btnLogout, StyleTheme.DANGER);
        btnLogout.addActionListener(e -> cardLayout.show(mainContainer, "LOGIN"));

        topBar.add(pointsLabel, BorderLayout.WEST);
        topBar.add(statsRight, BorderLayout.CENTER);
        topBar.add(btnLogout, BorderLayout.EAST);

        taskPanel = new TaskPanel(this);
        wishPanel = new WishPanel();

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tabs.addTab("  My Tasks  ", taskPanel);
        tabs.addTab("  Wish List  ", wishPanel);

        dashboard.add(topBar, BorderLayout.NORTH);
        dashboard.add(tabs, BorderLayout.CENTER);

        mainContainer.add(dashboard, "DASHBOARD");
    }

    public void updateDashboardStats() {
        int points = DataManager.calculateTotalPoints();
        int level = DataManager.calculateLevel();
        int progress = DataManager.calculateProgress();

        pointsLabel.setText("Total Points: " + points);
        levelLabel.setText("Level: " + level);
        levelProgress.setValue(progress);
        levelProgress.setString(progress + "% to Level " + (level + 1));
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new KidTaskMain().setVisible(true));
    }
}