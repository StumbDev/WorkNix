package com.worknix.desktop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.worknix.FileSystem;
import com.worknix.Directory;
import com.worknix.FSFile;
import java.util.Map;

public class FileManager extends JFrame {
    private final FileSystem fileSystem;
    private final JList<String> fileList;
    private final DefaultListModel<String> listModel;
    private final JLabel pathLabel;
    private Directory currentDirectory;

    public FileManager() {
        super("WorkNix File Manager");
        setSize(800, 600);
        setLocationRelativeTo(null);

        this.fileSystem = new FileSystem();
        this.currentDirectory = fileSystem.getCurrentDirectory();

        // Create toolbar
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        
        JButton upButton = new JButton("Up");
        JButton newFolderButton = new JButton("New Folder");
        JButton deleteButton = new JButton("Delete");
        
        toolbar.add(upButton);
        toolbar.add(newFolderButton);
        toolbar.add(deleteButton);

        // Create path label
        pathLabel = new JLabel();
        updatePathLabel();

        // Create file list
        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Layout
        setLayout(new BorderLayout());
        add(toolbar, BorderLayout.NORTH);
        add(pathLabel, BorderLayout.CENTER);
        add(new JScrollPane(fileList), BorderLayout.CENTER);

        // Button actions
        upButton.addActionListener(e -> navigateUp());
        newFolderButton.addActionListener(e -> createNewFolder());
        deleteButton.addActionListener(e -> deleteSelected());

        // Double click to navigate
        fileList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    navigateToSelected();
                }
            }
        });

        refreshFileList();
    }

    private void refreshFileList() {
        listModel.clear();
        
        // Add directories
        for (Map.Entry<String, Directory> entry : currentDirectory.getChildren().entrySet()) {
            listModel.addElement("[DIR] " + entry.getKey());
        }
        
        // Add files
        for (Map.Entry<String, FSFile> entry : currentDirectory.getFiles().entrySet()) {
            listModel.addElement(entry.getKey());
        }
    }

    private void updatePathLabel() {
        pathLabel.setText(" Current path: " + currentDirectory.getFullPath());
    }

    private void navigateUp() {
        Directory parent = currentDirectory.getParent();
        if (parent != null) {
            currentDirectory = parent;
            refreshFileList();
            updatePathLabel();
        }
    }

    private void navigateToSelected() {
        String selected = fileList.getSelectedValue();
        if (selected != null && selected.startsWith("[DIR] ")) {
            String dirName = selected.substring(6);
            Directory newDir = currentDirectory.getChild(dirName);
            if (newDir != null) {
                currentDirectory = newDir;
                refreshFileList();
                updatePathLabel();
            }
        }
    }

    private void createNewFolder() {
        String name = JOptionPane.showInputDialog(this, "Enter folder name:");
        if (name != null && !name.isEmpty()) {
            fileSystem.createDirectory(name);
            refreshFileList();
        }
    }

    private void deleteSelected() {
        String selected = fileList.getSelectedValue();
        if (selected != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete " + selected + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                if (selected.startsWith("[DIR] ")) {
                    String dirName = selected.substring(6);
                    fileSystem.deleteDirectory(dirName);
                } else {
                    fileSystem.deleteFile(selected);
                }
                refreshFileList();
            }
        }
    }
} 