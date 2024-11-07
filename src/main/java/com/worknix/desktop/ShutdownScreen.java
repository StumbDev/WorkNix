package com.worknix.desktop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class ShutdownScreen extends JWindow {
    private static final String[] MESSAGES = {
        "Saving system state...",
        "Stopping system services...",
        "Unmounting file systems...",
        "Shutting down..."
    };
    
    private Timer messageTimer;
    private Timer shutdownTimer;
    private int currentMessage = 0;
    private final JLabel messageLabel;
    
    public ShutdownScreen() {
        setSize(400, 200);
        setLocationRelativeTo(null);
        
        // Create message label first
        messageLabel = new JLabel(MESSAGES[0], SwingConstants.CENTER);
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        
        // Create gradient panel
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(20, 20, 20);
                Color color2 = new Color(40, 40, 40);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        panel.setLayout(new BorderLayout());
        
        // Create WorkNix logo
        JLabel logoLabel = createLogo();
        panel.add(logoLabel, BorderLayout.NORTH);
        
        // Add message label to panel
        panel.add(messageLabel, BorderLayout.CENTER);
        
        add(panel);
    }
    
    private JLabel createLogo() {
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
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        return logoLabel;
    }
    
    public void startShutdown() {
        // Initialize timers when starting shutdown
        messageTimer = new Timer(1000, e -> {
            currentMessage++;
            if (currentMessage < MESSAGES.length) {
                messageLabel.setText(MESSAGES[currentMessage]);
            }
        });
        messageTimer.setRepeats(true);

        shutdownTimer = new Timer(4000, e -> {
            messageTimer.stop();
            shutdownTimer.stop();
            System.exit(0);
        });
        shutdownTimer.setRepeats(false);

        setVisible(true);
        messageTimer.start();
        shutdownTimer.start();
    }
} 