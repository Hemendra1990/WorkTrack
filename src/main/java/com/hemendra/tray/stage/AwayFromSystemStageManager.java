package com.hemendra.tray.stage;

import com.hemendra.activity.systemevent.impl.MacOsSystemEventListener;
import com.hemendra.enums.ActivityType;
import com.hemendra.tray.controller.WtAwayFromSystemController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Slf4j
public class AwayFromSystemStageManager {
    //TODO: should be moving to another class, otherwise it =will brek SOLID
    public void launchSystemAwayScene(ActivityType activityType, LocalDateTime startTime, LocalDateTime endTime, long durationInSeconds, UUID sessionId) {
        try {
            Stage systemAwayStage = getStage();
            Result result = getSystemAwayFxml();
            // Get the screen dimensions
            Rectangle2D screenBounds = Screen.getPrimary().getBounds();
            StackPane blurryBackground = createBlurryStackPane(screenBounds);

            // Create a modal container and add the content
            StackPane modalContainer = new StackPane();
            modalContainer.getChildren().add(result.contentPane());

            applySyleToModalContainer(modalContainer);

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

            WtAwayFromSystemController controller = (WtAwayFromSystemController) result.fxmlLoader().getController();
            controller.setStage(systemAwayStage);
            controller.setData(activityType, startTime, endTime, durationInSeconds, sessionId);
        } catch (IOException exception) {
            log.error("Unable to show the away stage", exception);
        }
    }

    private static void applySyleToModalContainer(StackPane modalContainer) {
        // Apply styling (border and z-index-like stacking)
        modalContainer.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: black;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 10px;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-padding: 10;"
        );
    }

    private static StackPane createBlurryStackPane(Rectangle2D screenBounds) {
        // Create a blurry background pane
        StackPane blurryBackground = new StackPane();
        blurryBackground.setPrefSize(screenBounds.getWidth(), screenBounds.getHeight());
        blurryBackground.setEffect(new GaussianBlur(10));  // Apply GaussianBlur to the background
        blurryBackground.setStyle("-fx-background-color: rgba(81,146,223,0.09);");  // Semi-transparent black
        return blurryBackground;
    }

    private static Result getSystemAwayFxml() throws IOException {
        // Load the FXML for the modal content
        URL awayFromSystemFxmlResource = MacOsSystemEventListener.class.getResource("/com/hemendra/tray/wt-away-from-system.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(awayFromSystemFxmlResource);
        AnchorPane contentPane = fxmlLoader.load();  // The content pane for the modal
        Result result = new Result(fxmlLoader, contentPane);
        return result;
    }

    private record Result(FXMLLoader fxmlLoader, AnchorPane contentPane) {
    }

    private static Stage getStage() throws IOException {
        // Create the stage for the modal
        Stage systemAwayStage = new Stage();
        systemAwayStage.setTitle("Unity Tracker");
        InputStream iconStream = JavaFXApplication.class.getResource("/logo-light.png").openStream();
        Image image = new Image(iconStream);
        systemAwayStage.getIcons().add(image);
        systemAwayStage.setResizable(false);
        systemAwayStage.setAlwaysOnTop(true);
        return systemAwayStage;
    }
}
