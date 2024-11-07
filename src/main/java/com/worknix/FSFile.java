package com.worknix;

import java.io.Serializable;

public class FSFile implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String name;
    private String content;
    private final long creationTime;
    private long modificationTime;

    public FSFile(String name, String content) {
        this.name = name;
        this.content = content;
        this.creationTime = System.currentTimeMillis();
        this.modificationTime = this.creationTime;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getModificationTime() {
        return modificationTime;
    }

    public void updateModificationTime() {
        this.modificationTime = System.currentTimeMillis();
    }
} 