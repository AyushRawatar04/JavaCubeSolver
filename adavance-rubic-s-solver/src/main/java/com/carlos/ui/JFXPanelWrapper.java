package com.carlos.ui;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class JFXPanelWrapper extends JPanel {
    private final JFXPanel jfxPanel = new JFXPanel();
    private final Cube3D cube3D;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainCards = new JPanel(cardLayout);
    private final JPanel simplePanel = new JPanel(new BorderLayout());
    
    // Dashboard Components
    private JPanel centerPanel;
    private JLabel guideMoveLabel;
    private FaceMoveGuidePanel faceMoveGuidePanel;
    private JLabel guideDescLabel;
    
    private List<String> currentSolution = new ArrayList<>();
    private int currentStep = 0;

    public JFXPanelWrapper(Cube3D cube3D) {
        this.cube3D = cube3D;
        setLayout(new BorderLayout());
        setBackground(new Color(6, 10, 31)); // Deep navy background
        
        mainCards.setOpaque(false);
        add(mainCards, BorderLayout.CENTER);
        
        // Simple View (Only the 3D Cube)
        simplePanel.setOpaque(false);
        simplePanel.add(jfxPanel, BorderLayout.CENTER);
        mainCards.add(simplePanel, "simple");
        
        // Dashboard View
        JPanel dashboardPanel = createDashboardPanel();
        mainCards.add(dashboardPanel, "dashboard");
        
        Platform.runLater(this::initFX);
    }

    private void initFX() {
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(2000.0);
        camera.setTranslateZ(-1150);

        Scene scene = new Scene(cube3D, 800, 720, true, SceneAntialiasing.BALANCED);
        scene.setFill(javafx.scene.paint.Color.web("#060a1f"));
        scene.setCamera(camera);
        
        cube3D.setupInteraction(scene);
        jfxPanel.setScene(scene);
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // 1. Top Bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        
        JLabel titleLabel = new JLabel("CUBE VIEW");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        topBar.add(titleLabel, BorderLayout.WEST);
        
        StyledButton viewBtn = new StyledButton("📹 3D View", new Color(15, 23, 42), new Color(30, 41, 59), 10);
        viewBtn.setPreferredSize(new Dimension(100, 35));
        topBar.add(viewBtn, BorderLayout.EAST);
        
        panel.add(topBar, BorderLayout.NORTH);
        
        // 2. Center Panel (Hosts 3D Cube)
        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // 3. Right Panel (Guide & Legend)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(220, 400));
        
        // Move Guide Card
        RoundedPanel moveGuideCard = new RoundedPanel(16, new Color(15, 23, 42), new Color(30, 41, 59));
        moveGuideCard.setLayout(new BorderLayout(10, 10));
        moveGuideCard.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        moveGuideCard.setMaximumSize(new Dimension(220, 200));
        
        JLabel guideTitle = new JLabel("MOVE GUIDE", SwingConstants.CENTER);
        guideTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        guideTitle.setForeground(new Color(148, 163, 184)); // slate-400
        moveGuideCard.add(guideTitle, BorderLayout.NORTH);
        
        JPanel guideBody = new JPanel(new BorderLayout(5, 5));
        guideBody.setOpaque(false);
        
        guideMoveLabel = new JLabel("?", SwingConstants.CENTER);
        guideMoveLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        guideMoveLabel.setForeground(Color.WHITE);
        guideBody.add(guideMoveLabel, BorderLayout.NORTH);
        
        faceMoveGuidePanel = new FaceMoveGuidePanel();
        guideBody.add(faceMoveGuidePanel, BorderLayout.CENTER);
        
        guideDescLabel = new JLabel("None", SwingConstants.CENTER);
        guideDescLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        guideDescLabel.setForeground(new Color(148, 163, 184));
        guideBody.add(guideDescLabel, BorderLayout.SOUTH);
        
        moveGuideCard.add(guideBody, BorderLayout.CENTER);
        rightPanel.add(moveGuideCard);
        
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Quick Legend Card
        RoundedPanel legendCard = new RoundedPanel(16, new Color(15, 23, 42), new Color(30, 41, 59));
        legendCard.setLayout(new BorderLayout(10, 10));
        legendCard.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        
        JLabel legendTitle = new JLabel("QUICK LEGEND", SwingConstants.CENTER);
        legendTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        legendTitle.setForeground(new Color(148, 163, 184));
        legendCard.add(legendTitle, BorderLayout.NORTH);
        
        JPanel legendBody = new JPanel(new GridLayout(8, 1, 0, 4));
        legendBody.setOpaque(false);
        legendBody.add(createLegendRow("U", "Top (White)", new Color(248, 250, 252)));
        legendBody.add(createLegendRow("D", "Bottom (Yellow)", new Color(253, 224, 71)));
        legendBody.add(createLegendRow("R", "Right (Red)", new Color(239, 68, 68)));
        legendBody.add(createLegendRow("L", "Left (Orange)", new Color(249, 115, 22)));
        legendBody.add(createLegendRow("F", "Front (Green)", new Color(34, 197, 94)));
        legendBody.add(createLegendRow("B", "Back (Blue)", new Color(59, 130, 246)));
        legendBody.add(createLegendRow("'", "Counter-Clockwise", new Color(148, 163, 184)));
        legendBody.add(createLegendRow("", "Clockwise (no mark)", new Color(148, 163, 184)));
        
        legendCard.add(legendBody, BorderLayout.CENTER);
        rightPanel.add(legendCard);
        
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createLegendRow(String label, String desc, Color color) {
        JPanel r = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        r.setOpaque(false);
        JLabel l1 = new JLabel(label);
        l1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l1.setForeground(color);
        l1.setPreferredSize(new Dimension(20, 15));
        
        JLabel l2 = new JLabel(desc);
        l2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        l2.setForeground(new Color(148, 163, 184));
        
        r.add(l1);
        r.add(l2);
        return r;
    }
    
    public synchronized void updateSolvingState(List<String> solution, int currentStep) {
        this.currentSolution = (solution != null) ? solution : new ArrayList<>();
        this.currentStep = currentStep;
        
        if (this.currentSolution.isEmpty()) {
            cardLayout.show(mainCards, "simple");
            if (jfxPanel.getParent() != simplePanel) {
                simplePanel.removeAll();
                simplePanel.add(jfxPanel, BorderLayout.CENTER);
            }
            revalidate();
            repaint();
            return;
        }
        
        // Show dashboard layout
        cardLayout.show(mainCards, "dashboard");
        if (jfxPanel.getParent() != centerPanel) {
            centerPanel.removeAll();
            centerPanel.add(jfxPanel, BorderLayout.CENTER);
        }
        
        // Update Move Guide text
        if (currentStep < currentSolution.size()) {
            String currentMove = currentSolution.get(currentStep);
            guideMoveLabel.setText(currentMove);
            faceMoveGuidePanel.setCurrentMove(currentMove);
            guideDescLabel.setText(decodeMove(currentMove));
        } else {
            guideMoveLabel.setText("✔");
            faceMoveGuidePanel.setCurrentMove("");
            guideDescLabel.setText("Solved!");
        }
        
        revalidate();
        repaint();
    }
    private String decodeMove(String m) {
        String side = "";
        switch (m.charAt(0)) {
            case 'U': side = "Top (White)"; break;
            case 'D': side = "Bottom (Yellow)"; break;
            case 'R': side = "Right (Red)"; break;
            case 'L': side = "Left (Orange)"; break;
            case 'F': side = "Front (Green)"; break;
            case 'B': side = "Back (Blue)"; break;
        }
        String dir = m.endsWith("'") ? "Counter-Clockwise" : (m.endsWith("2") ? "180 Degrees" : "Clockwise");
        return "Rotate " + side + " face " + dir + ".";
    }
}

// Custom Helper Components

class RoundedPanel extends JPanel {
    private final int radius;
    private final Color bgColor;
    private final Color borderColor;

    public RoundedPanel(int radius, Color bgColor, Color borderColor) {
        this.radius = radius;
        this.bgColor = bgColor;
        this.borderColor = borderColor;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (bgColor != null) {
            g2d.setColor(bgColor);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
        if (borderColor != null) {
            g2d.setColor(borderColor);
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        }
        g2d.dispose();
    }
}

class StyledButton extends JButton {
    private final Color bg;
    private final Color hoverBg;
    private final int radius;
    
    public StyledButton(String text, Color bg, Color hoverBg, int radius) {
        super(text);
        this.bg = bg;
        this.hoverBg = hoverBg;
        this.radius = radius;
        
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setForeground(Color.WHITE);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (getModel().isPressed()) {
            g2d.setColor(bg.darker());
        } else if (getModel().isRollover()) {
            g2d.setColor(hoverBg);
        } else {
            g2d.setColor(bg);
        }
        
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        
        // Draw text
        g2d.setColor(getForeground());
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(getText(), x, y);
        
        g2d.dispose();
    }
}

class FaceMoveGuidePanel extends JPanel {
    private String currentMove = "";
    
    public FaceMoveGuidePanel() {
        setPreferredSize(new Dimension(80, 80));
        setMaximumSize(new Dimension(85, 85));
        setOpaque(false);
    }
    
    public void setCurrentMove(String move) {
        this.currentMove = move;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (currentMove == null || currentMove.isEmpty()) return;
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int w = getWidth();
        int h = getHeight();
        int size = Math.min(w, h) - 10;
        int startX = (w - size) / 2;
        int startY = (h - size) / 2;
        
        char notation = currentMove.charAt(0);
        Color faceColor = Color.LIGHT_GRAY;
        switch (notation) {
            case 'U': faceColor = Color.WHITE; break;
            case 'D': faceColor = new Color(253, 224, 71); break;
            case 'R': faceColor = new Color(239, 68, 68); break;
            case 'L': faceColor = new Color(249, 115, 22); break;
            case 'F': faceColor = new Color(34, 197, 94); break;
            case 'B': faceColor = new Color(59, 130, 246); break;
        }
        
        int stickerSize = (size - 6) / 3;
        g2d.setColor(new Color(30, 41, 59));
        g2d.fillRect(startX, startY, size, size);
        
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                int sx = startX + 2 + c * (stickerSize + 1);
                int sy = startY + 2 + r * (stickerSize + 1);
                g2d.setColor(faceColor);
                g2d.fillRoundRect(sx, sy, stickerSize, stickerSize, 4, 4);
            }
        }
        
        boolean isCCW = currentMove.endsWith("'");
        g2d.setColor(new Color(34, 197, 94));
        g2d.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        int rCenter = startX + size / 2;
        int arcSize = size - 12;
        int arcStart = rCenter - arcSize / 2;
        
        if (isCCW) {
            g2d.drawArc(arcStart, arcStart, arcSize, arcSize, 45, 270);
            g2d.fillPolygon(
                new int[]{rCenter - arcSize / 2 - 3, rCenter - arcSize / 2 + 5, rCenter - arcSize / 2 - 7},
                new int[]{startY + 10, startY + 18, startY + 20},
                3
            );
        } else {
            g2d.drawArc(arcStart, arcStart, arcSize, arcSize, 45, -270);
            g2d.fillPolygon(
                new int[]{rCenter + arcSize / 2 + 3, rCenter + arcSize / 2 - 5, rCenter + arcSize / 2 + 7},
                new int[]{startY + 10, startY + 18, startY + 20},
                3
            );
        }
        
        g2d.dispose();
    }
}
