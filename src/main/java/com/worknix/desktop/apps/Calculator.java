package com.worknix.desktop.apps;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Calculator extends JFrame {
    private JTextField display;
    private double result = 0;
    private String lastOperator = "=";
    private boolean startNumber = true;

    public Calculator() {
        super("WorkNix Calculator");
        setSize(250, 300);
        setLocationRelativeTo(null);

        // Create display
        display = new JTextField("0");
        display.setEditable(false);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setFont(new Font("Monospaced", Font.PLAIN, 20));

        // Create button panel
        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 2, 2));
        String[] buttonLabels = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+",
            "C", "±", "%", "√"
        };

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(e -> handleButton(label));
            buttonPanel.add(button);
        }

        // Layout
        setLayout(new BorderLayout(5, 5));
        add(display, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    private void handleButton(String label) {
        switch (label) {
            case "0": case "1": case "2": case "3": case "4":
            case "5": case "6": case "7": case "8": case "9":
            case ".":
                if (startNumber) {
                    display.setText(label);
                    startNumber = false;
                } else {
                    display.setText(display.getText() + label);
                }
                break;
            case "+": case "-": case "*": case "/": case "=":
                calculate(label);
                break;
            case "C":
                result = 0;
                lastOperator = "=";
                startNumber = true;
                display.setText("0");
                break;
            case "±":
                double value = Double.parseDouble(display.getText());
                display.setText(String.valueOf(-value));
                break;
            case "%":
                value = Double.parseDouble(display.getText());
                display.setText(String.valueOf(value / 100));
                break;
            case "√":
                value = Double.parseDouble(display.getText());
                display.setText(String.valueOf(Math.sqrt(value)));
                break;
        }
    }

    private void calculate(String operator) {
        double displayValue = Double.parseDouble(display.getText());
        
        switch (lastOperator) {
            case "+": result += displayValue; break;
            case "-": result -= displayValue; break;
            case "*": result *= displayValue; break;
            case "/": result /= displayValue; break;
            case "=": result = displayValue; break;
        }
        
        display.setText(String.valueOf(result));
        lastOperator = operator;
        startNumber = true;
    }
} 