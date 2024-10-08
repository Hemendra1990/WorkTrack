package com.hemendra.tray.stage;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class JavaFXApplication extends Application {
    private static Stage primaryStage;
    private static JavaFXApplication instance;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        instance = this;
        createAndSetScene();

        // Prevent JavaFX application from exiting when all windows are closed
        Platform.setImplicitExit(false);
    }

    private void createAndSetScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(JavaFXApplication.class.getResource("/com/hemendra/tray/wt-info.fxml"));
        //primaryStage.setScene(new Scene(new StackPane(new Label("Unity Tracker Details")), 350, 600));
        primaryStage.setScene(new Scene(fxmlLoader.load()));
        primaryStage.setResizable(false);
        primaryStage.setTitle("Unity Tracker");
        addStageIcon();
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            hideStage();
        });
    }

    private static void addStageIcon() throws IOException {
        InputStream iconStream = JavaFXApplication.class.getResource("/logo-light.png").openStream();
        Image image = new Image(iconStream);
        primaryStage.getIcons().add(image);
    }

    public static void showStage() {
        Platform.runLater(() -> {
            if (primaryStage == null) {
                log.error("Primary stage is null, recreating...");
                try {
                    instance.createAndSetScene();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (!primaryStage.isShowing()) {
                primaryStage.show();
            }
            primaryStage.toFront();
        });
    }

    private static void hideStage() {
        log.info("hideStage called");
        Platform.runLater(() -> {
            if (primaryStage != null) {
                primaryStage.hide();
            } else {
                log.error("Cannot hide stage: primary stage is null");
            }
        });
    }
}