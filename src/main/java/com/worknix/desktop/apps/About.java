package com.worknix.desktop.apps;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class About extends JDialog {
    private static final String VERSION = "1.0";
    private static final String BUILD = "2024.1";

    public About(Frame parent) {
        super(parent, "About WorkNix", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setResizable(false);

        // Create main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(50, 80, 100);
                Color color2 = new Color(30, 40, 60);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(new BorderLayout(10, 10));

        // Logo panel
        JPanel logoPanel = createLogoPanel();
        mainPanel.add(logoPanel, BorderLayout.NORTH);

        // Info panel
        JPanel infoPanel = createInfoPanel();
        mainPanel.add(infoPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        
        // Create WorkNix logo
        BufferedImage logo = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = logo.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw logo
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Dialog", Font.BOLD, 32));
        g2d.drawString("W", 12, 45);
        g2d.dispose();

        JLabel logoLabel = new JLabel(new ImageIcon(logo));
        logoPanel.add(logoLabel);

        return logoPanel;
    }

    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        
        // Title
        JLabel titleLabel = new JLabel("WorkNix");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Version
        JLabel versionLabel = new JLabel("Version " + VERSION + " (Build " + BUILD + ")");
        versionLabel.setForeground(Color.WHITE);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Description
        JTextArea descArea = new JTextArea(
            "WorkNix is a Portable Unix Workstation System\n" +
            "inspired by SunOS, implemented in Java.\n\n" +
            "Features:\n" +
            "• Unix-like file system\n" +
            "• Multi-user support\n" +
            "• Package management\n" +
            "• Desktop environment\n" +
            "• Built-in applications"
        );
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setForeground(Color.WHITE);
        descArea.setFont(new Font("Dialog", Font.PLAIN, 12));
        descArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Copyright
        JLabel copyrightLabel = new JLabel("© 2024 WorkNix Project");
        copyrightLabel.setForeground(Color.WHITE);
        copyrightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(versionLabel);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(descArea);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(copyrightLabel);

        return infoPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> dispose());
        buttonPanel.add(okButton);

        return buttonPanel;
    }
} 