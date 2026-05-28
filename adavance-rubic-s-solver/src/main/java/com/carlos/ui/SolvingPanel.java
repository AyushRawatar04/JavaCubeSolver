package com.carlos.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.ArrayList;
import com.carlos.cube.RubiksCube;

public class SolvingPanel extends JPanel {
    private final RubiksCube cube;
    private final MainFrame frame;
    private final Cube3D cube3D;
    private final JFXPanelWrapper jfxPanel;
    
    private JLabel stepBadgeText;
    private StepperPanel stepperPanel;
    
    private JLabel moveSymbolLabel;
    private JLabel moveBadgeText;
    
    private JLabel instructLabel;
    private DetailSubCard faceSubCard;
    private DetailSubCard dirSubCard;
    private DetailSubCard notSubCard;
    
    private JLabel progressPercentLabel;
    private JLabel progressStepLabel;
    private JProgressBar progressBar;
    
    private DefaultTableModel tableModel;
    private JTable moveTable;
    private LightCellRenderer cellRenderer;
    private LightStatusRenderer statusRenderer;
    
    private StyledButton prevBtn;
    private StyledButton nextBtn;
    
    private List<String> currentSolution = new ArrayList<>();
    private int currentStep = 0;

    public SolvingPanel(RubiksCube cube, MainFrame frame, Cube3D cube3D, JFXPanelWrapper jfxPanel) {
        this.cube = cube;
        this.frame = frame;
        this.cube3D = cube3D;
        this.jfxPanel = jfxPanel;
        
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(248, 250, 252)); // slate-50 background
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        setupUI();
    }

    private void setupUI() {
        // Main Vertical stack
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        // 1. Header Row
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(1000, 50));
        
        JPanel logoAndTitle = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoAndTitle.setOpaque(false);
        
        CubeLogo cubeLogo = new CubeLogo();
        logoAndTitle.add(cubeLogo);
        
        JPanel titleTextPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        titleTextPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("RUBIK'S CUBE SOLVER");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(new Color(15, 23, 42));
        JLabel subtitleLabel = new JLabel("Step-by-Step Interactive Guide");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        subtitleLabel.setForeground(new Color(100, 116, 139));
        titleTextPanel.add(titleLabel);
        titleTextPanel.add(subtitleLabel);
        logoAndTitle.add(titleTextPanel);
        
        headerPanel.add(logoAndTitle, BorderLayout.WEST);
        
        RoundedPanel stepBadge = new RoundedPanel(12, new Color(30, 41, 59), null);
        stepBadge.setLayout(new BorderLayout());
        stepBadgeText = new JLabel("STEP 0 OF 0", SwingConstants.CENTER);
        stepBadgeText.setFont(new Font("Segoe UI", Font.BOLD, 10));
        stepBadgeText.setForeground(Color.WHITE);
        stepBadgeText.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        stepBadge.add(stepBadgeText, BorderLayout.CENTER);
        headerPanel.add(stepBadge, BorderLayout.EAST);
        
        contentPanel.add(headerPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // 2. Stepper Timeline
        stepperPanel = new StepperPanel();
        stepperPanel.setMaximumSize(new Dimension(1000, 65));
        contentPanel.add(stepperPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // 3. Current Move & What To Do cards
        JPanel cardsRow = new JPanel(new GridLayout(1, 2, 12, 0));
        cardsRow.setOpaque(false);
        cardsRow.setMaximumSize(new Dimension(1000, 185));
        
        // Card A: CURRENT MOVE
        RoundedPanel moveCard = new RoundedPanel(16, Color.WHITE, new Color(226, 232, 240));
        moveCard.setLayout(new BorderLayout(5, 5));
        moveCard.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        
        JLabel moveTitle = new JLabel("CURRENT MOVE");
        moveTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        moveTitle.setForeground(new Color(100, 116, 139));
        moveCard.add(moveTitle, BorderLayout.NORTH);
        
        moveSymbolLabel = new JLabel("?", SwingConstants.CENTER);
        moveSymbolLabel.setFont(new Font("Segoe UI", Font.BOLD, 75));
        moveSymbolLabel.setForeground(new Color(30, 58, 138)); // dark blue
        moveCard.add(moveSymbolLabel, BorderLayout.CENTER);
        
        JPanel badgeWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        badgeWrapper.setOpaque(false);
        RoundedPanel moveBadge = new RoundedPanel(12, new Color(21, 128, 61), null); // green-700
        moveBadge.setLayout(new BorderLayout());
        moveBadgeText = new JLabel("Move 0 of 0", SwingConstants.CENTER);
        moveBadgeText.setFont(new Font("Segoe UI", Font.BOLD, 10));
        moveBadgeText.setForeground(Color.WHITE);
        moveBadgeText.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        moveBadge.add(moveBadgeText, BorderLayout.CENTER);
        badgeWrapper.add(moveBadge);
        moveCard.add(badgeWrapper, BorderLayout.SOUTH);
        
        cardsRow.add(moveCard);
        
        // Card B: WHAT TO DO
        RoundedPanel whatToDoCard = new RoundedPanel(16, Color.WHITE, new Color(226, 232, 240));
        whatToDoCard.setLayout(new BorderLayout(5, 10));
        whatToDoCard.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        
        JLabel whatTitle = new JLabel("WHAT TO DO");
        whatTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        whatTitle.setForeground(new Color(100, 116, 139));
        whatToDoCard.add(whatTitle, BorderLayout.NORTH);
        
        JPanel instructRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        instructRow.setOpaque(false);
        JLabel refreshIcon = new JLabel("🔄");
        refreshIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        instructLabel = new JLabel("Rotate the face as guided below.");
        instructLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        instructLabel.setForeground(new Color(15, 23, 42));
        instructRow.add(refreshIcon);
        instructRow.add(instructLabel);
        whatToDoCard.add(instructRow, BorderLayout.CENTER);
        
        JPanel subCardsGrid = new JPanel(new GridLayout(1, 3, 6, 0));
        subCardsGrid.setOpaque(false);
        faceSubCard = new DetailSubCard("Face");
        dirSubCard = new DetailSubCard("Direction");
        notSubCard = new DetailSubCard("Notation");
        subCardsGrid.add(faceSubCard);
        subCardsGrid.add(dirSubCard);
        subCardsGrid.add(notSubCard);
        whatToDoCard.add(subCardsGrid, BorderLayout.SOUTH);
        
        cardsRow.add(whatToDoCard);
        contentPanel.add(cardsRow);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // 4. Overall Progress Bar
        JPanel progressHeader = new JPanel(new BorderLayout());
        progressHeader.setOpaque(false);
        progressHeader.setMaximumSize(new Dimension(1000, 20));
        
        JLabel progressLabel = new JLabel("OVERALL PROGRESS");
        progressLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        progressLabel.setForeground(new Color(100, 116, 139));
        progressHeader.add(progressLabel, BorderLayout.WEST);
        
        JPanel labelsRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        labelsRight.setOpaque(false);
        progressPercentLabel = new JLabel("0%");
        progressPercentLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        progressPercentLabel.setForeground(new Color(30, 58, 138));
        progressStepLabel = new JLabel("0 of 0 steps completed");
        progressStepLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        progressStepLabel.setForeground(new Color(100, 116, 139));
        labelsRight.add(progressPercentLabel);
        labelsRight.add(progressStepLabel);
        progressHeader.add(labelsRight, BorderLayout.EAST);
        
        contentPanel.add(progressHeader);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        progressBar = new JProgressBar();
        progressBar.setForeground(new Color(30, 58, 138));
        progressBar.setBackground(new Color(241, 245, 249));
        progressBar.setBorderPainted(false);
        progressBar.setMaximumSize(new Dimension(1000, 15));
        progressBar.setPreferredSize(new Dimension(500, 15));
        contentPanel.add(progressBar);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // 5. Move List Table Card
        RoundedPanel moveListCard = new RoundedPanel(16, Color.WHITE, new Color(226, 232, 240));
        moveListCard.setLayout(new BorderLayout());
        moveListCard.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        moveListCard.setMaximumSize(new Dimension(1000, 160));
        moveListCard.setPreferredSize(new Dimension(500, 160));
        
        JLabel tableTitle = new JLabel("MOVE LIST (STEP-BY-STEP SEQUENCE)");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        tableTitle.setForeground(new Color(100, 116, 139));
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        moveListCard.add(tableTitle, BorderLayout.NORTH);
        
        // Setup JTable
        String[] columns = {"#", "MOVE", "NOTATION", "DESCRIPTION", "STATUS"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        moveTable = new JTable(tableModel);
        moveTable.setBackground(Color.WHITE);
        moveTable.setGridColor(new Color(241, 245, 249)); // light slate-100 grid
        moveTable.setRowHeight(28);
        moveTable.setShowVerticalLines(false);
        moveTable.getTableHeader().setBackground(new Color(248, 250, 252));
        moveTable.getTableHeader().setForeground(new Color(71, 85, 105));
        moveTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 10));
        moveTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));
        
        cellRenderer = new LightCellRenderer(currentStep);
        statusRenderer = new LightStatusRenderer(currentStep);
        
        for (int i = 0; i < moveTable.getColumnCount(); i++) {
            if (i == 4) {
                moveTable.getColumnModel().getColumn(i).setCellRenderer(statusRenderer);
            } else {
                moveTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
            }
        }
        
        // Set column widths
        moveTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        moveTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        moveTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        moveTable.getColumnModel().getColumn(3).setPreferredWidth(280);
        moveTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        
        JScrollPane tableScroll = new JScrollPane(moveTable);
        tableScroll.getViewport().setBackground(Color.WHITE);
        tableScroll.setBorder(null);
        moveListCard.add(tableScroll, BorderLayout.CENTER);
        
        contentPanel.add(moveListCard);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // 6. Buttons
        JPanel btnPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setMaximumSize(new Dimension(1000, 42));
        
        StyledButton backBtn = new StyledButton("⬅  BACK", new Color(100, 116, 139), new Color(71, 85, 105), 10);
        prevBtn = new StyledButton("◀  PREV", new Color(30, 41, 59), new Color(15, 23, 42), 10);
        nextBtn = new StyledButton("NEXT MOVE  ▶", new Color(21, 128, 61), new Color(22, 101, 52), 10);
        
        backBtn.addActionListener(e -> frame.goBack());
        prevBtn.addActionListener(e -> prevStep());
        nextBtn.addActionListener(e -> nextStep());
        
        btnPanel.add(backBtn);
        btnPanel.add(prevBtn);
        btnPanel.add(nextBtn);
        contentPanel.add(btnPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // 7. Bottom Tip Alert
        RoundedPanel tipPanel = new RoundedPanel(8, new Color(240, 253, 244), new Color(220, 252, 231));
        tipPanel.setLayout(new BorderLayout());
        tipPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        tipPanel.setMaximumSize(new Dimension(1000, 36));
        
        JLabel tipLabel = new JLabel("🎯   TIP: Follow the rotation direction carefully for accurate results!", SwingConstants.LEFT);
        tipLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        tipLabel.setForeground(new Color(21, 128, 61));
        tipPanel.add(tipLabel, BorderLayout.CENTER);
        contentPanel.add(tipPanel);
        
        add(contentPanel, BorderLayout.CENTER);
    }

    public synchronized void setSolution(List<String> solution) {
        this.currentSolution = (solution != null) ? solution : new ArrayList<>();
        this.currentStep = 0;
        this.progressBar.setMaximum(currentSolution.size());
        
        // Update JFX wrapper too
        if (jfxPanel != null) {
            jfxPanel.updateSolvingState(currentSolution, currentStep);
        }
        
        refreshSolverUI();
    }

    private void nextStep() {
        if (currentStep < currentSolution.size()) {
            String move = currentSolution.get(currentStep);
            cube.rotate(move);
            cube3D.updateColors(cube);
            currentStep++;
            refreshSolverUI();
            
            if (jfxPanel != null) {
                jfxPanel.updateSolvingState(currentSolution, currentStep);
            }
            
            if (currentStep == currentSolution.size()) {
                cube3D.startVictorySpin();
            }
        }
    }

    private void prevStep() {
        if (currentStep > 0) {
            currentStep--;
            String move = currentSolution.get(currentStep);
            String rev;
            if (move.endsWith("'")) {
                rev = move.substring(0, 1);
            } else if (move.endsWith("2")) {
                rev = move;
            } else {
                rev = move + "'";
            }
            
            cube.rotate(rev);
            cube3D.updateColors(cube);
            cube3D.stopVictorySpin();
            refreshSolverUI();
            
            if (jfxPanel != null) {
                jfxPanel.updateSolvingState(currentSolution, currentStep);
            }
        }
    }

    private void refreshSolverUI() {
        if (currentSolution.isEmpty()) {
            stepBadgeText.setText("STEP 0 OF 0");
            moveSymbolLabel.setText("?");
            moveBadgeText.setText("Move 0 of 0");
            instructLabel.setText("Solving logic active. Please wait...");
            faceSubCard.setValue("-");
            dirSubCard.setValue("-");
            notSubCard.setValue("-");
            
            progressPercentLabel.setText("0%");
            progressStepLabel.setText("0 of 0 completed");
            progressBar.setValue(0);
            
            tableModel.setRowCount(0);
            stepperPanel.setCurrentStep(0);
            
            nextBtn.setEnabled(false);
            prevBtn.setEnabled(false);
            return;
        }

        progressBar.setValue(currentStep);
        int percent = (int) (((double) currentStep / currentSolution.size()) * 100);
        progressPercentLabel.setText(percent + "%");
        progressStepLabel.setText(currentStep + " of " + currentSolution.size() + " steps completed");
        
        nextBtn.setEnabled(currentStep < currentSolution.size());
        prevBtn.setEnabled(currentStep > 0);
        
        // Refresh Table model
        tableModel.setRowCount(0);
        for (int i = 0; i < currentSolution.size(); i++) {
            String m = currentSolution.get(i);
            String arrow = getMoveArrow(m);
            String desc = "Rotate " + getFaceName(m.charAt(0)) + " face " + getDirectionName(m) + ".";
            tableModel.addRow(new Object[]{String.valueOf(i + 1), arrow, m, desc, ""});
        }
        
        // Update renderers
        cellRenderer.setCurrentStep(currentStep);
        statusRenderer.setCurrentStep(currentStep);
        
        // Auto scroll to make currentStep visible
        if (currentStep < currentSolution.size()) {
            moveTable.scrollRectToVisible(moveTable.getCellRect(currentStep, 0, true));
        }
        
        stepperPanel.setCurrentStep(currentStep);
        
        if (currentStep >= currentSolution.size()) {
            stepBadgeText.setText("SOLVED!");
            moveSymbolLabel.setText("✔");
            moveBadgeText.setText("Completed");
            instructLabel.setText("Rubik's Cube is solved! Great job!");
            faceSubCard.setValue("All");
            dirSubCard.setValue("Done");
            notSubCard.setValue("✔");
            
            nextBtn.setText("SOLVED!  ✔");
            nextBtn.setEnabled(false);
        } else {
            String move = currentSolution.get(currentStep);
            stepBadgeText.setText("STEP " + (currentStep + 1) + " OF " + currentSolution.size());
            moveSymbolLabel.setText(move);
            moveBadgeText.setText("Move " + (currentStep + 1) + " of " + currentSolution.size());
            
            instructLabel.setText("Rotate " + getFaceName(move.charAt(0)) + " face " + getDirectionName(move) + ".");
            faceSubCard.setValue(getFaceName(move.charAt(0)));
            dirSubCard.setValue(getDirectionName(move));
            notSubCard.setValue(move);
            
            nextBtn.setText("NEXT MOVE  ▶");
        }
    }

    private String getMoveArrow(String m) {
        if (m.startsWith("U")) return "↑";
        if (m.startsWith("D")) return "↓";
        if (m.startsWith("R")) return "→";
        if (m.startsWith("L")) return "←";
        if (m.startsWith("F")) return "⟳";
        if (m.startsWith("B")) return "⟲";
        return "?";
    }
    
    private String getFaceName(char f) {
        switch (f) {
            case 'U': return "Top (White)";
            case 'D': return "Bottom (Yellow)";
            case 'R': return "Right (Red)";
            case 'L': return "Left (Orange)";
            case 'F': return "Front (Green)";
            case 'B': return "Back (Blue)";
            default: return "Unknown";
        }
    }
    
    private String getDirectionName(String m) {
        if (m.endsWith("'")) return "Counter-Clockwise";
        if (m.endsWith("2")) return "180 Degrees";
        return "Clockwise";
    }
}

