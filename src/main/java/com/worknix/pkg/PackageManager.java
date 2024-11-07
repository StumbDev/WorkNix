package com.worknix.pkg;

import com.worknix.FileSystem;
import java.io.*;
import java.util.*;

public class PackageManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String PKG_DB_FILE = "worknix_packages.db";
    
    private final Map<String, Package> packages;
    private final FileSystem fileSystem;

    public PackageManager(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
        this.packages = new HashMap<>();
        loadPackageDatabase();
        initializeDefaultPackages();
    }

    private void initializeDefaultPackages() {
        if (packages.isEmpty()) {
            // Add some basic packages
            addPackage(new Package("coreutils", "1.0", "Basic file, shell and text manipulation utilities"));
            addPackage(new Package("bash", "1.0", "Bourne Again SHell"));
            addPackage(new Package("vim", "1.0", "Vi IMproved text editor"));
            addPackage(new Package("grep", "1.0", "Pattern matching utility"));
            addPackage(new Package("less", "1.0", "Text file viewer"));
            
            // Add dependencies
            packages.get("vim").addDependency("coreutils");
            packages.get("grep").addDependency("coreutils");
            
            // Add files for each package
            Package coreutils = packages.get("coreutils");
            coreutils.addFile("/bin/ls");
            coreutils.addFile("/bin/cp");
            coreutils.addFile("/bin/mv");
            coreutils.addFile("/bin/rm");
            
            savePackageDatabase();
        }
    }

    public void install(String packageName) {
        Package pkg = packages.get(packageName);
        if (pkg == null) {
            System.out.println("Package not found: " + packageName);
            return;
        }

        if (pkg.isInstalled()) {
            System.out.println("Package " + packageName + " is already installed");
            return;
        }

        // Check and install dependencies first
        for (String dep : pkg.getDependencies()) {
            if (!packages.get(dep).isInstalled()) {
                System.out.println("Installing dependency: " + dep);
                install(dep);
            }
        }

        // Install package files
        for (String file : pkg.getFiles()) {
            fileSystem.createFile(file, "# Binary content for " + file);
        }

        pkg.setInstalled(true);
        System.out.println("Successfully installed " + packageName);
        savePackageDatabase();
    }

    public void remove(String packageName) {
        Package pkg = packages.get(packageName);
        if (pkg == null) {
            System.out.println("Package not found: " + packageName);
            return;
        }

        if (!pkg.isInstalled()) {
            System.out.println("Package " + packageName + " is not installed");
            return;
        }

        // Check if other packages depend on this one
        for (Package p : packages.values()) {
            if (p.isInstalled() && p.getDependencies().contains(packageName)) {
                System.out.println("Cannot remove: package " + p.getName() + " depends on " + packageName);
                return;
            }
        }

        // Remove package files
        for (String file : pkg.getFiles()) {
            fileSystem.deleteFile(file);
        }

        pkg.setInstalled(false);
        System.out.println("Successfully removed " + packageName);
        savePackageDatabase();
    }

    public void list() {
        System.out.println("Available packages:");
        for (Package pkg : packages.values()) {
            System.out.printf("%-15s %-10s %s%n", 
                pkg.getName(), 
                pkg.getVersion(), 
                pkg.isInstalled() ? "[installed]" : "");
        }
    }

    public void info(String packageName) {
        Package pkg = packages.get(packageName);
        if (pkg == null) {
            System.out.println("Package not found: " + packageName);
            return;
        }

        System.out.println("Package: " + pkg.getName());
        System.out.println("Version: " + pkg.getVersion());
        System.out.println("Status: " + (pkg.isInstalled() ? "installed" : "not installed"));
        System.out.println("Description: " + pkg.getDescription());
        
        if (!pkg.getDependencies().isEmpty()) {
            System.out.println("Dependencies: " + String.join(", ", pkg.getDependencies()));
        }
        
        if (!pkg.getFiles().isEmpty()) {
            System.out.println("Files:");
            for (String file : pkg.getFiles()) {
                System.out.println("  " + file);
            }
        }
    }

    private void addPackage(Package pkg) {
        packages.put(pkg.getName(), pkg);
    }

    private void savePackageDatabase() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(PKG_DB_FILE))) {
            oos.writeObject(packages);
        } catch (IOException e) {
            System.err.println("Error saving package database: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadPackageDatabase() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(PKG_DB_FILE))) {
            Map<String, Package> loaded = (Map<String, Package>) ois.readObject();
            packages.putAll(loaded);
        } catch (FileNotFoundException e) {
            // Ignore - will create new database
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading package database: " + e.getMessage());
        }
    }
} 