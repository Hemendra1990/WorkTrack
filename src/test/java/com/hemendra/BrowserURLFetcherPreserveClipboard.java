package com.hemendra;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class BrowserURLFetcherPreserveClipboard {
    public static void main(String[] args) throws Exception {
        // Step 1: Save the current clipboard content
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable originalContent = clipboard.getContents(null);

        try {
            // Step 2: Copy the URL using Robot
            Robot robot = new Robot();

            // Allow some time for the focus to shift to the browser
            Thread.sleep(1000);

            // Press Alt + D to focus on the address bar (Alt + D works in most browsers)
            robot.keyPress(KeyEvent.VK_ALT);
            robot.keyPress(KeyEvent.VK_D);
            robot.keyRelease(KeyEvent.VK_D);
            robot.keyRelease(KeyEvent.VK_ALT);

            // Allow time for the address bar to be selected
            Thread.sleep(500);

            // Press Ctrl + C to copy the URL
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_C);
            robot.keyRelease(KeyEvent.VK_C);
            robot.keyRelease(KeyEvent.VK_CONTROL);

            // Allow time for the URL to be copied to the clipboard
            Thread.sleep(500);

            // Perform a mouse click on the page to dismiss any dropdowns
            //robot.mouseMove(200, 200);
            robot.mousePress(KeyEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(KeyEvent.BUTTON1_DOWN_MASK);


            // Allow some time after the click
            Thread.sleep(500);

            // Step 3: Get the copied URL
            String url = (String) clipboard.getData(DataFlavor.stringFlavor);

            // Print the URL
            System.out.println("URL: " + url);

        } finally {
            // Step 4: Restore the original clipboard content
            clipboard.setContents(originalContent, null);
            System.out.println("Clipboard content restored.");
        }
    }

}
