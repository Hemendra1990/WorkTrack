package com.hemendra.tray.stage;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JavaFXApplication extends Application {
    private static Stage primaryStage;
    private static JavaFXApplication instance;

    @Override
    public void start(Stage stage) {
        log.info("JavaFXApplication start method called");
        primaryStage = stage;
        instance = this;
        createAndSetScene();
        log.info("JavaFX application initialized");

        // Prevent JavaFX application from exiting when all windows are closed
        Platform.setImplicitExit(false);
    }

    private void createAndSetScene() {
        primaryStage.setScene(new Scene(new StackPane(new Label("Unity Tracker Details")), 350, 600));
        primaryStage.setTitle("Unity Tracker");
        primaryStage.setOnCloseRequest(event -> {
            log.info("Close request received");
            event.consume();
            hideStage();
        });
    }

    public static void showStage() {
        log.info("showStage called");
        Platform.runLater(() -> {
            if (primaryStage == null) {
                log.error("Primary stage is null, recreating...");
                instance.createAndSetScene();
            }
            if (!primaryStage.isShowing()) {
                primaryStage.show();
                log.info("Stage shown");
            }
            primaryStage.toFront();
            log.info("Stage brought to front");
        });
    }

    private static void hideStage() {
        log.info("hideStage called");
        Platform.runLater(() -> {
            if (primaryStage != null) {
                primaryStage.hide();
                log.info("Stage hidden");
            } else {
                log.error("Cannot hide stage: primary stage is null");
            }
        });
    }
}