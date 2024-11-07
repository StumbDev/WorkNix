package com.worknix.setup;

import com.worknix.user.User;
import com.worknix.user.UserManager;
import com.worknix.FileSystem;

import java.io.File;
import java.util.Scanner;

public class SystemSetupWizard {
    private final Scanner scanner;
    private final UserManager userManager;
    private final FileSystem fileSystem;

    public SystemSetupWizard(UserManager userManager, FileSystem fileSystem) {
        this.scanner = new Scanner(System.in);
        this.userManager = userManager;
        this.fileSystem = fileSystem;
        this.fileSystem.setSetupMode(true);
    }

    public void runSetup() {
        try {
            System.out.println("\n=== WorkNix System Setup ===");
            System.out.println("Welcome to the WorkNix setup wizard!");
            System.out.println("Let's configure your system.\n");

            setupRootPassword();
            createPrimaryUser();
            setHostname();
            setTimezone();
            saveConfiguration();

            System.out.println("\nSystem setup complete!");
            System.out.println("You can now log in with your user account.");
            System.out.println("===============================\n");
        } finally {
            fileSystem.setSetupMode(false);
        }
    }

    private void setupRootPassword() {
        System.out.println("First, let's set up the root password.");
        System.out.println("(Default root password is 'root')");
        while (true) {
            System.out.print("Enter new root password [root]: ");
            String rootPass = scanner.nextLine();
            
            // If empty, keep default password
            if (rootPass.isEmpty()) {
                System.out.println("Keeping default root password.");
                return;
            }

            System.out.print("Confirm root password: ");
            String confirmPass = scanner.nextLine();

            if (rootPass.equals(confirmPass)) {
                userManager.updateRootPassword(rootPass);
                break;
            } else {
                System.out.println("Passwords don't match. Please try again.");
                System.out.println("Or press Enter to keep default password 'root'");
            }
        }
    }

    private void createPrimaryUser() {
        System.out.println("\nNow, let's create your user account.");
        while (true) {
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();
            
            if (username.matches("[a-z_][a-z0-9_-]*$")) {
                System.out.print("Enter full name: ");
                String fullName = scanner.nextLine();
                
                System.out.print("Enter password: ");
                String password = scanner.nextLine();
                System.out.print("Confirm password: ");
                String confirmPass = scanner.nextLine();

                if (password.equals(confirmPass)) {
                    // Create user with next available UID (1000+)
                    User newUser = new User(username, password, 1000, 1000, 
                                         "/home/" + username, "/bin/bash");
                    userManager.addUser(newUser);
                    
                    // Create home directory
                    fileSystem.createDirectory("/home/" + username);
                    break;
                } else {
                    System.out.println("Passwords don't match. Please try again.");
                }
            } else {
                System.out.println("Invalid username. Use only lowercase letters, numbers, - and _");
            }
        }
    }

    private void setHostname() {
        System.out.print("\nEnter system hostname [worknix]: ");
        String hostname = scanner.nextLine().trim();
        if (hostname.isEmpty()) {
            hostname = "worknix";
        }
        fileSystem.createFile("/etc/hostname", hostname);
    }

    private void setTimezone() {
        System.out.print("\nEnter timezone [UTC]: ");
        String timezone = scanner.nextLine().trim();
        if (timezone.isEmpty()) {
            timezone = "UTC";
        }
        fileSystem.createFile("/etc/timezone", timezone);
    }

    private void saveConfiguration() {
        // Create basic configuration files
        fileSystem.createFile("/etc/motd", 
            "Welcome to WorkNix!\n" +
            "Type 'help' for available commands.\n");
        
        // Save all changes
        fileSystem.saveState();
    }
} 