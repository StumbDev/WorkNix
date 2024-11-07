package com.worknix.desktop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.worknix.FileSystem;
import com.worknix.user.UserManager;

public class Terminal extends JFrame {
    private final JTextArea textArea;
    private final JTextField inputField;
    private final FileSystem fileSystem;
    private final UserManager userManager;
    private final StringBuilder commandHistory;

    public Terminal() {
        super("WorkNix Terminal");
        setSize(600, 400);
        setLocationRelativeTo(null);

        this.fileSystem = new FileSystem();
        this.userManager = fileSystem.getUserManager();
        this.commandHistory = new StringBuilder();

        // Create terminal text area
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.GREEN);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        // Create input field
        inputField = new JTextField();
        inputField.setBackground(Color.BLACK);
        inputField.setForeground(Color.GREEN);
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        inputField.setCaretColor(Color.GREEN);

        // Layout
        setLayout(new BorderLayout());
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);

        // Handle input
        inputField.addActionListener(e -> {
            String command = inputField.getText();
            processCommand(command);
            inputField.setText("");
        });

        // Initial text
        appendText("WorkNix Terminal v0.1\n");
        appendText("Type 'help' for available commands\n\n");
        updatePrompt();
    }

    private void processCommand(String command) {
        appendText(getPrompt() + command + "\n");
        
        if (command.equals("clear")) {
            textArea.setText("");
            updatePrompt();
            return;
        }

        // Process command and capture output
        // This is a simplified version - you'll want to integrate with your existing Terminal class
        if (command.equals("help")) {
            appendText("Available commands:\n");
            appendText("  help  - Show this help message\n");
            appendText("  clear - Clear terminal screen\n");
            appendText("  exit  - Close terminal window\n");
        } else if (command.equals("exit")) {
            dispose();
        } else {
            appendText("Unknown command: " + command + "\n");
        }
        
        updatePrompt();
    }

    private void appendText(String text) {
        textArea.append(text);
        textArea.setCaretPosition(textArea.getDocument().getLength());
        commandHistory.append(text);
    }

    private String getPrompt() {
        String username = userManager.getCurrentUsername();
        String path = fileSystem.getCurrentPath();
        String prompt = userManager.isRoot() ? "# " : "$ ";
        return username + "@worknix:" + path + prompt;
    }

    private void updatePrompt() {
        appendText(getPrompt());
    }
} 