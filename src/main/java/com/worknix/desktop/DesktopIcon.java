package com.worknix.desktop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class DesktopIcon extends JPanel {
    private final JLabel iconLabel;
    private final JLabel textLabel;

    public DesktopIcon(String name, String iconType, ActionListener action) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);

        // Create icon based on type
        ImageIcon icon = createIconByType(name, iconType);

        // Icon
        iconLabel = new JLabel(icon);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Text
        textLabel = new JLabel(name);
        textLabel.setForeground(Color.WHITE);
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(iconLabel);
        add(Box.createVerticalStrut(5));
        add(textLabel);

        // Add double-click action
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    action.actionPerformed(null);
                }
            }
        });
    }

    private ImageIcon createIconByType(String name, String type) {
        int size = 32;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        switch (type) {
            case "terminal":
                createTerminalIcon(g2d, size);
                break;
            case "files":
                createFileManagerIcon(g2d, size);
                break;
            default:
                createDefaultIcon(g2d, size, name);
        }
        
        g2d.dispose();
        return new ImageIcon(image);
    }

    private void createTerminalIcon(Graphics2D g2d, int size) {
        // Terminal background
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, size, size);
        
        // Terminal border
        g2d.setColor(Color.GREEN);
        g2d.drawRect(2, 2, size-4, size-4);
        
        // Terminal prompt
        g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
        g2d.drawString(">_", 8, 20);
    }

    private void createFileManagerIcon(Graphics2D g2d, int size) {
        // Folder background
        g2d.setColor(new Color(255, 200, 0));  // Golden yellow
        
        // Draw folder base
        int[] xPoints = {2, size-2, size-2, 2};
        int[] yPoints = {8, 8, size-2, size-2};
        g2d.fillPolygon(xPoints, yPoints, 4);
        
        // Draw folder top
        g2d.fillRect(2, 2, size/2, 6);
        
        // Draw border
        g2d.setColor(new Color(200, 150, 0));
        g2d.drawPolygon(xPoints, yPoints, 4);
        g2d.drawRect(2, 2, size/2, 6);
    }

    private void createDefaultIcon(Graphics2D g2d, int size, String name) {
        // Background
        g2d.setColor(new Color(60, 60, 180));
        g2d.fillRect(0, 0, size, size);
        
        // First letter
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Dialog", Font.BOLD, 20));
        FontMetrics fm = g2d.getFontMetrics();
        String letter = name.substring(0, 1).toUpperCase();
        int x = (size - fm.stringWidth(letter)) / 2;
        int y = ((size - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(letter, x, y);
    }
} 