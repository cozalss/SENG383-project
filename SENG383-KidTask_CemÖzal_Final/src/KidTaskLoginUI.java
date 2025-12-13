import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KidTaskLoginUI extends JPanel {

    private final KidTaskMain mainApp;
    private int animationOffset = 60;
    private float animationAlpha = 0.0f;

    private static final Color BG_START = new Color(224, 195, 252);
    private static final Color BG_END = new Color(199, 210, 254);
    private static final Color TEXT_DARK = new Color(44, 62, 80);
    private static final Color TEXT_LIGHT = new Color(245, 245, 245);

    private static final Color CHILD_COLOR_1 = new Color(76, 209, 124);
    private static final Color CHILD_COLOR_2 = new Color(129, 236, 169);
    private static final Color PARENT_COLOR_1 = new Color(255, 152, 0);
    private static final Color PARENT_COLOR_2 = new Color(255, 183, 77);
    private static final Color TEACHER_COLOR_1 = new Color(123, 66, 246);
    private static final Color TEACHER_COLOR_2 = new Color(157, 114, 248);

    public KidTaskLoginUI(KidTaskMain mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout());

        AnimatedBackgroundPanel backgroundPanel = new AnimatedBackgroundPanel();
        backgroundPanel.setLayout(new GridBagLayout());

        JPanel contentBox = new JPanel() {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(0, animationOffset);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, animationAlpha));
                super.paint(g2);
                g2.dispose();
            }
        };

        contentBox.setOpaque(false);
        contentBox.setLayout(new BoxLayout(contentBox, BoxLayout.Y_AXIS));
        contentBox.setBorder(new EmptyBorder(30, 40, 50, 40));

        JLabel titleLabel = new JLabel("KidTask");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        titleLabel.setForeground(TEXT_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subTitleLabel = new JLabel("Task & Wish Management App");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subTitleLabel.setForeground(TEXT_DARK.brighter());
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentBox.add(titleLabel);
        contentBox.add(Box.createVerticalStrut(5));
        contentBox.add(subTitleLabel);
        contentBox.add(Box.createVerticalStrut(50));

        JLabel roleSelectLabel = new JLabel("Role Selection Screen");
        roleSelectLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        roleSelectLabel.setForeground(TEXT_DARK);
        roleSelectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentBox.add(roleSelectLabel);
        contentBox.add(Box.createVerticalStrut(35));

        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        cardsPanel.setOpaque(false);

        RoleCardButton childCard = new RoleCardButton(
                "👤", "Child", "Manage tasks & add wishes",
                CHILD_COLOR_1, CHILD_COLOR_2,
                e -> mainApp.performLogin("CHILD"));

        RoleCardButton parentCard = new RoleCardButton(
                "👨‍👩‍👧‍👦", "Parent", "Approve tasks & manage wishes",
                PARENT_COLOR_1, PARENT_COLOR_2,
                e -> mainApp.performLogin("PARENT"));

        RoleCardButton teacherCard = new RoleCardButton(
                "🎓", "Teacher", "Add school tasks & goals",
                TEACHER_COLOR_1, TEACHER_COLOR_2,
                e -> mainApp.performLogin("TEACHER"));

        cardsPanel.add(childCard);
        cardsPanel.add(parentCard);
        cardsPanel.add(teacherCard);

        contentBox.add(cardsPanel);
        backgroundPanel.add(contentBox);
        add(backgroundPanel, BorderLayout.CENTER);

        // Giriş Animasyonu Timer'ı
        Timer timer = new Timer(15, e -> {
            if (animationOffset > 0) {
                animationOffset -= 2;
                if (animationOffset < 0) animationOffset = 0;
            }
            if (animationAlpha < 1.0f) {
                animationAlpha += 0.04f;
                if (animationAlpha > 1.0f) animationAlpha = 1.0f;
            }
            if (animationOffset == 0 && animationAlpha == 1.0f) {
                ((Timer)e.getSource()).stop();
            }
            contentBox.repaint(); // Sadece içeriği boya
        });
        timer.setInitialDelay(100);
        timer.start();
    }

    private static class AnimatedBackgroundPanel extends JPanel {
        private final List<FloatingShape> shapes = new ArrayList<>();
        private final Timer animTimer;

        public AnimatedBackgroundPanel() {
            Random rand = new Random();
            for (int i = 0; i < 15; i++) {
                shapes.add(new FloatingShape(rand.nextInt(800), rand.nextInt(600), rand));
            }

            animTimer = new Timer(30, e -> {
                for (FloatingShape s : shapes) {
                    s.move(getWidth(), getHeight());
                }
                repaint();
            });
            animTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(); int h = getHeight();

            GradientPaint gp = new GradientPaint(0, 0, BG_START, w, h, BG_END);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);

            g2.setColor(new Color(255, 255, 255, 100)); // Yarı saydam beyaz
            for (FloatingShape s : shapes) {
                g2.fillOval((int)s.x, (int)s.y, s.size, s.size);
            }
        }
    }

    private static class FloatingShape {
        float x, y, dx, dy;
        int size;

        public FloatingShape(int startX, int startY, Random r) {
            x = startX; y = startY;
            dx = (r.nextFloat() - 0.5f) * 2; // Yavaş hız
            dy = (r.nextFloat() - 0.5f) * 2;
            size = 20 + r.nextInt(60);
        }

        public void move(int w, int h) {
            x += dx; y += dy;
            if (x < 0 || x > w) dx = -dx;
            if (y < 0 || y > h) dy = -dy;
        }
    }

    // Rol Kart Butonu (Aynı kaldı)
    private static class RoleCardButton extends JButton {
        private final Color color1, color2;
        private Color currentColor1, currentColor2;

        public RoleCardButton(String iconText, String title, String desc, Color c1, Color c2, ActionListener action) {
            this.color1 = c1; this.color2 = c2;
            this.currentColor1 = c1; this.currentColor2 = c2;

            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addActionListener(action);

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(new EmptyBorder(25, 20, 25, 20));
            setPreferredSize(new Dimension(240, 200));

            JLabel iconLabel = new JLabel(iconText);
            iconLabel.setFont(new Font("Dialog", Font.PLAIN, 50));
            iconLabel.setForeground(TEXT_LIGHT);
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            titleLabel.setForeground(TEXT_LIGHT);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel descLabel = new JLabel("<html><center>" + desc + "</center></html>");
            descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            descLabel.setForeground(new Color(255, 255, 255, 210));
            descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            add(iconLabel);
            add(Box.createVerticalStrut(20));
            add(titleLabel);
            add(Box.createVerticalStrut(8));
            add(descLabel);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    currentColor1 = color1.darker();
                    currentColor2 = color2.darker();
                    repaint();
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    currentColor1 = color1;
                    currentColor2 = color2;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, currentColor1, w, h, currentColor2);
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Double(0, 0, w, h, 35, 35));
            g2.dispose();
            super.paintComponent(g);
        }
    }
}