// Additional helper components inside com.carlos.ui

class CubeLogo extends JComponent {
    public CubeLogo() {
        setPreferredSize(new Dimension(32, 32));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int size = 9;
        
        // Top face rhombus
        Path2D top = new Path2D.Double();
        top.moveTo(cx, cy - size);
        top.lineTo(cx + size * 1.35, cy - size * 0.5);
        top.lineTo(cx, cy);
        top.lineTo(cx - size * 1.35, cy - size * 0.5);
        top.closePath();
        g2d.setColor(new Color(253, 224, 71)); // Yellow
        g2d.fill(top);
        g2d.setColor(new Color(15, 23, 42));
        g2d.setStroke(new BasicStroke(1.2f));
        g2d.draw(top);
        
        // Left face
        Path2D left = new Path2D.Double();
        left.moveTo(cx - size * 1.35, cy - size * 0.5);
        left.lineTo(cx, cy);
        left.lineTo(cx, cy + size);
        left.lineTo(cx - size * 1.35, cy + size * 0.5);
        left.closePath();
        g2d.setColor(new Color(34, 197, 94)); // Green
        g2d.fill(left);
        g2d.setColor(new Color(15, 23, 42));
        g2d.draw(left);
        
        // Right face
        Path2D right = new Path2D.Double();
        right.moveTo(cx, cy);
        right.lineTo(cx + size * 1.35, cy - size * 0.5);
        right.lineTo(cx + size * 1.35, cy + size * 0.5);
        right.lineTo(cx, cy + size);
        right.closePath();
        g2d.setColor(new Color(239, 68, 68)); // Red
        g2d.fill(right);
        g2d.setColor(new Color(15, 23, 42));
        g2d.draw(right);
        
        g2d.dispose();
    }
}

