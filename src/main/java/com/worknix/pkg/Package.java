package com.worknix.pkg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Package implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String name;
    private final String version;
    private final String description;
    private final List<String> dependencies;
    private final List<String> files;
    private boolean installed;

    public Package(String name, String version, String description) {
        this.name = name;
        this.version = version;
        this.description = description;
        this.dependencies = new ArrayList<>();
        this.files = new ArrayList<>();
        this.installed = false;
    }

    public String getName() { return name; }
    public String getVersion() { return version; }
    public String getDescription() { return description; }
    public List<String> getDependencies() { return dependencies; }
    public List<String> getFiles() { return files; }
    public boolean isInstalled() { return installed; }
    public void setInstalled(boolean installed) { this.installed = installed; }

    public void addDependency(String dep) {
        dependencies.add(dep);
    }

    public void addFile(String file) {
        files.add(file);
    }

    @Override
    public String toString() {
        return name + "-" + version + (installed ? " [installed]" : "");
    }
} 