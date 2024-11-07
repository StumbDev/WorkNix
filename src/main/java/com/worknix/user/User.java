package com.worknix.user;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String username;
    private String passwordHash;
    private final int uid;
    private final int gid;
    private String homeDirectory;
    private String shell;
    private boolean isRoot;

    public User(String username, String password, int uid, int gid, String homeDirectory, String shell) {
        this.username = username;
        this.passwordHash = hashPassword(password);
        this.uid = uid;
        this.gid = gid;
        this.homeDirectory = homeDirectory;
        this.shell = shell;
        this.isRoot = uid == 0;
    }

    private String hashPassword(String password) {
        // Simple hash for demonstration - in production use proper hashing
        return String.valueOf(password.hashCode());
    }

    public boolean verifyPassword(String password) {
        return hashPassword(password).equals(passwordHash);
    }

    public String getUsername() { return username; }
    public int getUid() { return uid; }
    public int getGid() { return gid; }
    public String getHomeDirectory() { return homeDirectory; }
    public String getShell() { return shell; }
    public boolean isRoot() { return isRoot; }
} 