class DetailSubCard extends JPanel {
    private final JLabel valueLabel;
    
    public DetailSubCard(String title) {
        setLayout(new GridLayout(2, 1, 0, 1));
        setBackground(new Color(248, 250, 252)); // slate-50
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(5, 6, 5, 6)
        ));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 9));
        titleLabel.setForeground(new Color(100, 116, 139));
        
        valueLabel = new JLabel("-", SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        valueLabel.setForeground(new Color(15, 23, 42));
        
        add(titleLabel);
        add(valueLabel);
    }
    
    public void setValue(String val) {
        valueLabel.setText(val);
    }
}

class StepperPanel extends JPanel {
    private int currentStep = 0;
    
    public StepperPanel() {
        setPreferredSize(new Dimension(500, 65));
        setOpaque(false);
    }
    
    public void setCurrentStep(int step) {
        this.currentStep = step;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int w = getWidth();
        int h = getHeight();
        int lineY = 16;
        
        // Draw standard connector line
        g2d.setColor(new Color(226, 232, 240));
        g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(40, lineY, w - 40, lineY);
        
        // Draw green progress line
        g2d.setColor(new Color(21, 128, 61));
        int progressX = 40;
        if (currentStep >= 1) progressX = w / 2;
        if (currentStep >= 2) progressX = w - 40;
        if (currentStep > 0) {
            g2d.drawLine(40, lineY, progressX, lineY);
        }
        
        String[] titles = {"Orientation", "Middle Layer", "Last Layer"};
        int[] xs = {40, w / 2, w - 40};
        
        for (int i = 0; i < 3; i++) {
            int cx = xs[i];
            int cy = lineY;
            int radius = 12;
            
            boolean completed = currentStep > i;
            boolean active = currentStep == i;
            
            if (completed) {
                g2d.setColor(new Color(21, 128, 61));
                g2d.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
                
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawLine(cx - 4, cy, cx - 1, cy + 3);
                g2d.drawLine(cx - 1, cy + 3, cx + 4, cy - 3);
            } else if (active) {
                g2d.setColor(new Color(30, 58, 138));
                g2d.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 10));
                String txt = String.valueOf(i + 1);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(txt, cx - fm.stringWidth(txt) / 2, cy + fm.getAscent() / 2 - 1);
            } else {
                g2d.setColor(Color.WHITE);
                g2d.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
                g2d.setColor(new Color(203, 213, 225));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);
                
