package com.hemendra.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;

@Component
@Slf4j
public class WindowsBrowserURLFetcher {
    public String fetchBrowserUrlPreservingClipboard() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable originalContent = clipboard.getContents(null);

        // Check if clipboard access is available
        if (!requestClipboardAccess()) {
            JOptionPane.showMessageDialog(null,
                    "Clipboard access is required. Please run the application with sufficient permissions or try again.",
                    "Clipboard Access Required",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        // Step 2: Copy the URL using Robot
        try {
            Robot robot = getBrowserUrlCopyingRobot();
            robot.mouseRelease(KeyEvent.BUTTON1_DOWN_MASK);

            // Step 3: Get the copied URL
            String url = (String) clipboard.getData(DataFlavor.stringFlavor);
            return url;
        } catch (AWTException | IOException | UnsupportedFlavorException e) {
            JOptionPane.showMessageDialog(null,
                    "Failed to access clipboard: " + e.getMessage(),
                    "Clipboard Access Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        } finally {
            // Step 4: Restore the original clipboard content
            try {
                clipboard.setContents(originalContent, null);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Failed to restore clipboard content: " + e.getMessage(),
                        "Clipboard Access Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean requestClipboardAccess() {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.getContents(null);  // Attempt to access the clipboard
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    private static Robot getBrowserUrlCopyingRobot() throws AWTException {
        Robot robot = new Robot();
        // Press Alt + D to focus on the address bar (Alt + D works in most browsers)
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_D);
        robot.keyRelease(KeyEvent.VK_D);
        robot.keyRelease(KeyEvent.VK_ALT);

        // Press Ctrl + C to copy the URL
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_C);
        robot.keyRelease(KeyEvent.VK_C);
        robot.keyRelease(KeyEvent.VK_CONTROL);

        return robot;
    }

}
