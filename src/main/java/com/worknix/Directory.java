package com.worknix;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Directory implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String name;
    private final Map<String, Directory> children;
    private final Map<String, FSFile> files;
    private Directory parent;
    
    public Directory(String name) {
        this(name, null);
    }

    public Directory(String name, Directory parent) {
        this.name = name;
        this.children = new HashMap<>();
        this.files = new HashMap<>();
        this.parent = parent;
    }
    
    public void addChild(Directory child) {
        children.put(child.getName(), child);
    }

    public void addFile(FSFile file) {
        files.put(file.getName(), file);
    }

    public Directory getChild(String name) {
        return children.get(name);
    }

    public FSFile getFile(String name) {
        return files.get(name);
    }

    public boolean removeFile(String name) {
        return files.remove(name) != null;
    }
    
    public String getName() {
        return name;
    }

    public Directory getParent() {
        return parent;
    }

    public String getFullPath() {
        if (parent == null) {
            return "/";
        }
        String parentPath = parent.getFullPath();
        return parentPath.equals("/") ? "/" + name : parentPath + "/" + name;
    }
    
    public void list() {
        System.out.println("Contents of " + getFullPath() + ":");
        for (String childName : children.keySet()) {
            System.out.println(childName + "/");
        }
        for (String fileName : files.keySet()) {
            System.out.println(fileName);
        }
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }

    public Map<String, Directory> getChildren() {
        return children;
    }

    public Map<String, FSFile> getFiles() {
        return files;
    }
} 