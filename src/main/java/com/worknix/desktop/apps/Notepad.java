package com.worknix.desktop.apps;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.worknix.FileSystem;

public class Notepad extends JFrame {
    private final JTextArea textArea;
    private final FileSystem fileSystem;
    private String currentFile = null;

    public Notepad(FileSystem fileSystem) {
        super("WorkNix Notepad");
        this.fileSystem = fileSystem;
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Create text area
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        
        JMenuItem newItem = new JMenuItem("New");
        newItem.addActionListener(e -> newFile());
        
        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(e -> openFile());
        
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> saveFile());
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> dispose());

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // Layout
        setLayout(new BorderLayout());
        setJMenuBar(menuBar);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
    }

    private void newFile() {
        textArea.setText("");
        currentFile = null;
        setTitle("WorkNix Notepad");
    }

    private void openFile() {
        String filename = JOptionPane.showInputDialog(this, "Enter filename:");
        if (filename != null) {
            try {
                String content = fileSystem.readFileContent(filename);
                textArea.setText(content);
                currentFile = filename;
                setTitle("WorkNix Notepad - " + filename);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error opening file: " + e.getMessage());
            }
        }
    }

    private void saveFile() {
        String filename = currentFile;
        if (filename == null) {
            filename = JOptionPane.showInputDialog(this, "Enter filename:");
        }
        if (filename != null) {
            try {
                fileSystem.createFile(filename, textArea.getText());
                currentFile = filename;
                setTitle("WorkNix Notepad - " + filename);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage());
            }
        }
    }
} 