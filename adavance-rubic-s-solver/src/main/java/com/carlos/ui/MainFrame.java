package com.carlos.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import com.carlos.cube.RubiksCube;
import com.carlos.cube.KociembaSolver;

public class MainFrame extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel cards = new JPanel(cardLayout);
    private RubiksCube cubeModel = new RubiksCube();
    private Cube3D cube3D = new Cube3D();
    private JFXPanelWrapper jfxPanel;
    private InputPanel inputPanel;
    private SolvingPanel solvingPanel;

    public MainFrame() {
        setTitle("Rubik Cube Solver by Team Carlos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 850);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);

        jfxPanel = new JFXPanelWrapper(cube3D);
        inputPanel = new InputPanel(cubeModel, cube3D, this);
        solvingPanel = new SolvingPanel(cubeModel, this, cube3D, jfxPanel);

        cards.add(createWelcomePanel(), "welcome");
        cards.add(inputPanel, "input");
        cards.add(solvingPanel, "solving");

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, cards, jfxPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(640);
        splitPane.setDividerSize(0);
        splitPane.setBorder(null);
        add(splitPane);
        
        // Button listeners are managed within the panels themselves
        
        setVisible(true);
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw deep navy gradient background
                GradientPaint bgGrad = new GradientPaint(
                    0, 0, new Color(10, 15, 48), 
                    getWidth(), getHeight(), new Color(4, 6, 24)
                );
                g2d.setPaint(bgGrad);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw subtle glowing accent grid/circles on left
                g2d.setColor(new Color(0, 176, 255, 20));
                for (int i = 0; i < 15; i++) {
                    g2d.fillOval(-100 + i * 20, 200 + i * 10, 10, 10);
                }
                
                g2d.dispose();
            }
        };
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // --- 1. presentsLabel ("TEAM CARLOS PRESENTS") ---
        JLabel presentsLabel = new JLabel("TEAM CARLOS PRESENTS");
        presentsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        presentsLabel.setForeground(new Color(186, 104, 200)); // Lavender pink
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(presentsLabel, gbc);
        
        // --- 2. Title Panel ("CUBE SOLVER" + "PRO" badge) ---
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);
        
        JLabel cubeLabel = new JLabel("CUBE");
        cubeLabel.setFont(new Font("Segoe UI", Font.BOLD, 54));
        cubeLabel.setForeground(Color.WHITE);
        titlePanel.add(cubeLabel);
        
        JLabel solverLabel = new JLabel("SOLVER");
        solverLabel.setFont(new Font("Segoe UI", Font.BOLD, 54));
        solverLabel.setForeground(new Color(0, 229, 255)); // Bright neon cyan
        titlePanel.add(solverLabel);
        
        // "PRO" Badge Label
        JPanel proBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(33, 150, 243, 30));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2d.setColor(new Color(33, 150, 243));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2d.dispose();
            }
        };
        proBadge.setOpaque(false);
        proBadge.setLayout(new BorderLayout());
        JLabel proText = new JLabel("PRO", SwingConstants.CENTER);
        proText.setFont(new Font("Segoe UI", Font.BOLD, 16));
        proText.setForeground(Color.WHITE);
        proText.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        proBadge.add(proText, BorderLayout.CENTER);
        titlePanel.add(proBadge);
        
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(titlePanel, gbc);
        
        // --- 3. Tagline ("Solve Smarter. Solve Faster. Solve Perfect.") ---
        JLabel tagline = new JLabel("Solve Smarter. Solve Faster. Solve Perfect.");
        tagline.setFont(new Font("Segoe UI", Font.BOLD, 22));
        tagline.setForeground(new Color(224, 64, 251)); // Vibrant neon magenta
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 30, 0);
        panel.add(tagline, gbc);
        
        // --- 4. Sub-description ---
        JLabel descLabel = new JLabel("<html>High-Precision 3D Algorithm<br>Engineered by <font color='#00E5FF'><b>Team Carlos</b></font></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        descLabel.setForeground(new Color(176, 190, 197)); // Light gray blue
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 45, 0);
        panel.add(descLabel, gbc);
        
        // --- 5. Custom Gradient "START EXPERIENCE" Button ---
        JButton startBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Glowing gradient paint
                GradientPaint btnGrad = new GradientPaint(
                    0, 0, new Color(0, 176, 255), 
                    getWidth(), 0, new Color(224, 64, 251)
                );
                g2d.setPaint(btnGrad);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                
                // Paint text & arrow icon manually for premium look
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
                FontMetrics fm = g2d.getFontMetrics();
                String text = "START EXPERIENCE      >";
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(text, x, y);
                
                g2d.dispose();
            }
        };
        startBtn.setOpaque(false);
        startBtn.setContentAreaFilled(false);
        startBtn.setBorderPainted(false);
        startBtn.setFocusPainted(false);
        startBtn.setPreferredSize(new Dimension(320, 60));
        startBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startBtn.addActionListener(e -> cardLayout.show(cards, "input"));
        
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 50, 0);
        panel.add(startBtn, gbc);
        
        // --- 6. Lower Features Bar (Lightning Fast, High Precision, Reliable Results) ---
        JPanel featuresPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        featuresPanel.setOpaque(false);
        
        featuresPanel.add(createFeatureItem("⚡", "Lightning Fast", "Ultra-optimized"));
        featuresPanel.add(createFeatureItem("🎯", "High Precision", "Advanced algos"));
        featuresPanel.add(createFeatureItem("🛡", "Reliable Results", "100% Accuracy"));
        
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(featuresPanel, gbc);
        
        return panel;
    }
    
    private JPanel createFeatureItem(String emoji, String title, String subtitle) {
        JPanel fItem = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 10));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2d.setColor(new Color(255, 255, 255, 20));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2d.dispose();
            }
        };
        fItem.setOpaque(false);
        fItem.setLayout(new BorderLayout(10, 5));
        fItem.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel emojiLabel = new JLabel(emoji, SwingConstants.CENTER);
        emojiLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        fItem.add(emojiLabel, BorderLayout.WEST);
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLabel.setForeground(new Color(176, 190, 197));
        
        textPanel.add(titleLabel);
        textPanel.add(subLabel);
        
        fItem.add(textPanel, BorderLayout.CENTER);
        return fItem;
    }

    public void startSolving() {
        cardLayout.show(cards, "solving");
        solvingPanel.setSolution(null); 

        Thread solveThread = new Thread(() -> {
            try {
                Thread.sleep(200); // Give UI time to breathe
                KociembaSolver solver = new KociembaSolver(cubeModel);
                java.util.List<String> solution = solver.solve();
                
                SwingUtilities.invokeLater(() -> {
                    solvingPanel.setSolution(solution);
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, 
                        "Error solving cube: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        });
        solveThread.setDaemon(true);
        solveThread.start();
    }

    public void goBack() { cardLayout.show(cards, "input"); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
