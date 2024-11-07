package com.worknix.desktop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.worknix.desktop.apps.*;
import com.worknix.FileSystem;

public class WorkNixDE extends JFrame {
    private final JPanel desktop;
    private final JPanel taskbar;
    private final Terminal terminal;
    private final FileManager fileManager;
    private final Clock clock;
    private JPanel runningApps;
    private int windowCount = 0;
    private final Calculator calculator;
    private final Notepad notepad;
    private final FileSystem fileSystem;
    private final ShutdownScreen shutdownScreen;

    public WorkNixDE() {
        super("WorkNix Desktop Environment");
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Initialize FileSystem first
        this.fileSystem = new FileSystem();

        // Set the look and feel to be more Unix-like
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize components first
        terminal = new Terminal();
        fileManager = new FileManager();
        clock = new Clock();

        // Initialize new applications
        calculator = new Calculator();
        notepad = new Notepad(fileSystem);

        // Initialize shutdown screen
        shutdownScreen = new ShutdownScreen();

        // Main container
        desktop = new JPanel(new BorderLayout());
        desktop.setBackground(new Color(50, 80, 100)); // Classic SunOS blue

        // Create taskbar
        taskbar = createTaskbar();
        desktop.add(taskbar, BorderLayout.SOUTH);

        // Create desktop icons
        JPanel iconPanel = createDesktopIcons();
        desktop.add(iconPanel, BorderLayout.CENTER);

        // Add desktop to frame
        add(desktop);

        // Add global keyboard shortcuts
        addKeyBindings();

        // Add system tray icon if supported
        setupSystemTray();

        // Add right-click desktop menu
        addDesktopMenu();
    }

