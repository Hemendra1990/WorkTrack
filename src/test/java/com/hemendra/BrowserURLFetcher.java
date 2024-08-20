package com.hemendra;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class BrowserURLFetcher {
    public static void main(String[] args) throws AWTException, IOException, UnsupportedFlavorException, InterruptedException {
        // Create a robot instance
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
        // Here we simulate a click at coordinates (200, 200). You can adjust this based on your needs.
        robot.mouseMove(200, 200);
        robot.mousePress(KeyEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(KeyEvent.BUTTON1_DOWN_MASK);

        // Allow some time after the click
        Thread.sleep(500);

        // Get the copied content from the clipboard
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String url = (String) clipboard.getData(DataFlavor.stringFlavor);

        // Print the URL
        System.out.println("URL: " + url);

        // Create a StringSelection with an empty string
        StringSelection emptySelection = new StringSelection("");

        // Set the clipboard content to the empty string
        clipboard.setContents(emptySelection, null);

        System.out.println("Clipboard has been cleared.");
    }

}
