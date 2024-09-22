package com.hemendra.tray.stage;

import com.hemendra.activity.systemevent.impl.MacOsSystemEventListener;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Component
@Slf4j
public class AwayFromSystemStageManager {
    //TODO: should be moving to another class, otherwise it =will brek SOLID
    public void launchSystemAwayScene() {
        try {
            // Create the stage for the modal
            Stage systemAwayStage = new Stage();
            systemAwayStage.setTitle("Unity Tracker");
            systemAwayStage.setResizable(false);
            systemAwayStage.setAlwaysOnTop(true);

            // Load the FXML for the modal content
            URL awayFromSystemFxmlResource = MacOsSystemEventListener.class.getResource("/com/hemendra/tray/wt-away-from-system.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(awayFromSystemFxmlResource);
            AnchorPane contentPane = fxmlLoader.load();  // The content pane for the modal

            // Get the screen dimensions
            Rectangle2D screenBounds = Screen.getPrimary().getBounds();

            // Create a blurry background pane
            StackPane blurryBackground = new StackPane();
            blurryBackground.setPrefSize(screenBounds.getWidth(), screenBounds.getHeight());
            blurryBackground.setEffect(new GaussianBlur(10));  // Apply GaussianBlur to the background
            blurryBackground.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");  // Semi-transparent black

            // Create a modal container and add the content
            StackPane modalContainer = new StackPane();
            modalContainer.getChildren().add(contentPane);

            // Apply styling (border and z-index-like stacking)
            modalContainer.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-border-color: black;" +
                            "-fx-border-width: 2px;" +
                            "-fx-border-radius: 10px;" +
                            "-fx-background-radius: 10px;" +
                            "-fx-padding: 10;"
            );

            // Set a max size for the modal container to ensure it doesn't get too large
            modalContainer.setMaxSize(400, 300);

            // Create a root pane to combine the blurry background and modal content
            StackPane rootPane = new StackPane();
            rootPane.getChildren().addAll(blurryBackground, modalContainer);

            // Create a scene that fills the screen
            Scene scene = new Scene(rootPane, screenBounds.getWidth(), screenBounds.getHeight());

            // Set the scene to the stage
            systemAwayStage.setScene(scene);

            // Make the modal block interaction with other windows
            systemAwayStage.initModality(Modality.APPLICATION_MODAL);

            // Make the stage full screen
            systemAwayStage.setFullScreen(true);
            systemAwayStage.setFullScreenExitHint("");  // Remove the exit full screen hint

            // Show the modal stage
            systemAwayStage.show();
        } catch (IOException exception) {
            log.error("Unable to show the away stage", exception);
        }
    }
}
