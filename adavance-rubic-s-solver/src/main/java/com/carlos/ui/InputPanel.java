package com.carlos.ui;

import javax.swing.*;
import java.awt.*;
import com.carlos.cube.RubiksCube;

public class InputPanel extends JPanel {
    private RubiksCube cube;
    private Cube3D cube3D;
    private MainFrame frame;

    private JTextField[] faceFields = new JTextField[6];
    private String[] faceNames = {"Top (U)", "Right (R)", "Front (F)", "Down (D)", "Left (L)", "Back (B)"};
    private Color[] faceColors = {Color.WHITE, Color.RED, Color.GREEN, Color.YELLOW, new Color(255, 140, 0), Color.BLUE};

    public InputPanel(RubiksCube cube, Cube3D cube3D, MainFrame frame) {
        this.cube = cube;
        this.cube3D = cube3D;
        this.frame = frame;
        
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        setupUI();
    }

    private void setupUI() {
        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("RUBIK'S CUBE SOLVER");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(26, 35, 126));
        header.add(title, BorderLayout.NORTH);
        header.add(new JLabel("Enter the cube configuration as 6 face strings (54 characters total)"), BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // --- MAIN CONTENT (Split between inputs and 3D) ---
        JPanel center = new JPanel(new GridLayout(1, 2, 20, 0));
        center.setOpaque(false);

        // Left: Inputs
        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        left.setBorder(BorderFactory.createTitledBorder("INPUT: ENTER 6 FACE STRINGS (ROW-WISE)"));

        JPanel rows = new JPanel(new GridLayout(6, 1, 0, 10));
        rows.setOpaque(false);
        rows.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < 6; i++) {
            rows.add(createInputRow(i));
        }
        left.add(rows, BorderLayout.NORTH);

        JPanel presetPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        presetPanel.setBorder(BorderFactory.createTitledBorder("LOAD PRESET EXAMPLES"));
        presetPanel.setOpaque(false);

        JButton btn1 = new JButton("Classic Scramble");
        btn1.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn1.addActionListener(e -> fillPreset("F R' B L D' U2 R' F2 B D' F L"));

        JButton btn2 = new JButton("Checkerboard");
        btn2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn2.addActionListener(e -> fillPreset("U2 D2 R2 L2 F2 B2"));

        JButton btn3 = new JButton("Superflip");
        btn3.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn3.addActionListener(e -> fillPreset("U R2 F B R B2 R U2 F2 R2 U' D' F2 D L2 B2 U L D2"));

        presetPanel.add(btn1);
        presetPanel.add(btn2);
        presetPanel.add(btn3);
        left.add(presetPanel, BorderLayout.SOUTH);

        center.add(left);
        
        // Right: 3D View Placeholder (The actual 3D view is managed by MainFrame, but we want to show it here)
        // Since the user wants the 3D view on the right as in the image:
        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        right.setBorder(BorderFactory.createTitledBorder("3D CUBE VIEW"));
        
        // We will just put a label here, because the JFXPanel is in the SplitPane of MainFrame.
        // To truly match the image, we would need to move the JFXPanel into this panel.
        // Let's assume for now it stays in the split pane for performance, but we style this panel as a container.
        JLabel tip = new JLabel("Visualizing state on the right...", SwingConstants.CENTER);
        right.add(tip, BorderLayout.CENTER);
        
        center.add(right);
        add(center, BorderLayout.CENTER);

        // --- BOTTOM ---
        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        
        JPanel footerInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footerInfo.setOpaque(false);
        footerInfo.add(new JLabel("The solver will try to solve the cube in minimum moves (optimal solution)."));
        
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        
        StyledButton solveBtn = new StyledButton("SOLVE CUBE", new Color(13, 71, 161), new Color(25, 118, 210), 10);
        solveBtn.setPreferredSize(new Dimension(200, 50));
        solveBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        solveBtn.addActionListener(e -> frame.startSolving());

        StyledButton resetBtn = new StyledButton("RESET ALL", new Color(100, 116, 139), new Color(71, 85, 105), 10);
        resetBtn.setPreferredSize(new Dimension(150, 50));
        resetBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resetBtn.addActionListener(e -> resetInputs());

        actions.add(solveBtn);
        actions.add(resetBtn);

        south.add(footerInfo, BorderLayout.WEST);
        south.add(actions, BorderLayout.EAST);
        add(south, BorderLayout.SOUTH);
    }

    private JPanel createInputRow(int i) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 0, 5);

        JLabel name = new JLabel(faceNames[i]);
        name.setPreferredSize(new Dimension(80, 25));
        gbc.weightx = 0.1;
        row.add(name, gbc);

        JPanel colorBlock = new JPanel();
        colorBlock.setBackground(faceColors[i]);
        colorBlock.setPreferredSize(new Dimension(25, 25));
        colorBlock.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        gbc.weightx = 0;
        row.add(colorBlock, gbc);

        faceFields[i] = new JTextField(15);
        faceFields[i].setFont(new Font("Monospaced", Font.BOLD, 14));
        faceFields[i].addActionListener(e -> syncToCube());
        gbc.weightx = 0.8;
        row.add(faceFields[i], gbc);

        return row;
    }

    private void syncToCube() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            String s = faceFields[i].getText().toUpperCase().trim();
            while (s.length() < 9) s += "U";
            sb.append(s.substring(0, 9));
        }
        cube.setFacelet(sb.toString());
        cube3D.updateColors(cube);
    }

    private void fillPreset(String scramble) {
        RubiksCube temp = new RubiksCube();
        if (scramble != null && !scramble.isEmpty()) {
            for (String move : scramble.split("\\s+")) {
                temp.rotate(move);
            }
        }
        String s = temp.getFaceletString();
        for (int i = 0; i < 6; i++) {
            faceFields[i].setText(s.substring(i * 9, (i + 1) * 9));
        }
        syncToCube();
    }

    private void resetInputs() {
        for (JTextField f : faceFields) f.setText("");
        cube.setFacelet("UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB");
        cube3D.updateColors(cube);
    }
}
