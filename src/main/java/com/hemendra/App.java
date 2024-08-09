package com.hemendra;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import java.awt.event.KeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        Logger logger = Logger.getLogger(App.class.getName());
        logger.setLevel(Level.OFF);

        try {
            GlobalScreen.registerNativeHook();
        } catch (Exception ex) {
            System.err.println("There was a problem registering the native hook.");
        }

        GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
            @Override
            public void nativeKeyTyped(NativeKeyEvent nativeEvent) {
                System.out.println("Typed: " + nativeEvent.getKeyLocation());
            }

            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
                System.out.println("Pressed: " + nativeEvent.getKeyCode());
            }
        });

        // Prevent the application from exiting until the user presses escape.
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
