package com.hemendra.tray.controller;

import com.hemendra.util.WtFxUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Component
public class WtSystemTrayController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Label timeStartedLabel;

    @FXML
    private Label timeElapsedLabel;

    @FXML
    private Label userName;

    @FXML
    private void initialize() {
        userName.setText(WtFxUtils.getUserName());
        // Create a timeline that runs every second
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateTime()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateTime() {
        LocalDateTime appStartTime = WtFxUtils.getAppStartTime();
        String durationFormatted = getDurationFormatted(appStartTime, LocalDateTime.now());
        timeStartedLabel.setText(DateTimeFormatter.ofPattern("dd MMM, hh:mm:ss a").format(appStartTime));
        timeElapsedLabel.setText(durationFormatted);
    }

    private static String getDurationFormatted(LocalDateTime start, LocalDateTime end) {
        // Calculate the difference in hours, minutes, and seconds
        long hours = ChronoUnit.HOURS.between(start, end);
        long minutes = ChronoUnit.MINUTES.between(start, end) % 60;
        long seconds = ChronoUnit.SECONDS.between(start, end) % 60;

        // Format and return the duration string in "hh:mm:ss"
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