                g2d.setColor(new Color(100, 116, 139));
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 10));
                String txt = String.valueOf(i + 1);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(txt, cx - fm.stringWidth(txt) / 2, cy + fm.getAscent() / 2 - 1);
            }
            
            // Draw titles
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 9));
            FontMetrics fmTitle = g2d.getFontMetrics();
            String title = titles[i];
            int titleY = cy + radius + 14;
            g2d.setColor(new Color(15, 23, 42));
            g2d.drawString(title, cx - fmTitle.stringWidth(title) / 2, titleY);
            
            // Draw status sub-labels
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 8));
            FontMetrics fmStatus = g2d.getFontMetrics();
            String status = completed ? "Completed" : (active ? "Current Step" : "Pending");
            g2d.setColor(completed ? new Color(21, 128, 61) : (active ? new Color(30, 58, 138) : new Color(148, 163, 184)));
            g2d.drawString(status, cx - fmStatus.stringWidth(status) / 2, titleY + 10);
        }
        
        g2d.dispose();
    }
}

class LightCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
    private int currentStep;
    
    public LightCellRenderer(int currentStep) {
        this.currentStep = currentStep;
        setOpaque(true);
    }
    
    public void setCurrentStep(int step) {
        this.currentStep = step;
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        setBackground(Color.WHITE);
        
        if (row == currentStep) {
            setBackground(new Color(239, 246, 255)); // light blue-50 highlight
            setForeground(new Color(30, 58, 138));    // deep blue
            setFont(new Font("Segoe UI", Font.BOLD, 12));
        } else if (row < currentStep) {
            setForeground(new Color(148, 163, 184)); // slate-400 for completed
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
        } else {
            setForeground(new Color(51, 65, 85));    // slate-700 for upcoming
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }
        
        setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        
        if (column == 0 || column == 1 || column == 2) {
            setHorizontalAlignment(JLabel.CENTER);
        } else {
            setHorizontalAlignment(JLabel.LEFT);
        }
        
        return this;
    }
}

