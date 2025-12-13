import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EffectPanel extends JPanel {
    private final List<Particle> particles = new ArrayList<>();
    private Timer confettiTimer;
    private final Random random = new Random();

    private String toastMessage = "";
    private Color toastColor = Color.BLACK;
    private float toastAlpha = 0.0f;
    private int toastY = 0;
    private Timer toastTimer;
    private long toastStartTime;
    private boolean isToastVisible = false;

    public EffectPanel() {
        setOpaque(false);
    }

    public void showToast(String message, Color color) {
        this.toastMessage = message;
        this.toastColor = color;
        this.toastAlpha = 0.0f;
        this.toastY = 50;
        this.isToastVisible = true;
        this.toastStartTime = System.currentTimeMillis();

        if (toastTimer != null && toastTimer.isRunning()) toastTimer.stop();

        toastTimer = new Timer(15, e -> {
            long elapsed = System.currentTimeMillis() - toastStartTime;

            if (elapsed < 300) {
                toastAlpha = (float) elapsed / 300.0f;
                toastY = (int) (50 * (1 - toastAlpha));
            }
            else if (elapsed < 2500) {
                toastAlpha = 1.0f;
                toastY = 0;
            }
            else if (elapsed < 3000) {
                float fadeOut = (elapsed - 2500) / 500.0f;
                toastAlpha = 1.0f - fadeOut;
                toastY = -(int)(30 * fadeOut);
            }
            else {
                isToastVisible = false;
                toastTimer.stop();
            }

            if (toastAlpha < 0) toastAlpha = 0;
            if (toastAlpha > 1) toastAlpha = 1;

            repaint();
        });
        toastTimer.start();
    }

    public void startConfetti() {
        particles.clear();
        for (int i = 0; i < 150; i++) {
            particles.add(new Particle(getWidth(), getHeight()));
        }

        if (confettiTimer != null && confettiTimer.isRunning()) confettiTimer.stop();

        setVisible(true);
        confettiTimer = new Timer(20, e -> {
            boolean allDone = true;
            for (Particle p : particles) {
                p.update();
                if (p.y < getHeight()) allDone = false;
            }
            repaint();
            if (allDone && !isToastVisible) {
                confettiTimer.stop();
            }
        });
        confettiTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Particle p : particles) {
            g2.setColor(p.color);
            g2.fillOval((int) p.x, (int) p.y, p.size, p.size);
        }

        if (isToastVisible && !toastMessage.isEmpty()) {
            drawToast(g2);
        }
    }

    private void drawToast(Graphics2D g2) {
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, toastAlpha));

        int width = 300;
        int height = 50;
        int x = (getWidth() - width) / 2;
        int y = getHeight() - 100 + toastY;

        g2.setColor(new Color(0, 0, 0, 50));
        g2.fillRoundRect(x + 2, y + 4, width, height, 30, 30);

        g2.setColor(toastColor);
        g2.fillRoundRect(x, y, width, height, 30, 30);

        g2.setColor(Color.WHITE);


        g2.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));

        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (width - fm.stringWidth(toastMessage)) / 2;
        int textY = y + ((height - fm.getHeight()) / 2) + fm.getAscent();

        g2.drawString(toastMessage, textX, textY);

        g2.setComposite(originalComposite);
    }

    private class Particle {
        float x, y;
        float speedY, speedX;
        int size;
        Color color;

        public Particle(int w, int h) {
            x = random.nextInt(w);
            y = -random.nextInt(h / 2);
            speedY = 3 + random.nextFloat() * 5;
            speedX = (random.nextFloat() - 0.5f) * 4;
            size = 6 + random.nextInt(8);
            Color[] colors = {
                    new Color(124, 58, 237), new Color(16, 185, 129),
                    new Color(245, 158, 11), new Color(59, 130, 246), new Color(236, 72, 153)
            };
            color = colors[random.nextInt(colors.length)];
        }
        void update() {
            y += speedY;
            x += speedX;
        }
    }
}