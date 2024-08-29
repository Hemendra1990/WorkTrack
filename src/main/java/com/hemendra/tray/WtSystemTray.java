package com.hemendra.tray;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.net.URL;

@Component
@Slf4j
public class WtSystemTray {
    public void createSystemTray() {
        // Ensure AWT is supported
        if (!SystemTray.isSupported()) {
            log.error("System tray is not supported!");
            return;
        }

        // Load the icon
        URL imageUrl = getClass().getResource("/logo-light.png");
        if (imageUrl == null) {
            log.error("Icon image not found.");
            return;
        }
        Image image = Toolkit.getDefaultToolkit().getImage(imageUrl);

        // Create tray icon
        TrayIcon trayIcon = new TrayIcon(image, "Unity Tracker");
        trayIcon.setImageAutoSize(true);

        // Create popup menu
        PopupMenu popupMenu = new PopupMenu();
        MenuItem openItem = new MenuItem("Open");
        MenuItem exitItem = new MenuItem("Exit");

        popupMenu.add(openItem);
        popupMenu.addSeparator();
        popupMenu.add(exitItem);

        trayIcon.setPopupMenu(popupMenu);

        // Add action listeners
        openItem.addActionListener(e -> log.info("Open clicked"));
        exitItem.addActionListener(e -> System.exit(0));
        trayIcon.addActionListener(e -> log.info("Tray icon clicked"));

        // Add tray icon to system tray
        try {
            SystemTray tray = SystemTray.getSystemTray();
            tray.add(trayIcon);
        } catch (AWTException e) {
            log.error("TrayIcon could not be added.");
        }
    }
}
