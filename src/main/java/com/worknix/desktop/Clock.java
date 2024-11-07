package com.worknix.desktop;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Clock {
    private final JLabel clockLabel;
    private final SimpleDateFormat timeFormat;

    public Clock() {
        clockLabel = new JLabel();
        clockLabel.setForeground(Color.WHITE);
        clockLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        timeFormat = new SimpleDateFormat("HH:mm:ss");

        // Update clock every second
        Timer timer = new Timer(1000, e -> updateTime());
        timer.start();
        updateTime();
    }

    private void updateTime() {
        clockLabel.setText(timeFormat.format(new Date()));
    }

    public JLabel getClockLabel() {
        return clockLabel;
    }
} 