package com.hemendra.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
        // Step 1: Save the current clipboard content
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable originalContent = clipboard.getContents(null);
        // Step 2: Copy the URL using Robot
        try {
            Robot robot = getBrowserUrlCopyingRobot();
            robot.mouseRelease(KeyEvent.BUTTON1_DOWN_MASK);

            // Step 3: Get the copied URL
            String url = (String) clipboard.getData(DataFlavor.stringFlavor);
            return url;
        } catch (AWTException | IOException | UnsupportedFlavorException e) {
            log.error(e.getMessage());
            return null;
        } finally {
            // Step 4: Restore the original clipboard content
            try {
                clipboard.setContents(originalContent, null);
            } catch (Exception e){}
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

        // Perform a mouse click on the page to dismiss any dropdowns
        //robot.mouseMove(200, 200);
        //robot.mousePress(KeyEvent.BUTTON1_DOWN_MASK);
        return robot;
    }

}
