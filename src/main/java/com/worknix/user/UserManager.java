package com.worknix.user;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import com.worknix.FileSystem;

public class UserManager implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final Map<String, User> users;
    private User currentUser;
    private boolean sudoMode = false;
    private final FileSystem fileSystem;

    public UserManager(FileSystem fileSystem) {
        this.users = new HashMap<>();
        this.fileSystem = fileSystem;
        initializeDefaultUsers();
    }

    private void initializeDefaultUsers() {
        // Create root user with default password "root"
        addUser(new User("root", "root", 0, 0, "/root", "/bin/bash"));
    }

    public void addUser(User user) {
        users.put(user.getUsername(), user);
        // Create home directory for new user
        String homePath = user.getHomeDirectory();
        if (!homePath.equals("/root")) {
            fileSystem.createDirectory(homePath);
        }
    }

    public boolean login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.verifyPassword(password)) {
            currentUser = user;
            sudoMode = false;
            return true;
        }
        return false;
    }

    public boolean sudo(String password) {
        if (currentUser != null && users.get("root").verifyPassword(password)) {
            sudoMode = true;
            return true;
        }
        return false;
    }

    public void exitSudo() {
        sudoMode = false;
    }

    public boolean isRoot() {
        return sudoMode || (currentUser != null && currentUser.isRoot());
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : "nobody";
    }

    public void updateRootPassword(String newPassword) {
        // Don't clear other users when updating root password
        users.remove("root");
        addUser(new User("root", newPassword, 0, 0, "/root", "/bin/bash"));
    }

    public boolean deleteUser(String username) {
        if (users.containsKey(username)) {
            User user = users.get(username);
            // Remove user's home directory
            if (!user.getHomeDirectory().equals("/root")) {
                fileSystem.deleteDirectory(user.getHomeDirectory());
            }
            users.remove(username);
            return true;
        }
        return false;
    }

    public boolean changePassword(String username, String newPassword) {
        User user = users.get(username);
        if (user != null) {
            // Create new user instance with updated password
            users.put(username, new User(username, newPassword, 
                     user.getUid(), user.getGid(), 
                     user.getHomeDirectory(), user.getShell()));
            return true;
        }
        return false;
    }
} 