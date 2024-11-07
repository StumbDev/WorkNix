package com.worknix.boot;

import java.util.Scanner;
import java.io.IOException;

public class BootManager {
    private static final String BANNER = 
        "\n" +
        "   WorkNix OpenBoot 1.0\n" +
        "------------------------\n" +
        "CPU: WorkNix Virtual Processor\n" +
        "Memory: 256MB\n" +
        "\n";

    private final Scanner scanner;
    private boolean bootInterrupted;

    public BootManager() {
        this.scanner = new Scanner(System.in);
        this.bootInterrupted = false;
    }

    public void startBoot() {
        System.out.print(BANNER);
        
        // Give user a chance to interrupt boot
        bootInterrupted = waitForInterrupt();

        if (bootInterrupted) {
            enterBootPrompt();
        } else {
            normalBoot();
        }
    }

    private boolean waitForInterrupt() {
        System.out.println("Press ESC to interrupt boot sequence...");
        long startTime = System.currentTimeMillis();
        
        while (System.currentTimeMillis() - startTime < 5000) {
            try {
                if (System.console() != null && System.console().reader().ready()) {
                    int key = System.console().reader().read();
                    if (key == 27) { // ESC key
                        return true;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading input: " + e.getMessage());
                return false;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    private void enterBootPrompt() {
        boolean running = true;
        System.out.println("\nWorkNix OpenBoot prompt");
        
        while (running) {
            System.out.print("ok> ");
            String command = scanner.nextLine().trim();
            
            switch (command) {
                case "boot":
                    running = false;
                    normalBoot();
                    break;
                case "banner":
                    System.out.print(BANNER);
                    break;
                case "help":
                    showBootHelp();
                    break;
                case "probe-all":
                    probeDevices();
                    break;
                case "show-disks":
                    showDisks();
                    break;
                case "show-devs":
                    showDevices();
                    break;
                case "reset":
                    System.out.println("Resetting system...");
                    System.exit(0);
                    break;
                case "":
                    break;
                default:
                    System.out.println("Unknown command: " + command);
                    System.out.println("Type 'help' for available commands");
            }
        }
    }

    private void normalBoot() {
        System.out.println("\nBooting WorkNix...");
        
        // Show boot progress
        showBootProgress("Probing devices", 1000);
        showBootProgress("Loading kernel", 1500);
        showBootProgress("Mounting root filesystem", 800);
        showBootProgress("Starting system services", 1200);
        
        System.out.println("\nBoot complete.\n");
    }

    private void showBootProgress(String message, long delay) {
        System.out.print(message + "...");
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            // Ignore
        }
        System.out.println(" done");
    }

    private void showBootHelp() {
        System.out.println("Available commands:");
        System.out.println("  banner     - Show system banner");
        System.out.println("  boot       - Boot the system");
        System.out.println("  probe-all  - Probe all devices");
        System.out.println("  show-disks - Show disk devices");
        System.out.println("  show-devs  - Show all devices");
        System.out.println("  reset      - Reset the system");
        System.out.println("  help       - Show this help message");
    }

    private void probeDevices() {
        System.out.println("Probing system devices...");
        System.out.println("/virtual-devices");
        System.out.println("    /console");
        System.out.println("    /keyboard");
        System.out.println("/packages");
        System.out.println("    /disk-label");
        System.out.println("    /terminal-emulator");
        System.out.println("/virtual-memory");
        System.out.println("    /ram@0,0");
    }

    private void showDisks() {
        System.out.println("Available disk devices:");
        System.out.println("/virtual-devices/disk@0,0");
        System.out.println("    Label: WorkNix-Root");
        System.out.println("    Size: 1024MB");
        System.out.println("    Type: Virtual Disk");
    }

    private void showDevices() {
        System.out.println("System devices:");
        System.out.println("/");
        System.out.println("    /virtual-devices");
        System.out.println("        /disk@0,0");
        System.out.println("        /console");
        System.out.println("        /keyboard");
        System.out.println("    /packages");
        System.out.println("    /virtual-memory");
        System.out.println("    /openprom");
        System.out.println("    /options");
    }
} 