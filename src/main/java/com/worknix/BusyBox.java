package com.worknix;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import com.worknix.pkg.PackageManager;
import com.worknix.editor.SimpleVI;
import com.worknix.user.User;

public class BusyBox {
    private final FileSystem fileSystem;
    private final PackageManager packageManager;

    public BusyBox(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
        this.packageManager = new PackageManager(fileSystem);
    }

    public void pwd() {
        System.out.println(fileSystem.getCurrentPath());
    }

    public void date() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        System.out.println(sdf.format(new Date()));
    }

    public void echo(String[] args) {
        if (args.length > 1) {
            System.out.println(String.join(" ", args));
        }
    }

    public void cat(String[] args) {
        if (args.length > 1) {
            fileSystem.readFile(args[1]);
        } else {
            System.out.println("Usage: cat <filename>");
        }
    }

    public void touch(String[] args) {
        if (args.length > 1) {
            fileSystem.createFile(args[1], "");
        } else {
            System.out.println("Usage: touch <filename>");
        }
    }

    public void mkdir(String[] args) {
        if (args.length > 1) {
            fileSystem.createDirectory(args[1]);
        } else {
            System.out.println("Usage: mkdir <directory>");
        }
    }

    public void rm(String[] args) {
        if (args.length > 1) {
            fileSystem.deleteFile(args[1]);
        } else {
            System.out.println("Usage: rm <filename>");
        }
    }

    public void cd(String[] args) {
        if (args.length > 1) {
            fileSystem.changeDirectory(args[1]);
        } else {
            fileSystem.changeDirectory("/");
        }
    }

    public void vi(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: vi <filename>");
            return;
        }

        String filename = args[1];
        String initialContent = "";
        FSFile existingFile = fileSystem.getCurrentDirectory().getFile(filename);
        
        if (existingFile != null) {
            initialContent = existingFile.getContent();
        }

        SimpleVI editor = new SimpleVI(initialContent, filename);
        editor.start();

        if (editor.isModified()) {
            fileSystem.createFile(filename, editor.getContent());
        }
    }

    public void pkg(String[] args) {
        if (args.length < 2) {
            showPkgHelp();
            return;
        }

        String subcommand = args[1];
        switch (subcommand) {
            case "install":
                if (args.length < 3) {
                    System.out.println("Usage: pkg install <package>");
                    return;
                }
                packageManager.install(args[2]);
                break;
            case "remove":
                if (args.length < 3) {
                    System.out.println("Usage: pkg remove <package>");
                    return;
                }
                packageManager.remove(args[2]);
                break;
            case "list":
                packageManager.list();
                break;
            case "info":
                if (args.length < 3) {
                    System.out.println("Usage: pkg info <package>");
                    return;
                }
                packageManager.info(args[2]);
                break;
            default:
                showPkgHelp();
        }
    }

    public void useradd(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: useradd <username>");
            return;
        }

        String username = args[1];
        if (!username.matches("[a-z_][a-z0-9_-]*$")) {
            System.out.println("Invalid username. Use only lowercase letters, numbers, - and _");
            return;
        }

        System.out.print("Enter password: ");
        String password = new Scanner(System.in).nextLine();
        System.out.print("Confirm password: ");
        String confirmPass = new Scanner(System.in).nextLine();

        if (!password.equals(confirmPass)) {
            System.out.println("Passwords don't match");
            return;
        }

        // Create user with next available UID (1000+)
        User newUser = new User(username, password, 1000, 1000, 
                             "/home/" + username, "/bin/bash");
        fileSystem.getUserManager().addUser(newUser);
        System.out.println("User " + username + " created");
    }

    public void userdel(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: userdel <username>");
            return;
        }

        String username = args[1];
        if (username.equals("root")) {
            System.out.println("Cannot delete root user");
            return;
        }

        if (fileSystem.getUserManager().deleteUser(username)) {
            System.out.println("User " + username + " deleted");
        } else {
            System.out.println("User " + username + " not found");
        }
    }

    public void passwd(String[] args) {
        String username;
        if (args.length > 1) {
            username = args[1];
        } else {
            username = fileSystem.getUserManager().getCurrentUsername();
        }

        // Only root can change other users' passwords
        if (!username.equals(fileSystem.getUserManager().getCurrentUsername()) && 
            !fileSystem.getUserManager().isRoot()) {
            System.out.println("Permission denied");
            return;
        }

        System.out.print("Enter new password: ");
        String password = new Scanner(System.in).nextLine();
        System.out.print("Confirm new password: ");
        String confirmPass = new Scanner(System.in).nextLine();

        if (!password.equals(confirmPass)) {
            System.out.println("Passwords don't match");
            return;
        }

        if (fileSystem.getUserManager().changePassword(username, password)) {
            System.out.println("Password changed successfully");
        } else {
            System.out.println("User not found");
        }
    }

    private void showPkgHelp() {
        System.out.println("Package manager commands:");
        System.out.println("  pkg install <package>  - Install a package");
        System.out.println("  pkg remove <package>   - Remove a package");
        System.out.println("  pkg list              - List all packages");
        System.out.println("  pkg info <package>     - Show package information");
    }
} 