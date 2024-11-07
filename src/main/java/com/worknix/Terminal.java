package com.worknix;

import com.worknix.user.UserManager;
import com.worknix.setup.SystemSetupWizard;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class Terminal {
    private final FileSystem fileSystem;
    private final BusyBox busyBox;
    private final Scanner scanner;
    private final UserManager userManager;
    private boolean running;
    private static final String SETUP_FLAG_FILE = "worknix_setup_complete";
    
    public Terminal() {
        this.fileSystem = new FileSystem();
        this.userManager = fileSystem.getUserManager();
        this.busyBox = new BusyBox(fileSystem);
        this.scanner = new Scanner(System.in);
        this.running = true;

        if (isFirstBoot()) {
            SystemSetupWizard wizard = new SystemSetupWizard(userManager, fileSystem);
            wizard.runSetup();
            markSetupComplete();
        }
        
        login();
    }

    private boolean isFirstBoot() {
        File setupFlag = new File(SETUP_FLAG_FILE);
        return !setupFlag.exists();
    }

    private void markSetupComplete() {
        try {
            File setupFlag = new File(SETUP_FLAG_FILE);
            setupFlag.createNewFile();
        } catch (IOException e) {
            System.err.println("Warning: Could not create setup flag file");
        }
    }

    private void login() {
        int attempts = 0;
        while (attempts < 3) {
            System.out.print("login: ");
            String username = scanner.nextLine();
            System.out.print("password: ");
            String password = scanner.nextLine();

            if (userManager.login(username, password)) {
                System.out.println("Welcome to WorkNix!");
                String homeDir = userManager.getCurrentUser().getHomeDirectory();
                fileSystem.changeDirectory(homeDir);
                return;
            } else {
                System.out.println("Login incorrect");
                attempts++;
            }
        }
        System.out.println("Too many failed attempts. Please try again later.");
        System.exit(1);
    }
    
    public void start() {
        System.out.println("WorkNix Terminal v0.1");
        System.out.println("Type 'help' for available commands");
        
        while (running) {
            String prompt = userManager.isRoot() ? "# " : "$ ";
            System.out.print(userManager.getCurrentUsername() + "@worknix:" + fileSystem.getCurrentPath() + prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                processCommand(input.split("\\s+"));
            }
        }
    }
    
    private void processCommand(String[] args) {
        String command = args[0].toLowerCase();
        
        switch (command) {
            case "sudo":
                if (args.length > 1) {
                    System.out.print("[sudo] password for " + userManager.getCurrentUsername() + ": ");
                    String password = scanner.nextLine();
                    if (userManager.sudo(password)) {
                        String[] newArgs = new String[args.length - 1];
                        System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                        processCommand(newArgs);
                        userManager.exitSudo();
                    } else {
                        System.out.println("Sorry, try again.");
                    }
                } else {
                    System.out.println("usage: sudo command");
                }
                break;
            case "whoami":
                System.out.println(userManager.getCurrentUsername());
                break;
            case "su":
                if (args.length > 1) {
                    System.out.print("Password: ");
                    String password = scanner.nextLine();
                    if (!userManager.login(args[1], password)) {
                        System.out.println("su: Authentication failure");
                    }
                } else {
                    System.out.print("Password: ");
                    String password = scanner.nextLine();
                    userManager.login("root", password);
                }
                break;
            case "exit":
                fileSystem.saveState();
                running = false;
                break;
            case "help":
                showHelp();
                break;
            case "ls":
                fileSystem.listCurrentDirectory();
                break;
            case "pwd":
                busyBox.pwd();
                break;
            case "date":
                busyBox.date();
                break;
            case "echo":
                busyBox.echo(args);
                break;
            case "cat":
                busyBox.cat(args);
                break;
            case "touch":
                busyBox.touch(args);
                break;
            case "mkdir":
                busyBox.mkdir(args);
                break;
            case "rm":
                busyBox.rm(args);
                break;
            case "cd":
                busyBox.cd(args);
                break;
            case "sync":
                fileSystem.sync();
                break;
            case "vi":
                busyBox.vi(args);
                break;
            case "pkg":
                if (!userManager.isRoot()) {
                    System.out.println("Package management requires root privileges");
                    System.out.println("Try: sudo pkg ...");
                    return;
                }
                busyBox.pkg(args);
                break;
            case "useradd":
                if (!userManager.isRoot()) {
                    System.out.println("This operation requires root privileges");
                    System.out.println("Try: sudo useradd ...");
                    return;
                }
                busyBox.useradd(args);
                break;
            case "userdel":
                if (!userManager.isRoot()) {
                    System.out.println("This operation requires root privileges");
                    System.out.println("Try: sudo userdel ...");
                    return;
                }
                busyBox.userdel(args);
                break;
            case "passwd":
                busyBox.passwd(args);
                break;
            default:
                if (command.startsWith("/")) {
                    // Check if trying to execute a file
                    if (!userManager.isRoot()) {
                        System.out.println("Permission denied");
                        return;
                    }
                }
                System.out.println("Unknown command: " + command);
        }
    }
    
    private void showHelp() {
        System.out.println("Available commands:");
        System.out.println("  help   - Show this help message");
        System.out.println("  ls     - List files in current directory");
        System.out.println("  pwd    - Print working directory");
        System.out.println("  cd     - Change directory");
        System.out.println("  mkdir  - Create directory");
        System.out.println("  touch  - Create empty file");
        System.out.println("  cat    - Display file contents");
        System.out.println("  rm     - Remove file");
        System.out.println("  echo   - Display a line of text");
        System.out.println("  date   - Display current date and time");
        System.out.println("  exit   - Exit the terminal");
        System.out.println("  sync   - Save file system state to disk");
        System.out.println("  sudo   - Execute command as superuser");
        System.out.println("  su     - Switch user");
        System.out.println("  whoami - Print current user name");
        System.out.println("  vi     - Text editor");
        System.out.println("  pkg    - Package manager");
        System.out.println("  useradd - Create a new user");
        System.out.println("  userdel - Delete a user");
        System.out.println("  passwd  - Change password");
    }
} 