class LightStatusRenderer extends javax.swing.table.DefaultTableCellRenderer {
    private int currentStep;
    
    public LightStatusRenderer(int currentStep) {
        this.currentStep = currentStep;
        setHorizontalAlignment(JLabel.CENTER);
        setOpaque(true);
    }
    
    public void setCurrentStep(int step) {
        this.currentStep = step;
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        setBackground(Color.WHITE);
        
        if (row < currentStep) {
            JLabel label = new JLabel("✔");
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setForeground(new Color(34, 197, 94));
            label.setHorizontalAlignment(JLabel.CENTER);
            return label;
        } else if (row == currentStep) {
            setBackground(new Color(239, 246, 255));
            JPanel p = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(30, 58, 138));
                    g2d.fillRoundRect(2, 3, getWidth() - 4, getHeight() - 6, 8, 8);
                    g2d.dispose();
                }
            };
            p.setBackground(new Color(239, 246, 255));
            p.setOpaque(true);
            p.setLayout(new BorderLayout());
            JLabel l = new JLabel("CURRENT", SwingConstants.CENTER);
            l.setFont(new Font("Segoe UI", Font.BOLD, 9));
            l.setForeground(Color.WHITE);
            p.add(l, BorderLayout.CENTER);
            return p;
        } else {
            JLabel label = new JLabel("-");
            label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            label.setForeground(new Color(148, 163, 184));
            label.setHorizontalAlignment(JLabel.CENTER);
            return label;
        }
    }
}
