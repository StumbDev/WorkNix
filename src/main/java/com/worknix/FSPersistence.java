package com.worknix;

import java.io.*;
import java.util.Map;

public class FSPersistence {
    private static final String SAVE_FILE = "worknix_fs.dat";

    public static void saveFileSystem(Directory root) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(root);
        } catch (IOException e) {
            System.err.println("Error saving file system: " + e.getMessage());
        }
    }

    public static Directory loadFileSystem() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(SAVE_FILE))) {
            return (Directory) ois.readObject();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading file system: " + e.getMessage());
            return null;
        }
    }
} 