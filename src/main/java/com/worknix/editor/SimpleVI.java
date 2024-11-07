package com.worknix.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SimpleVI {
    private List<String> buffer;
    private int cursorRow;
    private int cursorCol;
    private boolean insertMode;
    private final Scanner scanner;
    private boolean running;
    private String filename;
    private boolean modified;

    public SimpleVI(String initialContent, String filename) {
        this.buffer = new ArrayList<>();
        if (initialContent != null && !initialContent.isEmpty()) {
            String[] lines = initialContent.split("\n");
            for (String line : lines) {
                buffer.add(line);
            }
        }
        if (buffer.isEmpty()) {
            buffer.add("");
        }
        this.filename = filename;
        this.cursorRow = 0;
        this.cursorCol = 0;
        this.insertMode = false;
        this.scanner = new Scanner(System.in);
        this.running = true;
        this.modified = false;
    }

    public String getContent() {
        return String.join("\n", buffer);
    }

    public void start() {
        clearScreen();
        while (running) {
            displayBuffer();
            handleInput();
        }
    }

    private void displayBuffer() {
        clearScreen();
        // Display buffer content
        for (int i = 0; i < buffer.size(); i++) {
            if (i == cursorRow && !insertMode) {
                String line = buffer.get(i);
                System.out.print(line.substring(0, Math.min(cursorCol, line.length())));
                System.out.print("\u001B[47m \u001B[0m"); // Highlight cursor position
                if (cursorCol < line.length()) {
                    System.out.println(line.substring(cursorCol + 1));
                } else {
                    System.out.println();
                }
            } else {
                System.out.println(buffer.get(i));
            }
        }

        // Display status line
        System.out.println("----------------------------------------");
        if (insertMode) {
            System.out.println("-- INSERT --");
        } else {
            System.out.println("-- NORMAL --");
        }
    }

    private void handleInput() {
        String input = scanner.nextLine();
        if (insertMode) {
            handleInsertMode(input);
        } else {
            handleNormalMode(input);
        }
    }

    private void handleInsertMode(String input) {
        if (input.equals("\u001B")) { // ESC key
            insertMode = false;
            if (cursorCol > 0) {
                cursorCol--;
            }
        } else {
            String currentLine = buffer.get(cursorRow);
            String newLine = currentLine.substring(0, cursorCol) + input + 
                           currentLine.substring(cursorCol);
            buffer.set(cursorRow, newLine);
            cursorCol += input.length();
            modified = true;
        }
    }

    private void handleNormalMode(String input) {
        if (input.isEmpty()) return;

        switch (input.charAt(0)) {
            case 'i':
                insertMode = true;
                break;
            case 'h':
                if (cursorCol > 0) cursorCol--;
                break;
            case 'l':
                if (cursorCol < buffer.get(cursorRow).length()) cursorCol++;
                break;
            case 'j':
                if (cursorRow < buffer.size() - 1) {
                    cursorRow++;
                    cursorCol = Math.min(cursorCol, buffer.get(cursorRow).length());
                }
                break;
            case 'k':
                if (cursorRow > 0) {
                    cursorRow--;
                    cursorCol = Math.min(cursorCol, buffer.get(cursorRow).length());
                }
                break;
            case 'x':
                deleteCurrent();
                break;
            case 'o':
                insertNewLine();
                break;
            case ':':
                handleCommand(input.substring(1));
                break;
        }
    }

    private void handleCommand(String command) {
        switch (command.trim()) {
            case "w":
                // Signal to save
                running = false;
                break;
            case "q":
                if (!modified) {
                    running = false;
                } else {
                    System.out.println("No write since last change (add ! to override)");
                }
                break;
            case "q!":
                running = false;
                modified = false;
                break;
            case "wq":
                running = false;
                break;
        }
    }

    private void deleteCurrent() {
        String currentLine = buffer.get(cursorRow);
        if (cursorCol < currentLine.length()) {
            buffer.set(cursorRow, 
                currentLine.substring(0, cursorCol) + 
                currentLine.substring(cursorCol + 1));
            modified = true;
        }
    }

    private void insertNewLine() {
        buffer.add(cursorRow + 1, "");
        cursorRow++;
        cursorCol = 0;
        insertMode = true;
        modified = true;
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public boolean isModified() {
        return modified;
    }
} 