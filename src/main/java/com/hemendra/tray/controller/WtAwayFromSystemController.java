package com.hemendra.tray.controller;

import com.hemendra.activity.systemevent.service.SleepActivityLogService;
import com.hemendra.enums.ActivityState;
import com.hemendra.enums.ActivityType;
import com.hemendra.util.BeanUtils;
import com.hemendra.util.WtFxUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class WtAwayFromSystemController {

    @FXML
    private Pane wtAwayFromSystemRootPane;

    @FXML
    private Label activitySummaryLabel;

    @FXML
    private Label fromTimeLabel;

    @FXML
    private Label toTimeLabel;

    @FXML
    private ComboBox<String> activityComboBox;

    @FXML
    private TextArea activityDescTextarea;

    @FXML
    private Button activitySaveButton;

    @FXML
    private Label validationErrorMsgLabel;

    private Stage systemAwayStage;
    private SleepActivityLogService sleepActivityLogService;


    private final List<String> activityLists = Arrays.asList("Client Call", "Discussion", "Internal Meeting",
            "Long Break", "Lunch Break", "Self Training", "Short Break", "Team Meeting", "Training", "Work Review", "HR Activity");

    //String summaryLabel = "You have been away from your system for 00:17:22 since 16 Sep 2024 15:02:00. Please provide activity details during this period"
    String summaryLabel = "You have been away from your system for %s since %s. \n" +
            "Please provide activity details during this period.";

    ActivityType activityType;
    LocalDateTime startTime;
    LocalDateTime endTime;
    long durationInSeconds;
    UUID sessionId;

    @FXML
    public void initialize() {
        activityComboBox.getItems().addAll(activityLists);
        activitySaveButton.setOnMouseClicked(event -> {
            handleMouseClick();
        });
        this.sleepActivityLogService = BeanUtils.getBean(SleepActivityLogService.class);
    }

    private void handleMouseClick() {
        boolean isFormValid = validateForm();
        if (isFormValid) {
            String reason = activityDescTextarea.getText().toString();
            String activity = activityComboBox.getEditor().getText();
            sleepActivityLogService.saveActivityLogWithReason(reason, activity, activityType, startTime, endTime, durationInSeconds, sessionId, ActivityState.END);
        }
    }

    private boolean validateForm() {
        if (activityComboBox.getValue() == null) {
            validationErrorMsgLabel.setText("Please select a valid activity.");
            return false;
        }

        if (activityDescTextarea.getText().isEmpty()) {
            validationErrorMsgLabel.setText("Please enter a valid activity description.");
            return false;
        }
        validationErrorMsgLabel.setText("");
        this.systemAwayStage.close();
        return true;
    }

    public void setData(ActivityType activityType, LocalDateTime startTime, LocalDateTime endTime, long durationInSeconds, UUID sessionId) {
        this.activityType = activityType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationInSeconds = durationInSeconds;
        this.sessionId = sessionId;

        //Update the summaryText
        String awayForFormattedTime = WtFxUtils.getDurationInFormatHHMMSS(startTime, endTime);
        String formattedStartTime = WtFxUtils.getFormattedStartTime(startTime);
        activitySummaryLabel.setText(String.format(summaryLabel, awayForFormattedTime, formattedStartTime));

        //Update the from and to time
        String toTimeFormated = WtFxUtils.getTimeFormat(endTime);
        String fromTimeFormated = WtFxUtils.getTimeFormat(startTime);
        fromTimeLabel.setText(String.format(summaryLabel, awayForFormattedTime, formattedStartTime));
        fromTimeLabel.setText(fromTimeFormated);
        toTimeLabel.setText(toTimeFormated);
    }

    public void setStage(Stage systemAwayStage) {
        this.systemAwayStage = systemAwayStage;
    }

}
