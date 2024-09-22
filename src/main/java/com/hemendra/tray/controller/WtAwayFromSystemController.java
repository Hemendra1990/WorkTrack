package com.hemendra.tray.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

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

    private final List<String> activityLists = Arrays.asList("Client Call", "Discussion", "Internal Meeting",
            "Long Break", "Lunch Break", "Self Training", "Short Break", "Team Meeting", "Training", "Work Review", "HR Activity");

    @FXML
    public void initialize() {
        activityComboBox.getItems().addAll(activityLists);
        fromTimeLabel.setText("16:28 pm");
    }
}
