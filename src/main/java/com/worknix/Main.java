package com.worknix;

import com.worknix.boot.BootManager;
import com.worknix.desktop.WorkNixDE;

public class Main {
    public static void main(String[] args) {
        // Start boot sequence
        BootManager bootManager = new BootManager();
        bootManager.startBoot();
        
        // Check for GUI mode
        boolean guiMode = args.length > 0 && args[0].equals("--gui");
        
        if (guiMode) {
            // Start desktop environment
            javax.swing.SwingUtilities.invokeLater(() -> {
                WorkNixDE de = new WorkNixDE();
                de.setVisible(true);
            });
        } else {
            // Start terminal
            Terminal terminal = new Terminal();
            terminal.start();
        }
    }
} 