    private JPanel createTaskbar() {
        JPanel taskbar = new JPanel(new BorderLayout());
        taskbar.setBackground(new Color(60, 60, 60));
        taskbar.setPreferredSize(new Dimension(getWidth(), 30));
        taskbar.setBorder(BorderFactory.createEtchedBorder());

        // Start menu button with custom styling
        JButton startButton = new JButton("Launch");
        startButton.setForeground(Color.WHITE);
        startButton.setBackground(new Color(80, 80, 80));
        startButton.setBorderPainted(false);
        startButton.setFocusPainted(false);
        startButton.addActionListener(e -> showStartMenu());
        startButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                startButton.setBackground(new Color(100, 100, 100));
            }
            public void mouseExited(MouseEvent e) {
                startButton.setBackground(new Color(80, 80, 80));
            }
        });
        taskbar.add(startButton, BorderLayout.WEST);

        // Running applications area
        runningApps = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        runningApps.setOpaque(false);
        taskbar.add(runningApps, BorderLayout.CENTER);

        // System tray area
        JPanel trayArea = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        trayArea.setOpaque(false);
        
        // Add date next to clock
        JLabel dateLabel = new JLabel();
        dateLabel.setForeground(Color.WHITE);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        dateLabel.setText(dateFormat.format(new Date()));
        trayArea.add(dateLabel);
        
        // Add clock
        JLabel clockLabel = clock.getClockLabel();
        trayArea.add(clockLabel);

        taskbar.add(trayArea, BorderLayout.EAST);

        return taskbar;
    }

    private void addWindowToTaskbar(JFrame window) {
        JButton windowButton = new JButton(window.getTitle());
        windowButton.setForeground(Color.WHITE);
        windowButton.setBackground(new Color(80, 80, 80));
        windowButton.setBorderPainted(false);
        windowButton.setFocusPainted(false);
        windowButton.addActionListener(e -> {
            window.setVisible(true);
            window.setState(Frame.NORMAL);
            window.toFront();
        });
        runningApps.add(windowButton);
        runningApps.revalidate();
        windowCount++;

        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                runningApps.remove(windowButton);
                runningApps.revalidate();
                windowCount--;
            }
        });
    }

    private void setupSystemTray() {
        if (SystemTray.isSupported()) {
            try {
                SystemTray tray = SystemTray.getSystemTray();
                PopupMenu popup = new PopupMenu();
                
                MenuItem showItem = new MenuItem("Show Desktop");
                showItem.addActionListener(e -> setVisible(true));
                popup.add(showItem);
                
                MenuItem exitItem = new MenuItem("Exit");
                exitItem.addActionListener(e -> System.exit(0));
                popup.add(exitItem);
                
                Image icon = createTrayIcon();
                TrayIcon trayIcon = new TrayIcon(icon, "WorkNix", popup);
                trayIcon.setImageAutoSize(true);
                
                tray.add(trayIcon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Image createTrayIcon() {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(new Color(50, 80, 100));
        g2d.fillRect(0, 0, 16, 16);
        g2d.setColor(Color.WHITE);
        g2d.drawString("W", 3, 12);
        g2d.dispose();
        return image;
    }

    private void addDesktopMenu() {
        JPopupMenu desktopMenu = new JPopupMenu();
        
        JMenuItem newTerminal = new JMenuItem("New Terminal");
        newTerminal.addActionListener(e -> terminal.setVisible(true));
        desktopMenu.add(newTerminal);
        
        JMenuItem newFolder = new JMenuItem("New Folder");
        newFolder.addActionListener(e -> fileManager.setVisible(true));
        desktopMenu.add(newFolder);
        
        desktopMenu.addSeparator();
        
        JMenuItem refresh = new JMenuItem("Refresh Desktop");
        refresh.addActionListener(e -> desktop.repaint());
        desktopMenu.add(refresh);
        
        desktop.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    desktopMenu.show(desktop, e.getX(), e.getY());
                }
            }
        });
    }

    private JPanel createDesktopIcons() {
        JPanel iconPanel = new JPanel(null);
        iconPanel.setOpaque(false);

        // Existing icons
        DesktopIcon terminalIcon = new DesktopIcon("Terminal", "terminal", 
            e -> terminal.setVisible(true));
        terminalIcon.setBounds(20, 20, 80, 80);
        iconPanel.add(terminalIcon);

        DesktopIcon fileManagerIcon = new DesktopIcon("Files", "files", 
            e -> fileManager.setVisible(true));
        fileManagerIcon.setBounds(20, 120, 80, 80);
        iconPanel.add(fileManagerIcon);

        // New application icons
        DesktopIcon calculatorIcon = new DesktopIcon("Calculator", "calculator", 
            e -> calculator.setVisible(true));
        calculatorIcon.setBounds(20, 220, 80, 80);
        iconPanel.add(calculatorIcon);

        DesktopIcon notepadIcon = new DesktopIcon("Notepad", "notepad", 
            e -> notepad.setVisible(true));
        notepadIcon.setBounds(20, 320, 80, 80);
        iconPanel.add(notepadIcon);

        return iconPanel;
    }

    private void showStartMenu() {
        JPopupMenu startMenu = new JPopupMenu();
        
        JMenuItem terminal = new JMenuItem("Terminal");
        terminal.addActionListener(e -> this.terminal.setVisible(true));
        startMenu.add(terminal);

        JMenuItem files = new JMenuItem("File Manager");
        files.addActionListener(e -> fileManager.setVisible(true));
        startMenu.add(files);

        startMenu.addSeparator();

        JMenuItem logout = new JMenuItem("Log Out");
        logout.addActionListener(e -> logout());
        startMenu.add(logout);

        // Add new applications to start menu
        startMenu.addSeparator();
        
        JMenu accessoriesMenu = new JMenu("Accessories");
        
        JMenuItem calcItem = new JMenuItem("Calculator");
        calcItem.addActionListener(e -> calculator.setVisible(true));
        accessoriesMenu.add(calcItem);
        
        JMenuItem notepadItem = new JMenuItem("Notepad");
        notepadItem.addActionListener(e -> notepad.setVisible(true));
        accessoriesMenu.add(notepadItem);
        
        startMenu.add(accessoriesMenu);

        // Add to showStartMenu method after accessories menu
        startMenu.addSeparator();
        
        JMenuItem aboutItem = new JMenuItem("About WorkNix");
        aboutItem.addActionListener(e -> {
            About about = new About(this);
            about.setVisible(true);
        });
        startMenu.add(aboutItem);

        // Get the start button and its location
        Component button = taskbar.getComponent(0); // The start button
        Point buttonLoc = button.getLocationOnScreen();
        
        // Calculate position to show menu above the taskbar button
        int x = buttonLoc.x;
        int y = buttonLoc.y - startMenu.getPreferredSize().height;
        
        // Show the menu at the calculated position
        startMenu.show(button, 0, -startMenu.getPreferredSize().height);
    }

    private void addKeyBindings() {
        // Alt+T for terminal
        KeyStroke terminalKey = KeyStroke.getKeyStroke(KeyEvent.VK_T, 
            InputEvent.ALT_DOWN_MASK);
        desktop.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
               .put(terminalKey, "openTerminal");
        desktop.getActionMap().put("openTerminal", 
            new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    terminal.setVisible(true);
                }
            });

        // Alt+F for file manager
        KeyStroke fileKey = KeyStroke.getKeyStroke(KeyEvent.VK_F, 
            InputEvent.ALT_DOWN_MASK);
        desktop.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
               .put(fileKey, "openFiles");
        desktop.getActionMap().put("openFiles", 
            new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    fileManager.setVisible(true);
                }
            });
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to log out?",
            "Log Out",
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            // Save system state
            fileSystem.saveState();
            
            // Hide main window
            setVisible(false);
            
            // Show shutdown screen and start shutdown sequence
            shutdownScreen.startShutdown();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WorkNixDE de = new WorkNixDE();
            de.setVisible(true);
        });
    }
} 