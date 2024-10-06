package com.hemendra.tray.stage;

import com.hemendra.activity.systemevent.impl.MacOsSystemEventListener;
import com.hemendra.activity.systemreset.constant.Shift;
import com.hemendra.activity.systemreset.impl.WindowsSystemResetListener;
import com.hemendra.tray.controller.WtAwayFromSystemController;
import com.hemendra.tray.controller.WtShiftSelectionController;
import com.sun.jna.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
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
import java.util.function.Consumer;

@Slf4j
@Component
public class ShiftSelectionStageManager {
    public void launchShiftSelectionScene(Consumer<Shift> callback) {
        try {

            Stage shiftSelectionStage = getStage();
            Result result = getSystemShiftSelectionFxml();
            // Get the screen dimensions
            Rectangle2D screenBounds = Screen.getPrimary().getBounds();
            StackPane blurryBackground = createBlurryStackPane(screenBounds);

            // Create a modal container and add the content
            StackPane modalContainer = new StackPane();
            modalContainer.getChildren().add(result.contentPane());

            applySyleToModalContainer(modalContainer);

            modalContainer.setMaxSize(400, 300);

            StackPane rootPane = new StackPane();
            rootPane.getChildren().addAll(blurryBackground, modalContainer);

            // Create a scene that fills the screen
            Scene scene = new Scene(rootPane, screenBounds.getWidth(), screenBounds.getHeight());

            // Set the scene to the stage
            shiftSelectionStage.setScene(scene);

            // Make the modal block interaction with other windows
            shiftSelectionStage.initModality(Modality.APPLICATION_MODAL);

            // Make the stage full screen
            shiftSelectionStage.setFullScreen(true);
            shiftSelectionStage.setFullScreenExitHint("");  // Remove the exit full screen hint
            shiftSelectionStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            focusOnApp(shiftSelectionStage);

            // Show the modal stage
            shiftSelectionStage.show();

            WtShiftSelectionController controller = (WtShiftSelectionController) result.fxmlLoader().getController();
            controller.setStage(shiftSelectionStage);
            controller.setShiftUpdateCallback(callback);

        } catch (IOException e) {
            log.error("Unable to show the shift selection stage", e);
        }
    }

    private static void focusOnApp(Stage shiftSelectionStage) {
        shiftSelectionStage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            log.info("Focus on the shift selection stage old {}, new {}", oldValue, newValue);
            if (!newValue) {
                // Run the request to bring the stage to the front on the JavaFX Application Thread
                javafx.application.Platform
                        .runLater(() -> {
                            shiftSelectionStage.toFront();
                            shiftSelectionStage.requestFocus();
                        });
            }
        });
        if (Platform.isWindows()) {
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

    private static Result getSystemShiftSelectionFxml() throws IOException {
        // Load the FXML for the modal content
        URL shiftSelectionFxmlResource = JavaFXApplication.class.getResource("/com/hemendra/tray/shift-selection.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(shiftSelectionFxmlResource);
        AnchorPane contentPane = fxmlLoader.load();  // The content pane for the modal
        Result result = new Result(fxmlLoader, contentPane);
        return result;
    }

    private record Result(FXMLLoader fxmlLoader, AnchorPane contentPane) {
    }

    private static Stage getStage() throws IOException {
        // Create the stage for the modal
        Stage shiftSelectionStage = new Stage();
        shiftSelectionStage.setTitle("Unity Tracker");
        InputStream iconStream = JavaFXApplication.class.getResource("/logo-light.png").openStream();
        Image image = new Image(iconStream);
        shiftSelectionStage.getIcons().add(image);
        shiftSelectionStage.setResizable(false);
        shiftSelectionStage.setAlwaysOnTop(true);
        return shiftSelectionStage;
    }
}
