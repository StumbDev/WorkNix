package com.worknix;

import java.util.HashMap;
import java.util.Map;
import com.worknix.user.UserManager;

public class FileSystem {
    private final Directory root;
    private Directory currentDirectory;
    private final UserManager userManager;
    private boolean setupMode = false;
    
    public FileSystem() {
        Directory loadedRoot = FSPersistence.loadFileSystem();
        if (loadedRoot != null) {
            this.root = loadedRoot;
            this.currentDirectory = root;
        } else {
            this.root = new Directory("/");
            this.currentDirectory = root;
            initializeBasicStructure();
        }
        this.userManager = new UserManager(this);
    }
    
    private void initializeBasicStructure() {
        // Create standard Unix directory structure
        createDirectory("/bin");        // Essential command binaries
        createDirectory("/sbin");       // System binaries
        createDirectory("/etc");        // Host-specific system configuration
        createDirectory("/dev");        // Device files
        createDirectory("/proc");       // Process and kernel information
        createDirectory("/var");        // Variable data
        createDirectory("/var/log");    // Log files
        createDirectory("/var/tmp");    // Temporary files
        createDirectory("/tmp");        // Temporary files
        createDirectory("/usr");        // User utilities and applications
        createDirectory("/usr/bin");    // User command binaries
        createDirectory("/usr/sbin");   // System admin commands
        createDirectory("/usr/lib");    // Libraries
        createDirectory("/usr/share");  // Architecture-independent data
        createDirectory("/usr/local");  // Local hierarchy
        createDirectory("/home");       // User home directories
        createDirectory("/root");       // Root user's home directory
        createDirectory("/opt");        // Optional application software
        createDirectory("/mnt");        // Mount point for temporary filesystems
        createDirectory("/media");      // Mount point for removable media

        // Create some example files
        createFile("/etc/hostname", "worknix");
        createFile("/etc/hosts", "127.0.0.1 localhost\n::1 localhost");
        createFile("/etc/passwd", "root:x:0:0:root:/root:/bin/bash");
        createFile("/etc/group", "root:x:0:\nusers:x:100:");
    }
    
    public String getCurrentPath() {
        return currentDirectory.getFullPath();
    }
    
    public void createDirectory(String path) {
        if (path.startsWith("/") && !checkPermission("write")) {
            System.out.println("Permission denied");
            return;
        }
        if (path.startsWith("/")) {
            String[] parts = path.split("/");
            Directory current = root;
            
            for (String part : parts) {
                if (!part.isEmpty()) {
                    Directory child = current.getChild(part);
                    if (child == null) {
                        child = new Directory(part, current);
                        current.addChild(child);
                    }
                    current = child;
                }
            }
        } else {
            Directory newDir = new Directory(path, currentDirectory);
            currentDirectory.addChild(newDir);
        }
    }
    
    public void createFile(String name, String content) {
        if (!checkPermission("write")) {
            System.out.println("Permission denied");
            return;
        }
        currentDirectory.addFile(new FSFile(name, content));
    }
    
    public void readFile(String name) {
        FSFile file = currentDirectory.getFile(name);
        if (file != null) {
            System.out.println(file.getContent());
        } else {
            System.out.println("File not found: " + name);
        }
    }
    
    public void deleteFile(String name) {
        if (!checkPermission("write")) {
            System.out.println("Permission denied");
            return;
        }
        if (currentDirectory.removeFile(name)) {
            System.out.println("Removed: " + name);
        } else {
            System.out.println("File not found: " + name);
        }
    }
    
    public void changeDirectory(String path) {
        if (path.equals("/")) {
            currentDirectory = root;
            return;
        }
        
        if (path.equals("..")) {
            Directory parent = currentDirectory.getParent();
            if (parent != null) {
                currentDirectory = parent;
            }
            return;
        }

        Directory target = currentDirectory.getChild(path);
        if (target != null) {
            currentDirectory = target;
        } else {
            System.out.println("Directory not found: " + path);
        }
    }
    
    public void listCurrentDirectory() {
        currentDirectory.list();
    }

    public void saveState() {
        FSPersistence.saveFileSystem(root);
    }

    // Add this method to Terminal.java's processCommand method
    public void sync() {
        saveState();
        System.out.println("File system state saved.");
    }

    private boolean checkPermission(String operation) {
        // Bypass permission check during setup
        if (setupMode) {
            return true;
        }
        return userManager.isRoot();
    }

    public Directory getCurrentDirectory() {
        return currentDirectory;
    }

    public void setSetupMode(boolean setupMode) {
        this.setupMode = setupMode;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public void deleteDirectory(String path) {
        if (!checkPermission("write")) {
            System.out.println("Permission denied");
            return;
        }

        if (path.equals("/") || path.equals("/root")) {
            System.out.println("Cannot delete root or /root directory");
            return;
        }

        if (path.startsWith("/")) {
            String[] parts = path.split("/");
            Directory current = root;
            Directory parent = null;
            String lastPart = "";
            
            for (String part : parts) {
                if (!part.isEmpty()) {
                    parent = current;
                    current = current.getChild(part);
                    lastPart = part;
                    if (current == null) {
                        System.out.println("Directory not found: " + path);
                        return;
                    }
                }
            }
            
            if (parent != null && current != null) {
                parent.getChildren().remove(lastPart);
                System.out.println("Removed directory: " + path);
            }
        } else {
            Directory target = currentDirectory.getChild(path);
            if (target != null) {
                currentDirectory.getChildren().remove(path);
                System.out.println("Removed directory: " + path);
            } else {
                System.out.println("Directory not found: " + path);
            }
        }
    }

    // Add this method to FileSystem class
    public String readFileContent(String filename) {
        FSFile file = currentDirectory.getFile(filename);
        if (file != null) {
            return file.getContent();
        }
        throw new RuntimeException("File not found: " + filename);
    }
} 