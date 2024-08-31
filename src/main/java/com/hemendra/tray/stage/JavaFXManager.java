package com.hemendra.tray.stage;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JavaFXManager {
    private static boolean isInitialized = false;

    public static void initialize() {
        if (!isInitialized) {
            new Thread(() -> {
                JavaFXApplication.launch(JavaFXApplication.class);
            }).start();
            isInitialized = true;
        }
    }

    public static void showStage() {
        log.info("JavaFXManager showStage called");
        if (!isInitialized) {
            log.error("JavaFX not initialized. Call initialize() first.");
            return;
        }
        JavaFXApplication.showStage();
    }
}